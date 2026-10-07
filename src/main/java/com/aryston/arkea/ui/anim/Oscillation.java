package com.aryston.arkea.ui.anim;

public final class Oscillation {
    private static final double FULL_TURN = Math.PI * 2.0;

    private Oscillation() {
    }

    public static float wave(long now, int periodMs) {
        double phase = Math.floorMod(now, (long) periodMs) / (double) periodMs;
        return (float) (0.5 - 0.5 * Math.cos(phase * FULL_TURN));
    }
}
