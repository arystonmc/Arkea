package com.aryston.arkea.api.config;

import net.neoforged.neoforge.common.ModConfigSpec;

record SpecBinding<T>(ModConfigSpec.ConfigValue<T> value) implements Binding<T> {
    @Override
    public T get() {
        return this.value.getRaw();
    }

    @Override
    public void set(T newValue) {
        this.value.set(newValue);
    }

    @Override
    public T defaultValue() {
        return this.value.getDefault();
    }

    @Override
    public void save() {
        this.value.save();
    }

    @Override
    public boolean requiresRestart() {
        return this.value.getSpec().restartType() != ModConfigSpec.RestartType.NONE;
    }
}
