package com.aryston.arkea.ui.anim;

public final class Timeline {
    private Timeline() {
    }

    public static float enter(long elapsed, int delay, int duration) {
        return Easing.STANDARD.apply(fraction(elapsed - delay, duration));
    }

    public static float exit(long elapsed, int duration) {
        return 1.0F - Easing.EXIT.apply(fraction(elapsed, duration));
    }

    private static float fraction(long elapsed, int duration) {
        if (duration <= 0) {
            return elapsed >= 0 ? 1.0F : 0.0F;
        }
        return Math.clamp(elapsed / (float) duration, 0.0F, 1.0F);
    }
}
