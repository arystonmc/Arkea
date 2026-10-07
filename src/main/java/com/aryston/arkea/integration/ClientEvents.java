package com.aryston.arkea.integration;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.screen.title.ArkTitleScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class ClientEvents {
    private ClientEvents() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ClientEvents::onScreenOpening);
    }

    private static void onScreenOpening(ScreenEvent.Opening event) {
        if (event.getNewScreen().getClass() == TitleScreen.class && ArkeaConfig.TITLE_SCREEN.get() && !Minecraft.getInstance().isDemo()) {
            event.setNewScreen(new ArkTitleScreen());
        }
    }
}
