package com.aryston.arkea.ui.anim;

public final class ProgressMotion {
    private static final float CREEP_LIMIT = 0.9F;
    private static final double CREEP_MILLIS = 1600.0;
    private static final double FOLLOW_MILLIS = 140.0;

    private ProgressMotion() {
    }

    public static float target(float start, float end, float fraction, long millisInSpan) {
        if (!Float.isNaN(fraction)) {
            return start + (end - start) * Math.clamp(fraction, 0.0F, 1.0F);
        }
        float creep = (float) (1.0 - Math.exp(-Math.max(0L, millisInSpan) / CREEP_MILLIS));
        return start + (end - start) * CREEP_LIMIT * creep;
    }

    public static float follow(float shown, float target, long deltaMillis) {
        if (target <= shown) {
            return shown;
        }
        float step = (float) (1.0 - Math.exp(-Math.max(0L, deltaMillis) / FOLLOW_MILLIS));
        return Math.min(target, shown + (target - shown) * step);
    }
}
