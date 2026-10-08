package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.vanilla.VanillaTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
    @Redirect(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V", ordinal = 0))
    private void arkea$paintInputBar(GuiGraphicsExtractor graphics, int left, int top, int right, int bottom, int color) {
        if (!VanillaTheme.active()) {
            graphics.fill(left, top, right, bottom, color);
            return;
        }
        VanillaTheme.chatBar(graphics, left, top, right - left, bottom - top);
    }
}
