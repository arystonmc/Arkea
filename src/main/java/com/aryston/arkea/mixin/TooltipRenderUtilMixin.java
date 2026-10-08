package com.aryston.arkea.mixin;

import com.aryston.arkea.tooltip.ArkTooltips;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TooltipRenderUtil.class)
public abstract class TooltipRenderUtilMixin {
    @Inject(method = "extractTooltipBackground", at = @At("HEAD"), cancellable = true)
    private static void arkea$drawCard(GuiGraphicsExtractor graphics, int x, int y, int width, int height, @Nullable Identifier style, CallbackInfo info) {
        if (ArkTooltips.styled(style)) {
            ArkTooltips.background(graphics, x, y, width, height);
            info.cancel();
        }
    }
}
