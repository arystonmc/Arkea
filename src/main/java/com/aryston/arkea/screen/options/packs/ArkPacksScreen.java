package com.aryston.arkea.screen.options.packs;

import com.aryston.arkea.Arkea;
import com.aryston.arkea.screen.options.OptionsPage;
import com.aryston.arkea.screen.options.OptionsPageScreen;
import com.aryston.arkea.ui.overlay.ArkDialog;
import com.aryston.arkea.ui.overlay.DialogContent;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.screen.SettingsPanel;
import com.aryston.arkea.ui.screen.SettingsSection;
import com.aryston.arkea.ui.text.SearchText;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkButton;
import com.aryston.arkea.ui.widget.ArkIconButton;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.ButtonVariant;
import com.aryston.arkea.ui.widget.IconButtonStyle;
import com.mojang.blaze3d.Blaze3D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.PackDetector;
import net.minecraft.util.Util;
import net.minecraft.world.level.validation.ForbiddenSymlinkInfo;
import org.jspecify.annotations.Nullable;

public final class ArkPacksScreen extends OptionsPageScreen {
    private static final int RELOAD_DELAY = 20;
    private static final float DIALOG_WIDTH = 440.0F;
    private static final int INCOMPATIBLE = ArkColors.WARNING;
    private static final int INCOMPATIBLE_FILL = ArkColors.rgba(224, 166, 58, 0.14F);
    private static final int REQUIRED = ArkColors.TEXT_SOFT;
    private static final int REQUIRED_FILL = ArkColors.rgba(255, 255, 255, 0.08F);

    private final PackIcons icons = new PackIcons();
    private final PackSelectionModel model;
    private final Path folder;
    private final List<String> initialSelection;
    private @Nullable PackFolderWatcher watcher;
    private int reloadIn;

    public ArkPacksScreen(Screen lastScreen) {
        super(OptionsPage.RESOURCE_PACKS, lastScreen);
        this.folder = this.minecraft.getResourcePackDirectory();
        this.model = new PackSelectionModel(entry -> this.rebuild(), this.icons::get, this.minecraft.getResourcePackRepository(),
            repository -> this.options.updateResourcePacks(repository));
        this.initialSelection = this.selectedIds();
    }

    private List<String> selectedIds() {
        return this.model.getSelected().map(PackSelectionModel.Entry::getId).toList();
    }

    @Override
    public void added() {
        super.added();
        if (this.watcher == null) {
            this.watcher = PackFolderWatcher.create(this.folder);
        }
    }

    @Override
    protected @Nullable Component searchHint() {
        return Component.translatable("arkea.packs.search");
    }

    @Override
    protected void addSettings(SettingsPanel settings) {
        String query = SearchText.normalize(this.searchQuery());
        List<PackSelectionModel.Entry> available = this.filter(this.model.getUnselected(), query);
        List<PackSelectionModel.Entry> selected = this.filter(this.model.getSelected(), query);
        SettingsSection left = settings.section(Component.translatable("arkea.packs.available", available.size()), false)
            .hint(Component.translatable("arkea.packs.available.hint"));
        for (PackSelectionModel.Entry entry : available) {
            left.add(this.row(entry, false));
        }
        left.add(new DropHintRow(Icons.DROP, Component.translatable("arkea.packs.drop")));
        SettingsSection right = settings.section(Component.translatable("arkea.packs.selected", selected.size()), false)
            .hint(Component.translatable("arkea.packs.selected.hint"));
        for (PackSelectionModel.Entry entry : selected) {
            right.add(this.row(entry, true));
        }
    }

    private List<PackSelectionModel.Entry> filter(Stream<PackSelectionModel.Entry> entries, String query) {
        return entries.filter(entry -> SearchText.matches(query, entry.getId(), entry.getTitle().getString(), entry.getDescription().getString()))
            .toList();
    }

    private PackRow row(PackSelectionModel.Entry entry, boolean selected) {
        List<ArkWidget> actions = new ArrayList<>();
        Component title = entry.getTitle();
        if (!selected) {
            actions.add(this.action(entry, "add", Icons.ARROW_R, Component.translatable("arkea.packs.add", title), () -> this.select(entry)));
        } else {
            if (entry.canMoveUp()) {
                actions.add(this.action(entry, "up", Icons.UP, Component.translatable("arkea.packs.up", title), entry::moveUp));
            }
            if (entry.canMoveDown()) {
                actions.add(this.action(entry, "down", Icons.DOWN, Component.translatable("arkea.packs.down", title), entry::moveDown));
            }
            if (entry.canUnselect()) {
                actions.add(this.action(entry, "remove", Icons.ARROW_L, Component.translatable("arkea.packs.remove", title), entry::unselect));
            }
        }
        boolean compatible = entry.getCompatibility().isCompatible();
        PackRow.Tag tag = null;
        if (!compatible) {
            tag = new PackRow.Tag(Component.translatable("arkea.packs.incompatible"), INCOMPATIBLE, INCOMPATIBLE_FILL);
        } else if (selected && (entry.isRequired() || entry.isFixedPosition())) {
            tag = new PackRow.Tag(Component.translatable("arkea.packs.required"), REQUIRED, REQUIRED_FILL);
        }
        Component description = Component.literal(entry.getDescription().getString().lines().findFirst().orElse(""));
        Component tooltip = compatible ? entry.getExtendedDescription()
            : Component.empty().append(entry.getExtendedDescription()).append("\n").append(entry.getCompatibility().getDescription());
        return new PackRow(entry.getIconTexture(), title, description, tag, selected, actions, tooltip);
    }

