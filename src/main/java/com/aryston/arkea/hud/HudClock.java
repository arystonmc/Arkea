package com.aryston.arkea.hud;

import java.util.function.LongSupplier;
import net.minecraft.util.Util;

public final class HudClock {
    private static LongSupplier source = Util::getMillis;

    private HudClock() {
    }

    public static long now() {
        return source.getAsLong();
    }

    public static void use(LongSupplier clock) {
        source = clock;
    }
}
