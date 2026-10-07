package com.aryston.arkea.screen.title;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class LastPlayedTest {
    private static final ZoneId ZONE = ZoneId.of("Europe/Istanbul");
    private static final long NOW = ZonedDateTime.of(2026, 10, 7, 14, 32, 0, 0, ZONE).toInstant().toEpochMilli();

    @Test
    void sameDayIsToday() {
        long played = ZonedDateTime.of(2026, 10, 7, 9, 5, 0, 0, ZONE).toInstant().toEpochMilli();
        LastPlayed lastPlayed = LastPlayed.describe(played, NOW, ZONE, Locale.ENGLISH);
        assertEquals(LastPlayed.Day.TODAY, lastPlayed.day());
        assertEquals("09:05", lastPlayed.time());
    }

    @Test
    void previousDayIsYesterdayEvenAcrossMidnight() {
        long played = ZonedDateTime.of(2026, 10, 6, 23, 59, 0, 0, ZONE).toInstant().toEpochMilli();
        assertEquals(LastPlayed.Day.YESTERDAY, LastPlayed.describe(played, NOW, ZONE, Locale.ENGLISH).day());
    }

    @Test
    void olderDatesUseTheLocalizedDate() {
        long played = ZonedDateTime.of(2026, 9, 1, 12, 0, 0, 0, ZONE).toInstant().toEpochMilli();
        LastPlayed lastPlayed = LastPlayed.describe(played, NOW, ZONE, Locale.ENGLISH);
        assertEquals(LastPlayed.Day.EARLIER, lastPlayed.day());
        assertEquals("Sep 1, 2026", lastPlayed.date());
    }
}
