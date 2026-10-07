package com.aryston.arkea.mixin;

import com.aryston.arkea.background.MenuBackground;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.Panorama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Panorama.class)
public abstract class PanoramaMixin {
    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void arkea$drawCustomBackground(GuiGraphicsExtractor graphics, int width, int height, CallbackInfo info) {
        if (MenuBackground.draw(graphics, width, height)) {
            info.cancel();
        }
    }
}
