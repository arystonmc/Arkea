package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.game.ArkStatsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Inject(method = "handleAwardStats", at = @At("TAIL"))
    private void arkea$updateStatsScreen(ClientboundAwardStatsPacket packet, CallbackInfo info) {
        if (Minecraft.getInstance().gui.screen() instanceof ArkStatsScreen screen) {
            screen.onStatsUpdated();
        }
    }
}
