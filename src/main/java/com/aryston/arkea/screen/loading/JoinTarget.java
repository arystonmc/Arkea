package com.aryston.arkea.screen.loading;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class JoinTarget {
    private static @Nullable Component name;
    private static @Nullable ServerData server;

    private JoinTarget() {
    }

    public static void set(String targetName) {
        name = Component.literal(targetName);
        server = null;
    }

    public static void server(ServerData data) {
        name = Component.literal(data.name);
        server = data;
    }

    public static @Nullable ServerData lastServer() {
        return server;
    }

    static @Nullable Component name() {
        return name;
    }
}
