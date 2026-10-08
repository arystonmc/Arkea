package com.aryston.arkea.mixin;

import com.aryston.arkea.hud.extra.Crosshair;
import net.minecraft.client.gui.Hud;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Hud.class)
public abstract class HudCrosshairMixin {
    @ModifyArg(method = "extractCrosshair", index = 1, at = @At(value = "INVOKE", ordinal = 0,
        target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private Identifier arkea$crosshairSprite(Identifier sprite) {
        return Crosshair.sprite(sprite);
    }
}
