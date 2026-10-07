package com.aryston.arkea.debug;

import com.aryston.arkea.screen.loading.JoinTarget;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;

final class UiCheckLoading {
    private static final String UNROUTABLE = "10.255.255.1";
    private static final String SERVER_NAME = "Arkea Check";
    private static final int SAMPLE_PROGRESS = 64;

    private UiCheckLoading() {
    }

    static Screen progress() {
        ProgressScreen screen = new ProgressScreen(false);
        screen.progressStartNoAbort(Component.translatable("menu.savingLevel"));
        screen.progressStage(Component.translatable("menu.savingChunks"));
        screen.progressStagePercentage(SAMPLE_PROGRESS);
        return screen;
    }

    static void connect(Minecraft minecraft) {
        JoinTarget.set(SERVER_NAME);
        ServerData data = new ServerData(SERVER_NAME, UNROUTABLE, ServerData.Type.OTHER);
        ConnectScreen.startConnecting(new TitleScreen(), minecraft, ServerAddress.parseString(UNROUTABLE), data, false, null);
    }

    static void cancel(Minecraft minecraft) {
        Screen screen = minecraft.gui.screen();
        if (screen == null) {
            return;
        }
        for (var child : screen.children()) {
            if (child instanceof Button button) {
                button.onPress(new KeyEvent(InputConstants.KEY_RETURN, 0, 0));
                return;
            }
        }
    }
}
