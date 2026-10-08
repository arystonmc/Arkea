package com.aryston.arkea.screen.config;

import com.aryston.arkea.api.config.ChoiceStyle;
import com.aryston.arkea.api.config.ConfigOption;
import com.aryston.arkea.api.config.OptionKind;
import com.aryston.arkea.ui.screen.SettingsPanel;
import com.aryston.arkea.ui.widget.ArkButton;
import com.aryston.arkea.ui.widget.ArkColorButton;
import com.aryston.arkea.ui.widget.ArkCycle;
import com.aryston.arkea.ui.widget.ArkDropdown;
import com.aryston.arkea.ui.widget.ArkSegmented;
import com.aryston.arkea.ui.widget.ArkSlider;
import com.aryston.arkea.ui.widget.ArkStepper;
import com.aryston.arkea.ui.widget.ArkSwitch;
import com.aryston.arkea.ui.widget.ArkTextField;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.ButtonVariant;
import com.aryston.arkea.ui.widget.DoubleSliderModel;
import com.aryston.arkea.ui.widget.IntSliderModel;
import com.aryston.arkea.ui.widget.ListCycleModel;
import com.aryston.arkea.ui.widget.SliderRange;
import com.aryston.arkea.ui.widget.UiHost;
import java.util.List;
import net.minecraft.network.chat.Component;

final class ConfigControls {
    private static final int CYCLE_LIMIT = 6;
    private static final float WIDE_CONTROL = 200.0F;

    private ConfigControls() {
    }

    static Control create(UiHost host, ConfigSession session, ConfigOption<?> option) {
        return switch (option.kind()) {
            case OptionKind.Toggle _ -> toggle(host, session, cast(option));
            case OptionKind.Choice<?> choice -> choice(host, session, option, choice);
            case OptionKind.IntRange range -> intRange(host, session, cast(option), range);
            case OptionKind.DoubleRange range -> doubleRange(host, session, cast(option), range);
            case OptionKind.Text text -> text(host, session, cast(option), text);
            case OptionKind.Color _ -> color(host, session, cast(option));
            case OptionKind.Multi<?> _ -> throw new IllegalArgumentException("Multi options use a chip row: " + option.id());
            case OptionKind.Action action -> action(host, action);
        };
    }

    private static Control toggle(UiHost host, ConfigSession session, ConfigOption<Boolean> option) {
        ArkSwitch widget = new ArkSwitch(host, option.name(), () -> session.value(option), value -> session.stage(option, value));
        return new Control(widget, ArkSwitch.WIDTH, ArkSwitch.HEIGHT);
    }

    @SuppressWarnings("unchecked")
    private static <T> Control choice(UiHost host, ConfigSession session, ConfigOption<?> raw, OptionKind.Choice<?> rawChoice) {
        ConfigOption<T> option = (ConfigOption<T>) raw;
        OptionKind.Choice<T> choice = (OptionKind.Choice<T>) rawChoice;
        List<T> values = choice.values();
        ListCycleModel<T> model = new ListCycleModel<>(() -> values, () -> session.value(option), value -> session.stage(option, value), choice.label());
        ChoiceStyle style = choice.style() == ChoiceStyle.AUTO ? (values.size() > CYCLE_LIMIT ? ChoiceStyle.DROPDOWN : ChoiceStyle.CYCLE) : choice.style();
        return switch (style) {
            case DROPDOWN -> new Control(new ArkDropdown(host, option.name(), model), ArkDropdown.WIDTH, ArkDropdown.HEIGHT);
            case SEGMENTED -> {
                List<Component> labels = values.stream().map(choice.label()).toList();
                ArkSegmented segmented = new ArkSegmented(host, option.name(), labels, model::index, model::select);
                yield new Control(segmented, segmented.preferredWidth(), ArkSegmented.HEIGHT);
            }
            default -> new Control(new ArkCycle(host, option.name(), model), ArkCycle.WIDTH, ArkCycle.HEIGHT);
        };
    }

    private static Control intRange(UiHost host, ConfigSession session, ConfigOption<Integer> option, OptionKind.IntRange range) {
        IntSliderModel model = new IntSliderModel(new SliderRange(range.min(), range.max(), range.step()), () -> session.value(option),
            value -> session.stage(option, value), range.label());
        if (range.stepper()) {
            return new Control(new ArkStepper(host, option.name(), model), ArkStepper.WIDTH, ArkStepper.HEIGHT);
        }
        return new Control(new ArkSlider(host, option.name(), model), ArkSlider.WIDTH, ArkSlider.HEIGHT);
    }

    private static Control doubleRange(UiHost host, ConfigSession session, ConfigOption<Double> option, OptionKind.DoubleRange range) {
        DoubleSliderModel model = new DoubleSliderModel(range.min(), range.max(), range.step(), () -> session.value(option),
            value -> session.stage(option, value), range.label());
        return new Control(new ArkSlider(host, option.name(), model), ArkSlider.WIDTH, ArkSlider.HEIGHT);
    }

    private static Control text(UiHost host, ConfigSession session, ConfigOption<String> option, OptionKind.Text text) {
        ArkTextField field = new ArkTextField(host, option.name(), session.textState(option, text.maxLength()), value -> {
            if (text.validator().test(value)) {
                session.stage(option, value);
            }
        });
        return new Control(field, WIDE_CONTROL, ArkTextField.HEIGHT);
    }

    private static Control color(UiHost host, ConfigSession session, ConfigOption<Integer> option) {
        ArkColorButton button = new ArkColorButton(host, option.name(), () -> session.value(option), value -> session.stage(option, value));
        return new Control(button, ArkColorButton.WIDTH, ArkColorButton.HEIGHT);
    }

    private static Control action(UiHost host, OptionKind.Action action) {
        ArkButton button = new ArkButton(host, action.label(), ButtonVariant.SUBTLE, action.action());
        return new Control(button, button.preferredWidth(), SettingsPanel.CONTROL_HEIGHT);
    }

    @SuppressWarnings("unchecked")
    private static <T> ConfigOption<T> cast(ConfigOption<?> option) {
        return (ConfigOption<T>) option;
    }

    record Control(ArkWidget widget, float width, float height) {
    }
}
