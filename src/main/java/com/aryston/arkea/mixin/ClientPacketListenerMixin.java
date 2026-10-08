package com.aryston.arkea.mixin;

import com.aryston.arkea.hud.extra.PickupFeed;
import com.aryston.arkea.screen.game.ArkStatsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Shadow
    private ClientLevel level;

    @Inject(method = "handleTakeItemEntity", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
        shift = At.Shift.AFTER))
    private void arkea$recordPickup(ClientboundTakeItemEntityPacket packet, CallbackInfo info) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || this.level == null || packet.getPlayerId() != player.getId()) {
            return;
        }
        Entity taken = this.level.getEntity(packet.getItemId());
        if (taken instanceof ItemEntity item) {
            PickupFeed.add(item.getItem(), packet.getAmount());
        } else if (taken instanceof ExperienceOrb orb) {
            PickupFeed.experience(orb.getValue());
        }
    }

    @Inject(method = "handleAwardStats", at = @At("TAIL"))
    private void arkea$updateStatsScreen(ClientboundAwardStatsPacket packet, CallbackInfo info) {
        if (Minecraft.getInstance().gui.screen() instanceof ArkStatsScreen screen) {
            screen.onStatsUpdated();
        }
    }
}
