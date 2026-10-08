package com.aryston.arkea.screen.gallery;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.overlay.ArkDialog;
import com.aryston.arkea.ui.overlay.ArkSideSheet;
import com.aryston.arkea.ui.overlay.ContextMenuItem;
import com.aryston.arkea.ui.overlay.DialogContent;
import com.aryston.arkea.ui.overlay.DialogForm;
import com.aryston.arkea.ui.overlay.DialogList;
import com.aryston.arkea.ui.overlay.DialogProgress;
import com.aryston.arkea.ui.overlay.TooltipHint;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.ItemSlot;
import com.aryston.arkea.ui.render.Meter;
import com.aryston.arkea.ui.render.PixelSpinner;
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
import com.aryston.arkea.ui.widget.ArkCheckbox;
import com.aryston.arkea.ui.widget.ArkChip;
import com.aryston.arkea.ui.widget.ArkColorButton;
import com.aryston.arkea.ui.widget.ArkCompareView;
import com.aryston.arkea.ui.widget.ArkDisclosure;
import com.aryston.arkea.ui.widget.ArkPagination;
import com.aryston.arkea.ui.widget.ArkPresetTile;
import com.aryston.arkea.ui.widget.ArkRadio;
import com.aryston.arkea.ui.widget.ArkRangeSlider;
import com.aryston.arkea.ui.widget.ArkSegmented;
import com.aryston.arkea.ui.widget.ArkSlider;
import com.aryston.arkea.ui.widget.ArkStepper;
import com.aryston.arkea.ui.widget.ArkSwitch;
import com.aryston.arkea.ui.widget.ArkTextField;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.ButtonVariant;
import com.aryston.arkea.ui.widget.ChipsRow;
import com.aryston.arkea.ui.widget.IntRangeModel;
import com.aryston.arkea.ui.widget.IntSliderModel;
import com.aryston.arkea.ui.widget.ItemContent;
import com.aryston.arkea.ui.widget.NavEntry;
import com.aryston.arkea.ui.widget.NoticeRow;
import com.aryston.arkea.ui.widget.SettingRow;
import com.aryston.arkea.ui.widget.SliderRange;
import com.aryston.arkea.ui.widget.Tag;
import com.aryston.arkea.ui.widget.TextFieldState;
import com.aryston.arkea.ui.widget.ToolbarRow;
import com.aryston.arkea.ui.widget.WidgetRow;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

public final class ArkGalleryScreen extends ArkWindowScreen {
    private static final Identifier BEFORE = Identifier.fromNamespaceAndPath("arkea", "textures/gui/gallery_before.png");
    private static final Identifier AFTER = Identifier.fromNamespaceAndPath("arkea", "textures/gui/gallery_after.png");
    private static final float PREVIEW_ASPECT = 16.0F / 9.0F;
    private static final float PREVIEW_HEIGHT = 360.0F;
    private static final float TOOLBAR_HEIGHT = 34.0F;
    private static final float PAINT_HEIGHT = 64.0F;
    private static final float CHOICE_HEIGHT = 24.0F;
    private static final float DIVIDER_DASH = 4.0F;
    private static final float ITEM_GAP = 8.0F;
    private static final float SECTION_ITEM_GAP = 24.0F;
    private static final float METER_WIDTH = 220.0F;
    private static final float PROGRESS_HEIGHT = 4.0F;
    private static final float DIVIDER_WIDTH = 200.0F;
    private static final float PROGRESS_PERIOD = 4000.0F;
    private static final int PAGE_COUNT = 5;
    private static final int STEP_COUNT = 3;
    private static final int TEXT_LIMIT = 32;
    private static final int COST_LEVEL = 2;
    private static final int COST_LEVELS = 3;
    private static final float METER_FRACTION = 0.7F;
    private static final List<String> FOG = List.of("vanilla", "height", "volumetric");
    private static final List<String> PLACES = List.of("overworld", "nether", "end", "underwater");
    private static final TextStyle DIVIDER_LABEL = TextStyle.of(9.0F).spacing(1.0F);

    private static final GalleryState STATE = new GalleryState();

    private final List<SettingRow> rows = new ArrayList<>();
    private SettingsPanel panel = new SettingsPanel();

    public ArkGalleryScreen(Screen lastScreen) {
        super(Component.translatable("arkea.gallery.title"), lastScreen);
    }

    @Override
    protected Component crumb() {
        return Component.translatable("arkea.gallery.crumb");
    }

