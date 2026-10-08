package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.vanilla.VanillaTheme;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin {
    @Inject(method = "blit(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V", at = @At("HEAD"), cancellable = true)
    private void arkea$paintSeparator(RenderPipeline pipeline, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth,
        int textureHeight, CallbackInfo info) {
        boolean header = texture == Screen.HEADER_SEPARATOR || texture == Screen.INWORLD_HEADER_SEPARATOR;
        boolean footer = texture == Screen.FOOTER_SEPARATOR || texture == Screen.INWORLD_FOOTER_SEPARATOR;
        if ((header || footer) && VanillaTheme.active()) {
            VanillaTheme.separator((GuiGraphicsExtractor) (Object) this, x, y, width, height, header);
            info.cancel();
        }
    }
}
