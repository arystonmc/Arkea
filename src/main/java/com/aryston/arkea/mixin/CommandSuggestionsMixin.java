package com.aryston.arkea.mixin;

import com.aryston.arkea.hud.chat.ArkChat;
import com.aryston.arkea.hud.chat.ChatInput;
import com.aryston.arkea.screen.vanilla.VanillaTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandSuggestions.class)
public abstract class CommandSuggestionsMixin {
    @Shadow
    @Final
    private Screen screen;
    @Shadow
    @Final
    private EditBox input;

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void arkea$drawAboveChat(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo info) {
        if (this.screen instanceof ChatScreen && ArkChat.active()) {
            graphics.nextStratum();
        }
    }

    @Redirect(method = "extractUsage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"))
    private void arkea$paintUsage(GuiGraphicsExtractor graphics, int left, int top, int right, int bottom, int color) {
        if (!VanillaTheme.active()) {
            graphics.fill(left, top, right, bottom, color);
            return;
        }
        VanillaTheme.chatBar(graphics, left, top, right - left, bottom - top);
    }

    @Redirect(method = {"showSuggestions", "extractUsage"},
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/Screen;height:I", opcode = Opcodes.GETFIELD))
    private int arkea$anchorToInput(Screen screen) {
        return screen instanceof ChatScreen && ArkChat.active() ? ChatInput.suggestionAnchor(this.input) : screen.height;
    }
}