    @Override
    protected @Nullable SidebarBrand brand() {
        return null;
    }

    @Override
    protected List<NavGroup> navigation() {
        return List.of();
    }

    @Override
    protected String currentNav() {
        return "";
    }

    @Override
    protected void openNav(NavEntry entry) {
    }

    @Override
    protected float buildContent(Box area) {
        this.panel = new SettingsPanel();
        this.rows.clear();
        this.addInputs();
        this.addChoices();
        this.addFeedback();
        this.addOverlays();
        this.addCollapsible();
        return this.panel.layout(area, area.y());
    }

    private Component text(String key) {
        return Component.translatable("arkea.gallery." + key);
    }

    private void row(SettingsSection section, String key, ArkWidget control, float width, float height) {
        SettingRow row = new SettingRow(new ItemContent(Icons.SLIDERS, this.text(key), this.text(key + ".description")));
        section.add(row, this.add(control), width, height);
        this.rows.add(row);
    }

    private void addInputs() {
        SettingsSection section = this.panel.section(this.text("inputs"), true);
        this.row(section, "stepper", new ArkStepper(this, this.text("stepper"), new IntSliderModel(new SliderRange(2, 32, 1), () -> STATE.distance,
            value -> STATE.distance = value, value -> Component.literal(Integer.toString(value)))), ArkStepper.WIDTH, ArkStepper.HEIGHT);
        this.row(section, "color", new ArkColorButton(this, this.text("color"), () -> STATE.color, value -> STATE.color = value),
            ArkColorButton.WIDTH, ArkColorButton.HEIGHT);
        IntRangeModel range = new IntRangeModel(new SliderRange(0, 100, 5), () -> STATE.rangeLow, value -> STATE.rangeLow = value,
            () -> STATE.rangeHigh, value -> STATE.rangeHigh = value, (low, high) -> Component.literal(low + "% - " + high + "%"));
        this.row(section, "range", new ArkRangeSlider(this, this.text("range"), range), ArkRangeSlider.WIDTH, ArkRangeSlider.HEIGHT);
        this.row(section, "ticks", new ArkSlider(this, this.text("ticks"), new IntSliderModel(new SliderRange(0, 4, 1), () -> STATE.mipmap,
            value -> STATE.mipmap = value, value -> Component.literal(Integer.toString(value)))), ArkSlider.WIDTH, ArkSlider.HEIGHT);
        List<Component> qualities = List.of(this.text("quality.off"), this.text("quality.low"), this.text("quality.high"), this.text("quality.ultra"));
        ArkSegmented segmented = new ArkSegmented(this, this.text("segmented"), qualities, () -> STATE.quality, value -> STATE.quality = value);
        this.row(section, "segmented", segmented, segmented.preferredWidth(), ArkSegmented.HEIGHT);
        ArkPagination pages = new ArkPagination(this, () -> STATE.page, () -> PAGE_COUNT, value -> STATE.page = value);
        this.row(section, "pagination", pages, pages.preferredWidth(), ArkPagination.HEIGHT);
        this.row(section, "text", new ArkTextField(this, this.text("text.hint"), STATE.name, value -> { }), ArkColorButton.WIDTH, ArkTextField.HEIGHT);
        this.row(section, "switch", new ArkSwitch(this, this.text("switch"), () -> STATE.enabled, value -> STATE.enabled = value),
            ArkSwitch.WIDTH, ArkSwitch.HEIGHT);
        List<ArkChip> chips = new ArrayList<>();
        for (String place : PLACES) {
            chips.add(this.add(ArkChip.toggle(this, this.text("place." + place), () -> STATE.places.contains(place), () -> {
                if (!STATE.places.remove(place)) {
                    STATE.places.add(place);
                }
            })));
        }
        chips.add(this.add(ArkChip.add(this, this.text("place.add"), () -> { })));
        section.add(new ChipsRow(this.text("chips"), this.text("chips.description"), chips));
    }

