package com.aryston.arkea.ui.widget;

public record SliderRange(int min, int max, int step) {
    public float fraction(int value) {
        if (this.max == this.min) {
            return 0.0F;
        }
        return Math.clamp((value - this.min) / (float) (this.max - this.min), 0.0F, 1.0F);
    }

    public int valueAt(float fraction) {
        float raw = this.min + Math.clamp(fraction, 0.0F, 1.0F) * (this.max - this.min);
        return this.clamp(this.min + Math.round((raw - this.min) / this.step) * this.step);
    }

    public int clamp(int value) {
        return Math.clamp(value, this.min, this.max);
    }
}
