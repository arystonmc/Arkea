package com.aryston.arkea.api.config;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class ConfigSection {
    private final @Nullable Component title;
    private final boolean full;
    private final List<ConfigOption<?>> options = new ArrayList<>();
    private boolean collapsible;
    private boolean expandedByDefault = true;
    private @Nullable Component hint;

    ConfigSection(@Nullable Component title, boolean full) {
        this.title = title;
        this.full = full;
    }

    public ConfigSection add(ConfigOption<?> option) {
        this.options.add(option);
        return this;
    }

    public ConfigSection collapsible(boolean expanded) {
        this.collapsible = true;
        this.expandedByDefault = expanded;
        return this;
    }

    public ConfigSection hint(Component text) {
        this.hint = text;
        return this;
    }

    public @Nullable Component title() {
        return this.title;
    }

    public boolean isFull() {
        return this.full;
    }

    public boolean isCollapsible() {
        return this.collapsible;
    }

    public boolean isExpandedByDefault() {
        return this.expandedByDefault;
    }

    public @Nullable Component hint() {
        return this.hint;
    }

    public List<ConfigOption<?>> options() {
        return List.copyOf(this.options);
    }
}
