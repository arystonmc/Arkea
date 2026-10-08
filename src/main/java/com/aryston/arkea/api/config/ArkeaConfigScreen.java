package com.aryston.arkea.api.config;

import com.aryston.arkea.screen.config.ArkConfigScreen;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.screen.SidebarBrand;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jspecify.annotations.Nullable;

public final class ArkeaConfigScreen {
    private ArkeaConfigScreen() {
    }

    public static Builder builder(Component title) {
        return new Builder(title);
    }

    public static final class Builder {
        private final Component title;
        private SidebarBrand brand;
        private final List<Group> groups = new ArrayList<>();
        private ApplyMode applyMode = ApplyMode.ON_APPLY;
        private final List<Runnable> applyListeners = new ArrayList<>();
        private @Nullable ConfigMeter meter;

        private Builder(Component title) {
            this.title = title;
            this.brand = new SidebarBrand(Icons.SLIDERS, title, Component.translatable("arkea.config.subtitle"));
        }

        public Builder brand(Icon icon, Component name, Component subtitle) {
            this.brand = new SidebarBrand(icon, name, subtitle);
            return this;
        }

        public Builder brand(Identifier logo, Component name, Component subtitle) {
            this.brand = new SidebarBrand(Icons.SLIDERS, logo, name, subtitle);
            return this;
        }

        public Builder group(Component label) {
            this.groups.add(new Group(label, new ArrayList<>()));
            return this;
        }

        public Builder page(String id, Component pageTitle, Icon icon, Consumer<ConfigPage> content) {
            if (this.groups.isEmpty()) {
                this.group(Component.translatable("arkea.config.group"));
            }
            ConfigPage page = new ConfigPage(id, pageTitle, icon);
            content.accept(page);
            this.groups.getLast().pages().add(page);
            return this;
        }

        public Builder page(ConfigPage page) {
            if (this.groups.isEmpty()) {
                this.group(Component.translatable("arkea.config.group"));
            }
            this.groups.getLast().pages().add(page);
            return this;
        }

        public Builder applyMode(ApplyMode mode) {
            this.applyMode = mode;
            return this;
        }

        public Builder onApply(Runnable listener) {
            this.applyListeners.add(listener);
            return this;
        }

        public Builder meter(ConfigMeter value) {
            this.meter = value;
            return this;
        }

        public Screen build(Screen parent) {
            return ArkConfigScreen.open(this.definition(), parent);
        }

        public IConfigScreenFactory factory() {
            return (container, parent) -> this.build(parent);
        }

        private Definition definition() {
            List<Group> copy = this.groups.stream().filter(group -> !group.pages().isEmpty())
                .map(group -> new Group(group.label(), List.copyOf(group.pages())))
                .toList();
            if (copy.isEmpty()) {
                throw new IllegalStateException("A config screen needs at least one page");
            }
            return new Definition(this.title, this.brand, copy, this.applyMode, List.copyOf(this.applyListeners), this.meter);
        }
    }

    public static ConfigPage page(String id, Component title, Icon icon) {
        return new ConfigPage(id, title, icon);
    }

    public record Group(Component label, List<ConfigPage> pages) {
    }

    public record Definition(Component title, SidebarBrand brand, List<Group> groups, ApplyMode applyMode, List<Runnable> applyListeners,
        @Nullable ConfigMeter meter) {

        public List<ConfigPage> pages() {
            return this.groups.stream().flatMap(group -> group.pages().stream()).toList();
        }
    }
}
