package com.aryston.arkea.screen.options.keys;

import com.aryston.arkea.screen.options.OptionsPage;
import com.aryston.arkea.screen.options.OptionsPageScreen;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.screen.SettingsPanel;
import com.aryston.arkea.ui.screen.SettingsSection;
import com.aryston.arkea.ui.text.SearchText;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkButton;
import com.aryston.arkea.ui.widget.ArkIconButton;
import com.aryston.arkea.ui.widget.ArkKeycap;
import com.aryston.arkea.ui.widget.ArkSegmented;
import com.aryston.arkea.ui.widget.ButtonVariant;
import com.aryston.arkea.ui.widget.IconButtonStyle;
import com.aryston.arkea.ui.widget.NoticeRow;
import com.aryston.arkea.ui.widget.ToolbarRow;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.jspecify.annotations.Nullable;

public final class ArkKeyBindsScreen extends OptionsPageScreen {
    private static final float TOOLBAR_HEIGHT = 34.0F;
    private static final int CONFLICT_KEYS_SHOWN = 3;

    private Filter filter = Filter.ALL;
    private @Nullable KeyMapping listening;
    private boolean findingKey;
    private InputConstants.@Nullable Key keyFilter;
    private InputConstants.Key pressedKey = InputConstants.UNKNOWN;
    private InputConstants.Key pressedModifier = InputConstants.UNKNOWN;
    private boolean keyHeld;
    private boolean modifierHeld;
    private KeyConflicts conflicts = new KeyConflicts(new KeyMapping[0], null);

    public ArkKeyBindsScreen(Screen lastScreen) {
        super(OptionsPage.KEY_BINDS, lastScreen);
    }

    private KeyMapping[] mappings() {
        KeyMapping[] mappings = this.options.keyMappings.clone();
        Arrays.sort(mappings);
        return mappings;
    }

    @Override
    protected @Nullable Component searchHint() {
        return Component.translatable("arkea.keybinds.search");
    }

    @Override
    protected boolean focusSearchFirst() {
        return true;
    }

    @Override
    protected void addSettings(SettingsPanel settings) {
        KeyMapping[] mappings = this.mappings();
        this.conflicts = new KeyConflicts(mappings, this.options.keyDebugModifier);
        settings.section(null, true).columns(1).add(this.toolbar(mappings));
        if (this.conflicts.keyCount() > 0 && this.filter != Filter.CONFLICTS) {
            ArkButton show = this.add(new ArkButton(this, Component.translatable("arkea.keybinds.conflict.show"), ButtonVariant.SUBTLE,
                () -> this.setFilter(Filter.CONFLICTS)));
            show.key("show-conflicts");
            Component text = Component.translatable("arkea.keybinds.conflict.notice", this.conflicts.keyList(CONFLICT_KEYS_SHOWN));
            settings.section(null, true).columns(1).add(new NoticeRow(Icons.WARNING, ArkColors.WARNING, ArkColors.WARNING_TEXT, text, show));
        }
        String query = SearchText.normalize(this.searchQuery());
        Locale locale = this.minecraft.getLanguageManager().getJavaLocale();
        KeyMapping.Category category = null;
        SettingsSection section = null;
        int shown = 0;
        for (KeyMapping mapping : mappings) {
            if (!this.matches(mapping, query)) {
                continue;
            }
            if (mapping.getCategory() != category || section == null) {
                category = mapping.getCategory();
                section = settings.section(Component.literal(category.label().getString().toUpperCase(locale)), true);
            }
            section.add(this.row(mapping));
            shown++;
        }
        if (shown == 0) {
            ArkButton clear = this.add(new ArkButton(this, Component.translatable("arkea.keybinds.clear"), ButtonVariant.SUBTLE, this::clearFilters));
            clear.key("clear-filters");
            settings.section(null, true).columns(1)
                .add(new NoticeRow(Icons.SEARCH, ArkColors.INFO, ArkColors.INFO_TEXT, Component.translatable("arkea.keybinds.empty"), clear));
        }
    }

