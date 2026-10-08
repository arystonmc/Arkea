package com.aryston.arkea.ui.widget;

import java.util.function.BiFunction;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.network.chat.Component;

public final class IntRangeModel implements RangeModel {
    private static final int LARGE_STEP = 10;

    private final SliderRange range;
    private final IntSupplier lowGetter;
    private final IntConsumer lowSetter;
    private final IntSupplier highGetter;
    private final IntConsumer highSetter;
    private final BiFunction<Integer, Integer, Component> label;

    public IntRangeModel(SliderRange range, IntSupplier lowGetter, IntConsumer lowSetter, IntSupplier highGetter, IntConsumer highSetter,
        BiFunction<Integer, Integer, Component> label) {
        this.range = range;
        this.lowGetter = lowGetter;
        this.lowSetter = lowSetter;
        this.highGetter = highGetter;
        this.highSetter = highSetter;
        this.label = label;
    }

    @Override
    public float low() {
        return this.range.fraction(this.lowGetter.getAsInt());
    }

    @Override
    public float high() {
        return this.range.fraction(this.highGetter.getAsInt());
    }

    @Override
    public void setLow(float fraction) {
        this.lowSetter.accept(Math.min(this.range.valueAt(fraction), this.highGetter.getAsInt()));
    }

    @Override
    public void setHigh(float fraction) {
        this.highSetter.accept(Math.max(this.range.valueAt(fraction), this.lowGetter.getAsInt()));
    }

    @Override
    public void step(boolean highHandle, int direction, boolean large) {
        int step = this.range.step() * (large ? LARGE_STEP : 1) * direction;
        if (highHandle) {
            this.highSetter.accept(Math.max(this.range.clamp(this.highGetter.getAsInt() + step), this.lowGetter.getAsInt()));
        } else {
            this.lowSetter.accept(Math.min(this.range.clamp(this.lowGetter.getAsInt() + step), this.highGetter.getAsInt()));
        }
    }

    @Override
    public Component label() {
        return this.label.apply(this.lowGetter.getAsInt(), this.highGetter.getAsInt());
    }
}
