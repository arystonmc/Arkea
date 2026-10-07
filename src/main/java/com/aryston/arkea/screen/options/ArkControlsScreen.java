package com.aryston.arkea.screen.options;

import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.screen.SettingsPanel;
import com.aryston.arkea.ui.screen.SettingsSection;
import com.aryston.arkea.ui.widget.ItemContent;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.input.InputQuirks;
import net.minecraft.network.chat.Component;

public final class ArkControlsScreen extends OptionsPageScreen {
    public ArkControlsScreen(Screen lastScreen) {
        super(OptionsPage.CONTROLS, lastScreen);
    }

    @Override
    protected void addSettings(SettingsPanel settings) {
        Options options = this.options;
        SettingsSection mouse = settings.section(this.section("mouse"), false);
        this.option(mouse, options.sensitivity(), Icons.MOUSE);
        this.option(mouse, options.mouseWheelSensitivity(), Icons.SCROLL);
        this.option(mouse, options.discreteMouseScroll(), Icons.SCROLL);
        this.option(mouse, options.invertMouseX(), Icons.INVERT);
        this.option(mouse, options.invertMouseY(), Icons.INVERT);
        this.option(mouse, options.allowCursorChanges(), Icons.MOUSE);
        SettingsSection movement = settings.section(this.section("movement"), false);
        this.option(movement, options.toggleCrouch(), Icons.SNEAK);
        this.option(movement, options.toggleSprint(), Icons.SPEED);
        this.option(movement, options.sprintWindow(), Icons.SPEED);
        this.option(movement, options.toggleAttack(), Icons.SWORD);
        this.option(movement, options.toggleUse(), Icons.HAND);
        this.option(movement, options.autoJump(), Icons.JUMP);
        SettingsSection other = settings.section(this.section("other"), true);
        this.option(other, options.quitShortcuts(), Icons.POWER);
        if (InputQuirks.EMULATE_RIGHT_CLICK_WITH_CTRL_KEY) {
            this.option(other, options.ctrlClickEmulatesRightClick(), Icons.MOUSE);
        }
        this.option(other, options.operatorItemsTab(), Icons.CMD);
        SettingsSection keys = settings.section(this.section("keys"), true);
        this.link(keys, new ItemContent(OptionsPage.KEY_BINDS.icon(), OptionsPage.KEY_BINDS.title(), OptionsPage.KEY_BINDS.description()),
            Component.translatable("arkea.options.open"), () -> this.navigate(() -> new KeyBindsScreen(this, options)));
    }
}
