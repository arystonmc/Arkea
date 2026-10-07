package com.aryston.arkea.screen.worlds;

import java.util.Comparator;
import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelSummary;

public enum WorldSort {
    LAST_PLAYED(Comparator.comparingLong(LevelSummary::getLastPlayed).reversed()),
    NAME(Comparator.comparing((LevelSummary summary) -> summary.getLevelName().toLowerCase(Locale.ROOT))),
    GAME_MODE(Comparator.comparing((LevelSummary summary) -> summary.isHardcore() ? Integer.MAX_VALUE : summary.getGameMode().getId())
        .thenComparing(Comparator.comparingLong(LevelSummary::getLastPlayed).reversed()));

    private final Comparator<LevelSummary> order;

    WorldSort(Comparator<LevelSummary> order) {
        this.order = order;
    }

    Comparator<LevelSummary> order() {
        return this.order;
    }

    Component label() {
        return Component.translatable("arkea.worlds.sort." + this.name().toLowerCase(Locale.ROOT));
    }

    WorldSort next() {
        WorldSort[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }
}
