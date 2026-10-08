package com.aryston.arkea.api.config;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.neoforged.neoforge.common.ModConfigSpec;

public interface Binding<T> {
    T get();

    void set(T value);

    T defaultValue();

    default void save() {
    }

    default boolean requiresRestart() {
        return false;
    }

    static <T> Binding<T> of(ModConfigSpec.ConfigValue<T> value) {
        return new SpecBinding<>(value);
    }

    static <T> Binding<T> of(Supplier<T> getter, Consumer<T> setter, T defaultValue) {
        return new Binding<>() {
            @Override
            public T get() {
                return getter.get();
            }

            @Override
            public void set(T value) {
                setter.accept(value);
            }

            @Override
            public T defaultValue() {
                return defaultValue;
            }
        };
    }
}
