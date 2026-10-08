package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.toasts.GameToasts;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvancementToast.class)
public abstract class AdvancementToastMixin implements Toast {
    @Shadow
    @Final
    private AdvancementHolder advancement;
    @Shadow
    @Final
    private ItemStack iconItem;

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void arkea$render(GuiGraphicsExtractor graphics, Font font, long fullyVisibleForMs, CallbackInfo info) {
        if (GameToasts.enabled()) {
            GameToasts.advancement(this.advancement, this.iconItem).paint(graphics, font);
            info.cancel();
        }
    }

    @Override
    public int width() {
        return GameToasts.enabled() ? GameToasts.advancement(this.advancement, this.iconItem).guiWidth() : DEFAULT_WIDTH;
    }

    @Override
    public int height() {
        return GameToasts.enabled() ? GameToasts.advancement(this.advancement, this.iconItem).guiHeight() : SLOT_HEIGHT;
    }
}
