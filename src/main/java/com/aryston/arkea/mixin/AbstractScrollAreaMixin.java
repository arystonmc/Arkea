package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.vanilla.VanillaTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractScrollArea;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractScrollArea.class)
public abstract class AbstractScrollAreaMixin {
    @Shadow
    protected abstract boolean scrollable();

    @Shadow
    protected abstract int scrollBarX();

    @Shadow
    protected abstract int scrollerHeight();

    @Shadow
    public abstract int scrollBarY();

    @Shadow
    public abstract int scrollbarWidth();

    @Shadow
    protected abstract boolean isOverScrollbar(double x, double y);

    @Inject(method = "extractScrollbar", at = @At("HEAD"), cancellable = true)
    private void arkea$paintScrollbar(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo info) {
        if (!VanillaTheme.active()) {
            return;
        }
        info.cancel();
        if (!this.scrollable()) {
            return;
        }
        AbstractScrollArea area = (AbstractScrollArea) (Object) this;
        VanillaTheme.scrollbar(graphics, area, this.scrollBarX(), area.getY(), this.scrollbarWidth(), area.getHeight(), this.scrollBarY(),
            this.scrollerHeight(), this.isOverScrollbar(mouseX, mouseY));
    }
}
