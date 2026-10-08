package com.aryston.arkea.mixin;

import com.aryston.arkea.hud.chat.ArkChat;
import com.aryston.arkea.hud.chat.ChatInput;
import com.aryston.arkea.screen.vanilla.VanillaTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
    private static final String SCROLL_CHAT = "Lnet/minecraft/client/gui/components/ChatComponent;scrollChat(I)V";

    @Shadow
    protected EditBox input;

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CommandSuggestions;updateCommandInfo()V"))
    private void arkea$placeInput(CallbackInfo info) {
        if (ArkChat.active()) {
            ChatInput.layout(this.input, ((Screen) (Object) this).height);
        }
    }

    @Redirect(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V", ordinal = 0))
    private void arkea$paintInputBar(GuiGraphicsExtractor graphics, int left, int top, int right, int bottom, int color) {
        if (ArkChat.active()) {
            ChatInput.paint(graphics, ((Screen) (Object) this).height);
        } else if (VanillaTheme.active()) {
            VanillaTheme.chatBar(graphics, left, top, right - left, bottom - top);
        } else {
            graphics.fill(left, top, right, bottom, color);
        }
    }

    @Redirect(method = {"mouseScrolled", "keyPressed"}, at = @At(value = "INVOKE", target = SCROLL_CHAT))
    private void arkea$scrollSmoothly(ChatComponent chat, int lines) {
        ArkChat.scroll(chat, lines);
    }
}