    private ArkIconButton action(PackSelectionModel.Entry entry, String name, Icon icon, Component label, Runnable run) {
        ArkIconButton button = this.add(new ArkIconButton(this, icon, label, IconButtonStyle.TILE, run));
        button.key("pack:" + name + ":" + entry.getId());
        button.setTooltip(label);
        return button;
    }

    private void select(PackSelectionModel.Entry entry) {
        if (entry.getCompatibility().isCompatible()) {
            entry.select();
            return;
        }
        DialogContent content = new DialogContent(Component.translatable("pack.incompatible.confirm.title"), entry.getCompatibility().getConfirmation(),
            Icons.WARNING, ArkColors.WARNING);
        ArkDialog dialog = new ArkDialog(this, content, DIALOG_WIDTH, this::closeDialog);
        dialog.button(new ArkButton(this, CommonComponents.GUI_CANCEL, ButtonVariant.SUBTLE, this::closeDialog));
        dialog.button(new ArkButton(this, Component.translatable("arkea.packs.add.anyway"), ButtonVariant.PRIMARY, () -> {
            this.closeDialog();
            entry.select();
        }));
        this.openDialog(dialog);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.watcher != null) {
            try {
                if (this.watcher.poll()) {
                    this.reloadIn = RELOAD_DELAY;
                }
            } catch (IOException exception) {
                Arkea.LOGGER.warn("Failed to poll pack folder {}, stopping", this.folder);
                this.closeWatcher();
            }
        }
        if (this.reloadIn > 0 && --this.reloadIn == 0) {
            this.reload();
        }
    }

    private void reload() {
        this.model.findNewPacks();
        this.icons.clear();
        this.rebuild();
    }

    @Override
    public void onFilesDrop(List<Path> files) {
        String names = files.stream().map(path -> path.getFileName().toString()).collect(Collectors.joining(", "));
        DialogContent content = new DialogContent(Component.translatable("pack.dropConfirm"), Component.literal(names), Icons.DROP, ArkColors.INFO);
        ArkDialog dialog = new ArkDialog(this, content, DIALOG_WIDTH, this::closeDialog);
        dialog.button(new ArkButton(this, CommonComponents.GUI_CANCEL, ButtonVariant.SUBTLE, this::closeDialog));
        dialog.button(new ArkButton(this, Component.translatable("arkea.packs.drop.add"), ButtonVariant.PRIMARY, () -> {
            this.closeDialog();
            this.copyPacks(files);
        }));
        this.openDialog(dialog);
    }

    private void copyPacks(List<Path> files) {
        PackDetector<Path> detector = new PackDetector<>(this.minecraft.directoryValidator()) {
            @Override
            protected Path createZipPack(Path content) {
                return content;
            }

            @Override
            protected Path createDirectoryPack(Path content) {
                return content;
            }
        };
        List<ForbiddenSymlinkInfo> issues = new ArrayList<>();
        List<Path> packs = new ArrayList<>();
        for (Path file : files) {
            try {
                Path pack = detector.detectPackResources(file, issues);
                if (pack != null) {
                    packs.add(pack);
                }
            } catch (IOException exception) {
                Arkea.LOGGER.warn("Failed to check {} for packs", file, exception);
            }
        }
        if (!issues.isEmpty()) {
            Arkea.LOGGER.warn("Refused to copy packs with forbidden symbolic links: {}", issues);
            SystemToast.onPackCopyFailure(this.minecraft, this.folder.toString());
            return;
        }
        boolean failed = false;
        for (Path pack : packs) {
            try (Stream<Path> contents = Files.walk(pack)) {
                for (Path path : contents.toList()) {
                    Util.copyBetweenDirs(pack.getParent(), this.folder, path);
                }
            } catch (IOException exception) {
                Arkea.LOGGER.warn("Failed to copy pack {} to {}", pack, this.folder, exception);
                failed = true;
            }
        }
        if (failed || packs.size() < files.size()) {
            SystemToast.onPackCopyFailure(this.minecraft, this.folder.toString());
        }
        this.reload();
    }

    @Override
    protected Component footerNote() {
        return Component.translatable("arkea.packs.note");
    }

    @Override
    protected List<ArkButton> footerButtons() {
        ArkButton folder = new ArkButton(this, Component.translatable("pack.openFolder"), ButtonVariant.SECONDARY, () -> Blaze3D.openPath(this.folder));
        folder.setTooltip(Component.translatable("pack.folderInfo"));
        return List.of(folder, new ArkButton(this, CommonComponents.GUI_DONE, ButtonVariant.PRIMARY, this::onClose));
    }

    private void closeWatcher() {
        if (this.watcher != null) {
            try {
                this.watcher.close();
            } catch (IOException ignored) {
            }
            this.watcher = null;
        }
    }

    @Override
    public void removed() {
        if (!this.selectedIds().equals(this.initialSelection)) {
            this.model.commit();
        }
        this.closeWatcher();
        super.removed();
    }
}
