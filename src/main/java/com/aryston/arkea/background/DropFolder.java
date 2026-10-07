package com.aryston.arkea.background;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

final class DropFolder {
    private final Map<Path, Snapshot> seen = new HashMap<>();
    private final Map<Path, Snapshot> ignored = new HashMap<>();

    void ignore(Path file) {
        Snapshot snapshot = Snapshot.of(file);
        if (snapshot != null) {
            this.ignored.put(file, snapshot);
        }
    }

    List<Path> settled(List<Path> files) {
        List<Path> settled = new ArrayList<>();
        Map<Path, Snapshot> current = new HashMap<>();
        for (Path file : files) {
            Snapshot snapshot = Snapshot.of(file);
            if (snapshot == null || snapshot.equals(this.ignored.get(file))) {
                continue;
            }
            if (snapshot.equals(this.seen.get(file)) && isClosed(file)) {
                settled.add(file);
            } else {
                current.put(file, snapshot);
            }
        }
        this.seen.clear();
        this.seen.putAll(current);
        return settled;
    }

    private static boolean isClosed(Path file) {
        if (!Files.isWritable(file)) {
            return true;
        }
        try (FileChannel channel = FileChannel.open(file, StandardOpenOption.WRITE)) {
            return channel.isOpen();
        } catch (IOException exception) {
            return false;
        }
    }

    private record Snapshot(long size, long modified) {
        static @Nullable Snapshot of(Path file) {
            try {
                return new Snapshot(Files.size(file), Files.getLastModifiedTime(file).toMillis());
            } catch (IOException exception) {
                return null;
            }
        }
    }
}
