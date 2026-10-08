package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.vanilla.VanillaTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractButton.class)
public abstract class AbstractButtonMixin {
    @Inject(method = "extractDefaultSprite", at = @At("HEAD"), cancellable = true)
    private void arkea$paintButton(GuiGraphicsExtractor graphics, CallbackInfo info) {
        if (!VanillaTheme.active()) {
            return;
        }
        AbstractButton button = (AbstractButton) (Object) this;
        VanillaTheme.button(graphics, button, button.getMessage(), button.getX(), button.getY(), button.getWidth(), button.getHeight(), button.active,
            button.isHoveredOrFocused());
        info.cancel();
    }
}
