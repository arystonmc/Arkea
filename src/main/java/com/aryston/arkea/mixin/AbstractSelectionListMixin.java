package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.vanilla.VanillaTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin {
    private static final int SEPARATOR_HEIGHT = 2;

    @Inject(method = "extractListBackground", at = @At("HEAD"), cancellable = true)
    private void arkea$paintBackground(GuiGraphicsExtractor graphics, CallbackInfo info) {
        if (VanillaTheme.active()) {
            AbstractSelectionList<?> list = (AbstractSelectionList<?>) (Object) this;
            VanillaTheme.listBackground(graphics, list.getX(), list.getY(), list.getWidth(), list.getHeight());
            info.cancel();
        }
    }

    @Inject(method = "extractListSeparators", at = @At("HEAD"), cancellable = true)
    private void arkea$paintSeparators(GuiGraphicsExtractor graphics, CallbackInfo info) {
        if (VanillaTheme.active()) {
            AbstractSelectionList<?> list = (AbstractSelectionList<?>) (Object) this;
            VanillaTheme.separator(graphics, list.getX(), list.getY() - SEPARATOR_HEIGHT, list.getWidth(), SEPARATOR_HEIGHT, true);
            VanillaTheme.separator(graphics, list.getX(), list.getBottom(), list.getWidth(), SEPARATOR_HEIGHT, false);
            info.cancel();
        }
    }
}
