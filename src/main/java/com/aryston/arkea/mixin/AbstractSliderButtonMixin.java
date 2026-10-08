package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.vanilla.VanillaTheme;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractSliderButton.class)
public abstract class AbstractSliderButtonMixin {
    private static final String BLIT_SPRITE =
        "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIII)V";

    @Shadow
    protected double value;

    @Redirect(method = "extractWidgetRenderState", at = @At(value = "INVOKE", target = BLIT_SPRITE, ordinal = 0))
    private void arkea$paintTrack(GuiGraphicsExtractor graphics, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height,
        int color) {
        AbstractSliderButton slider = (AbstractSliderButton) (Object) this;
        if (!VanillaTheme.active()) {
            graphics.blitSprite(pipeline, sprite, x, y, width, height, color);
            return;
        }
        VanillaTheme.sliderTrack(graphics, slider, x, y, width, height, this.value, slider.active, slider.isHoveredOrFocused());
    }

    @Redirect(method = "extractWidgetRenderState", at = @At(value = "INVOKE", target = BLIT_SPRITE, ordinal = 1))
    private void arkea$paintHandle(GuiGraphicsExtractor graphics, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height,
        int color) {
        if (!VanillaTheme.active()) {
            graphics.blitSprite(pipeline, sprite, x, y, width, height, color);
            return;
        }
        VanillaTheme.sliderHandle(graphics, x, y, width, height, ((AbstractSliderButton) (Object) this).active);
    }
}
