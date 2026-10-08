package com.aryston.arkea.mixin;

import com.aryston.arkea.hud.ArkChat;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {
    private static final String EXTRACT =
        "extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V";

    @ModifyVariable(method = EXTRACT, at = @At("HEAD"), argsOnly = true)
    private ChatComponent.ChatGraphicsAccess arkea$style(ChatComponent.ChatGraphicsAccess graphics) {
        return ArkChat.wrap(graphics);
    }

    @ModifyConstant(method = EXTRACT, constant = @Constant(intValue = 40))
    private int arkea$bottom(int bottom) {
        return ArkChat.bottom(bottom);
    }

    @Inject(method = "getWidth()I", at = @At("RETURN"), cancellable = true)
    private void arkea$width(CallbackInfoReturnable<Integer> info) {
        info.setReturnValue(ArkChat.width(info.getReturnValue()));
    }

    @Inject(method = "getScale", at = @At("RETURN"), cancellable = true)
    private void arkea$scale(CallbackInfoReturnable<Double> info) {
        info.setReturnValue(ArkChat.scale(info.getReturnValue()));
    }
}
