package com.aryston.arkea.mixin;

import com.aryston.arkea.hud.chat.ArkChat;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {
    private static final String EXTRACT =
        "extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V";
    private static final String DRAW =
        "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V";
    private static final String SPLIT = "Lnet/minecraft/client/multiplayer/chat/GuiMessage;splitLines(Lnet/minecraft/client/gui/Font;I)Ljava/util/List;";

    @Inject(method = DRAW, at = @At("HEAD"))
    private void arkea$beginDrawing(GuiGraphicsExtractor graphics, Font font, int ticks, int mouseX, int mouseY, ChatComponent.DisplayMode displayMode,
        boolean changeCursorOnInsertions, CallbackInfo info) {
        ArkChat.begin(graphics);
    }

    @Inject(method = DRAW, at = @At("RETURN"))
    private void arkea$endDrawing(GuiGraphicsExtractor graphics, Font font, int ticks, int mouseX, int mouseY, ChatComponent.DisplayMode displayMode,
        boolean changeCursorOnInsertions, CallbackInfo info) {
        ArkChat.end();
    }

    @ModifyVariable(method = EXTRACT, at = @At("HEAD"), argsOnly = true)
    private ChatComponent.ChatGraphicsAccess arkea$style(ChatComponent.ChatGraphicsAccess graphics) {
        return ArkChat.wrap(graphics);
    }

    @ModifyConstant(method = EXTRACT, constant = @Constant(intValue = 40))
    private int arkea$bottom(int bottom) {
        return ArkChat.bottom(bottom);
    }

    @ModifyVariable(method = "forEachLine", at = @At("STORE"))
    private GuiMessage.Line arkea$enterLine(GuiMessage.Line line) {
        return ArkChat.enter(line);
    }

    @Inject(method = "forEachLine", at = @At("RETURN"))
    private void arkea$leaveLines(CallbackInfoReturnable<Integer> info) {
        ArkChat.leave();
    }

    @Redirect(method = "addMessageToDisplayQueue", at = @At(value = "INVOKE", target = SPLIT))
    private List<FormattedCharSequence> arkea$split(GuiMessage message, Font font, int maxWidth) {
        return ArkChat.split(message, font, maxWidth);
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
