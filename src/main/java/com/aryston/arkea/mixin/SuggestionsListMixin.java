package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.vanilla.VanillaTheme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.Rect2i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.components.CommandSuggestions$SuggestionsList")
public abstract class SuggestionsListMixin {
    private static final String RENDER = "extractRenderState";
    private static final int ROW_HEIGHT = 12;
    private static final int MORE_MARKER = -1;
    private static final int SELECTED_TEXT = -256;
    private static final int TEXT_SHIFT = 3;

    @Shadow
    @Final
    private Rect2i rect;
    @Shadow
    private int offset;
    @Shadow
    private int current;

    @Inject(method = RENDER, at = @At("HEAD"))
    private void arkea$paintPanel(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo info) {
        if (VanillaTheme.active()) {
            VanillaTheme.suggestions(graphics, this.rect.getX(), this.rect.getY(), this.rect.getWidth(), this.rect.getHeight(),
                (this.current - this.offset) * ROW_HEIGHT, ROW_HEIGHT);
        }
    }

    @Redirect(method = RENDER, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"))
    private void arkea$paintRows(GuiGraphicsExtractor graphics, int left, int top, int right, int bottom, int color) {
        if (!VanillaTheme.active()) {
            graphics.fill(left, top, right, bottom, color);
        } else if (color == MORE_MARKER) {
            graphics.fill(left, top, right, bottom, VanillaTheme.suggestionMarker());
        }
    }

    @Redirect(method = RENDER, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V"))
    private void arkea$paintText(GuiGraphicsExtractor graphics, Font font, String text, int x, int y, int color) {
        if (!VanillaTheme.active()) {
            graphics.text(font, text, x, y, color);
            return;
        }
        graphics.text(font, text, x + TEXT_SHIFT, y, VanillaTheme.suggestionText(color == SELECTED_TEXT));
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Rect2i;<init>(IIII)V"), index = 2)
    private int arkea$widenForShift(int width) {
        return VanillaTheme.active() ? width + TEXT_SHIFT : width;
    }
}
