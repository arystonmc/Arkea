package com.aryston.arkea.hud.chat;

import com.aryston.arkea.hud.HudClock;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;

final class ChatStyle implements ChatComponent.ChatGraphicsAccess {
    static final int INDENT = 11;
    private static final int HEAD = 8;
    private static final int MESSAGE_HEIGHT = 9;
    private static final float INSET = 4.0F;
    private static final float SLIDE = 14.0F;
    private static final int PLATE_RGB = 0x0C0C0E;
    private static final float PLATE_SHARE = 0.85F;
    private static final float STRIPE_SHARE = 0.9F;
    private static final float DIVIDER_SHARE = 0.06F;
    private static final int STRIPE_WIDTH = 1;
    private static final int RGB_MASK = 0xFFFFFF;

    private final ChatComponent.ChatGraphicsAccess graphics;
    private final @Nullable GuiGraphicsExtractor drawing;
    private final long now;
    private final float rise;

    ChatStyle(ChatComponent.ChatGraphicsAccess graphics, @Nullable GuiGraphicsExtractor drawing) {
        this.graphics = graphics;
        this.drawing = drawing;
        this.now = HudClock.now();
        int entryHeight = (int) (MESSAGE_HEIGHT * (Minecraft.getInstance().options.chatLineSpacing().get() + 1.0));
        this.rise = ChatEntries.risingLines(this.now) * entryHeight;
    }

    @Override
    public void updatePose(Consumer<Matrix3x2f> updater) {
        this.graphics.updatePose(pose -> {
            updater.accept(pose);
            pose.translate(INSET, 0.0F);
        });
    }

    @Override
    public void fill(int x0, int y0, int x1, int y1, int color) {
        if ((color & RGB_MASK) != 0) {
            this.graphics.fill(x0, y0, x1, y1, ArkColors.withAlpha(Theme.accent().light(), ArkColors.alpha(color)));
            return;
        }
        GuiMessage.Line line = ArkChat.line();
        if (line == null) {
            this.plate(x0, y0, x1, y1, ArkColors.alpha(color), Theme.accent().light(), false);
            return;
        }
        ChatEntry entry = ChatEntries.entry(line.parent());
        float appear = entry.appear(this.now);
        ChatSender sender = entry.sender();
        int stripe = sender != null ? sender.color() : Theme.accent().light();
        this.shifted(this.slide(appear), this.rise, () -> this.plate(x0, y0, x1, y1, ArkColors.alpha(color) * appear, stripe, entry.startsWith(line.content())));
    }

    @Override
    public boolean handleMessage(int textTop, float opacity, FormattedCharSequence message) {
        GuiMessage.Line line = ArkChat.line();
        if (line == null) {
            return this.graphics.handleMessage(textTop, opacity, message);
        }
        ChatEntry entry = ChatEntries.entry(line.parent());
        float appear = entry.appear(this.now);
        ChatSender sender = entry.sender();
        float indent = sender != null ? INDENT : 0.0F;
        boolean[] hovered = new boolean[1];
        this.shifted(this.slide(appear), this.rise, () -> {
            if (sender != null && this.drawing != null && entry.startsWith(line.content())) {
                sender.drawFace(this.drawing, 0, textTop, HEAD, ARGB.white(opacity * appear));
            }
            this.shifted(indent, 0.0F, () -> hovered[0] = this.graphics.handleMessage(textTop, opacity * appear, message));
        });
        return hovered[0];
    }

    @Override
    public void handleTag(int x0, int y0, int x1, int y1, float opacity, GuiMessageTag tag) {
        GuiMessage.Line line = ArkChat.line();
        float appear = line != null ? ChatEntries.entry(line.parent()).appear(this.now) : 1.0F;
        this.shifted(this.slide(appear), line != null ? this.rise : 0.0F, () -> this.graphics.handleTag(x0, y0, x1, y1, opacity * appear, tag));
    }

    @Override
    public void handleTagIcon(int left, int bottom, boolean forceVisible, GuiMessageTag tag, GuiMessageTag.Icon icon) {
        GuiMessage.Line line = ArkChat.line();
        if (line == null) {
            this.graphics.handleTagIcon(left, bottom, forceVisible, tag, icon);
            return;
        }
        ChatEntry entry = ChatEntries.entry(line.parent());
        float indent = entry.sender() != null ? INDENT : 0.0F;
        this.shifted(this.slide(entry.appear(this.now)) + indent, this.rise, () -> this.graphics.handleTagIcon(left, bottom, forceVisible, tag, icon));
    }

    private float slide(float appear) {
        return -(1.0F - appear) * SLIDE;
    }

    private void plate(int x0, int y0, int x1, int y1, float alpha, int stripe, boolean firstLine) {
        this.graphics.fill(x0, y0, x1, y1, ArkColors.withAlpha(ArkColors.rgb(PLATE_RGB), alpha * PLATE_SHARE));
        this.graphics.fill(x0, y0, x0 + STRIPE_WIDTH, y1, ArkColors.withAlpha(stripe, alpha * STRIPE_SHARE));
        if (firstLine) {
            this.graphics.fill(x0 + STRIPE_WIDTH, y0, x1, y0 + 1, ArkColors.withAlpha(ArkColors.TEXT_PRIMARY, alpha * DIVIDER_SHARE));
        }
    }

    private void shifted(float x, float y, Runnable draw) {
        if (x == 0.0F && y == 0.0F) {
            draw.run();
            return;
        }
        this.graphics.updatePose(pose -> pose.translate(x, y));
        draw.run();
        this.graphics.updatePose(pose -> pose.translate(-x, -y));
    }
}
