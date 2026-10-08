package com.aryston.arkea.hud;

public final class BossBarStack {
    private static float bottom;

    private BossBarStack() {
    }

    public static void clear() {
        bottom = 0.0F;
    }

    public static void add(float guiBottom) {
        bottom = Math.max(bottom, guiBottom);
    }

    public static boolean any() {
        return bottom > 0.0F;
    }

    public static float bottom() {
        return bottom;
    }
}
