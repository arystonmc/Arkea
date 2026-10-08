package com.aryston.arkea.debug;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.network.chat.Component;

final class UiCheckGame {
    private static final String SAMPLE_CAUSE = "Can was slain by Zombie";
    private static final String SAMPLE_COMMAND = "/";
    private static final List<String> SAMPLE_MESSAGES = List.of("Hey, the new chat looks great!",
        "This one is long on purpose, so it wraps onto a second line and shows how the text lines up next to the head.");
    private static final String SAMPLE_LATE_MESSAGE = "gg";
    private static final String SAMPLE_COMMAND_START = "t";

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

    static void messages(Minecraft minecraft) {
        minecraft.debugEntries.setOverlayVisible(false);
        if (minecraft.player == null) {
            return;
        }
        SAMPLE_MESSAGES.forEach(minecraft.player.connection::sendChat);
    }

    static void lateMessage(Minecraft minecraft) {
        UiCheckHud.slowClock();
        if (minecraft.player != null) {
            minecraft.player.connection.sendChat(SAMPLE_LATE_MESSAGE);
        }
    }

    static void typeSuggestion(ChatScreen screen) {
        screen.insertText(SAMPLE_COMMAND_START, false);
    }

    static void chat(Minecraft minecraft) {
        UiCheckHud.realTime(minecraft);
        minecraft.debugEntries.setOverlayVisible(false);
        minecraft.gui.setScreen(new ChatScreen(SAMPLE_COMMAND, false));
    }
}
