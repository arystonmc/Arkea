package com.aryston.arkea.hud.chat;

import com.aryston.arkea.hud.HudClock;
import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.util.Mth;

public final class ChatInput {
    static final int HEIGHT = 12;
    private static final int TEXT_TOP = 2;
    private static final int TEXT_LEFT = 6;
    private static final int PLATE_LEFT = 4;
    private static final int PLATE_RIGHT_PAD = 16;
    private static final int SUGGESTION_ANCHOR = 12;
    private static final int STRIPE_WIDTH = 1;
    private static final int PLATE = ArkColors.rgba(12, 12, 14, 0.88F);
    private static final float DIVIDER_SHARE = 0.4F;
    private static final Transition LIFT = new Transition(0.0F, Motion.CHAT_OPEN, Easing.STANDARD);

    private ChatInput() {
    }

    static int lift() {
        long now = HudClock.now();
        LIFT.setTarget(Minecraft.getInstance().gui.screen() instanceof ChatScreen ? 1.0F : 0.0F, now);
        return Math.round(LIFT.value(now) * HEIGHT);
    }

    public static void layout(EditBox input, int screenHeight) {
        Bar bar = bar(screenHeight);
        input.setX(Mth.floor((bar.left() + TEXT_LEFT) * bar.scale()));
        input.setY(Mth.floor(bar.top() * bar.scale()) + TEXT_TOP);
        input.setWidth(Mth.floor((bar.right() - bar.left() - TEXT_LEFT * 2) * bar.scale()));
    }

    public static void paint(GuiGraphicsExtractor graphics, int screenHeight) {
        Bar bar = bar(screenHeight);
        graphics.pose().pushMatrix();
        graphics.pose().scale(bar.scale(), bar.scale());
        graphics.fill(bar.left(), bar.top(), bar.right(), bar.bottom(), PLATE);
        graphics.fill(bar.left(), bar.top(), bar.left() + STRIPE_WIDTH, bar.bottom(), Theme.accent().light());
        graphics.fill(bar.left() + STRIPE_WIDTH, bar.top(), bar.right(), bar.top() + 1, ArkColors.withAlpha(Theme.accent().light(), DIVIDER_SHARE));
        graphics.pose().popMatrix();
    }

    public static int suggestionAnchor(EditBox input) {
        return input.getY() + SUGGESTION_ANCHOR;
    }

    private static Bar bar(int screenHeight) {
        ChatComponent chat = Minecraft.getInstance().gui.hud.getChat();
        float scale = (float) chat.getScale();
        int width = ArkChat.width(ChatComponent.getWidth(Minecraft.getInstance().options.chatWidth().get()));
        int maxWidth = Mth.ceil(width / scale);
        int bottom = screenHeight - ArkChat.base(ArkChat.VANILLA_BOTTOM);
        int top = Mth.floor((bottom - HEIGHT) / scale);
        return new Bar(scale, PLATE_LEFT, top, maxWidth + PLATE_RIGHT_PAD, top + Mth.ceil(HEIGHT / scale));
    }

    private record Bar(float scale, int left, int top, int right, int bottom) {
    }
}