    private ToolbarRow toolbar(KeyMapping[] mappings) {
        List<Component> labels = Arrays.stream(Filter.values())
            .map(value -> (Component) Component.translatable("arkea.keybinds.filter." + value.key(), this.count(mappings, value)))
            .toList();
        ArkSegmented filters = this.add(new ArkSegmented(this, Component.translatable("arkea.keybinds.filter"), labels,
            () -> this.filter.ordinal(), index -> this.setFilter(Filter.values()[index])));
        filters.key("filter");
        ToolbarRow toolbar = new ToolbarRow(TOOLBAR_HEIGHT).add(filters, filters.preferredWidth(), ArkSegmented.HEIGHT, false);
        Component findLabel = Component.translatable(this.findingKey ? "arkea.keybinds.find.listening" : "arkea.keybinds.find");
        ArkButton find = this.add(new ArkButton(this, findLabel, this.findingKey ? ButtonVariant.PRIMARY : ButtonVariant.SUBTLE, this::toggleFind)
            .icon(Icons.KEYBOARD));
        find.key("find");
        find.setTooltip(Component.translatable("arkea.keybinds.find.tooltip"));
        toolbar.add(find, find.preferredWidth(), ArkButton.HEIGHT, true);
        InputConstants.Key filterKey = this.keyFilter;
        if (filterKey != null) {
            ArkButton chip = this.add(new ArkButton(this, Component.translatable("arkea.keybinds.find.active", filterKey.getDisplayName()),
                ButtonVariant.SECONDARY, () -> {
                    this.keyFilter = null;
                    this.rebuild();
                }).trailingIcon(Icons.CLOSE));
            chip.key("key-filter");
            toolbar.add(chip, chip.preferredWidth(), ArkButton.HEIGHT, true);
        }
        return toolbar;
    }

    private int count(KeyMapping[] mappings, Filter value) {
        int count = 0;
        for (KeyMapping mapping : mappings) {
            if (value.test(mapping, this.conflicts)) {
                count++;
            }
        }
        return count;
    }

    private boolean matches(KeyMapping mapping, String query) {
        if (!this.filter.test(mapping, this.conflicts)) {
            return false;
        }
        InputConstants.Key filterKey = this.keyFilter;
        if (filterKey != null && !mapping.getKey().equals(filterKey) && !mapping.getKeyModifier().matches(filterKey)) {
            return false;
        }
        return SearchText.matches(query, mapping.getDisplayName().getString(), mapping.getTranslatedKeyMessage().getString(),
            mapping.getCategory().label().getString());
    }

    private KeyBindRow row(KeyMapping mapping) {
        Component name = mapping.getDisplayName();
        boolean conflict = this.conflicts.has(mapping);
        ArkKeycap keycap = this.add(new ArkKeycap(this, name, mapping::getTranslatedKeyMessage, () -> this.listening == mapping,
            () -> this.conflicts.has(mapping), mapping::isUnbound, () -> this.toggleListening(mapping)));
        keycap.key("key:" + mapping.getName());
        if (conflict) {
            MutableComponent others = Component.empty();
            List<KeyMapping> with = this.conflicts.with(mapping);
            for (int index = 0; index < with.size(); index++) {
                others.append(index == 0 ? Component.empty() : Component.literal(", ")).append(with.get(index).getDisplayName());
            }
            keycap.setTooltip(Component.translatable("controls.keybinds.duplicateKeybinds", others));
        }
        Component resetLabel = Component.translatable("narrator.controls.reset", name);
        ArkIconButton reset = this.add(new ArkIconButton(this, Icons.UNDO, resetLabel, IconButtonStyle.QUIET, () -> this.resetKey(mapping)));
        reset.key("reset:" + mapping.getName());
        reset.setActive(!mapping.isDefault());
        reset.setTooltip(Component.translatable("arkea.keybinds.reset.tooltip", defaultKeyName(mapping)));
        return new KeyBindRow(name, keycap, reset, conflict, null);
    }

    private static Component defaultKeyName(KeyMapping mapping) {
        InputConstants.Key key = mapping.getDefaultKey();
        return mapping.getDefaultKeyModifier().getCombinedName(key, key::getDisplayName);
    }

    private void setFilter(Filter value) {
        this.filter = value;
        this.rebuild();
    }

    private void clearFilters() {
        this.filter = Filter.ALL;
        this.keyFilter = null;
        this.clearSearch();
        this.rebuild();
    }

    private void toggleListening(KeyMapping mapping) {
        this.findingKey = false;
        this.listening = this.listening == mapping ? null : mapping;
        this.resetCapture();
    }

    private void toggleFind() {
        this.listening = null;
        this.findingKey = !this.findingKey;
        this.resetCapture();
        this.rebuild();
    }

