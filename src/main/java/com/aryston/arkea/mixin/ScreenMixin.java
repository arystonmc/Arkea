package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.vanilla.VanillaTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Inject(method = "extractMenuBackgroundTexture", at = @At("HEAD"), cancellable = true)
    private static void arkea$paintMenuBackground(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, float u, float v, int width, int height,
        CallbackInfo info) {
        if (VanillaTheme.active()) {
            VanillaTheme.menuBackground(graphics, x, y, width, height);
            info.cancel();
        }
    }
}