    private void addChoices() {
        SettingsSection section = this.panel.section(this.text("choices"), true).columns(2);
        ToolbarRow checks = new ToolbarRow(CHOICE_HEIGHT * 2.0F);
        ArkCheckbox checkbox = this.add(new ArkCheckbox(this, this.text("checkbox"), () -> STATE.checked, value -> STATE.checked = value));
        checks.add(checkbox, checkbox.preferredWidth(), CHOICE_HEIGHT, false);
        section.add(checks);
        ToolbarRow radios = new ToolbarRow(CHOICE_HEIGHT * 2.0F);
        for (int index = 0; index < FOG.size(); index++) {
            int choice = index;
            ArkRadio radio = this.add(new ArkRadio(this, this.text("fog." + FOG.get(index)), () -> STATE.fog == choice, () -> STATE.fog = choice));
            radios.add(radio, radio.preferredWidth(), ArkRadio.HEIGHT, false);
        }
        section.add(radios);
        section.add(new WidgetRow(this.add(new ArkPresetTile(this, new ItemContent(Icons.SPARKLE, this.text("preset.cinematic"),
            this.text("preset.cinematic.description")), () -> STATE.preset == 0, () -> STATE.preset = 0)), ArkPresetTile.HEIGHT));
        section.add(new WidgetRow(this.add(new ArkPresetTile(this, new ItemContent(Icons.BOLT, this.text("preset.fast"),
            this.text("preset.fast.description")), () -> STATE.preset == 1, () -> STATE.preset = 1)), ArkPresetTile.HEIGHT));
    }

    private void addFeedback() {
        SettingsSection section = this.panel.section(this.text("feedback"), true).columns(1);
        section.add(new PaintRow(PAINT_HEIGHT, this::paintTags));
        section.add(new PaintRow(PAINT_HEIGHT, this::paintMeters));
        section.add(new PaintRow(PAINT_HEIGHT, this::paintDividers));
        section.add(new PaintRow(PAINT_HEIGHT, this::paintSlots));
    }

    private void paintTags(UiGraphics graphics, Box area) {
        float x = area.x();
        float y = area.centerY();
        List<Tag> tags = List.of(Tag.neutral(this.text("tag.default")), Tag.beta(this.text("tag.beta")), Tag.tone(this.text("tag.recommended"),
            Theme.accent().light()), Tag.tone(this.text("tag.restart"), ArkColors.WARNING), Tag.tone(this.text("tag.unstable"), ArkColors.ERROR),
            Tag.tone(this.text("tag.info"), ArkColors.INFO));
        for (Tag tag : tags) {
            x = tag.draw(graphics, x, y) + Tag.GAP;
        }
    }

    private void paintMeters(UiGraphics graphics, Box area) {
        float y = area.centerY();
        Meter.segments(graphics, new Box(area.x(), y - Meter.HEIGHT * 0.5F, METER_WIDTH, Meter.HEIGHT), METER_FRACTION, Theme.accent().base());
        float x = area.x() + METER_WIDTH + SECTION_ITEM_GAP;
        Meter.pips(graphics, x, y, COST_LEVEL, COST_LEVELS, Theme.accent().light());
        x += Meter.pipsWidth(COST_LEVELS) + SECTION_ITEM_GAP;
        Meter.stepDots(graphics, x, y - PROGRESS_HEIGHT * 0.5F, STATE.page % STEP_COUNT, STEP_COUNT, Theme.accent().base());
        x += METER_WIDTH * 0.5F;
        float progress = graphics.now() % (long) PROGRESS_PERIOD / PROGRESS_PERIOD;
        Meter.progress(graphics, new Box(x, y - PROGRESS_HEIGHT * 0.5F, METER_WIDTH, PROGRESS_HEIGHT), progress, Theme.accent().base());
        x += METER_WIDTH + SECTION_ITEM_GAP;
        PixelSpinner.render(graphics, x, y - PixelSpinner.SIZE * 0.5F, Theme.accent().light());
    }

    private void paintDividers(UiGraphics graphics, Box area) {
        float x = area.x();
        float y = area.centerY();
        Meter.divider(graphics, x, y, DIVIDER_WIDTH);
        x += DIVIDER_WIDTH + SECTION_ITEM_GAP;
        Meter.labeledDivider(graphics, x, y, DIVIDER_WIDTH, this.text("divider.or").getString(), DIVIDER_LABEL);
        x += DIVIDER_WIDTH + SECTION_ITEM_GAP;
        Meter.dashedDivider(graphics, x, y, DIVIDER_WIDTH, DIVIDER_DASH);
    }

    private void paintSlots(UiGraphics graphics, Box area) {
        float y = area.centerY() - ItemSlot.SIZE * 0.5F;
        List<ItemStack> stacks = List.of(new ItemStack(Items.DIAMOND_SWORD), new ItemStack(Items.TORCH), new ItemStack(Items.GRASS_BLOCK), ItemStack.EMPTY);
        float x = area.x();
        for (int index = 0; index < stacks.size(); index++) {
            ItemSlot.draw(graphics, new Box(x, y, ItemSlot.SIZE, ItemSlot.SIZE), stacks.get(index), index == 1);
            x += ItemSlot.SIZE + ITEM_GAP;
        }
    }