    private void resetKey(KeyMapping mapping) {
        mapping.setToDefault();
        mapping.setKey(mapping.getDefaultKey());
        KeyMapping.resetMapping();
        this.rebuild();
    }

    private boolean capturing() {
        return (this.listening != null || this.findingKey) && this.isInteractive();
    }

    private void resetCapture() {
        this.pressedKey = InputConstants.UNKNOWN;
        this.pressedModifier = InputConstants.UNKNOWN;
        this.keyHeld = false;
        this.modifierHeld = false;
    }

    private void stopCapture() {
        boolean wasFinding = this.findingKey;
        this.listening = null;
        this.findingKey = false;
        this.resetCapture();
        if (wasFinding) {
            this.rebuild();
        }
    }

    private void capture(KeyModifier modifier, InputConstants.Key key) {
        if (this.findingKey) {
            this.keyFilter = key;
            this.findingKey = false;
        } else if (this.listening != null) {
            this.listening.setKeyModifierAndCode(modifier, key);
            this.listening.setKey(key);
            KeyMapping.resetMapping();
            this.listening = null;
        }
        this.resetCapture();
        this.rebuild();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!this.capturing()) {
            return super.keyPressed(event);
        }
        if (event.isEscape()) {
            this.stopCapture();
            return true;
        }
        boolean nothingHeld = this.pressedKey == InputConstants.UNKNOWN && this.pressedModifier == InputConstants.UNKNOWN;
        if (this.listening != null && nothingHeld && (event.key() == InputConstants.KEY_BACKSPACE || event.key() == InputConstants.KEY_DELETE)) {
            this.capture(KeyModifier.NONE, InputConstants.UNKNOWN);
            return true;
        }
        InputConstants.Key key = InputConstants.getKey(event);
        if (this.pressedModifier == InputConstants.UNKNOWN && KeyModifier.isKeyCodeModifier(key)) {
            this.pressedModifier = key;
            this.modifierHeld = true;
        } else {
            this.pressedKey = key;
            this.keyHeld = true;
        }
        return true;
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        if (!this.capturing()) {
            return super.keyReleased(event);
        }
        if (this.pressedKey == InputConstants.UNKNOWN && this.pressedModifier == InputConstants.UNKNOWN) {
            return true;
        }
        InputConstants.Key key = InputConstants.getKey(event);
        if (key.equals(this.pressedKey)) {
            this.keyHeld = false;
        } else if (key.equals(this.pressedModifier)) {
            this.modifierHeld = false;
        }
        if (this.keyHeld || this.modifierHeld) {
            return true;
        }
        if (this.pressedKey != InputConstants.UNKNOWN) {
            this.capture(KeyModifier.getKeyModifier(this.pressedModifier), this.pressedKey);
        } else {
            this.capture(KeyModifier.NONE, this.pressedModifier);
        }
        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!this.capturing()) {
            return super.mouseClicked(event, doubleClick);
        }
        List<KeyModifier> held = KeyModifier.getActiveModifiers();
        this.capture(held.isEmpty() ? KeyModifier.NONE : held.getFirst(), InputConstants.Type.MOUSE.getOrCreate(event.button()));
        return true;
    }

    @Override
    protected Component footerNote() {
        int count = this.conflicts.keyCount();
        if (count == 0) {
            return Component.translatable("arkea.keybinds.no_conflicts");
        }
        return Component.translatable(count == 1 ? "arkea.keybinds.conflicts.one" : "arkea.keybinds.conflicts.many", count);
    }

    @Override
    protected Component resetLabel() {
        return Component.translatable("controls.resetAll");
    }

    @Override
    protected boolean canReset() {
        for (KeyMapping mapping : this.options.keyMappings) {
            if (!mapping.isDefault()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void resetDefaults() {
        for (KeyMapping mapping : this.options.keyMappings) {
            mapping.setToDefault();
            mapping.setKey(mapping.getDefaultKey());
        }
        KeyMapping.resetMapping();
        this.options.save();
        this.rebuild();
    }

    @Override
    public void removed() {
        this.stopCapture();
        super.removed();
    }

    private enum Filter {
        ALL,
        CHANGED,
        CONFLICTS,
        UNBOUND;

        private String key() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        private boolean test(KeyMapping mapping, KeyConflicts conflicts) {
            return switch (this) {
                case ALL -> true;
                case CHANGED -> !mapping.isDefault();
                case CONFLICTS -> conflicts.has(mapping);
                case UNBOUND -> mapping.isUnbound();
            };
        }
    }
}
