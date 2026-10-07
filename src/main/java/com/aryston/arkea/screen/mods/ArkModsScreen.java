package com.aryston.arkea.screen.mods;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.screen.ArkWindowScreen;
import com.aryston.arkea.ui.screen.NavGroup;
import com.aryston.arkea.ui.screen.SidebarBrand;
import com.aryston.arkea.ui.text.SearchText;
import com.aryston.arkea.ui.widget.ArkButton;
import com.aryston.arkea.ui.widget.ButtonVariant;
import com.aryston.arkea.ui.widget.NavEntry;
import com.mojang.blaze3d.Blaze3D;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.loading.FMLPaths;
import org.jspecify.annotations.Nullable;

public final class ArkModsScreen extends ArkWindowScreen {
    private static final float LIST_WIDTH = 340.0F;
    private static final float PANEL_GAP = 24.0F;
    private static final float ROW_GAP = 6.0F;

    private final List<ModEntry> mods = ModEntry.all();
    private final ModIcons icons = new ModIcons();
    private final List<ModRow> rows = new ArrayList<>();
    private @Nullable ModDetails details;
    private @Nullable String selectedId;

    public ArkModsScreen(@Nullable Screen lastScreen) {
        super(Component.translatable("arkea.mods.title"), lastScreen != null ? lastScreen : new TitleScreen());
    }

    @Override
    protected Component crumb() {
        return Component.translatable("arkea.mods.crumb");
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
    protected @Nullable Component searchHint() {
        return Component.translatable("arkea.mods.search");
    }

    private List<ModEntry> visible() {
        String query = SearchText.normalize(this.searchQuery());
        return this.mods.stream()
            .filter(entry -> SearchText.matches(query, entry.info().displayName().getString(), entry.id(), entry.authors().getString()))
            .toList();
    }

    @Override
    protected float buildContent(Box area) {
        this.rows.clear();
        List<ModEntry> visible = this.visible();
        ModEntry selected = visible.stream().filter(entry -> entry.id().equals(this.selectedId)).findFirst()
            .orElse(visible.isEmpty() ? null : visible.getFirst());
        this.selectedId = selected != null ? selected.id() : null;
        float y = area.y();
        for (ModEntry entry : visible) {
            String id = entry.id();
            ModRow row = this.add(new ModRow(this, entry, this.icons, () -> id.equals(this.selectedId), () -> this.select(id)));
            row.key("mod:" + id);
            row.setBounds(new Box(area.x(), y, LIST_WIDTH, ModRow.HEIGHT));
            this.rows.add(row);
            y += ModRow.HEIGHT + ROW_GAP;
        }
        float bottom = y - ROW_GAP;
        this.details = selected != null ? this.buildDetails(selected) : null;
        if (this.details != null) {
            Box panel = new Box(area.x() + LIST_WIDTH + PANEL_GAP, area.y(), area.width() - LIST_WIDTH - PANEL_GAP, 0.0F);
            bottom = Math.max(bottom, this.details.layout(panel, this.metrics()));
        }
        return bottom;
    }

    private ModDetails buildDetails(ModEntry entry) {
        List<ArkButton> buttons = new ArrayList<>();
        boolean hasConfig = entry.configFactory().isPresent();
        if (hasConfig) {
            ArkButton config = this.add(new ArkButton(this, Component.translatable("arkea.mods.config"), ButtonVariant.PRIMARY,
                () -> this.openConfig(entry)).icon(Icons.SLIDERS));
            config.key("details:config");
            buttons.add(config);
        }
        this.addLink(buttons, "arkea.mods.homepage", entry.info().displayUrl());
        this.addLink(buttons, "arkea.mods.issues", entry.info().issuesUrl());
        return new ModDetails(entry, this.icons, buttons, hasConfig);
    }

    private void addLink(List<ArkButton> buttons, String key, @Nullable URI uri) {
        if (uri == null) {
            return;
        }
        ArkButton button = this.add(new ArkButton(this, Component.translatable(key), ButtonVariant.SECONDARY,
            () -> ConfirmLinkScreen.confirmLinkNow(this, uri)).trailingIcon(Icons.EXTERNAL));
        button.key("details:" + key);
        button.setTooltip(Component.literal(uri.toString()));
        buttons.add(button);
    }

    private void openConfig(ModEntry entry) {
        Screen screen = entry.configScreen(this);
        if (screen != null) {
            this.navigate(() -> screen);
        }
    }

    private void select(String id) {
        if (!id.equals(this.selectedId)) {
            this.selectedId = id;
            this.rebuild();
        }
    }

    @Override
    protected void renderContent(UiGraphics graphics, float mouseX, float mouseY) {
        for (int index = 0; index < this.rows.size(); index++) {
            ModRow row = this.rows.get(index);
            this.renderRow(graphics, index, () -> row.render(graphics, mouseX, mouseY));
        }
        ModDetails shown = this.details;
        if (shown != null) {
            this.renderRow(graphics, 0, () -> shown.render(graphics, mouseX, mouseY));
        }
    }

    @Override
    protected Component footerNote() {
        return Component.translatable("arkea.mods.note", this.mods.size());
    }

    @Override
    protected List<ArkButton> footerButtons() {
        ArkButton folder = new ArkButton(this, Component.translatable("neoforge.screen.mods.button.open_folder"), ButtonVariant.SECONDARY,
            () -> Blaze3D.openPath(FMLPaths.MODSDIR.get())).icon(Icons.FOLDER);
        return List.of(folder, new ArkButton(this, CommonComponents.GUI_DONE, ButtonVariant.PRIMARY, this::onClose));
    }

    @Override
    public void removed() {
        this.icons.close();
        super.removed();
    }
}
