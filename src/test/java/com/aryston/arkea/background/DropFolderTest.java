package com.aryston.arkea.background;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DropFolderTest {
    @TempDir
    Path folder;

    @Test
    void fileSettlesWhenItStopsChanging() throws IOException {
        Path file = Files.write(this.folder.resolve("clip.mp4"), new byte[] {1, 2, 3});
        DropFolder drop = new DropFolder();

        assertTrue(drop.settled(List.of(file)).isEmpty(), "a file seen for the first time may still be copying");
        assertEquals(List.of(file), drop.settled(List.of(file)));
    }

    @Test
    void growingFileWaits() throws IOException {
        Path file = Files.write(this.folder.resolve("clip.mp4"), new byte[] {1});
        DropFolder drop = new DropFolder();
        drop.settled(List.of(file));

        Files.write(file, new byte[] {1, 2});
        Files.setLastModifiedTime(file, FileTime.fromMillis(Files.getLastModifiedTime(file).toMillis() + 1000L));

        assertTrue(drop.settled(List.of(file)).isEmpty());
        assertEquals(List.of(file), drop.settled(List.of(file)));
    }

    @Test
    void ignoredFileWaitsUntilItChanges() throws IOException {
        Path file = Files.write(this.folder.resolve("clip.mp4"), new byte[] {1});
        DropFolder drop = new DropFolder();
        drop.ignore(file);

        drop.settled(List.of(file));
        assertTrue(drop.settled(List.of(file)).isEmpty(), "a cancelled import must not start again");

        Files.write(file, new byte[] {1, 2});
        drop.settled(List.of(file));
        assertEquals(List.of(file), drop.settled(List.of(file)));
    }

    @Test
    void missingFilesAreForgotten() {
        DropFolder drop = new DropFolder();

        assertTrue(drop.settled(List.of(this.folder.resolve("gone.mp4"))).isEmpty());
    }
}
