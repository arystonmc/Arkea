package com.aryston.arkea.ui.anim;

public final class Presence {
    private final int enterMs;
    private final int exitMs;
    private final Easing enterEasing;
    private final Easing exitEasing;
    private boolean shown;
    private long changedAt;
    private int delayMs;
    private float startProgress;

    public Presence(int enterMs, int exitMs, Easing enterEasing, Easing exitEasing) {
        this.enterMs = enterMs;
        this.exitMs = exitMs;
        this.enterEasing = enterEasing;
        this.exitEasing = exitEasing;
    }

    public static Presence of(int enterMs, int exitMs) {
        return new Presence(enterMs, exitMs, Easing.STANDARD, Easing.EXIT);
    }

    public void enter(long now, int delay) {
        this.shown = true;
        this.changedAt = now;
        this.delayMs = delay;
        this.startProgress = 0.0F;
    }

    public void show(long now) {
        if (this.shown) {
            return;
        }
        this.startProgress = this.progress(now);
        this.shown = true;
        this.changedAt = now;
        this.delayMs = 0;
    }

    public void hide(long now) {
        if (!this.shown) {
            return;
        }
        this.startProgress = this.progress(now);
        this.shown = false;
        this.changedAt = now;
        this.delayMs = 0;
    }

    public float progress(long now) {
        long elapsed = now - this.changedAt - this.delayMs;
        if (this.shown) {
            float time = this.timeFraction(elapsed, this.enterMs);
            return this.startProgress + (1.0F - this.startProgress) * this.enterEasing.apply(time);
        }
        float time = this.timeFraction(elapsed, this.exitMs);
        return this.startProgress * (1.0F - this.exitEasing.apply(time));
    }

    public boolean isShown() {
        return this.shown;
    }

    public boolean isGone(long now) {
        return !this.shown && now - this.changedAt >= this.exitMs;
    }

    private float timeFraction(long elapsed, int duration) {
        if (duration <= 0) {
            return 1.0F;
        }
        return Math.clamp(elapsed / (float) duration, 0.0F, 1.0F);
    }
}
