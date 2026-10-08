package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.vanilla.VanillaTheme;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.tabs.MenuTabBar;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MenuTabBar.MenuTabButton.class)
public abstract class MenuTabButtonMixin {
    @Redirect(method = "extractWidgetRenderState", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void arkea$paintTab(GuiGraphicsExtractor graphics, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height) {
        if (!VanillaTheme.active()) {
            graphics.blitSprite(pipeline, sprite, x, y, width, height);
            return;
        }
        MenuTabBar.MenuTabButton tab = (MenuTabBar.MenuTabButton) (Object) this;
        VanillaTheme.tab(graphics, tab, x, y, width, height, tab.isHoveredOrFocused() && !tab.isSelected());
    }

    @Inject(method = "renderMenuBackground", at = @At("HEAD"), cancellable = true)
    private void arkea$skipTabBackground(GuiGraphicsExtractor graphics, int left, int top, int right, int bottom, CallbackInfo info) {
        if (VanillaTheme.active()) {
            info.cancel();
        }
    }

    @Inject(method = "renderFocusUnderline", at = @At("HEAD"), cancellable = true)
    private void arkea$paintUnderline(GuiGraphicsExtractor graphics, Font font, int color, CallbackInfo info) {
        if (VanillaTheme.active()) {
            MenuTabBar.MenuTabButton tab = (MenuTabBar.MenuTabButton) (Object) this;
            VanillaTheme.tabUnderline(graphics, tab.getX(), tab.getY(), tab.getWidth(), tab.getHeight());
            info.cancel();
        }
    }
}
