package com.aryston.arkea.screen.title;

import com.aryston.arkea.Arkea;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.jspecify.annotations.Nullable;

public record RecentWorld(LevelSummary summary, byte @Nullable [] iconBytes) {
    public static CompletableFuture<Optional<RecentWorld>> load(Minecraft minecraft) {
        LevelStorageSource source = minecraft.getLevelSource();
        LevelStorageSource.LevelCandidates candidates;
        try {
            candidates = source.findLevelCandidates();
        } catch (LevelStorageException exception) {
            Arkea.LOGGER.warn("Could not list worlds for the title screen", exception);
            return CompletableFuture.completedFuture(Optional.empty());
        }
        if (candidates.isEmpty()) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return source.loadLevelSummaries(candidates)
            .thenApply(RecentWorld::mostRecent)
            .exceptionally(exception -> {
                Arkea.LOGGER.warn("Could not read worlds for the title screen", exception);
                return Optional.empty();
            });
    }

    private static Optional<RecentWorld> mostRecent(List<LevelSummary> summaries) {
        return summaries.stream()
            .filter(summary -> !summary.isDisabled() && summary.primaryActionActive())
            .max(Comparator.comparingLong(LevelSummary::getLastPlayed))
            .map(summary -> new RecentWorld(summary, readIcon(summary.getIcon())));
    }

    private static byte @Nullable [] readIcon(Path icon) {
        if (!Files.isRegularFile(icon)) {
            return null;
        }
        try {
            return Files.readAllBytes(icon);
        } catch (IOException exception) {
            Arkea.LOGGER.warn("Could not read world icon {}", icon, exception);
            return null;
        }
    }
}
