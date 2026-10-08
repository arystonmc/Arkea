package com.aryston.arkea.api.config;

import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.widget.Tag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public final class ConfigOption<T> {
    private static final int UNLIMITED_TEXT = 256;
    private static final double PERCENT = 100.0;
    private static final double WHOLE_STEP = 1.0;
    private static final double TENTH_STEP = 0.1;

    private final String id;
    private final Binding<T> binding;
    private OptionKind<T> kind;
    private Component name;
    private Component description = Component.empty();
    private Icon icon;
    private @Nullable Component tooltip;
    private final List<Tag> tags = new ArrayList<>();
    private int cost;
    private boolean restart;
    private final List<ConfigOption<Boolean>> requirements = new ArrayList<>();
    private @Nullable Identifier previewBefore;
    private @Nullable Identifier previewAfter;
    private float previewAspect;

    ConfigOption(String id, Binding<T> binding, OptionKind<T> kind, Icon icon) {
        this.id = id;
        this.binding = binding;
        this.kind = kind;
        this.icon = icon;
        this.name = Component.translatable(id);
        this.restart = binding.requiresRestart();
    }

    public static ConfigOption<Boolean> toggle(String id, Binding<Boolean> binding) {
        return new ConfigOption<>(id, binding, new OptionKind.Toggle(), Icons.POWER);
    }

    public static <T> ConfigOption<T> choice(String id, Binding<T> binding, List<T> values, Function<T, Component> label) {
        return new ConfigOption<>(id, binding, new OptionKind.Choice<>(List.copyOf(values), label, ChoiceStyle.AUTO), Icons.LAYERS);
    }

    public static <E extends Enum<E>> ConfigOption<E> enumChoice(String id, Binding<E> binding, Class<E> type) {
        return choice(id, binding, Arrays.asList(type.getEnumConstants()), value -> enumLabel(id, value));
    }

    public static ConfigOption<Integer> slider(String id, Binding<Integer> binding, int min, int max, int step) {
        return new ConfigOption<>(id, binding, new OptionKind.IntRange(min, max, step, value -> Component.literal(Integer.toString(value)), false),
            Icons.SLIDERS);
    }

    public static ConfigOption<Double> slider(String id, Binding<Double> binding, double min, double max, double step) {
        return new ConfigOption<>(id, binding, new OptionKind.DoubleRange(min, max, step, value -> Component.literal(decimal(value, step))), Icons.SLIDERS);
    }

    public static ConfigOption<Integer> stepper(String id, Binding<Integer> binding, int min, int max, int step) {
        return new ConfigOption<>(id, binding, new OptionKind.IntRange(min, max, step, value -> Component.literal(Integer.toString(value)), true),
            Icons.SLIDERS);
    }

    public static ConfigOption<String> text(String id, Binding<String> binding, int maxLength) {
        return new ConfigOption<>(id, binding, new OptionKind.Text(maxLength, value -> true), Icons.EDIT);
    }

    public static ConfigOption<String> text(String id, Binding<String> binding) {
        return text(id, binding, UNLIMITED_TEXT);
    }

    public static ConfigOption<Integer> color(String id, Binding<Integer> binding) {
        return new ConfigOption<>(id, binding, new OptionKind.Color(), Icons.BLEND);
    }

    public static <E> ConfigOption<List<E>> multi(String id, Binding<List<E>> binding, List<E> values, Function<E, Component> label) {
        return new ConfigOption<>(id, binding, new OptionKind.Multi<>(List.copyOf(values), label), Icons.FILTER);
    }

    public static ConfigOption<Boolean> action(String id, Component buttonLabel, Runnable action) {
        Binding<Boolean> none = Binding.of(() -> false, value -> {
        }, false);
        return new ConfigOption<>(id, none, new OptionKind.Action(buttonLabel, action), Icons.PLAY);
    }

    public ConfigOption<T> name(Component value) {
        this.name = value;
        return this;
    }

    public ConfigOption<T> description(Component value) {
        this.description = value;
        return this;
    }

    public ConfigOption<T> icon(Icon value) {
        this.icon = value;
        return this;
    }

    public ConfigOption<T> tooltip(Component value) {
        this.tooltip = value;
        return this;
    }

    public ConfigOption<T> tag(Tag value) {
        this.tags.add(value);
        return this;
    }

    public ConfigOption<T> cost(int level) {
        this.cost = level;
        return this;
    }

    public ConfigOption<T> requiresRestart() {
        this.restart = true;
        return this;
    }

    @SafeVarargs
    public final ConfigOption<T> requires(ConfigOption<Boolean>... options) {
        this.requirements.addAll(Arrays.asList(options));
        return this;
    }

    public ConfigOption<T> preview(Identifier before, Identifier after, float aspect) {
        this.previewBefore = before;
        this.previewAfter = after;
        this.previewAspect = aspect;
        return this;
    }

    public ConfigOption<T> style(ChoiceStyle style) {
        if (this.kind instanceof OptionKind.Choice<T> choice) {
            this.kind = new OptionKind.Choice<>(choice.values(), choice.label(), style);
        }
        return this;
    }

    public ConfigOption<T> labels(Function<T, Component> label) {
        if (this.kind instanceof OptionKind.Choice<T> choice) {
            this.kind = new OptionKind.Choice<>(choice.values(), label, choice.style());
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public ConfigOption<T> format(IntFunction<Component> label) {
        if (this.kind instanceof OptionKind.IntRange range) {
            this.kind = (OptionKind<T>) new OptionKind.IntRange(range.min(), range.max(), range.step(), label, range.stepper());
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public ConfigOption<T> format(DoubleFunction<Component> label) {
        if (this.kind instanceof OptionKind.DoubleRange range) {
            this.kind = (OptionKind<T>) new OptionKind.DoubleRange(range.min(), range.max(), range.step(), label);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public ConfigOption<T> percent() {
        if (this.kind instanceof OptionKind.DoubleRange range) {
            this.kind = (OptionKind<T>) new OptionKind.DoubleRange(range.min(), range.max(), range.step(),
                value -> Component.literal(Math.round(value * PERCENT) + "%"));
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public ConfigOption<T> validate(Predicate<String> validator) {
        if (this.kind instanceof OptionKind.Text text) {
            this.kind = (OptionKind<T>) new OptionKind.Text(text.maxLength(), validator);
        }
        return this;
    }

    public String id() {
        return this.id;
    }

    public Binding<T> binding() {
        return this.binding;
    }

    public OptionKind<T> kind() {
        return this.kind;
    }

    public Component name() {
        return this.name;
    }

    public Component description() {
        return this.description;
    }

    public Icon icon() {
        return this.icon;
    }

    public @Nullable Component tooltip() {
        return this.tooltip;
    }

    public List<Tag> tags() {
        return List.copyOf(this.tags);
    }

    public int cost() {
        return this.cost;
    }

    public boolean restart() {
        return this.restart;
    }

    public List<ConfigOption<Boolean>> requirements() {
        return List.copyOf(this.requirements);
    }

    public @Nullable Identifier previewBefore() {
        return this.previewBefore;
    }

    public @Nullable Identifier previewAfter() {
        return this.previewAfter;
    }

    public float previewAspect() {
        return this.previewAspect;
    }

    public boolean isAction() {
        return this.kind instanceof OptionKind.Action;
    }

    public boolean isDefault(T value) {
        return Objects.equals(value, this.binding.defaultValue());
    }

    static <E extends Enum<E>> Component enumLabel(String id, E value) {
        String raw = value.name().toLowerCase(Locale.ROOT);
        String pretty = Arrays.stream(raw.split("_"))
            .map(word -> word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1))
            .reduce((left, right) -> left + " " + right)
            .orElse(raw);
        return Component.translatableWithFallback(id + "." + raw, pretty);
    }

    static String decimal(double value, double step) {
        int digits = step >= WHOLE_STEP ? 0 : step >= TENTH_STEP ? 1 : 2;
        return String.format(Locale.ROOT, "%." + digits + "f", value);
    }
}
