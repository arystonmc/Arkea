package com.aryston.arkea.api.config;

import com.aryston.arkea.ui.render.Icon;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class ConfigPage {
    private final String id;
    private final Component title;
    private final Icon icon;
    private final List<ConfigSection> sections = new ArrayList<>();
    private final List<ConfigPreset> presets = new ArrayList<>();
    private @Nullable Component notice;

    ConfigPage(String id, Component title, Icon icon) {
        this.id = id;
        this.title = title;
        this.icon = icon;
    }

    public ConfigPage section(@Nullable Component title, Consumer<ConfigSection> content) {
        return this.addSection(new ConfigSection(title, false), content);
    }

    public ConfigPage fullSection(@Nullable Component title, Consumer<ConfigSection> content) {
        return this.addSection(new ConfigSection(title, true), content);
    }

    private ConfigPage addSection(ConfigSection section, Consumer<ConfigSection> content) {
        content.accept(section);
        this.sections.add(section);
        return this;
    }

    public ConfigPage preset(ConfigPreset preset) {
        this.presets.add(preset);
        return this;
    }

    public ConfigPage notice(Component text) {
        this.notice = text;
        return this;
    }

    public String id() {
        return this.id;
    }

    public Component title() {
        return this.title;
    }

    public Icon icon() {
        return this.icon;
    }

    public List<ConfigSection> sections() {
        return List.copyOf(this.sections);
    }

    public List<ConfigPreset> presets() {
        return List.copyOf(this.presets);
    }

    public @Nullable Component notice() {
        return this.notice;
    }

    public List<ConfigOption<?>> options() {
        List<ConfigOption<?>> all = new ArrayList<>();
        for (ConfigSection section : this.sections) {
            all.addAll(section.options());
        }
        return all;
    }
}
