package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.debug.DebugCards;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenOverlayMixin {
    @Inject(method = "extractLines", at = @At("HEAD"), cancellable = true)
    private void arkea$drawCards(GuiGraphicsExtractor graphics, List<String> lines, boolean alignLeft, int scaledScreenWidth, CallbackInfo info) {
        if (DebugCards.active()) {
            DebugCards.draw(graphics, lines, alignLeft, scaledScreenWidth);
            info.cancel();
        }
    }
}
