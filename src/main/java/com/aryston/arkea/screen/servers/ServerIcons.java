package com.aryston.arkea.screen.servers;

import com.aryston.arkea.Arkea;
import com.mojang.blaze3d.platform.NativeImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

final class ServerIcons implements AutoCloseable {
    private final Minecraft minecraft;
    private final Map<String, Entry> icons = new HashMap<>();

    ServerIcons(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Nullable Identifier icon(ServerData data) {
        byte[] bytes = data.getIconBytes();
        Entry entry = this.icons.computeIfAbsent(data.ip, ip -> new Entry(FaviconTexture.forServer(this.minecraft.getTextureManager(), ip)));
        if (!Arrays.equals(bytes, entry.bytes)) {
            entry.bytes = bytes;
            entry.loaded = upload(entry.texture, bytes, data);
        }
        return entry.loaded ? entry.texture.textureLocation() : null;
    }

    private static boolean upload(FaviconTexture texture, byte @Nullable [] bytes, ServerData data) {
        if (bytes == null) {
            texture.clear();
            return false;
        }
        try {
            texture.upload(NativeImage.read(bytes));
            return true;
        } catch (Exception exception) {
            Arkea.LOGGER.warn("Invalid icon for server {} ({})", data.name, data.ip, exception);
            return false;
        }
    }

    @Override
    public void close() {
        this.icons.values().forEach(entry -> entry.texture.close());
        this.icons.clear();
    }

    private static final class Entry {
        private final FaviconTexture texture;
        private byte @Nullable [] bytes;
        private boolean loaded;

        Entry(FaviconTexture texture) {
            this.texture = texture;
        }
    }
}
