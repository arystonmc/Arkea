package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.vanilla.VanillaTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.CommandSuggestions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandSuggestions.class)
public abstract class CommandSuggestionsMixin {
    @Redirect(method = "extractUsage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"))
    private void arkea$paintUsage(GuiGraphicsExtractor graphics, int left, int top, int right, int bottom, int color) {
        if (!VanillaTheme.active()) {
            graphics.fill(left, top, right, bottom, color);
            return;
        }
        VanillaTheme.chatBar(graphics, left, top, right - left, bottom - top);
    }
}
