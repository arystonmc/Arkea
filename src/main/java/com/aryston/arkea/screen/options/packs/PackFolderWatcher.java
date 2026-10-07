package com.aryston.arkea.screen.options.packs;

import com.aryston.arkea.Arkea;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import org.jspecify.annotations.Nullable;

final class PackFolderWatcher implements AutoCloseable {
    private final WatchService service;
    private final Path folder;

    private PackFolderWatcher(Path folder) throws IOException {
        this.folder = folder;
        this.service = folder.getFileSystem().newWatchService();
        try {
            this.watch(folder);
            try (DirectoryStream<Path> paths = Files.newDirectoryStream(folder)) {
                for (Path path : paths) {
                    if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                        this.watch(path);
                    }
                }
            }
        } catch (IOException exception) {
            this.service.close();
            throw exception;
        }
    }

    static @Nullable PackFolderWatcher create(Path folder) {
        try {
            return new PackFolderWatcher(folder);
        } catch (IOException exception) {
            Arkea.LOGGER.warn("Failed to watch pack folder {}", folder, exception);
            return null;
        }
    }

    private void watch(Path path) throws IOException {
        path.register(this.service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
    }

    boolean poll() throws IOException {
        boolean changed = false;
        WatchKey key;
        while ((key = this.service.poll()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                changed = true;
                if (key.watchable() == this.folder && event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    Path created = this.folder.resolve((Path) event.context());
                    if (Files.isDirectory(created, LinkOption.NOFOLLOW_LINKS)) {
                        this.watch(created);
                    }
                }
            }
            key.reset();
        }
        return changed;
    }

    @Override
    public void close() throws IOException {
        this.service.close();
    }
}
