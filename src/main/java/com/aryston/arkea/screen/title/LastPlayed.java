package com.aryston.arkea.screen.title;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public record LastPlayed(LastPlayed.Day day, String time, String date) {
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    public static LastPlayed describe(long lastPlayedMillis, long nowMillis, ZoneId zone, Locale locale) {
        ZonedDateTime played = Instant.ofEpochMilli(lastPlayedMillis).atZone(zone);
        LocalDate today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate();
        LocalDate playedDay = played.toLocalDate();
        Day day = playedDay.equals(today) ? Day.TODAY : playedDay.equals(today.minusDays(1)) ? Day.YESTERDAY : Day.EARLIER;
        String date = played.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale));
        return new LastPlayed(day, played.format(TIME), date);
    }

    public enum Day {
        TODAY,
        YESTERDAY,
        EARLIER
    }
}
