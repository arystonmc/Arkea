package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.toasts.GameToasts;
import com.aryston.arkea.ui.overlay.GameToast;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TutorialToast.class)
public abstract class TutorialToastMixin implements Toast {
    @Shadow
    @Final
    private TutorialToast.Icons icon;
    @Shadow
    @Final
    private List<FormattedCharSequence> lines;
    @Shadow
    @Final
    private boolean progressable;
    @Shadow
    private float smoothedProgress;

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void arkea$render(GuiGraphicsExtractor graphics, Font font, long fullyVisibleForMs, CallbackInfo info) {
        if (GameToasts.enabled()) {
            this.arkea$toast().paint(graphics, font);
            info.cancel();
        }
    }

    @Inject(method = "height", at = @At("HEAD"), cancellable = true)
    private void arkea$height(CallbackInfoReturnable<Integer> info) {
        if (GameToasts.enabled()) {
            info.setReturnValue(this.arkea$toast().guiHeight());
        }
    }

    private GameToast arkea$toast() {
        return GameToasts.tutorial(this.icon, this.lines, this.progressable, this.smoothedProgress);
    }

    @Override
    public int width() {
        return GameToasts.enabled() ? this.arkea$toast().guiWidth() : DEFAULT_WIDTH;
    }
}
