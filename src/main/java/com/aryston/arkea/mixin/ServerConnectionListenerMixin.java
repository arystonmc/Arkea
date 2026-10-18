package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.loading.LoadingFrames;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.netty.channel.ChannelFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.server.network.ServerConnectionListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerConnectionListener.class)
abstract class ServerConnectionListenerMixin {
    @WrapOperation(
        method = "startMemoryChannel",
        at = @At(value = "INVOKE", target = "Lio/netty/channel/ChannelFuture;syncUninterruptibly()Lio/netty/channel/ChannelFuture;")
    )
    private ChannelFuture arkea$drawWhileOpening(ChannelFuture future, Operation<ChannelFuture> original) {
        LoadingFrames.await(Minecraft.getInstance(), future);
        return original.call(future);
    }
}