    private void addOverlays() {
        SettingsSection section = this.panel.section(this.text("overlays"), true).columns(1);
        ToolbarRow toolbar = new ToolbarRow(TOOLBAR_HEIGHT);
        this.toolbarButton(toolbar, "whatsnew", Icons.SPARKLE, this::openWhatsNew);
        this.toolbarButton(toolbar, "progress", Icons.CLOCK, this::openProgress);
        this.toolbarButton(toolbar, "input", Icons.EDIT, this::openInput);
        this.toolbarButton(toolbar, "sheet", Icons.LAYERS, this::openSheet);
        this.toolbarButton(toolbar, "preview", Icons.EYE, this::openPreview);
        section.add(toolbar);
        section.add(new NoticeRow(Icons.INFO, ArkColors.INFO, ArkColors.INFO_TEXT, this.text("contextHint"), null));
    }

    private void toolbarButton(ToolbarRow toolbar, String key, Icon icon, Runnable action) {
        ArkButton button = this.add(new ArkButton(this, this.text("open." + key), ButtonVariant.SECONDARY, action).icon(icon));
        button.key("gallery:" + key);
        toolbar.add(button, button.preferredWidth(), ArkButton.HEIGHT, false);
    }

    private void addCollapsible() {
        SettingsSection section = this.panel.section(this.text("collapsible"), true).columns(1);
        ArkDisclosure disclosure = this.add(new ArkDisclosure(this, this.text("collapsible.advanced"), () -> STATE.expanded, () -> {
            STATE.expanded = !STATE.expanded;
            this.rebuild();
        }));
        disclosure.detail(Component.translatable("arkea.config.count", 2));
        section.add(new WidgetRow(disclosure, ArkDisclosure.HEIGHT));
        if (STATE.expanded) {
            this.row(section, "switch", new ArkSwitch(this, this.text("switch"), () -> STATE.checked, value -> STATE.checked = value),
                ArkSwitch.WIDTH, ArkSwitch.HEIGHT);
        }
        ArkDisclosure developer = this.add(new ArkDisclosure(this, this.text("collapsible.developer"), () -> false, () -> { }));
        developer.tag(Tag.tone(this.text("tag.unstable"), ArkColors.ERROR));
        section.add(new WidgetRow(developer, ArkDisclosure.HEIGHT));
    }

    private void openWhatsNew() {
        DialogList list = new DialogList()
            .entry(Icons.SPARKLE, Theme.accent().light(), this.text("whatsnew.one"), this.text("whatsnew.one.detail"), Tag.tone(this.text("tag.new"),
                Theme.accent().light()))
            .entry(Icons.TICK, ArkColors.INFO, this.text("whatsnew.two"), this.text("whatsnew.two.detail"), Tag.tone(this.text("tag.fixed"), ArkColors.INFO))
            .entry(Icons.BOLT, ArkColors.WARNING, this.text("whatsnew.three"), null, Tag.tone(this.text("tag.improved"), ArkColors.WARNING));
        DialogContent content = new DialogContent(this.text("whatsnew"), this.text("whatsnew.body"), Icons.SPARKLE, Theme.accent().light());
        this.openDialog(new ArkDialog(this, content, ArkDialog.WIDTH_MEDIUM, this::closeDialog).content(list)
            .button(new ArkButton(this, CommonComponents.GUI_DONE, ButtonVariant.PRIMARY, this::closeDialog)));
    }

    private void openProgress() {
        long start = this.now();
        DialogProgress progress = new DialogProgress(() -> this.text("progress.step"), () -> (this.now() - start) % (long) PROGRESS_PERIOD / PROGRESS_PERIOD);
        DialogContent content = new DialogContent(this.text("progress"), this.text("progress.body"), Icons.CLOCK, ArkColors.INFO);
        this.openDialog(new ArkDialog(this, content, ArkDialog.WIDTH_MEDIUM, this::closeDialog).content(progress)
            .button(new ArkButton(this, CommonComponents.GUI_CANCEL, ButtonVariant.SUBTLE, this::closeDialog)));
    }

