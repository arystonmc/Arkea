package com.aryston.arkea.integration;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.screen.options.ArkOptionsScreen;
import com.aryston.arkea.screen.title.ArkTitleScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class ClientEvents {
    private ClientEvents() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ClientEvents::onScreenOpening);
    }

    private static void onScreenOpening(ScreenEvent.Opening event) {
        Screen screen = event.getNewScreen();
        if (screen.getClass() == TitleScreen.class && ArkeaConfig.TITLE_SCREEN.get() && !Minecraft.getInstance().isDemo()) {
            event.setNewScreen(new ArkTitleScreen());
        } else if (screen.getClass() == OptionsScreen.class && ArkeaConfig.OPTIONS_SCREEN.get()) {
            event.setNewScreen(new ArkOptionsScreen(((OptionsScreen) screen).getLastScreen()));
        }
    }
}
