package com.aryston.arkea.integration;

import com.aryston.arkea.background.MenuBackground;
import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.mixin.OptionsSubScreenAccessor;
import com.aryston.arkea.screen.options.ArkAccessibilityScreen;
import com.aryston.arkea.screen.options.ArkChatScreen;
import com.aryston.arkea.screen.options.ArkControlsScreen;
import com.aryston.arkea.screen.options.ArkLanguageScreen;
import com.aryston.arkea.screen.options.ArkOptionsScreen;
import com.aryston.arkea.screen.options.ArkSkinScreen;
import com.aryston.arkea.screen.options.ArkSoundScreen;
import com.aryston.arkea.screen.options.ArkVideoScreen;
import com.aryston.arkea.screen.options.keys.ArkKeyBindsScreen;
import com.aryston.arkea.screen.title.ArkTitleScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.ChatOptionsScreen;
import net.minecraft.client.gui.screens.options.FontOptionsScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.MouseSettingsScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.SkinCustomizationScreen;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.client.gui.screens.options.controls.ControlsScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.Nullable;

public final class ClientEvents {
    private ClientEvents() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ClientEvents::onScreenOpening);
        NeoForge.EVENT_BUS.addListener(ClientEvents::onClientTick);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        MenuBackground.tick();
    }

    private static void onScreenOpening(ScreenEvent.Opening event) {
        Screen screen = event.getNewScreen();
        if (screen.getClass() == TitleScreen.class && ArkeaConfig.TITLE_SCREEN.get() && !Minecraft.getInstance().isDemo()) {
            event.setNewScreen(new ArkTitleScreen());
        } else if (screen.getClass() == OptionsScreen.class && ArkeaConfig.OPTIONS_SCREEN.get()) {
            event.setNewScreen(new ArkOptionsScreen(((OptionsScreen) screen).getLastScreen()));
        } else if (screen instanceof OptionsSubScreen subScreen && ArkeaConfig.OPTIONS_SCREEN.get()) {
            Screen replacement = optionsPage(subScreen.getClass(), ((OptionsSubScreenAccessor) subScreen).arkea$lastScreen());
            if (replacement != null) {
                event.setNewScreen(replacement);
            }
        }
    }

    private static @Nullable Screen optionsPage(Class<?> type, Screen lastScreen) {
        if (type == VideoSettingsScreen.class) {
            return new ArkVideoScreen(lastScreen);
        }
        if (type == SoundOptionsScreen.class) {
            return new ArkSoundScreen(lastScreen);
        }
        if (type == ChatOptionsScreen.class) {
            return new ArkChatScreen(lastScreen);
        }
        if (type == AccessibilityOptionsScreen.class) {
            return new ArkAccessibilityScreen(lastScreen);
        }
        if (type == SkinCustomizationScreen.class) {
            return new ArkSkinScreen(lastScreen);
        }
        if (type == ControlsScreen.class || type == MouseSettingsScreen.class) {
            return new ArkControlsScreen(lastScreen);
        }
        if (type == KeyBindsScreen.class) {
            return new ArkKeyBindsScreen(lastScreen);
        }
        if (type == LanguageSelectScreen.class || type == FontOptionsScreen.class) {
            return new ArkLanguageScreen(lastScreen);
        }
        return null;
    }
}
