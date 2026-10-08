package com.aryston.arkea.screen.loading;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class Reconnect {
    private Reconnect() {
    }

    public static @Nullable Button button(Screen screen) {
        ServerData server = JoinTarget.lastServer();
        if (!(screen instanceof DisconnectedScreen) || server == null) {
            return null;
        }
        return Button.builder(Component.translatable("arkea.loading.reconnect"), button -> {
            Minecraft minecraft = Minecraft.getInstance();
            ConnectScreen.startConnecting(new JoinMultiplayerScreen(new TitleScreen()), minecraft, ServerAddress.parseString(server.ip), server, false, null);
        }).build();
    }
}
