package com.aryston.arkea.ui.widget;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleSupplier;
import net.minecraft.network.chat.Component;

public final class DoubleSliderModel implements SliderModel {
    private static final int LARGE_STEP = 10;
    private static final int MAX_TICKS = 5;

    private final double min;
    private final double max;
    private final double step;
    private final DoubleSupplier getter;
    private final DoubleConsumer setter;
    private final DoubleFunction<Component> label;

    public DoubleSliderModel(double min, double max, double step, DoubleSupplier getter, DoubleConsumer setter, DoubleFunction<Component> label) {
        this.min = min;
        this.max = max;
        this.step = step;
        this.getter = getter;
        this.setter = setter;
        this.label = label;
    }

    @Override
    public float fraction() {
        if (this.max <= this.min) {
            return 0.0F;
        }
        return (float) Math.clamp((this.getter.getAsDouble() - this.min) / (this.max - this.min), 0.0, 1.0);
    }

    private double valueAt(float fraction) {
        double raw = this.min + Math.clamp(fraction, 0.0F, 1.0F) * (this.max - this.min);
        return this.clamp(this.snap(raw));
    }

    private double snap(double value) {
        if (this.step <= 0.0) {
            return value;
        }
        return this.min + Math.round((value - this.min) / this.step) * this.step;
    }

    private double clamp(double value) {
        return Math.clamp(value, this.min, this.max);
    }

    @Override
    public void drag(float fraction) {
        this.setter.accept(this.valueAt(fraction));
    }

    @Override
    public void step(int direction, boolean large) {
        double amount = this.step * (large ? LARGE_STEP : 1) * direction;
        this.setter.accept(this.clamp(this.snap(this.getter.getAsDouble() + amount)));
    }

    @Override
    public Component label() {
        return this.label.apply(this.getter.getAsDouble());
    }

    @Override
    public Component labelAt(float fraction) {
        return this.label.apply(this.valueAt(fraction));
    }

    @Override
    public int ticks() {
        if (this.step <= 0.0) {
            return 0;
        }
        long steps = Math.round((this.max - this.min) / this.step) + 1;
        return steps <= MAX_TICKS ? (int) steps : 0;
    }
}
