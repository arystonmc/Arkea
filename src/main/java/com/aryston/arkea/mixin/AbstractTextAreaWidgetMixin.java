package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.vanilla.VanillaTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractTextAreaWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractTextAreaWidget.class)
public abstract class AbstractTextAreaWidgetMixin {
    @Inject(method = "extractBorder", at = @At("HEAD"), cancellable = true)
    private void arkea$paintBorder(GuiGraphicsExtractor graphics, int x, int y, int width, int height, CallbackInfo info) {
        if (VanillaTheme.active()) {
            AbstractTextAreaWidget area = (AbstractTextAreaWidget) (Object) this;
            VanillaTheme.textField(graphics, x, y, width, height, area.isActive(), area.isFocused());
            info.cancel();
        }
    }
}
