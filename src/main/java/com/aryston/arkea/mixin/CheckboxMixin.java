package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.vanilla.VanillaTheme;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Checkbox.class)
public abstract class CheckboxMixin {
    @Redirect(method = "extractContents", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIII)V"))
    private void arkea$paintBox(GuiGraphicsExtractor graphics, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, int color) {
        if (!VanillaTheme.active()) {
            graphics.blitSprite(pipeline, sprite, x, y, width, height, color);
            return;
        }
        Checkbox checkbox = (Checkbox) (Object) this;
        VanillaTheme.checkbox(graphics, checkbox, x, y, width, checkbox.selected(), checkbox.isHoveredOrFocused());
    }
}
