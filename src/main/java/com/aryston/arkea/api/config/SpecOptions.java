package com.aryston.arkea.api.config;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.jspecify.annotations.Nullable;

public final class SpecOptions {
    private static final String TOOLTIP_SUFFIX = ".tooltip";
    private static final String LIST_SEPARATOR = ",";
    private static final String LIST_JOINER = ", ";
    private static final long SLIDER_SPAN = 1000L;
    private static final int TEXT_LENGTH = 512;
    private static final int DOUBLE_SLIDER_STEPS = 100;

    private SpecOptions() {
    }

    public static @Nullable ConfigOption<?> of(String modId, ModConfigSpec.ConfigValue<?> value) {
        ModConfigSpec.ValueSpec spec = value.getSpec();
        String key = translationKey(modId, value);
        ConfigOption<?> option = create(key, value, spec);
        if (option == null) {
            return null;
        }
        String fallback = pretty(value.getPath().getLast());
        option.name(Component.translatableWithFallback(key, fallback));
        String comment = spec.getComment();
        Component description = Component.translatableWithFallback(key + TOOLTIP_SUFFIX, comment == null ? "" : comment);
        option.description(description);
        if (!description.getString().isEmpty()) {
            option.tooltip(description);
        }
        return option;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static @Nullable ConfigOption<?> create(String key, ModConfigSpec.ConfigValue<?> value, ModConfigSpec.ValueSpec spec) {
        Object fallback = spec.getDefault();
        if (spec instanceof ModConfigSpec.ListValueSpec) {
            return list(key, (ModConfigSpec.ConfigValue<List<?>>) value, spec);
        }
        if (value instanceof ModConfigSpec.BooleanValue || fallback instanceof Boolean) {
            return ConfigOption.toggle(key, Binding.of((ModConfigSpec.ConfigValue<Boolean>) value));
        }
        if (value instanceof ModConfigSpec.EnumValue || fallback instanceof Enum<?>) {
            return enumOption(key, (ModConfigSpec.ConfigValue) value, ((Enum<?>) fallback).getDeclaringClass());
        }
        if (value instanceof ModConfigSpec.IntValue || fallback instanceof Integer) {
            return integer(key, (ModConfigSpec.ConfigValue<Integer>) value, spec);
        }
        if (value instanceof ModConfigSpec.DoubleValue || fallback instanceof Double) {
            return decimal(key, (ModConfigSpec.ConfigValue<Double>) value, spec);
        }
        if (value instanceof ModConfigSpec.LongValue || fallback instanceof Long) {
            return longText(key, (ModConfigSpec.ConfigValue<Long>) value, spec);
        }
        if (fallback instanceof String) {
            return ConfigOption.text(key, Binding.of((ModConfigSpec.ConfigValue<String>) value), TEXT_LENGTH).validate(spec::test);
        }
        return null;
    }

    private static <E extends Enum<E>> ConfigOption<E> enumOption(String key, ModConfigSpec.ConfigValue<E> value, Class<E> type) {
        Function<E, Component> label = constant -> constant instanceof TranslatableEnum translatable ? translatable.getTranslatedName()
            : ConfigOption.enumLabel(key, constant);
        return ConfigOption.choice(key, Binding.of(value), Arrays.asList(type.getEnumConstants()), label);
    }

    private static ConfigOption<Integer> integer(String key, ModConfigSpec.ConfigValue<Integer> value, ModConfigSpec.ValueSpec spec) {
        ModConfigSpec.Range<Integer> range = spec.getRange();
        if (range == null) {
            return ConfigOption.stepper(key, Binding.of(value), Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
        }
        long span = (long) range.getMax() - range.getMin();
        if (span <= SLIDER_SPAN) {
            return ConfigOption.slider(key, Binding.of(value), range.getMin(), range.getMax(), 1);
        }
        return ConfigOption.stepper(key, Binding.of(value), range.getMin(), range.getMax(), 1);
    }

    private static ConfigOption<?> decimal(String key, ModConfigSpec.ConfigValue<Double> value, ModConfigSpec.ValueSpec spec) {
        ModConfigSpec.Range<Double> range = spec.getRange();
        if (range == null || Double.isInfinite(range.getMax() - range.getMin())) {
            Binding<Double> binding = Binding.of(value);
            Binding<String> text = Binding.of(() -> Double.toString(binding.get()), raw -> binding.set(Double.parseDouble(raw)),
                Double.toString(binding.defaultValue()));
            return ConfigOption.text(key, new SavingBinding<>(text, binding), TEXT_LENGTH).validate(raw -> parsesTo(raw, Double::parseDouble, spec));
        }
        double step = (range.getMax() - range.getMin()) / DOUBLE_SLIDER_STEPS;
        return ConfigOption.slider(key, Binding.of(value), range.getMin(), range.getMax(), step);
    }

    private static ConfigOption<String> longText(String key, ModConfigSpec.ConfigValue<Long> value, ModConfigSpec.ValueSpec spec) {
        Binding<Long> binding = Binding.of(value);
        Binding<String> text = Binding.of(() -> Long.toString(binding.get()), raw -> binding.set(Long.parseLong(raw)), Long.toString(binding.defaultValue()));
        return ConfigOption.text(key, new SavingBinding<>(text, binding), TEXT_LENGTH).validate(raw -> parsesTo(raw, Long::parseLong, spec));
    }

    private static ConfigOption<String> list(String key, ModConfigSpec.ConfigValue<List<?>> value, ModConfigSpec.ValueSpec spec) {
        Binding<List<?>> binding = Binding.of(value);
        Binding<String> text = Binding.of(() -> join(binding.get()), raw -> binding.set(split(raw)), join(binding.defaultValue()));
        return ConfigOption.text(key, new SavingBinding<>(text, binding), TEXT_LENGTH).validate(raw -> spec.test(split(raw)));
    }

    private static String join(List<?> values) {
        return values.stream().map(String::valueOf).collect(Collectors.joining(LIST_JOINER));
    }

    private static List<String> split(String raw) {
        return Arrays.stream(raw.split(LIST_SEPARATOR)).map(String::trim).filter(part -> !part.isEmpty()).toList();
    }

    private static boolean parsesTo(String raw, Function<String, Object> parser, ModConfigSpec.ValueSpec spec) {
        try {
            return spec.test(parser.apply(raw.trim()));
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    static String translationKey(String modId, ModConfigSpec.ConfigValue<?> value) {
        String key = value.getSpec().getTranslationKey();
        return key != null ? key : modId + ".configuration." + String.join(".", value.getPath());
    }

    static String pretty(String key) {
        String spaced = key.replaceAll("([a-z0-9])([A-Z])", "$1 $2").replace('_', ' ');
        return spaced.isEmpty() ? spaced : Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1).toLowerCase(Locale.ROOT);
    }

    private record SavingBinding<T>(Binding<T> view, Binding<?> source) implements Binding<T> {
        @Override
        public T get() {
            return this.view.get();
        }

        @Override
        public void set(T value) {
            this.view.set(value);
        }

        @Override
        public T defaultValue() {
            return this.view.defaultValue();
        }

        @Override
        public void save() {
            this.source.save();
        }

        @Override
        public boolean requiresRestart() {
            return this.source.requiresRestart();
        }
    }
}
