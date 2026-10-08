package com.aryston.arkea.screen.options.arkea;

import com.aryston.arkea.background.BackgroundEntry;
import com.aryston.arkea.background.BackgroundImporter;
import com.aryston.arkea.background.BackgroundLibrary;
import com.aryston.arkea.background.BackgroundThumbnails;
import com.aryston.arkea.background.ImportJob;
import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.integration.FileDialogs;
import com.aryston.arkea.screen.gallery.ArkGalleryScreen;
import com.aryston.arkea.screen.options.OptionsPage;
import com.aryston.arkea.screen.options.OptionsPageScreen;
import com.aryston.arkea.ui.overlay.ArkDialog;
import com.aryston.arkea.ui.overlay.DialogContent;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.screen.SettingsPanel;
import com.aryston.arkea.ui.screen.SettingsSection;
import com.aryston.arkea.ui.theme.Accent;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkButton;
import com.aryston.arkea.ui.widget.ArkIconButton;
import com.aryston.arkea.ui.widget.ArkSwatch;
import com.aryston.arkea.ui.widget.ButtonVariant;
import com.aryston.arkea.ui.widget.IconButtonStyle;
import com.aryston.arkea.ui.widget.ItemContent;
import com.aryston.arkea.ui.widget.SettingRow;
import com.aryston.arkea.ui.widget.ToolbarRow;
import com.mojang.blaze3d.Blaze3D;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class ArkArkeaScreen extends OptionsPageScreen {
    private static final int COLUMNS = 3;
    private static final float CELL_GAP = 6.0F;
    private static final float TOOLBAR_HEIGHT = 34.0F;
    private static final float DIALOG_WIDTH = 440.0F;
    private static final String FILTER_EXTENSIONS = "mp4;m4v;mov;gif;png;jpg;jpeg;bmp";

    private final BackgroundLibrary library = BackgroundLibrary.get();
    private int shownVersion = -1;

    public ArkArkeaScreen(Screen lastScreen) {
        super(OptionsPage.ARKEA, lastScreen);
    }

    @Override
    protected void addSettings(SettingsPanel settings) {
        this.shownVersion = this.library.version();
        ArkButton importButton = this.add(new ArkButton(this, Component.translatable("arkea.background.import"), ButtonVariant.PRIMARY, this::chooseFiles)
            .icon(Icons.PLUS));
        importButton.key("import");
        ArkButton folder = this.add(new ArkButton(this, Component.translatable("arkea.background.folder"), ButtonVariant.SECONDARY,
            () -> Blaze3D.openPath(this.library.root())).icon(Icons.FOLDER));
        folder.key("folder");
        folder.setTooltip(Component.translatable("arkea.background.folder.tooltip"));
        settings.section(Component.translatable("arkea.options.section.background"), true).columns(1)
            .hint(Component.translatable("arkea.background.import.formats"))
            .add(new ToolbarRow(TOOLBAR_HEIGHT)
                .add(importButton, importButton.preferredWidth(), ArkButton.HEIGHT, false)
                .add(folder, folder.preferredWidth(), ArkButton.HEIGHT, false));
        SettingsSection gallery = settings.section(null, true).columns(COLUMNS);
        float tileWidth = (this.contentWidth() - CELL_GAP * (COLUMNS - 1)) / COLUMNS;
        float tileHeight = MediaTile.height(tileWidth);
        gallery.add(this.backgroundRow(null, tileHeight));
        for (BackgroundEntry entry : this.library.entries()) {
            gallery.add(this.backgroundRow(entry, tileHeight));
        }
        for (ImportJob job : this.library.jobs()) {
            JobTile tile = this.add(new JobTile(this, job));
            boolean failed = job.state() == ImportJob.State.FAILED;
            Component label = Component.translatable(failed ? "arkea.background.job.dismiss" : "arkea.background.job.cancel");
            ArkIconButton cancel = this.add(new ArkIconButton(this, Icons.CLOSE, label, IconButtonStyle.TILE, () -> this.library.dismiss(job)));
            cancel.setTooltip(label);
            gallery.add(new TileRow(tile, cancel, tileHeight));
        }
        ImportTile importTile = this.add(new ImportTile(this, this::chooseFiles));
        importTile.key("import-tile");
        gallery.add(new TileRow(importTile, null, tileHeight));

        SettingsSection appearance = settings.section(this.section("appearance"), true);
        this.toggle(appearance, new ItemContent(Icons.PANO, Component.translatable("arkea.settings.pan"), Component.translatable("arkea.settings.pan.description")),
            ArkeaConfig.BACKGROUND_PAN::get, value -> ArkeaConfig.set(ArkeaConfig.BACKGROUND_PAN, value), true);
        this.addAccent(appearance);
        SettingsSection screens = settings.section(this.section("screens"), true);
        this.link(screens, new ItemContent(Icons.MONITOR, Component.translatable("arkea.options.hud"), Component.translatable("arkea.settings.hud.description")),
            Component.translatable("arkea.hud.open"), () -> this.openNav(OptionsPage.HUD.navEntry()));
        this.toggle(screens, new ItemContent(Icons.SLIDERS, Component.translatable("arkea.configuration.titleScreen"),
            Component.translatable("arkea.settings.title.description")), ArkeaConfig.TITLE_SCREEN::get, value -> ArkeaConfig.set(ArkeaConfig.TITLE_SCREEN, value), true);
        this.toggle(screens, new ItemContent(Icons.LAYERS, Component.translatable("arkea.configuration.optionsScreen"),
            Component.translatable("arkea.settings.options.description")), ArkeaConfig.OPTIONS_SCREEN::get, value -> ArkeaConfig.set(ArkeaConfig.OPTIONS_SCREEN, value), true);
        this.toggle(screens, new ItemContent(Icons.WORLD, Component.translatable("arkea.configuration.menuScreens"),
            Component.translatable("arkea.settings.menus.description")), ArkeaConfig.MENU_SCREENS::get, value -> ArkeaConfig.set(ArkeaConfig.MENU_SCREENS, value), true);
        this.toggle(screens, new ItemContent(Icons.BELL, Component.translatable("arkea.configuration.gameToasts"),
            Component.translatable("arkea.settings.toasts.description")), ArkeaConfig.GAME_TOASTS::get, value -> ArkeaConfig.set(ArkeaConfig.GAME_TOASTS, value), true);
        this.toggle(screens, new ItemContent(Icons.PLAY, Component.translatable("arkea.configuration.inGameScreens"),
            Component.translatable("arkea.settings.inGame.description")), ArkeaConfig.IN_GAME_SCREENS::get,
            value -> ArkeaConfig.set(ArkeaConfig.IN_GAME_SCREENS, value), true);
        this.toggle(screens, new ItemContent(Icons.PACK, Component.translatable("arkea.configuration.modConfigScreens"),
            Component.translatable("arkea.settings.modConfig.description")), ArkeaConfig.MOD_CONFIG_SCREENS::get,
            value -> ArkeaConfig.set(ArkeaConfig.MOD_CONFIG_SCREENS, value), true);
        this.toggle(screens, new ItemContent(Icons.BLEND, Component.translatable("arkea.configuration.vanillaTheme"),
            Component.translatable("arkea.settings.vanillaTheme.description")), ArkeaConfig.VANILLA_THEME::get,
            value -> ArkeaConfig.set(ArkeaConfig.VANILLA_THEME, value), true);
        this.toggle(screens, new ItemContent(Icons.CHUNKS, Component.translatable("arkea.configuration.debugOverlay"),
            Component.translatable("arkea.settings.debugOverlay.description")), ArkeaConfig.DEBUG_OVERLAY::get,
            value -> ArkeaConfig.set(ArkeaConfig.DEBUG_OVERLAY, value), true);
        SettingsSection developers = settings.section(this.section("developers"), true);
        this.link(developers, new ItemContent(Icons.LAYERS, Component.translatable("arkea.gallery.title"), Component.translatable("arkea.gallery.link")),
            Component.translatable("arkea.gallery.open"), () -> this.switchTo(() -> new ArkGalleryScreen(this)));
        this.addReset(() -> {
            this.library.select(null);
            ArkeaConfig.set(ArkeaConfig.ACCENT, Accent.GREEN);
        });
    }

    private TileRow backgroundRow(@Nullable BackgroundEntry entry, float height) {
        BooleanSupplier selected = () -> {
            BackgroundEntry current = this.library.selected();
            return entry == null ? current == null : entry.equals(current);
        };
        BackgroundTile tile = this.add(new BackgroundTile(this, entry, selected, () -> this.library.select(entry)));
        tile.key("background:" + (entry == null ? ArkeaConfig.VANILLA_BACKGROUND : entry.id()));
        if (entry == null) {
            return new TileRow(tile, null, height);
        }
        Component label = Component.translatable("arkea.background.delete", entry.name());
        ArkIconButton delete = this.add(new ArkIconButton(this, Icons.TRASH, label, IconButtonStyle.TILE, () -> this.confirmDelete(entry)));
        delete.setTooltip(label);
        return new TileRow(tile, delete, height);
    }

    private void addAccent(SettingsSection section) {
        Accent[] accents = Accent.values();
        List<Component> names = Arrays.stream(accents)
            .map(accent -> (Component) Component.translatable("arkea.accent." + accent.name().toLowerCase(Locale.ROOT)))
            .toList();
        int[] colors = Arrays.stream(accents).mapToInt(Accent::base).toArray();
        Component name = Component.translatable("arkea.configuration.accent");
        ArkSwatch swatch = this.add(new ArkSwatch(this, name, names, colors, () -> ArkeaConfig.ACCENT.get().ordinal(),
            index -> ArkeaConfig.set(ArkeaConfig.ACCENT, accents[index])));
        section.add(new SettingRow(new ItemContent(Icons.SPARKLE, name, Component.translatable("arkea.settings.accent.description"))), swatch,
            swatch.preferredWidth(), ArkSwatch.SIZE + 8.0F);
    }

    private void chooseFiles() {
        FileDialogs.openFiles(Component.translatable("arkea.background.import.filter").getString(), FILTER_EXTENSIONS, this::importFiles,
            error -> this.showMessage(Component.translatable("arkea.background.dialog.failed"), Component.translatable("arkea.background.dialog.failed.message")));
    }

    private void importFiles(List<Path> files) {
        List<String> rejected = new ArrayList<>();
        for (Path file : files) {
            if (BackgroundImporter.detect(file) == null) {
                rejected.add(file.getFileName().toString());
            } else {
                this.library.importFile(file, false);
            }
        }
        this.rebuild();
        if (!rejected.isEmpty()) {
            this.showMessage(Component.translatable("arkea.background.unsupported"),
                Component.translatable("arkea.background.unsupported.message", String.join(", ", rejected)));
        }
    }

    @Override
    public void onFilesDrop(List<Path> files) {
        this.importFiles(files);
    }

    private void confirmDelete(BackgroundEntry entry) {
        DialogContent content = new DialogContent(Component.translatable("arkea.background.delete.title", entry.name()),
            Component.translatable("arkea.background.delete.message"), Icons.TRASH, ArkColors.ERROR);
        ArkDialog dialog = new ArkDialog(this, content, DIALOG_WIDTH, this::closeDialog);
        dialog.button(new ArkButton(this, CommonComponents.GUI_CANCEL, ButtonVariant.SUBTLE, this::closeDialog));
        dialog.button(new ArkButton(this, Component.translatable("arkea.background.delete.confirm"), ButtonVariant.DANGER, () -> {
            this.closeDialog();
            this.library.delete(entry);
        }));
        dialog.keepOpenOnScrimClick();
        this.openDialog(dialog);
    }

    private void showMessage(Component title, Component message) {
        ArkDialog dialog = new ArkDialog(this, new DialogContent(title, message, Icons.WARNING, ArkColors.WARNING), DIALOG_WIDTH, this::closeDialog);
        dialog.button(new ArkButton(this, CommonComponents.GUI_OK, ButtonVariant.PRIMARY, this::closeDialog));
        this.openDialog(dialog);
    }

    @Override
    public void added() {
        this.library.refresh();
        super.added();
    }

    @Override
    public void tick() {
        super.tick();
        this.library.poll();
        if (this.library.version() != this.shownVersion) {
            this.rebuild();
        }
    }

    @Override
    protected Component footerNote() {
        return Component.translatable("arkea.background.note");
    }

    @Override
    public void removed() {
        BackgroundThumbnails.clear();
        super.removed();
    }
}
