package com.aryston.arkea.ui.widget;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import net.minecraft.network.chat.Component;

public final class IntSliderModel implements SliderModel {
    private static final int LARGE_STEP = 10;
    private static final int MAX_TICKS = 5;

    private final SliderRange range;
    private final IntSupplier getter;
    private final IntConsumer setter;
    private final IntFunction<Component> label;

    public IntSliderModel(SliderRange range, IntSupplier getter, IntConsumer setter, IntFunction<Component> label) {
        this.range = range;
        this.getter = getter;
        this.setter = setter;
        this.label = label;
    }

    @Override
    public float fraction() {
        return this.range.fraction(this.getter.getAsInt());
    }

    @Override
    public void drag(float fraction) {
        this.setter.accept(this.range.valueAt(fraction));
    }

    @Override
    public void step(int direction, boolean large) {
        int step = this.range.step() * (large ? LARGE_STEP : 1);
        this.setter.accept(this.range.clamp(this.getter.getAsInt() + direction * step));
    }

    @Override
    public Component label() {
        return this.label.apply(this.getter.getAsInt());
    }

    @Override
    public Component labelAt(float fraction) {
        return this.label.apply(this.range.valueAt(fraction));
    }

    @Override
    public int ticks() {
        int steps = (this.range.max() - this.range.min()) / this.range.step() + 1;
        return steps <= MAX_TICKS ? steps : 0;
    }
}
