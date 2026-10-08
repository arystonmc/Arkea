package com.aryston.arkea.screen.config;

import com.aryston.arkea.api.config.ApplyMode;
import com.aryston.arkea.api.config.ArkeaConfigScreen;
import com.aryston.arkea.api.config.ConfigMeter;
import com.aryston.arkea.api.config.ConfigOption;
import com.aryston.arkea.api.config.ConfigPage;
import com.aryston.arkea.api.config.ConfigPreset;
import com.aryston.arkea.api.config.ConfigSection;
import com.aryston.arkea.api.config.OptionKind;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.overlay.ArkDialog;
import com.aryston.arkea.ui.overlay.ArkToasts;
import com.aryston.arkea.ui.overlay.ContextMenuItem;
import com.aryston.arkea.ui.overlay.DialogContent;
import com.aryston.arkea.ui.overlay.DialogForm;
import com.aryston.arkea.ui.overlay.ToastTone;
import com.aryston.arkea.ui.overlay.TooltipHint;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.Meter;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.screen.ArkWindowScreen;
import com.aryston.arkea.ui.screen.NavGroup;
import com.aryston.arkea.ui.screen.SettingsPanel;
import com.aryston.arkea.ui.screen.SettingsSection;
import com.aryston.arkea.ui.screen.SidebarBrand;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.ArkButton;
import com.aryston.arkea.ui.widget.ArkChip;
import com.aryston.arkea.ui.widget.ArkCompareView;
import com.aryston.arkea.ui.widget.ArkDisclosure;
import com.aryston.arkea.ui.widget.ArkIconButton;
import com.aryston.arkea.ui.widget.ArkPresetTile;
import com.aryston.arkea.ui.widget.ArkSegmented;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.ButtonVariant;
import com.aryston.arkea.ui.widget.ChipsRow;
import com.aryston.arkea.ui.widget.IconButtonStyle;
import com.aryston.arkea.ui.widget.ItemContent;
import com.aryston.arkea.ui.widget.NavEntry;
import com.aryston.arkea.ui.widget.NoticeRow;
import com.aryston.arkea.ui.widget.PanelRow;
import com.aryston.arkea.ui.widget.SettingRow;
import com.aryston.arkea.ui.widget.Tag;
import com.aryston.arkea.ui.widget.WidgetRow;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class ArkConfigScreen extends ArkWindowScreen {
    private static final int MAX_PRESET_COLUMNS = 4;
    private static final String PRESET_KEY = "preset:";
    private static final String APPLY_KEY = "apply";
    private static final String DISCARD_KEY = "discard";
    private static final float RESET_DIALOG_WIDTH = 440.0F;
    private static final float DIALOG_PADDING = 48.0F;
    private static final float PREVIEW_MAX_HEIGHT = 360.0F;
    private static final float METER_BOX_HEIGHT = 112.0F;
    private static final float METER_PADDING = 16.0F;
    private static final float METER_GAP = 12.0F;
    private static final float METER_NOTE_GAP = 10.0F;
    private static final float NOTE_LINE_HEIGHT = 12.0F;
    private static final int METER_FILL = ArkColors.rgba(255, 255, 255, 0.03F);
    private static final TextStyle METER_LABEL = TextStyle.of(10.0F).spacing(1.0F);
    private static final TextStyle METER_VALUE = TextStyle.of(11.0F);
    private static final TextStyle METER_NOTE = TextStyle.of(10.0F);

    private final ConfigSession session;
    private final List<Bound> bound = new ArrayList<>();
    private SettingsPanel panel = new SettingsPanel();
    private @Nullable ArkButton applyButton;
    private @Nullable ArkButton discardButton;

    private ArkConfigScreen(ConfigSession session, Screen parent) {
        super(session.currentPage().title(), parent);
        this.session = session;
    }

    public static Screen open(ArkeaConfigScreen.Definition definition, Screen parent) {
        return new ArkConfigScreen(new ConfigSession(definition), parent);
    }

    @Override
    protected Component crumb() {
        String text = this.session.definition().title().getString() + " / " + this.session.currentPage().title().getString();
        return Component.literal(text.toUpperCase(Locale.ROOT));
    }

    @Override
    protected SidebarBrand brand() {
        return this.session.definition().brand();
    }

    @Override
    protected List<NavGroup> navigation() {
        List<NavGroup> groups = new ArrayList<>();
        for (ArkeaConfigScreen.Group group : this.session.definition().groups()) {
            List<NavEntry> entries = group.pages().stream()
                .map(page -> new NavEntry(page.id(), page.icon(), null, page.title(), false, () -> this.session.pageIsDirty(page)))
                .toList();
            groups.add(new NavGroup(group.label(), entries));
        }
        return groups;
    }

    @Override
    protected String currentNav() {
        return this.session.currentPage().id();
    }

    @Override
    protected void openNav(NavEntry entry) {
        this.session.setPage(entry.id());
        this.switchTo(() -> new ArkConfigScreen(this.session, this.lastScreen));
    }

    @Override
    protected Component searchHint() {
        return Component.translatable("arkea.config.search");
    }

    @Override
    protected float buildContent(Box area) {
        this.panel = new SettingsPanel();
        this.bound.clear();
        this.addNotices();
        String query = this.searchQuery().trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty()) {
            this.addPage(this.session.currentPage());
        } else {
            this.addSearchResults(query);
        }
        return this.panel.layout(area, area.y());
    }

    private void addNotices() {
        SettingsSection notices = this.panel.section(null, true).columns(1);
        if (this.session.restartNeeded()) {
            notices.add(new NoticeRow(Icons.WARNING, ArkColors.WARNING, ArkColors.WARNING_TEXT, Component.translatable("arkea.config.restart"), null));
        }
        Component notice = this.session.currentPage().notice();
        if (notice != null && this.searchQuery().isBlank()) {
            notices.add(new NoticeRow(Icons.INFO, ArkColors.INFO, ArkColors.INFO_TEXT, notice, null));
        }
    }

    private void addPage(ConfigPage page) {
        List<ConfigPreset> presets = page.presets();
        if (!presets.isEmpty()) {
            SettingsSection section = this.panel.section(Component.translatable("arkea.config.presets"), true)
                .columns(Math.min(MAX_PRESET_COLUMNS, presets.size()));
            for (int index = 0; index < presets.size(); index++) {
                ConfigPreset preset = presets.get(index);
                ArkPresetTile tile = this.add(new ArkPresetTile(this, new ItemContent(preset.icon(), preset.name(), preset.description()),
                    () -> this.session.isPresetActive(preset), () -> {
                        this.session.applyPreset(preset);
                        this.rebuild();
                    }));
                tile.key(PRESET_KEY + index);
                section.add(new WidgetRow(tile, ArkPresetTile.HEIGHT));
            }
        }
        for (ConfigSection configSection : page.sections()) {
            this.addSection(configSection);
        }
    }

    private void addSection(ConfigSection configSection) {
        if (!configSection.isCollapsible()) {
            SettingsSection section = this.panel.section(configSection.title(), configSection.isFull()).hint(configSection.hint());
            for (ConfigOption<?> option : configSection.options()) {
                this.addOption(section, option);
            }
            return;
        }
        SettingsSection header = this.panel.section(null, true).columns(1);
        Component label = configSection.title() != null ? configSection.title() : Component.translatable("arkea.config.more");
        ArkDisclosure disclosure = this.add(new ArkDisclosure(this, label, () -> this.session.isExpanded(configSection), () -> {
            this.session.toggleExpanded(configSection);
            this.rebuild();
        }));
        disclosure.detail(Component.translatable("arkea.config.count", configSection.options().size()));
        header.add(new WidgetRow(disclosure, ArkDisclosure.HEIGHT));
        if (this.session.isExpanded(configSection)) {
            SettingsSection body = this.panel.section(null, configSection.isFull());
            for (ConfigOption<?> option : configSection.options()) {
                this.addOption(body, option);
            }
        }
    }

    private void addSearchResults(String query) {
        boolean found = false;
        for (ConfigPage page : this.session.definition().pages()) {
            List<ConfigOption<?>> matches = page.options().stream().filter(option -> matches(option, query)).toList();
            if (matches.isEmpty()) {
                continue;
            }
            found = true;
            SettingsSection section = this.panel.section(page.title(), true);
            for (ConfigOption<?> option : matches) {
                this.addOption(section, option);
            }
        }
        if (!found) {
            this.panel.section(null, true).columns(1).add(new NoticeRow(Icons.SEARCH, ArkColors.TEXT_MUTED, ArkColors.TEXT_SOFT,
                Component.translatable("arkea.config.search.none"), null));
        }
    }

    private static boolean matches(ConfigOption<?> option, String query) {
        return option.name().getString().toLowerCase(Locale.ROOT).contains(query)
            || option.description().getString().toLowerCase(Locale.ROOT).contains(query);
    }

    private void addOption(SettingsSection section, ConfigOption<?> option) {
        if (option.kind() instanceof OptionKind.Multi<?> multi) {
            this.addChips(section, option, multi);
            return;
        }
        ConfigControls.Control control = ConfigControls.create(this, this.session, option);
        SettingRow row = new SettingRow(new ItemContent(option.icon(), option.name(), option.description()))
            .litWhen(() -> this.isLit(option))
            .tooltip(() -> this.tooltip(option))
            .tags(this.tags(option))
            .cost(option.cost());
        if (!option.isAction()) {
            row.resettable(this, () -> this.session.isModified(option), () -> this.reset(option));
        }
        if (option.previewBefore() != null) {
            Component label = Component.translatable("arkea.config.preview");
            ArkIconButton eye = new ArkIconButton(this, Icons.EYE, label, IconButtonStyle.QUIET, () -> this.openPreview(option));
            eye.setTooltip(label);
            row.accessory(eye, () -> true);
        }
        section.add(row, this.add(control.widget()), control.width(), control.height());
        for (ArkWidget widget : row.widgets()) {
            if (widget != control.widget()) {
                this.add(widget);
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
            chips.add(this.add(ArkChip.toggle(this, kind.label().apply(value), () -> this.session.value(option).contains(value),
                () -> this.toggleElement(option, kind, value))));
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

    private void reset(ConfigOption<?> option) {
        this.session.resetToDefault(option);
        this.rebuild();
    }

    private void openPreview(ConfigOption<?> option) {
        if (option.previewBefore() == null || option.previewAfter() == null) {
            return;
        }
        float width = ArkDialog.WIDTH_LARGE - DIALOG_PADDING;
        float height = Math.min(PREVIEW_MAX_HEIGHT, width / Math.max(option.previewAspect(), 1.0F));
        ArkCompareView view = new ArkCompareView(this, option.name(), option.previewBefore(), option.previewAfter(), option.previewAspect());
        ArkSegmented modes = new ArkSegmented(this, option.name(), ArkCompareView.MODE_LABELS, view::mode, view::setMode);
        DialogForm form = new DialogForm().field(view, height).field(modes, ArkSegmented.HEIGHT);
        DialogContent content = new DialogContent(option.name(), option.description(), Icons.EYE, Theme.accent().light());
        ArkDialog dialog = new ArkDialog(this, content, ArkDialog.WIDTH_LARGE, this::closeDialog).content(form);
        dialog.button(new ArkButton(this, CommonComponents.GUI_DONE, ButtonVariant.PRIMARY, this::closeDialog));
        this.openDialog(dialog);
    }

    @Override
    protected void renderContent(UiGraphics graphics, float mouseX, float mouseY) {
        for (Bound entry : this.bound) {
            if (entry.widget() != null) {
                entry.widget().setActive(this.session.isEnabled(entry.option()));
            }
        }
        boolean dirty = this.session.isDirty();
        if (this.applyButton != null) {
            this.applyButton.setActive(dirty);
        }
        if (this.discardButton != null) {
            this.discardButton.setActive(dirty);
        }
        this.panel.render(graphics, mouseX, mouseY, (row, draw) -> this.renderRow(graphics, row, draw), 0);
    }

    @Override
    protected void renderSidebarFooter(UiGraphics graphics, Box area) {
        ConfigMeter meter = this.session.definition().meter();
        if (meter == null) {
            return;
        }
        Box box = new Box(area.x(), area.bottom() - METER_BOX_HEIGHT, area.width(), METER_BOX_HEIGHT);
        graphics.fill(box, METER_FILL);
        graphics.border(box, 1.0F, ArkColors.BORDER_DEFAULT);
        TextMetrics metrics = graphics.metrics();
        float x = box.x() + METER_PADDING;
        float y = box.y() + METER_PADDING;
        graphics.text(meter.label().getString().toUpperCase(Locale.ROOT), x, y, METER_LABEL, ArkColors.TEXT_LABEL);
        String value = meter.value().apply(this.session).getString();
        graphics.text(value, box.right() - METER_PADDING - metrics.width(value, METER_VALUE), y, METER_VALUE, Theme.accent().light());
        float barY = y + metrics.capHeight(METER_LABEL) + METER_GAP;
        float fraction = (float) meter.fraction().applyAsDouble(this.session);
        Meter.segments(graphics, new Box(x, barY, box.width() - METER_PADDING * 2.0F, Meter.HEIGHT), fraction, Theme.accent().base());
        float noteY = barY + Meter.HEIGHT + METER_NOTE_GAP;
        for (String line : metrics.wrap(meter.note().getString(), METER_NOTE, box.width() - METER_PADDING * 2.0F)) {
            graphics.text(line, x, noteY, METER_NOTE, ArkColors.TEXT_FAINT);
            noteY += NOTE_LINE_HEIGHT;
        }
    }

    @Override
    protected @Nullable TooltipHint hoveredHint() {
        TooltipHint widgetHint = super.hoveredHint();
        return widgetHint != null ? widgetHint : this.panel.hoveredHint();
    }

    @Override
    protected Box revealBox(ArkWidget widget) {
        Box row = this.panel.rowOf(widget);
        return row != null ? row : widget.bounds();
    }

    @Override
    protected List<ContextMenuItem> contextMenu(float x, float y) {
        for (Bound entry : this.bound) {
            if (entry.row().isHovered() && !entry.option().isAction()) {
                return this.menuFor(entry.option());
            }
        }
        return List.of();
    }

    private List<ContextMenuItem> menuFor(ConfigOption<?> option) {
        List<ContextMenuItem> items = new ArrayList<>();
        items.add(ContextMenuItem.action(Icons.RESET, Component.translatable("arkea.config.menu.reset"), () -> this.reset(option))
            .enabled(this.session.isModified(option) && this.session.isEnabled(option)));
        items.add(ContextMenuItem.action(Icons.COPY, Component.translatable("arkea.config.menu.copy"), () -> ConfigClipboard.copy(this.session, option)));
        items.add(ContextMenuItem.action(Icons.UPLOAD, Component.translatable("arkea.config.menu.paste"), () -> {
            ConfigClipboard.paste(this.session, option);
            this.rebuild();
        }).enabled(this.session.isEnabled(option) && ConfigClipboard.canPaste(option)));
        if (option.previewBefore() != null) {
            items.add(ContextMenuItem.separator());
            items.add(ContextMenuItem.action(Icons.EYE, Component.translatable("arkea.config.preview"), () -> this.openPreview(option)));
        }
        return items;
    }

    @Override
    protected Component footerNote() {
        if (this.session.definition().applyMode() == ApplyMode.IMMEDIATE) {
            return Component.translatable("arkea.options.saved");
        }
        if (this.session.isDirty()) {
            return Component.translatable("arkea.config.pending", this.session.pendingCount());
        }
        return Component.translatable("arkea.config.applied");
    }

    @Override
    protected List<ArkButton> footerButtons() {
        ArkButton reset = new ArkButton(this, Component.translatable("arkea.options.reset"), ButtonVariant.SECONDARY, this::confirmReset);
        if (this.session.definition().applyMode() == ApplyMode.IMMEDIATE) {
            this.applyButton = null;
            this.discardButton = null;
            return List.of(reset, new ArkButton(this, CommonComponents.GUI_DONE, ButtonVariant.PRIMARY, this::onClose));
        }
        this.discardButton = new ArkButton(this, Component.translatable("arkea.config.discard"), ButtonVariant.SUBTLE, () -> {
            this.session.discard();
            this.rebuild();
        });
        this.applyButton = new ArkButton(this, Component.translatable("arkea.config.apply"), ButtonVariant.PRIMARY, this::apply);
        this.applyButton.key(APPLY_KEY);
        this.discardButton.key(DISCARD_KEY);
        return List.of(reset, this.discardButton, this.applyButton);
    }

    private void apply() {
        boolean restartBefore = this.session.restartNeeded();
        this.session.apply();
        ArkToasts.show(ToastTone.SUCCESS, Component.translatable("arkea.config.applied.toast"));
        if (this.session.restartNeeded() != restartBefore) {
            this.rebuild();
        }
    }

    private void confirmReset() {
        Component pageTitle = this.session.currentPage().title();
        DialogContent content = new DialogContent(Component.translatable("arkea.options.reset.title", pageTitle),
            Component.translatable("arkea.config.reset.message"), Icons.RESET, ArkColors.WARNING);
        ArkDialog dialog = new ArkDialog(this, content, RESET_DIALOG_WIDTH, this::closeDialog);
        dialog.button(new ArkButton(this, CommonComponents.GUI_CANCEL, ButtonVariant.SUBTLE, this::closeDialog));
        dialog.button(new ArkButton(this, Component.translatable("arkea.options.reset"), ButtonVariant.DANGER, () -> {
            this.session.resetAll(this.session.currentPage().options());
            this.closeDialog();
            this.rebuild();
        }));
        this.openDialog(dialog);
    }

    @Override
    public void onClose() {
        this.leaveChecked(super::onClose);
    }

    @Override
    protected void closeAll() {
        this.leaveChecked(super::closeAll);
    }

    private void leaveChecked(Runnable proceed) {
        if (!this.session.isDirty()) {
            proceed.run();
            return;
        }
        DialogContent content = new DialogContent(Component.translatable("arkea.config.unsaved.title"),
            Component.translatable("arkea.config.unsaved.message", this.session.pendingCount()), Icons.WARNING, ArkColors.WARNING);
        ArkDialog dialog = new ArkDialog(this, content, ArkDialog.WIDTH_MEDIUM, this::closeDialog);
        dialog.button(new ArkButton(this, Component.translatable("arkea.config.discard"), ButtonVariant.DANGER, () -> {
            this.session.discard();
            this.closeDialog();
            proceed.run();
        }));
        dialog.button(new ArkButton(this, Component.translatable("arkea.config.keepEditing"), ButtonVariant.SUBTLE, this::closeDialog));
        dialog.button(new ArkButton(this, Component.translatable("arkea.config.apply"), ButtonVariant.PRIMARY, () -> {
            this.session.apply();
            this.closeDialog();
            proceed.run();
        }));
        this.openDialog(dialog);
    }

    private record Bound(ConfigOption<?> option, @Nullable ArkWidget widget, PanelRow row) {
    }
}
