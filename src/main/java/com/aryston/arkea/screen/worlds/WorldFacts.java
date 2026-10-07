package com.aryston.arkea.screen.worlds;

import com.aryston.arkea.Arkea;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.world.level.storage.LevelResource;

record WorldFacts(long bytes, long playTicks) {
    static final WorldFacts UNKNOWN = new WorldFacts(-1L, -1L);
    private static final String STATS = "stats";
    private static final String CUSTOM = "minecraft:custom";
    private static final String PLAY_TIME = "minecraft:play_time";
    private static final String JSON = ".json";

    static WorldFacts read(Path folder, UUID player) {
        return new WorldFacts(size(folder), playTicks(folder.resolve(LevelResource.PLAYER_STATS_DIR.id()).resolve(player + JSON)));
    }

    private static long size(Path folder) {
        try (Stream<Path> paths = Files.walk(folder)) {
            return paths.filter(Files::isRegularFile).mapToLong(WorldFacts::fileSize).sum();
        } catch (IOException | RuntimeException exception) {
            Arkea.LOGGER.debug("Could not measure world folder {}", folder, exception);
            return -1L;
        }
    }

    private static long fileSize(Path file) {
        try {
            return Files.size(file);
        } catch (IOException exception) {
            return 0L;
        }
    }

    private static long playTicks(Path statsFile) {
        if (!Files.isRegularFile(statsFile)) {
            return -1L;
        }
        try (Reader reader = Files.newBufferedReader(statsFile, StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(reader);
            JsonObject custom = root.getAsJsonObject().getAsJsonObject(STATS).getAsJsonObject(CUSTOM);
            return custom != null && custom.has(PLAY_TIME) ? custom.get(PLAY_TIME).getAsLong() : -1L;
        } catch (IOException | RuntimeException exception) {
            Arkea.LOGGER.debug("Could not read play time from {}", statsFile, exception);
            return -1L;
        }
    }
}
