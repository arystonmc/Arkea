package com.aryston.arkea.api.config;

import java.util.List;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import net.minecraft.network.chat.Component;

public sealed interface OptionKind<T> {
    record Toggle() implements OptionKind<Boolean> {
    }

    record Choice<T>(List<T> values, Function<T, Component> label, ChoiceStyle style) implements OptionKind<T> {
    }

    record IntRange(int min, int max, int step, IntFunction<Component> label, boolean stepper) implements OptionKind<Integer> {
    }

    record DoubleRange(double min, double max, double step, DoubleFunction<Component> label) implements OptionKind<Double> {
    }

    record Text(int maxLength, Predicate<String> validator) implements OptionKind<String> {
    }

    record Color() implements OptionKind<Integer> {
    }

    record Multi<E>(List<E> values, Function<E, Component> label) implements OptionKind<List<E>> {
    }

    record Action(Component label, Runnable action) implements OptionKind<Boolean> {
    }
}
