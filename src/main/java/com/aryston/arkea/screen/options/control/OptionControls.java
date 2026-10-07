package com.aryston.arkea.screen.options.control;

import com.aryston.arkea.mixin.OptionInstanceAccessor;
import com.aryston.arkea.ui.screen.SettingsPanel;
import com.aryston.arkea.ui.widget.ArkCycle;
import com.aryston.arkea.ui.widget.ArkDropdown;
import com.aryston.arkea.ui.widget.ArkSlider;
import com.aryston.arkea.ui.widget.ArkSwitch;
import com.aryston.arkea.ui.widget.UiHost;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.OptionInstance;

public final class OptionControls {
    private static final List<Boolean> BOOLEAN_ORDER = List.of(Boolean.FALSE, Boolean.TRUE);

    private OptionControls() {
    }

    public static <T> OptionControl create(UiHost host, OptionInstance<T> option) {
        OptionInstance.ValueSet<T> values = option.values();
        Runnable reset = () -> option.set(initialValue(option));
        if (option.get() instanceof Boolean && values instanceof OptionInstance.CycleableValueSet<T> cycleable) {
            @SuppressWarnings("unchecked")
            OptionInstance<Boolean> flag = (OptionInstance<Boolean>) option;
            @SuppressWarnings("unchecked")
            OptionInstance.CycleableValueSet<Boolean> flags = (OptionInstance.CycleableValueSet<Boolean>) cycleable;
            if (!OptionText.isOnOff(flag)) {
                return cycle(host, flag, () -> BOOLEAN_ORDER, value -> flags.valueSetter().set(flag, value), () -> flag.set(initialValue(flag)));
            }
            ArkSwitch widget = new ArkSwitch(host, OptionText.name(option.caption), flag::get, value -> flags.valueSetter().set(flag, value));
            return new OptionControl(widget, ArkSwitch.WIDTH, ArkSwitch.HEIGHT, flag::get, reset);
        }
        if (values instanceof OptionInstance.SliderableOrCyclableValueSet<T> both && both.createCycleButton()) {
            return cycle(host, option, () -> both.valueListSupplier().getDefaultList(), value -> both.valueSetter().set(option, value), reset);
        }
        if (values instanceof OptionInstance.SliderableEnum<T> enumeration) {
            return cycle(host, option, enumeration::values, option::set, reset);
        }
        if (values instanceof OptionInstance.SliderableValueSet<T> sliderable) {
            OptionSliderModel<T> model = new OptionSliderModel<>(option, sliderable);
            ArkSlider widget = new ArkSlider(host, OptionText.name(option.caption), model);
            return new OptionControl(widget, ArkSlider.WIDTH, SettingsPanel.CONTROL_HEIGHT, () -> !OptionText.isOff(model.label()), reset);
        }
        if (values instanceof OptionInstance.CycleableValueSet<T> cycleable) {
            return cycle(host, option, () -> cycleable.valueListSupplier().getDefaultList(), value -> cycleable.valueSetter().set(option, value), reset);
        }
        throw new IllegalArgumentException("Unsupported option " + option);
    }

    public static <T> OptionControl cycle(UiHost host, OptionInstance<T> option, Supplier<List<T>> values, Consumer<T> setter, Runnable reset) {
        OptionCycleModel<T> model = new OptionCycleModel<>(option, values, setter);
        ArkCycle widget = new ArkCycle(host, OptionText.name(option.caption), model);
        return new OptionControl(widget, ArkCycle.WIDTH, ArkCycle.HEIGHT, () -> !OptionText.isOff(model.label(model.index())), reset);
    }

    public static <T> OptionControl dropdown(UiHost host, OptionInstance<T> option, Supplier<List<T>> values, Consumer<T> setter, Runnable reset) {
        OptionCycleModel<T> model = new OptionCycleModel<>(option, values, setter);
        ArkDropdown widget = new ArkDropdown(host, OptionText.name(option.caption), model);
        return new OptionControl(widget, ArkDropdown.WIDTH, ArkDropdown.HEIGHT, () -> true, reset);
    }

    public static <T> OptionControl dropdown(UiHost host, OptionInstance<T> option) {
        if (!(option.values() instanceof OptionInstance.CycleableValueSet<T> cycleable)) {
            return create(host, option);
        }
        return dropdown(host, option, () -> cycleable.valueListSupplier().getDefaultList(), value -> cycleable.valueSetter().set(option, value),
            () -> option.set(initialValue(option)));
    }

    @SuppressWarnings("unchecked")
    public static <T> T initialValue(OptionInstance<T> option) {
        return (T) ((OptionInstanceAccessor) (Object) option).arkea$initialValue();
    }
}
