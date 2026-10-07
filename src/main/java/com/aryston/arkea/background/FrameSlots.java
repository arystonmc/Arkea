package com.aryston.arkea.background;

import java.util.Arrays;

final class FrameSlots {
    private final double[] times;
    private final int[] bounds;

    private FrameSlots(double[] times, int[] bounds) {
        this.times = times;
        this.bounds = bounds;
    }

    static FrameSlots of(double[] presentationTimes, double end, int fps, int maxSlots) {
        double[] times = presentationTimes.clone();
        Arrays.sort(times);
        int[] bounds = new int[times.length + 1];
        for (int index = 0; index < times.length; index++) {
            bounds[index] = slot(times[index], fps, maxSlots);
        }
        bounds[times.length] = Math.max(bounds[Math.max(0, times.length - 1)], slot(end, fps, maxSlots));
        return new FrameSlots(times, bounds);
    }

    private static int slot(double time, int fps, int maxSlots) {
        return (int) Math.clamp(Math.round(time * fps), 0L, maxSlots);
    }

    int first(double time) {
        int index = Arrays.binarySearch(this.times, time);
        if (index < 0) {
            return 0;
        }
        while (index > 0 && this.times[index - 1] == time) {
            index--;
        }
        return this.bounds[index];
    }

    int end(double time) {
        int index = Arrays.binarySearch(this.times, time);
        if (index < 0) {
            return 0;
        }
        while (index + 1 < this.times.length && this.times[index + 1] == time) {
            index++;
        }
        return this.bounds[index + 1];
    }

    int last() {
        return this.bounds[this.bounds.length - 1];
    }
}
