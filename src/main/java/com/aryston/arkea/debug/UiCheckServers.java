package com.aryston.arkea.debug;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;

final class UiCheckServers {
    private static final String PREFIX = "Arkea Check ";
    private static final List<String> ADDRESSES = List.of("127.0.0.1:25599", "arkea-check.invalid", "localhost:25598");

    private UiCheckServers() {
    }

    static void add(Minecraft minecraft) {
        ServerList servers = new ServerList(minecraft);
        servers.load();
        for (int index = 0; index < ADDRESSES.size(); index++) {
            servers.add(new ServerData(PREFIX + (index + 1), ADDRESSES.get(index), ServerData.Type.OTHER), false);
        }
        servers.save();
    }

    static void remove(Minecraft minecraft) {
        ServerList servers = new ServerList(minecraft);
        servers.load();
        for (int index = servers.size() - 1; index >= 0; index--) {
            ServerData data = servers.get(index);
            if (data.name.startsWith(PREFIX)) {
                servers.remove(data);
            }
        }
        servers.save();
    }
}
