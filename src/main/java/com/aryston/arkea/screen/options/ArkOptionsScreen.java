package com.aryston.arkea.screen.options;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.screen.ArkWindowScreen;
import com.aryston.arkea.ui.screen.NavGroup;
import com.aryston.arkea.ui.screen.SidebarBrand;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkButton;
import com.aryston.arkea.ui.widget.ArkSlider;
import com.aryston.arkea.ui.widget.ArkTile;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.ButtonVariant;
import com.aryston.arkea.ui.widget.ItemContent;
import com.aryston.arkea.ui.widget.NavEntry;
import com.aryston.arkea.ui.widget.SettingRow;
import com.aryston.arkea.ui.widget.SliderBinding;
import com.aryston.arkea.ui.widget.SliderRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OnlineOptionsScreen;
import net.minecraft.client.gui.screens.telemetry.TelemetryInfoScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class ArkOptionsScreen extends ArkWindowScreen {
    private static final float SECTION_LABEL_HEIGHT = 16.0F;
    private static final float SECTION_LABEL_GAP = 8.0F;
    private static final float SECTION_GAP = 24.0F;
    private static final float COLUMN_GAP = 20.0F;
    private static final float TILE_GAP = 8.0F;
    private static final int TILE_COLUMNS = 3;
    private static final float CONTROL_HEIGHT = 30.0F;
    private static final int FOV_MIN = 30;
    private static final int FOV_MAX = 110;
    private static final int FOV_NORMAL = 70;
    private static final TextStyle SECTION_LABEL = TextStyle.of(10.0F).spacing(1.0F);

    private final Options options;
    private final List<SectionLabel> sectionLabels = new ArrayList<>();
    private final List<RowEntry> rows = new ArrayList<>();
    private final List<ArkWidget> tiles = new ArrayList<>();

    public ArkOptionsScreen(Screen lastScreen) {
        super(Component.translatable("options.title"), lastScreen);
        this.options = this.minecraft.options;
    }

    @Override
    protected Component crumb() {
        return Component.translatable("arkea.options.crumb");
    }

    @Override
    protected SidebarBrand brand() {
        return new SidebarBrand(Icons.SLIDERS, Component.translatable("arkea.options.brand"), Component.translatable("arkea.options.brand.subtitle"));
    }

    @Override
    protected List<NavGroup> navigation() {
        return Arrays.stream(OptionsPage.Group.values())
            .map(group -> new NavGroup(group.label(), Arrays.stream(OptionsPage.values())
                .filter(page -> page.group() == group && page.isAvailable())
                .map(OptionsPage::navEntry)
                .toList()))
            .toList();
    }

    @Override
    protected String currentNav() {
        return OptionsPage.OVERVIEW.id();
    }

    @Override
    protected void openNav(NavEntry entry) {
        OptionsPage page = OptionsPage.valueOf(entry.id());
        this.navigate(() -> page.open(this, this.minecraft));
    }

    @Override
    protected void buildContent(Box area) {
        this.sectionLabels.clear();
        this.rows.clear();
        this.tiles.clear();
        float y = this.addSectionLabel("arkea.options.section.quick", area.x(), area.y());
        float columnWidth = (area.width() - COLUMN_GAP) * 0.5F;
        this.addFovRow(new Box(area.x(), y, columnWidth, SettingRow.HEIGHT));
        this.addOnlineRow(new Box(area.x() + columnWidth + COLUMN_GAP, y, columnWidth, SettingRow.HEIGHT));
        y = this.addSectionLabel("arkea.options.section.all", area.x(), y + SettingRow.HEIGHT + SECTION_GAP);
        List<OptionsPage> pages = Arrays.stream(OptionsPage.values())
            .filter(page -> page != OptionsPage.OVERVIEW && !page.isExternal())
            .toList();
        float tileWidth = (area.width() - TILE_GAP * (TILE_COLUMNS - 1)) / TILE_COLUMNS;
        for (int index = 0; index < pages.size(); index++) {
            OptionsPage page = pages.get(index);
            this.addTile(new ItemContent(page.icon(), page.title(), page.description()), () -> page.open(this, this.minecraft),
                this.tileBox(area.x(), y, tileWidth, index));
        }
        int tileRows = (pages.size() + TILE_COLUMNS - 1) / TILE_COLUMNS;
        y = this.addSectionLabel("arkea.options.section.more", area.x(), y + tileRows * (ArkTile.HEIGHT + TILE_GAP) - TILE_GAP + SECTION_GAP);
        ArkTile telemetry = this.addTile(new ItemContent(Icons.CPU, OptionsPage.withoutEllipsis(Component.translatable("options.telemetry")),
            Component.translatable("arkea.options.description.telemetry")), () -> new TelemetryInfoScreen(this, this.options), this.tileBox(area.x(), y, tileWidth, 0));
        if (!this.minecraft.allowsTelemetry()) {
            telemetry.setActive(false);
            telemetry.setTooltip(Component.translatable("options.telemetry.disabled"));
        }
        this.addTile(new ItemContent(Icons.HEART, OptionsPage.withoutEllipsis(Component.translatable("options.credits_and_attribution")),
            Component.translatable("arkea.options.description.credits")), () -> new CreditsAndAttributionScreen(this), this.tileBox(area.x(), y, tileWidth, 1));
    }

    private float addSectionLabel(String key, float x, float y) {
        this.sectionLabels.add(new SectionLabel(Component.translatable(key), x, y));
        return y + SECTION_LABEL_HEIGHT + SECTION_LABEL_GAP;
    }

    private Box tileBox(float x, float y, float width, int index) {
        int column = index % TILE_COLUMNS;
        int row = index / TILE_COLUMNS;
        return new Box(x + column * (width + TILE_GAP), y + row * (ArkTile.HEIGHT + TILE_GAP), width, ArkTile.HEIGHT);
    }

    private ArkTile addTile(ItemContent content, Supplier<Screen> target, Box box) {
        ArkTile tile = this.add(new ArkTile(this, content, () -> this.navigate(target)));
        tile.setBounds(box);
        this.tiles.add(tile);
        return tile;
    }

    private void addFovRow(Box box) {
        SettingRow row = new SettingRow(new ItemContent(Icons.EYE, Component.translatable("arkea.options.fov.name"),
            Component.translatable("arkea.options.fov.description")));
        row.setBounds(box);
        SliderBinding binding = new SliderBinding(() -> this.options.fov().get(), value -> this.options.fov().set(value), ArkOptionsScreen::fovLabel);
        ArkSlider slider = this.add(new ArkSlider(this, Component.translatable("arkea.options.fov.name"), new SliderRange(FOV_MIN, FOV_MAX, 1), binding));
        slider.setBounds(row.controlSlot(ArkSlider.WIDTH, CONTROL_HEIGHT));
        this.rows.add(new RowEntry(row, slider));
    }

    private void addOnlineRow(Box box) {
        Component title = OptionsPage.withoutEllipsis(Component.translatable("options.online"));
        SettingRow row = new SettingRow(new ItemContent(Icons.LINK, title, Component.translatable("arkea.options.description.online")));
        row.setBounds(box);
        ArkButton open = this.add(new ArkButton(this, Component.translatable("arkea.options.open"), ButtonVariant.SUBTLE,
            () -> this.navigate(() -> new OnlineOptionsScreen(this, this.options))).trailingIcon(Icons.CHEVRON_RIGHT));
        open.setBounds(row.controlSlot(open.preferredWidth(), CONTROL_HEIGHT));
        this.rows.add(new RowEntry(row, open));
    }

    private static Component fovLabel(int value) {
        return switch (value) {
            case FOV_NORMAL -> Component.translatable("options.fov.min");
            case FOV_MAX -> Component.translatable("options.fov.max");
            default -> Component.literal(Integer.toString(value));
        };
    }

    @Override
    protected void renderContent(UiGraphics graphics, float mouseX, float mouseY) {
        for (SectionLabel label : this.sectionLabels) {
            float y = label.y() + (SECTION_LABEL_HEIGHT - graphics.metrics().capHeight(SECTION_LABEL)) * 0.5F;
            graphics.text(label.text().getString(), label.x(), y, SECTION_LABEL, ArkColors.TEXT_LABEL);
        }
        int index = 0;
        for (RowEntry entry : this.rows) {
            this.renderRow(graphics, index++, () -> {
                entry.row().render(graphics, mouseX, mouseY);
                entry.control().render(graphics, mouseX, mouseY);
            });
        }
        for (ArkWidget tile : this.tiles) {
            this.renderRow(graphics, index++, () -> tile.render(graphics, mouseX, mouseY));
        }
    }

    @Override
    protected Component footerNote() {
        return Component.translatable("arkea.options.saved");
    }

    @Override
    protected List<ArkButton> footerButtons() {
        return List.of(new ArkButton(this, CommonComponents.GUI_DONE, ButtonVariant.PRIMARY, this::onClose));
    }

    @Override
    public void removed() {
        this.options.save();
        super.removed();
    }

    private record SectionLabel(Component text, float x, float y) {
    }

    private record RowEntry(SettingRow row, ArkWidget control) {
    }
}
