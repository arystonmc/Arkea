package com.aryston.arkea.screen.worlds;

import com.aryston.arkea.screen.title.LastPlayed;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelSummary;

final class WorldText {
    private static final String SEPARATOR = " · ";
    private static final long KILOBYTE = 1024L;
    private static final long MEGABYTE = KILOBYTE * KILOBYTE;
    private static final long GIGABYTE = MEGABYTE * KILOBYTE;
    private static final long TICKS_PER_MINUTE = 20L * 60L;
    private static final long MINUTES_PER_HOUR = 60L;
    private static final int DAYS_PER_MONTH = 30;
    private static final int MONTHS_PER_YEAR = 12;

    private WorldText() {
    }

    static Component gameMode(LevelSummary summary) {
        return summary.isHardcore() ? Component.translatable("selectWorld.gameMode.hardcore") : summary.getGameMode().getShortDisplayName();
    }

    static Component difficulty(LevelSummary summary) {
        return summary.getSettings().difficultySettings().difficulty().getDisplayName();
    }

    static Component details(LevelSummary summary) {
        if (summary.isDisabled() || summary.getLastPlayed() <= 0L && !summary.primaryActionActive()) {
            return summary.getInfo();
        }
        return Component.empty().append(gameMode(summary)).append(SEPARATOR).append(difficulty(summary)).append(SEPARATOR)
            .append(summary.getWorldVersionName());
    }

    static Component lastPlayed(LevelSummary summary) {
        return Component.translatable("arkea.worlds.last_played", ago(summary.getLastPlayed()));
    }

    static Component ago(long lastPlayed) {
        if (lastPlayed <= 0L) {
            return Component.translatable("arkea.worlds.ago.never");
        }
        ZoneId zone = ZoneId.systemDefault();
        long now = Util.getEpochMillis();
        LastPlayed played = LastPlayed.describe(lastPlayed, now, zone, locale());
        if (played.day() == LastPlayed.Day.TODAY) {
            return Component.translatable("arkea.worlds.ago.today", played.time());
        }
        if (played.day() == LastPlayed.Day.YESTERDAY) {
            return Component.translatable("arkea.worlds.ago.yesterday");
        }
        long days = ChronoUnit.DAYS.between(day(lastPlayed, zone), day(now, zone));
        if (days < DAYS_PER_MONTH) {
            return Component.translatable("arkea.worlds.ago.days", days);
        }
        long months = days / DAYS_PER_MONTH;
        if (months < MONTHS_PER_YEAR) {
            return Component.translatable(months == 1L ? "arkea.worlds.ago.month" : "arkea.worlds.ago.months", months);
        }
        long years = months / MONTHS_PER_YEAR;
        return Component.translatable(years == 1L ? "arkea.worlds.ago.year" : "arkea.worlds.ago.years", years);
    }

    static Component size(long bytes) {
        if (bytes < 0L) {
            return Component.translatable("arkea.worlds.measuring");
        }
        if (bytes < MEGABYTE) {
            return Component.translatable("arkea.worlds.size.kb", Math.max(1L, bytes / KILOBYTE));
        }
        if (bytes < GIGABYTE) {
            return Component.translatable("arkea.worlds.size.mb", bytes / MEGABYTE);
        }
        return Component.translatable("arkea.worlds.size.gb", String.format(locale(), "%.1f", bytes / (double) GIGABYTE));
    }

    static Component playTime(long ticks) {
        if (ticks < 0L) {
            return Component.translatable("arkea.worlds.unknown");
        }
        long minutes = ticks / TICKS_PER_MINUTE;
        if (minutes < MINUTES_PER_HOUR) {
            return Component.translatable("arkea.worlds.time.minutes", minutes);
        }
        return Component.translatable("arkea.worlds.time.hours", minutes / MINUTES_PER_HOUR, minutes % MINUTES_PER_HOUR);
    }

    private static LocalDate day(long millis, ZoneId zone) {
        return Instant.ofEpochMilli(millis).atZone(zone).toLocalDate();
    }

    private static Locale locale() {
        return Minecraft.getInstance().getLanguageManager().getJavaLocale();
    }
}