    private void openInput() {
        ArkTextField field = new ArkTextField(this, this.text("text.hint"), STATE.name, value -> { });
        DialogForm form = new DialogForm().label(this.text("input.label")).field(field, ArkTextField.HEIGHT);
        DialogContent content = new DialogContent(this.text("input"), this.text("input.body"), Icons.EDIT, Theme.accent().light());
        this.openDialog(new ArkDialog(this, content, ArkDialog.WIDTH_SMALL, this::closeDialog).content(form)
            .button(new ArkButton(this, CommonComponents.GUI_CANCEL, ButtonVariant.SUBTLE, this::closeDialog))
            .button(new ArkButton(this, CommonComponents.GUI_DONE, ButtonVariant.PRIMARY, this::closeDialog)));
    }

    private void openSheet() {
        DialogList list = new DialogList()
            .entry(Icons.USER, Theme.accent().light(), this.text("preset.cinematic"), this.text("preset.cinematic.description"),
                Tag.tone(this.text("tag.active"), Theme.accent().light()))
            .entry(Icons.BOLT, ArkColors.TEXT_SOFT, this.text("preset.fast"), this.text("preset.fast.description"), null);
        ArkSideSheet sheet = new ArkSideSheet(this, this.text("sheet"), this.text("sheet.body"), this::closeDialog).content(list)
            .button(new ArkButton(this, CommonComponents.GUI_DONE, ButtonVariant.PRIMARY, this::closeDialog));
        this.openDialog(sheet);
    }

    private void openPreview() {
        ArkCompareView view = new ArkCompareView(this, this.text("preview"), BEFORE, AFTER, PREVIEW_ASPECT);
        ArkSegmented modes = new ArkSegmented(this, this.text("preview"), ArkCompareView.MODE_LABELS, view::mode, view::setMode);
        DialogForm form = new DialogForm().field(view, PREVIEW_HEIGHT).field(modes, ArkSegmented.HEIGHT);
        DialogContent content = new DialogContent(this.text("preview"), this.text("preview.body"), Icons.EYE, Theme.accent().light());
        this.openDialog(new ArkDialog(this, content, ArkDialog.WIDTH_LARGE, this::closeDialog).content(form)
            .button(new ArkButton(this, CommonComponents.GUI_DONE, ButtonVariant.PRIMARY, this::closeDialog)));
    }

    @Override
    protected List<ContextMenuItem> contextMenu(float x, float y) {
        for (SettingRow row : this.rows) {
            if (row.isHovered()) {
                return List.of(
                    ContextMenuItem.action(Icons.RESET, Component.translatable("arkea.config.menu.reset"), () -> { }).shortcut(Component.literal("R")),
                    ContextMenuItem.action(Icons.COPY, Component.translatable("arkea.config.menu.copy"), () -> { }).shortcut(Component.literal("CTRL C")),
                    ContextMenuItem.action(Icons.EYE, Component.translatable("arkea.config.preview"), this::openPreview),
                    ContextMenuItem.separator(),
                    ContextMenuItem.action(Icons.LOCK, this.text("menu.lock"), () -> { }),
                    ContextMenuItem.action(Icons.UPLOAD, Component.translatable("arkea.config.menu.paste"), () -> { }).enabled(false),
                    ContextMenuItem.action(Icons.TRASH, this.text("menu.remove"), () -> { }).dangerous());
            }
        }
        return List.of();
    }

    @Override
    protected void renderContent(UiGraphics graphics, float mouseX, float mouseY) {
        this.panel.render(graphics, mouseX, mouseY, (row, draw) -> this.renderRow(graphics, row, draw), 0);
    }

    @Override
    protected @Nullable TooltipHint hoveredHint() {
        TooltipHint widgetHint = super.hoveredHint();
        return widgetHint != null ? widgetHint : this.panel.hoveredHint();
    }

    @Override
    protected Component footerNote() {
        return this.text("note");
    }

    @Override
    protected List<ArkButton> footerButtons() {
        return List.of(new ArkButton(this, CommonComponents.GUI_DONE, ButtonVariant.PRIMARY, this::onClose));
    }

    private static final class GalleryState {
        private int distance = 12;
        private int color = 0xF0A050;
        private int rangeLow = 20;
        private int rangeHigh = 75;
        private int mipmap = 3;
        private int quality = 2;
        private int page;
        private int fog;
        private int preset;
        private boolean enabled = true;
        private boolean checked = true;
        private boolean expanded = true;
        private final Set<String> places = new HashSet<>(List.of("overworld", "nether"));
        private final TextFieldState name = new TextFieldState(TEXT_LIMIT);
    }
}
