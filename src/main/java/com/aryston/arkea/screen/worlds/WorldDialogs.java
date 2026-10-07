package com.aryston.arkea.screen.worlds;

import com.aryston.arkea.ui.overlay.ArkDialog;
import com.aryston.arkea.ui.overlay.DialogContent;
import com.aryston.arkea.ui.overlay.DialogForm;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkButton;
import com.aryston.arkea.ui.widget.ArkCheckbox;
import com.aryston.arkea.ui.widget.ArkTextField;
import com.aryston.arkea.ui.widget.ButtonVariant;
import com.aryston.arkea.ui.widget.TextFieldState;
import java.nio.file.Files;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.storage.LevelSummary;

final class WorldDialogs {
    private static final float WIDTH = 420.0F;
    private static final float FIELD_HEIGHT = 32.0F;
    private static final float ACTION_HEIGHT = 30.0F;
    private static final int NAME_LENGTH = 64;

    private final ArkWorldSelectScreen screen;
    private final WorldOperations operations;
    private final WorldLibrary library;

    WorldDialogs(ArkWorldSelectScreen screen, WorldOperations operations, WorldLibrary library) {
        this.screen = screen;
        this.operations = operations;
        this.library = library;
    }

    void delete(LevelSummary summary) {
        boolean[] backup = {false};
        DialogContent content = new DialogContent(Component.translatable("arkea.worlds.delete.title", summary.getLevelName()),
            Component.translatable("arkea.worlds.delete.message"), Icons.TRASH, ArkColors.ERROR);
        ArkDialog dialog = new ArkDialog(this.screen, content, WIDTH, this.screen::hideDialog);
        ArkCheckbox checkbox = new ArkCheckbox(this.screen, Component.translatable("arkea.worlds.delete.backup"), () -> backup[0],
            value -> backup[0] = value);
        dialog.hero(new WorldImage(this.library, summary));
        dialog.content(new DialogForm().field(checkbox, ArkCheckbox.BOX));
        dialog.button(new ArkButton(this.screen, CommonComponents.GUI_CANCEL, ButtonVariant.SUBTLE, this.screen::hideDialog));
        dialog.button(new ArkButton(this.screen, Component.translatable("arkea.worlds.delete.confirm"), ButtonVariant.DANGER, () -> {
            this.screen.hideDialog();
            this.operations.delete(summary, backup[0]);
        }));
        dialog.keepOpenOnScrimClick();
        this.screen.showDialog(dialog);
    }

    void edit(LevelSummary summary) {
        TextFieldState name = new TextFieldState(NAME_LENGTH);
        name.setText(summary.getLevelName());
        DialogContent content = new DialogContent(Component.translatable("selectWorld.edit.title"), Component.empty(), Icons.WORLD, ArkColors.INFO);
        ArkDialog dialog = new ArkDialog(this.screen, content, WIDTH, this.screen::hideDialog);
        dialog.thumbnail(new WorldImage(this.library, summary));
        ArkButton save = new ArkButton(this.screen, Component.translatable("selectWorld.edit.save"), ButtonVariant.PRIMARY, () -> {
            this.screen.hideDialog();
            if (!name.text().strip().equals(summary.getLevelName())) {
                this.operations.rename(summary, name.text().strip());
            }
        });
        ArkTextField field = new ArkTextField(this.screen, Component.translatable("selectWorld.enterName"), name,
            value -> save.setActive(!StringUtil.isBlank(value))).icon(null);
        ArkButton backup = this.action(Component.translatable("selectWorld.edit.backup"), () -> this.operations.backup(summary));
        ArkButton folder = this.action(Component.translatable("arkea.worlds.edit.folder"), () -> this.operations.openFolder(summary));
        ArkButton optimize = this.action(Component.translatable("arkea.worlds.edit.optimize"), () -> this.optimize(summary));
        ArkButton resetIcon = this.action(Component.translatable("selectWorld.edit.resetIcon"), () -> this.operations.resetIcon(summary));
        resetIcon.setActive(Files.isRegularFile(summary.getIcon()));
        ArkButton backups = this.action(Component.translatable("arkea.worlds.edit.backups"), this.operations::openBackups);
        optimize.setActive(!summary.requiresFileFixing());
        dialog.content(new DialogForm()
            .label(Component.translatable("arkea.worlds.edit.name"))
            .field(field, FIELD_HEIGHT)
            .columns(ACTION_HEIGHT, backup, folder, optimize)
            .columns(ACTION_HEIGHT, resetIcon, backups));
        dialog.button(new ArkButton(this.screen, CommonComponents.GUI_CANCEL, ButtonVariant.SUBTLE, this.screen::hideDialog));
        dialog.button(save);
        this.screen.showDialog(dialog);
    }

    private void optimize(LevelSummary summary) {
        boolean[] backup = {true};
        boolean[] eraseCache = {false};
        DialogContent content = new DialogContent(Component.translatable("optimizeWorld.confirm.title"),
            Component.translatable("optimizeWorld.confirm.description"), Icons.WARNING, ArkColors.WARNING);
        ArkDialog dialog = new ArkDialog(this.screen, content, WIDTH, this.screen::hideDialog);
        dialog.content(new DialogForm()
            .field(new ArkCheckbox(this.screen, Component.translatable("arkea.worlds.optimize.backup"), () -> backup[0], value -> backup[0] = value),
                ArkCheckbox.BOX)
            .field(new ArkCheckbox(this.screen, Component.translatable("selectWorld.backupEraseCache"), () -> eraseCache[0],
                value -> eraseCache[0] = value), ArkCheckbox.BOX));
        dialog.button(new ArkButton(this.screen, CommonComponents.GUI_CANCEL, ButtonVariant.SUBTLE, this.screen::hideDialog));
        dialog.button(new ArkButton(this.screen, Component.translatable("optimizeWorld.confirm.proceed"), ButtonVariant.PRIMARY, () -> {
            this.screen.hideDialog();
            this.operations.optimize(summary, backup[0], eraseCache[0]);
        }));
        this.screen.showDialog(dialog);
    }

    private ArkButton action(Component label, Runnable run) {
        ArkButton button = new ArkButton(this.screen, label, ButtonVariant.SUBTLE, run);
        button.setTooltip(label);
        return button;
    }
}
