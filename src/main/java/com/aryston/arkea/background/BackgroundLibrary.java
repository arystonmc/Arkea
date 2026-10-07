package com.aryston.arkea.background;

import com.aryston.arkea.Arkea;
import com.aryston.arkea.config.ArkeaConfig;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public final class BackgroundLibrary {
    private static final String TEMP_PREFIX = ".importing-";
    private static final String IMPORTED_FOLDER = "imported";
    private static final String FAILED_FOLDER = "failed";
    private static final int NAME_LENGTH = 40;
    private static final int ID_LENGTH = 24;
    private static final long POLL_INTERVAL_MS = 1000L;
    private static @Nullable BackgroundLibrary instance;

    private final Path root;
    private final List<ImportJob> jobs = new CopyOnWriteArrayList<>();
    private final ExecutorService worker = Executors.newSingleThreadExecutor(WorkerThreads.named("Arkea background import"));
    private final DropFolder dropFolder = new DropFolder();
    private List<BackgroundEntry> entries = List.of();
    private Set<Path> folders = Set.of();
    private boolean scanned;
    private long lastPoll;
    private int version;

    private BackgroundLibrary(Path root) {
        this.root = root;
    }

    public static BackgroundLibrary get() {
        if (instance == null) {
            instance = new BackgroundLibrary(Minecraft.getInstance().gameDirectory.toPath().resolve(Arkea.MOD_ID).resolve("backgrounds"));
        }
        return instance;
    }

    public Path root() {
        try {
            Files.createDirectories(this.root);
        } catch (IOException exception) {
            Arkea.LOGGER.warn("Could not create {}", this.root, exception);
        }
        return this.root;
    }

    public int version() {
        return this.version;
    }

    public List<BackgroundEntry> entries() {
        if (!this.scanned) {
            this.refresh();
        }
        return this.entries;
    }

    public List<ImportJob> jobs() {
        return this.jobs;
    }

    public void refresh() {
        this.read(this.list());
    }

    public void poll() {
        long now = Util.getMillis();
        if (now - this.lastPoll < POLL_INTERVAL_MS) {
            return;
        }
        this.lastPoll = now;
        Listing listing = this.list();
        if (!this.scanned || !listing.folders().equals(this.folders)) {
            this.read(listing);
        }
        for (Path file : this.dropFolder.settled(listing.files())) {
            if (this.jobs.stream().noneMatch(job -> job.source().equals(file))) {
                this.importFile(file, true);
            }
        }
    }

    private void read(Listing listing) {
        this.scanned = true;
        this.folders = listing.folders();
        List<BackgroundEntry> found = new ArrayList<>();
        for (Path folder : listing.folders()) {
            BackgroundEntry entry = BackgroundEntry.read(folder);
            if (entry != null) {
                found.add(entry);
            }
        }
        found.sort(Comparator.comparingLong(BackgroundEntry::created).reversed());
        this.entries = List.copyOf(found);
        this.version++;
    }

    private Listing list() {
        boolean firstScan = !this.scanned;
        Set<Path> folders = new HashSet<>();
        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(this.root())) {
            for (Path path : paths) {
                if (!Files.isDirectory(path)) {
                    if (BackgroundImporter.detect(path) != null) {
                        files.add(path);
                    }
                } else if (!path.getFileName().toString().startsWith(TEMP_PREFIX)) {
                    folders.add(path);
                } else if (firstScan) {
                    deleteFolder(path);
                }
            }
        } catch (IOException exception) {
            Arkea.LOGGER.warn("Could not read the background library", exception);
        }
        return new Listing(Set.copyOf(folders), List.copyOf(files));
    }

    public @Nullable BackgroundEntry selected() {
        String id = ArkeaConfig.BACKGROUND.get();
        if (ArkeaConfig.VANILLA_BACKGROUND.equals(id)) {
            return null;
        }
        for (BackgroundEntry entry : this.entries()) {
            if (entry.id().equals(id)) {
                return entry;
            }
        }
        return null;
    }

    public void select(@Nullable BackgroundEntry entry) {
        ArkeaConfig.set(ArkeaConfig.BACKGROUND, entry == null ? ArkeaConfig.VANILLA_BACKGROUND : entry.id());
        this.version++;
    }

    public void delete(BackgroundEntry entry) {
        if (entry.equals(this.selected())) {
            this.select(null);
        }
        MenuBackground.release(entry);
        deleteFolder(entry.folder());
        this.refresh();
    }

    public void dismiss(ImportJob job) {
        job.cancel();
        this.dropFolder.ignore(job.source());
        this.jobs.remove(job);
        this.version++;
    }

    public ImportJob importFile(Path source, boolean moveSourceAfterwards) {
        String fileName = source.getFileName().toString();
        int dot = fileName.lastIndexOf('.');
        String name = (dot > 0 ? fileName.substring(0, dot) : fileName).strip();
        if (name.length() > NAME_LENGTH) {
            name = name.substring(0, NAME_LENGTH).strip();
        }
        ImportJob job = new ImportJob(name.isEmpty() ? fileName : name, source);
        this.jobs.add(job);
        this.version++;
        this.worker.execute(() -> this.run(job, moveSourceAfterwards));
        return job;
    }

    private void run(ImportJob job, boolean moveSourceAfterwards) {
        if (job.isCancelled() || !Files.exists(job.source())) {
            job.state(ImportJob.State.CANCELLED);
            Minecraft.getInstance().execute(() -> this.dismiss(job));
            return;
        }
        job.state(ImportJob.State.RUNNING);
        String id = uniqueId(job.name());
        Path temp = this.root().resolve(TEMP_PREFIX + id);
        try {
            BackgroundImporter.convert(job.source(), temp, job.name(), job::progress, job::isCancelled);
            Path folder = this.root().resolve(id);
            Files.move(temp, folder, StandardCopyOption.ATOMIC_MOVE);
            if (moveSourceAfterwards) {
                this.moveSource(job, IMPORTED_FOLDER);
            }
            job.progress(1.0F);
            job.state(ImportJob.State.DONE);
            Minecraft.getInstance().execute(() -> this.finish(job, id));
        } catch (CancellationException exception) {
            deleteFolder(temp);
            job.state(ImportJob.State.CANCELLED);
            Minecraft.getInstance().execute(() -> this.dismiss(job));
        } catch (IOException | RuntimeException | OutOfMemoryError exception) {
            deleteFolder(temp);
            Arkea.LOGGER.warn("Could not import {} as a background", job.source(), exception);
            if (moveSourceAfterwards) {
                this.moveSource(job, FAILED_FOLDER);
            }
            job.fail(exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage());
            Minecraft.getInstance().execute(() -> this.version++);
        }
    }

    private void moveSource(ImportJob job, String folderName) {
        if (!Files.exists(job.source())) {
            return;
        }
        try {
            Path folder = this.root().resolve(folderName);
            Files.createDirectories(folder);
            Files.move(job.source(), folder.resolve(job.source().getFileName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            Arkea.LOGGER.warn("Could not move {} out of the background folder", job.source(), exception);
        }
    }

    private void finish(ImportJob job, String id) {
        this.jobs.remove(job);
        this.refresh();
        for (BackgroundEntry entry : this.entries) {
            if (entry.id().equals(id)) {
                this.select(entry);
            }
        }
    }

    private String uniqueId(String name) {
        String slug = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
        if (slug.length() > ID_LENGTH) {
            slug = slug.substring(0, ID_LENGTH);
        }
        return (slug.isEmpty() ? "background" : slug) + "-" + Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);
    }

    private record Listing(Set<Path> folders, List<Path> files) {
    }

    static void deleteFolder(Path folder) {
        if (!Files.exists(folder)) {
            return;
        }
        try (var paths = Files.walk(folder)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        } catch (IOException exception) {
            Arkea.LOGGER.warn("Could not delete {}", folder, exception);
        }
    }
}
