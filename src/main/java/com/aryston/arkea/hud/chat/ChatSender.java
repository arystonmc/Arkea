package com.aryston.arkea.hud.chat;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.world.entity.player.PlayerSkin;

public record ChatSender(UUID id, int color) {
    public void drawFace(GuiGraphicsExtractor graphics, int x, int y, int size, int tint) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        PlayerInfo info = connection != null ? connection.getPlayerInfo(this.id) : null;
        PlayerSkin skin = info != null ? info.getSkin() : DefaultPlayerSkin.get(this.id);
        boolean hat = info == null || info.showHat();
        PlayerFaceExtractor.extractRenderState(graphics, skin.body().texturePath(), x, y, size, hat, false, tint);
    }
}
