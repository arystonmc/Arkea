package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.toasts.GameToasts;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SystemToast.class)
public abstract class SystemToastMixin {
    @Shadow
    @Final
    private SystemToast.SystemToastId id;
    @Shadow
    private List<FormattedCharSequence> titleLines;
    @Shadow
    private List<FormattedCharSequence> messageLines;

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void arkea$render(GuiGraphicsExtractor graphics, Font font, long fullyVisibleForMs, CallbackInfo info) {
        if (GameToasts.enabled()) {
            GameToasts.system(this.id, this.titleLines, this.messageLines).paint(graphics, font);
            info.cancel();
        }
    }

    @Inject(method = "width", at = @At("HEAD"), cancellable = true)
    private void arkea$width(CallbackInfoReturnable<Integer> info) {
        if (GameToasts.enabled()) {
            info.setReturnValue(GameToasts.system(this.id, this.titleLines, this.messageLines).guiWidth());
        }
    }

    @Inject(method = "height", at = @At("HEAD"), cancellable = true)
    private void arkea$height(CallbackInfoReturnable<Integer> info) {
        if (GameToasts.enabled()) {
            info.setReturnValue(GameToasts.system(this.id, this.titleLines, this.messageLines).guiHeight());
        }
    }
}
