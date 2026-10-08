package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.toasts.GameToasts;
import com.aryston.arkea.ui.overlay.GameToast;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeToast.class)
public abstract class RecipeToastMixin implements Toast {
    @Shadow
    @Final
    private List<?> recipeItems;
    @Shadow
    private int displayedRecipeIndex;

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void arkea$render(GuiGraphicsExtractor graphics, Font font, long fullyVisibleForMs, CallbackInfo info) {
        if (this.arkea$styled()) {
            this.arkea$toast().paint(graphics, font);
            info.cancel();
        }
    }

    private boolean arkea$styled() {
        return GameToasts.enabled() && !this.recipeItems.isEmpty();
    }

    private GameToast arkea$toast() {
        Object entry = this.recipeItems.get(Math.min(this.displayedRecipeIndex, this.recipeItems.size() - 1));
        ItemStack item = ((RecipeToastEntryAccessor) entry).arkea$unlockedItem();
        return GameToasts.recipe(item);
    }

    @Override
    public int width() {
        return this.arkea$styled() ? this.arkea$toast().guiWidth() : DEFAULT_WIDTH;
    }

    @Override
    public int height() {
        return this.arkea$styled() ? this.arkea$toast().guiHeight() : SLOT_HEIGHT;
    }
}
