package com.aryston.arkea.ui.anim;

public final class Transition {
    private final int durationMs;
    private final Easing easing;
    private float from;
    private float to;
    private long startedAt;

    public Transition(float initial, int durationMs, Easing easing) {
        this.durationMs = durationMs;
        this.easing = easing;
        this.from = initial;
        this.to = initial;
    }

    public void setTarget(float target, long now) {
        if (target == this.to) {
            return;
        }
        this.from = this.value(now);
        this.to = target;
        this.startedAt = now;
    }

    public void snap(float value) {
        this.from = value;
        this.to = value;
    }

    public float value(long now) {
        if (this.from == this.to) {
            return this.to;
        }
        float progress = Math.clamp((now - this.startedAt) / (float) this.durationMs, 0.0F, 1.0F);
        if (progress >= 1.0F) {
            this.from = this.to;
            return this.to;
        }
        return this.from + (this.to - this.from) * this.easing.apply(progress);
    }

    public float target() {
        return this.to;
    }
}
