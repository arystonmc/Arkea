package com.aryston.arkea.screen.title;

import com.mojang.authlib.minecraft.BanDetails;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

final class MultiplayerAccess {
    private MultiplayerAccess() {
    }

    static @Nullable Component blockedReason(Minecraft minecraft) {
        if (minecraft.allowsMultiplayer()) {
            return null;
        }
        if (minecraft.isNameBanned()) {
            return Component.translatable("title.multiplayer.disabled.banned.name");
        }
        BanDetails ban = minecraft.multiplayerBan();
        if (ban == null) {
            return Component.translatable("title.multiplayer.disabled");
        }
        String key = ban.expires() != null ? "title.multiplayer.disabled.banned.temporary" : "title.multiplayer.disabled.banned.permanent";
        return Component.translatable(key);
    }
}
