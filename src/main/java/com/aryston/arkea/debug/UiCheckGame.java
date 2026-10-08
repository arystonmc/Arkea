package com.aryston.arkea.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.network.chat.Component;

final class UiCheckGame {
    private static final String SAMPLE_CAUSE = "Can was slain by Zombie";
    private static final String SAMPLE_COMMAND = "/gamemode ";

    private UiCheckGame() {
    }

    static void stats(Minecraft minecraft) {
        if (minecraft.player != null) {
            minecraft.gui.setScreen(new StatsScreen(new PauseScreen(true), minecraft.player.getStats()));
        }
    }

    static void death(Minecraft minecraft) {
        if (minecraft.player != null) {
            minecraft.gui.setScreen(new DeathScreen(Component.literal(SAMPLE_CAUSE), false, minecraft.player));
        }
    }

    static void debug(Minecraft minecraft) {
        minecraft.gui.setScreen(null);
        minecraft.debugEntries.setOverlayVisible(true);
    }

    static void chat(Minecraft minecraft) {
        minecraft.debugEntries.setOverlayVisible(false);
        minecraft.gui.setScreen(new ChatScreen(SAMPLE_COMMAND, false));
    }
}
