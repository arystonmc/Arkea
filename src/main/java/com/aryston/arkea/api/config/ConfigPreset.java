package com.aryston.arkea.api.config;

import com.aryston.arkea.ui.render.Icon;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;

public final class ConfigPreset {
    private final Component name;
    private final Component description;
    private final Icon icon;
    private final List<Value<?>> values = new ArrayList<>();

    public ConfigPreset(Component name, Component description, Icon icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public <T> ConfigPreset set(ConfigOption<T> option, T value) {
        this.values.add(new Value<>(option, value));
        return this;
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

    public List<Value<?>> values() {
        return List.copyOf(this.values);
    }

    public record Value<T>(ConfigOption<T> option, T value) {
    }
}
