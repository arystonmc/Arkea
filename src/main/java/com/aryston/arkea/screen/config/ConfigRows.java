package com.aryston.arkea.screen.config;

import com.aryston.arkea.api.config.ConfigOption;
import com.aryston.arkea.api.config.OptionKind;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.screen.SettingsSection;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkChip;
import com.aryston.arkea.ui.widget.ArkIconButton;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.ChipsRow;
import com.aryston.arkea.ui.widget.IconButtonStyle;
import com.aryston.arkea.ui.widget.ItemContent;
import com.aryston.arkea.ui.widget.PanelRow;
import com.aryston.arkea.ui.widget.SettingRow;
import com.aryston.arkea.ui.widget.Tag;
import com.aryston.arkea.ui.widget.UiHost;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

final class ConfigRows {
    private final UiHost host;
    private final ConfigSession session;
    private final UnaryOperator<ArkWidget> register;
    private final Consumer<ConfigOption<?>> reset;
    private final @Nullable Consumer<ConfigOption<?>> preview;
    private final List<Bound> bound = new ArrayList<>();

    ConfigRows(UiHost host, ConfigSession session, UnaryOperator<ArkWidget> register, Consumer<ConfigOption<?>> reset,
        @Nullable Consumer<ConfigOption<?>> preview) {
        this.host = host;
        this.session = session;
        this.register = register;
        this.reset = reset;
        this.preview = preview;
    }

    List<Bound> bound() {
        return this.bound;
    }

    void clear() {
        this.bound.clear();
    }

    void refreshActive() {
        for (Bound entry : this.bound) {
            if (entry.widget() != null) {
                entry.widget().setActive(this.session.isEnabled(entry.option()));
            }
        }
    }

    void add(SettingsSection section, ConfigOption<?> option) {
        if (option.kind() instanceof OptionKind.Multi<?> multi) {
            this.addChips(section, option, multi);
            return;
        }
        ConfigControls.Control control = ConfigControls.create(this.host, this.session, option);
        SettingRow row = new SettingRow(new ItemContent(option.icon(), option.name(), option.description()))
            .litWhen(() -> this.isLit(option))
            .tooltip(() -> this.tooltip(option))
            .tags(this.tags(option))
            .cost(option.cost());
        if (!option.isAction()) {
            row.resettable(this.host, () -> this.session.isModified(option), () -> this.reset.accept(option));
        }
        Consumer<ConfigOption<?>> previewAction = this.preview;
        if (option.previewBefore() != null && previewAction != null) {
            Component label = Component.translatable("arkea.config.preview");
            ArkIconButton eye = new ArkIconButton(this.host, Icons.EYE, label, IconButtonStyle.QUIET, () -> previewAction.accept(option));
            eye.setTooltip(label);
            row.accessory(eye, () -> true);
        }
        section.add(row, this.register.apply(control.widget()), control.width(), control.height());
        for (ArkWidget widget : row.widgets()) {
            if (widget != control.widget()) {
                this.register.apply(widget);
            }
        }
        this.bound.add(new Bound(option, control.widget(), row));
    }

    @SuppressWarnings("unchecked")
    private <E> void addChips(SettingsSection section, ConfigOption<?> raw, OptionKind.Multi<?> rawKind) {
        ConfigOption<List<E>> option = (ConfigOption<List<E>>) raw;
        OptionKind.Multi<E> kind = (OptionKind.Multi<E>) rawKind;
        List<ArkChip> chips = new ArrayList<>();
        for (E value : kind.values()) {
            ArkChip chip = ArkChip.toggle(this.host, kind.label().apply(value), () -> this.session.value(option).contains(value),
                () -> this.toggleElement(option, kind, value));
            this.register.apply(chip);
            chips.add(chip);
        }
        ChipsRow row = new ChipsRow(option.name(), option.description(), chips).tooltip(option.tooltip());
        section.add(row);
        this.bound.add(new Bound(option, null, row));
    }

    private <E> void toggleElement(ConfigOption<List<E>> option, OptionKind.Multi<E> kind, E value) {
        List<E> current = this.session.value(option);
        List<E> next = kind.values().stream().filter(element -> element.equals(value) != current.contains(element)).toList();
        this.session.stage(option, next);
    }

    private List<Tag> tags(ConfigOption<?> option) {
        List<Tag> tags = new ArrayList<>(option.tags());
        if (option.restart()) {
            tags.add(Tag.tone(Component.translatable("arkea.config.tag.restart"), ArkColors.WARNING));
        }
        return tags;
    }

    private boolean isLit(ConfigOption<?> option) {
        if (option.kind() instanceof OptionKind.Toggle) {
            return Boolean.TRUE.equals(this.session.value(option)) && this.session.isEnabled(option);
        }
        return this.session.isEnabled(option);
    }

    private @Nullable Component tooltip(ConfigOption<?> option) {
        ConfigOption<Boolean> requirement = this.session.missingRequirement(option);
        if (requirement != null) {
            return Component.translatable("arkea.config.requires", requirement.name());
        }
        return option.tooltip();
    }

    record Bound(ConfigOption<?> option, @Nullable ArkWidget widget, PanelRow row) {
    }
}
