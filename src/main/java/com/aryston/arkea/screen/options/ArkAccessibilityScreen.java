package com.aryston.arkea.screen.options;

import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.screen.SettingsPanel;
import com.aryston.arkea.ui.screen.SettingsSection;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.ItemContent;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonLinks;
import net.minecraft.world.flag.FeatureFlags;

public final class ArkAccessibilityScreen extends OptionsPageScreen {
    private static final String HIGH_CONTRAST_PACK = "high_contrast";

    public ArkAccessibilityScreen(Screen lastScreen) {
        super(OptionsPage.ACCESSIBILITY, lastScreen);
    }

    @Override
    protected void addSettings(SettingsPanel settings) {
        Options options = this.options;
        SettingsSection text = settings.section(this.section("narration"), false);
        this.option(text, options.narrator(), Icons.NARRATOR);
        this.option(text, options.narratorHotkey(), Icons.KEYBOARD);
        this.option(text, options.showSubtitles(), Icons.SUBS);
        ArkWidget highContrast = this.option(text, options.highContrast(), Icons.CONTRAST);
        if (!this.minecraft.getResourcePackRepository().getAvailableIds().contains(HIGH_CONTRAST_PACK)) {
            highContrast.setActive(false);
            highContrast.setTooltip(Component.translatable("options.accessibility.high_contrast.error.tooltip"));
        }
        this.option(text, options.highContrastBlockOutline(), Icons.CUBE);
        this.option(text, options.textBackgroundOpacity(), Icons.TEXTBG);
        this.option(text, options.backgroundForChatOnly(), Icons.TEXTBG);
        this.option(text, options.chatOpacity(), Icons.CHAT);
        this.option(text, options.chatLineSpacing(), Icons.RULES);
        this.option(text, options.chatDelay(), Icons.CLOCK);
        this.option(text, options.notificationDisplayTime(), Icons.BELL);
        this.option(text, options.hideSplashTexts(), Icons.SPARKLE);
        SettingsSection motion = settings.section(this.section("motion"), false);
        this.option(motion, options.menuBackgroundBlurriness(), Icons.BLUR);
        this.option(motion, options.panoramaSpeed(), Icons.PANO);
        this.option(motion, options.bobView(), Icons.BOB);
        this.option(motion, options.screenEffectScale(), Icons.WAVES);
        this.option(motion, options.fovEffectScale(), Icons.EYE);
        this.option(motion, options.darknessEffectScale(), Icons.SHADOW);
        this.option(motion, options.damageTiltStrength(), Icons.BOB);
        this.option(motion, options.glintSpeed(), Icons.SPARKLE);
        this.option(motion, options.glintStrength(), Icons.SPARKLE);
        this.option(motion, options.hideLightningFlash(), Icons.BOLT);
        this.option(motion, options.darkMojangStudiosBackground(), Icons.CONTRAST);
        ArkWidget minecart = this.option(motion, options.rotateWithMinecart(), Icons.COMPASS);
        minecart.setActive(this.minecraft.level != null && this.minecraft.level.enabledFeatures().contains(FeatureFlags.MINECART_IMPROVEMENTS));
        SettingsSection more = settings.section(this.section("more"), true);
        this.link(more, new ItemContent(OptionsPage.CONTROLS.icon(), OptionsPage.CONTROLS.title(), OptionsPage.CONTROLS.description()),
            Component.translatable("arkea.options.open"), () -> this.openPage(OptionsPage.CONTROLS, () -> new ArkControlsScreen(this)));
        this.link(more, new ItemContent(Icons.EXTERNAL, Component.translatable("options.accessibility.link"),
            Component.translatable("arkea.options.description.accessibility_guide")),
            Component.translatable("arkea.options.open"), () -> ConfirmLinkScreen.confirmLinkNow(this, CommonLinks.ACCESSIBILITY_HELP));
    }
}
