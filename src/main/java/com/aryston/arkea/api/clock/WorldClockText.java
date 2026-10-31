package com.aryston.arkea.api.clock;

import java.util.Locale;
import java.util.Objects;

public final class WorldClockText {
    private static final long TICKS_PER_DAY = 24000L;
    private static final long TICKS_PER_HOUR = 1000L;
    private static final long DAWN_HOUR = 6L;
    private static final long HOURS_PER_DAY = 24L;
    private static final long MINUTES_PER_HOUR = 60L;

    public static final Source VANILLA = new Source() {
        @Override
        public String time(long clockTicks) {
            long tick = Math.floorMod(clockTicks, TICKS_PER_DAY);
            long hour = (tick / TICKS_PER_HOUR + DAWN_HOUR) % HOURS_PER_DAY;
            long minute = tick % TICKS_PER_HOUR * MINUTES_PER_HOUR / TICKS_PER_HOUR;
            return String.format(Locale.ROOT, "%02d:%02d", hour, minute);
        }

        @Override
        public long day(long clockTicks) {
            return Math.floorDiv(clockTicks, TICKS_PER_DAY) + 1L;
        }
    };

    private static Source source = VANILLA;

    private WorldClockText() {
    }

    public static void register(Source value) {
        source = Objects.requireNonNull(value);
    }

    public static String time(long clockTicks) {
        return source.time(clockTicks);
    }

    public static long day(long clockTicks) {
        return source.day(clockTicks);
    }

    public interface Source {
        String time(long clockTicks);

        long day(long clockTicks);
    }
}
