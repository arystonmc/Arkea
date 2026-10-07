package com.aryston.arkea.screen.options.control;

import com.aryston.arkea.ui.widget.SliderModel;
import java.util.Optional;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

final class OptionSliderModel<T> implements SliderModel {
    private static final int KEY_APPLY_DELAY = 600;
    private static final int LARGE_STEPS = 10;
    private static final double FINE_STEP = 0.01;

    private final OptionInstance<T> option;
    private final OptionInstance.SliderableValueSet<T> values;
    private @Nullable T pending;
    private long applyAt;

    OptionSliderModel(OptionInstance<T> option, OptionInstance.SliderableValueSet<T> values) {
        this.option = option;
        this.values = values;
    }

    private T shown() {
        if (this.pending != null && this.applyAt != 0L && Util.getMillis() >= this.applyAt) {
            this.apply();
        }
        return this.pending != null ? this.pending : this.option.get();
    }

    private void apply() {
        if (this.pending != null) {
            this.option.set(this.pending);
        }
        this.pending = null;
        this.applyAt = 0L;
    }

    @Override
    public float fraction() {
        return (float) this.values.toSliderValue(this.shown());
    }

    @Override
    public void drag(float fraction) {
        T value = this.values.fromSliderValue(fraction);
        if (this.values.applyValueImmediately()) {
            this.option.set(value);
        } else {
            this.pending = value;
            this.applyAt = 0L;
        }
    }

    @Override
    public void release() {
        this.apply();
    }

    @Override
    public void step(int direction, boolean large) {
        T value = this.shown();
        for (int index = 0; index < (large ? LARGE_STEPS : 1); index++) {
            Optional<T> next = direction > 0 ? this.values.next(value) : this.values.previous(value);
            if (next.isPresent()) {
                value = next.get();
            } else {
                double fraction = Math.clamp(this.values.toSliderValue(value) + direction * FINE_STEP, 0.0, 1.0);
                value = this.values.fromSliderValue(fraction);
            }
        }
        if (this.values.applyValueImmediately()) {
            this.option.set(value);
        } else {
            this.pending = value;
            this.applyAt = Util.getMillis() + KEY_APPLY_DELAY;
        }
    }

    @Override
    public Component label() {
        return OptionText.value(this.option, this.shown());
    }

    @Override
    public Component labelAt(float fraction) {
        return OptionText.value(this.option, this.values.fromSliderValue(fraction));
    }
}
