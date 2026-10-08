package com.aryston.arkea.integration;

import com.aryston.arkea.background.MenuBackground;
import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.debug.UiCheck;
import com.aryston.arkea.mixin.JoinMultiplayerScreenAccessor;
import com.aryston.arkea.mixin.ModListScreenAccessor;
import com.aryston.arkea.mixin.OptionsSubScreenAccessor;
import com.aryston.arkea.mixin.SelectWorldScreenAccessor;
import com.aryston.arkea.screen.loading.LoadingSkin;
import com.aryston.arkea.screen.loading.Reconnect;
import com.aryston.arkea.screen.mods.ArkModsScreen;
import com.aryston.arkea.screen.options.ArkAccessibilityScreen;
import com.aryston.arkea.screen.palette.CommandPaletteScreen;
import com.aryston.arkea.screen.options.ArkChatScreen;
import com.aryston.arkea.screen.options.ArkControlsScreen;
import com.aryston.arkea.screen.options.ArkLanguageScreen;
import com.aryston.arkea.screen.options.ArkOptionsScreen;
import com.aryston.arkea.screen.options.ArkSkinScreen;
import com.aryston.arkea.screen.options.ArkSoundScreen;
import com.aryston.arkea.screen.options.ArkVideoScreen;
import com.aryston.arkea.screen.options.keys.ArkKeyBindsScreen;
import com.aryston.arkea.screen.servers.ArkMultiplayerScreen;
import com.aryston.arkea.screen.title.ArkTitleScreen;
import com.aryston.arkea.screen.worlds.ArkCreateWorldScreen;
import com.aryston.arkea.screen.worlds.ArkWorldSelectScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
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
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.modlist.ModListScreen;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.Nullable;

public final class ClientEvents {
    private static final @Nullable UiCheck UI_CHECK = UiCheck.requested().orElse(null);
    private static final LoadingSkin LOADING_SKIN = new LoadingSkin();

    private ClientEvents() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ClientEvents::onScreenOpening);
        NeoForge.EVENT_BUS.addListener(ClientEvents::onClientTick);
        NeoForge.EVENT_BUS.addListener(ClientEvents::onScreenRender);
        NeoForge.EVENT_BUS.addListener(ClientEvents::onScreenKey);
        NeoForge.EVENT_BUS.addListener(ClientEvents::onScreenInit);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        MenuBackground.tick();
        if (UI_CHECK != null) {
            UI_CHECK.tick(Minecraft.getInstance());
        }
    }

    private static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!ArkeaConfig.MENU_SCREENS.get()) {
            return;
        }
        Button reconnect = Reconnect.button(event.getScreen());
        if (reconnect != null) {
            event.addListener(reconnect);
        }
    }

    private static void onScreenKey(ScreenEvent.KeyPressed.Pre event) {
        boolean shortcut = event.getKey() == InputConstants.KEY_K && event.getKeyEvent().hasControlDown();
        if (shortcut && ArkeaConfig.MENU_SCREENS.get() && !(event.getScreen() instanceof CommandPaletteScreen)) {
            event.setCanceled(true);
            Minecraft.getInstance().gui.pushScreenLayer(new CommandPaletteScreen());
        }
    }

    private static void onScreenRender(ScreenEvent.Render.Pre event) {
        Screen screen = event.getScreen();
        if (ArkeaConfig.MENU_SCREENS.get() && LoadingSkin.handles(screen)) {
            event.setCanceled(true);
            LOADING_SKIN.render(screen, event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
        }
    }

    private static void onScreenOpening(ScreenEvent.Opening event) {
        Screen screen = event.getNewScreen();
        if (screen.getClass() == TitleScreen.class && ArkeaConfig.TITLE_SCREEN.get() && !Minecraft.getInstance().isDemo()) {
            event.setNewScreen(new ArkTitleScreen());
        } else if (screen.getClass() == OptionsScreen.class && ArkeaConfig.OPTIONS_SCREEN.get()) {
            event.setNewScreen(new ArkOptionsScreen(((OptionsScreen) screen).getLastScreen()));
        } else if (screen.getClass() == SelectWorldScreen.class && ArkeaConfig.MENU_SCREENS.get()) {
            event.setNewScreen(new ArkWorldSelectScreen(((SelectWorldScreenAccessor) screen).arkea$lastScreen()));
        } else if (screen.getClass() == JoinMultiplayerScreen.class && ArkeaConfig.MENU_SCREENS.get()) {
            event.setNewScreen(new ArkMultiplayerScreen(((JoinMultiplayerScreenAccessor) screen).arkea$lastScreen()));
        } else if (screen.getClass() == CreateWorldScreen.class && ArkeaConfig.MENU_SCREENS.get()) {
            event.setNewScreen(new ArkCreateWorldScreen((CreateWorldScreen) screen));
        } else if (screen.getClass() == ModListScreen.class && ArkeaConfig.MENU_SCREENS.get()) {
            event.setNewScreen(new ArkModsScreen(((ModListScreenAccessor) screen).arkea$lastScreen()));
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
