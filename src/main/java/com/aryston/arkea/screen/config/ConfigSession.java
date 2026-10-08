package com.aryston.arkea.screen.config;

import com.aryston.arkea.api.config.ApplyMode;
import com.aryston.arkea.api.config.ArkeaConfigScreen;
import com.aryston.arkea.api.config.Binding;
import com.aryston.arkea.api.config.ConfigOption;
import com.aryston.arkea.api.config.ConfigPage;
import com.aryston.arkea.api.config.ConfigPreset;
import com.aryston.arkea.api.config.ConfigSection;
import com.aryston.arkea.api.config.ConfigValues;
import com.aryston.arkea.ui.widget.TextFieldState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

final class ConfigSession implements ConfigValues {
    private final ArkeaConfigScreen.Definition definition;
    private final Map<ConfigOption<?>, Object> pending = new LinkedHashMap<>();
    private final Map<ConfigSection, Boolean> expanded = new IdentityHashMap<>();
    private final Map<ConfigOption<?>, TextFieldState> textStates = new IdentityHashMap<>();
    private String page;
    private boolean restartNeeded;

    ConfigSession(ArkeaConfigScreen.Definition definition) {
        this.definition = definition;
        this.page = definition.pages().stream().filter(candidate -> !candidate.options().isEmpty()).findFirst()
            .orElse(definition.pages().getFirst()).id();
    }

    ArkeaConfigScreen.Definition definition() {
        return this.definition;
    }

    ConfigPage currentPage() {
        return this.definition.pages().stream().filter(candidate -> candidate.id().equals(this.page)).findFirst()
            .orElse(this.definition.pages().getFirst());
    }

    void setPage(String id) {
        this.page = id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T value(ConfigOption<T> option) {
        if (this.pending.containsKey(option)) {
            return (T) this.pending.get(option);
        }
        return option.binding().get();
    }

    <T> void stage(ConfigOption<T> option, T value) {
        if (this.definition.applyMode() == ApplyMode.IMMEDIATE) {
            this.write(option, value);
            option.binding().save();
            this.afterApply();
            return;
        }
        if (Objects.equals(value, option.binding().get())) {
            this.pending.remove(option);
        } else {
            this.pending.put(option, value);
        }
    }

    <T> void resetToDefault(ConfigOption<T> option) {
        if (!option.isAction()) {
            this.stage(option, option.binding().defaultValue());
            this.textStates.remove(option);
        }
    }

    void resetAll(Collection<ConfigOption<?>> options) {
        for (ConfigOption<?> option : options) {
            this.resetToDefault(option);
        }
    }

    <T> boolean isModified(ConfigOption<T> option) {
        return !option.isAction() && !option.isDefault(this.value(option));
    }

    boolean isPending(ConfigOption<?> option) {
        return this.pending.containsKey(option);
    }

    boolean isEnabled(ConfigOption<?> option) {
        return this.missingRequirement(option) == null;
    }

    @Nullable ConfigOption<Boolean> missingRequirement(ConfigOption<?> option) {
        for (ConfigOption<Boolean> requirement : option.requirements()) {
            if (!Boolean.TRUE.equals(this.value(requirement))) {
                return requirement;
            }
            ConfigOption<Boolean> deeper = this.missingRequirement(requirement);
            if (deeper != null) {
                return deeper;
            }
        }
        return null;
    }

    boolean isDirty() {
        return !this.pending.isEmpty();
    }

    int pendingCount() {
        return this.pending.size();
    }

    boolean pageIsDirty(ConfigPage candidate) {
        for (ConfigOption<?> option : candidate.options()) {
            if (this.isPending(option)) {
                return true;
            }
        }
        return false;
    }

    void apply() {
        List<Binding<?>> written = new ArrayList<>();
        for (Map.Entry<ConfigOption<?>, Object> entry : this.pending.entrySet()) {
            this.writeRaw(entry.getKey(), entry.getValue());
            written.add(entry.getKey().binding());
        }
        this.pending.clear();
        for (Binding<?> binding : written) {
            binding.save();
        }
        this.afterApply();
    }

    private void afterApply() {
        for (Runnable listener : this.definition.applyListeners()) {
            listener.run();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void writeRaw(ConfigOption<T> option, Object value) {
        this.write(option, (T) value);
    }

    private <T> void write(ConfigOption<T> option, T value) {
        option.binding().set(value);
        if (option.restart()) {
            this.restartNeeded = true;
        }
    }

    void discard() {
        this.pending.clear();
        this.textStates.clear();
    }

    boolean restartNeeded() {
        return this.restartNeeded;
    }

    boolean isPresetActive(ConfigPreset preset) {
        for (ConfigPreset.Value<?> value : preset.values()) {
            if (!Objects.equals(this.value(value.option()), value.value())) {
                return false;
            }
        }
        return !preset.values().isEmpty();
    }

    void applyPreset(ConfigPreset preset) {
        for (ConfigPreset.Value<?> value : preset.values()) {
            this.stagePresetValue(value);
        }
    }

    private <T> void stagePresetValue(ConfigPreset.Value<T> value) {
        this.stage(value.option(), value.value());
        this.textStates.remove(value.option());
    }

    boolean isExpanded(ConfigSection section) {
        return this.expanded.getOrDefault(section, section.isExpandedByDefault());
    }

    void toggleExpanded(ConfigSection section) {
        this.expanded.put(section, !this.isExpanded(section));
    }

    TextFieldState textState(ConfigOption<String> option, int maxLength) {
        return this.textStates.computeIfAbsent(option, key -> {
            TextFieldState state = new TextFieldState(maxLength);
            state.setText(this.value(option));
            return state;
        });
    }
}
