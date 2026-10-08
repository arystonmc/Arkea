package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.mojang.blaze3d.platform.Window;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;

public record GameToast(List<FormattedCharSequence> title, int titleColor, List<FormattedCharSequence> message, GameToastIcon icon, float progress) {
    public static final float NO_PROGRESS = Float.NaN;
    private static final float MIN_WIDTH = 300.0F;
    private static final float MAX_WIDTH = 420.0F;
    private static final float MIN_HEIGHT = 56.0F;
    private static final float PADDING_LEFT = 8.0F;
    private static final float PADDING_RIGHT = 12.0F;
    private static final float PADDING_Y = 10.0F;
    private static final float TILE = 40.0F;
    private static final float GAP = 12.0F;
    private static final float TITLE_LINE = 14.0F;
    private static final float MESSAGE_LINE = 13.0F;
    private static final float PROGRESS_HEIGHT = 2.0F;
    private static final float SHADOW_BLUR = 24.0F;
    private static final float SHADOW_OFFSET = 8.0F;
    private static final int FILL = ArkColors.rgba(14, 14, 15, 0.92F);
    private static final int BORDER = ArkColors.rgba(255, 255, 255, 0.14F);
    private static final int TILE_FILL = ArkColors.rgba(255, 255, 255, 0.05F);
    private static final int TILE_BORDER = ArkColors.rgba(255, 255, 255, 0.10F);
    private static final int SHADOW = ArkColors.rgba(0, 0, 0, 0.5F);
    private static final int PROGRESS_TRACK = ArkColors.rgba(255, 255, 255, 0.08F);
    private static final TextStyle TITLE = TextStyle.of(12.0F);
    private static final TextStyle MESSAGE = TextStyle.of(11.0F);

    public int guiWidth() {
        UiScale scale = scale();
        return (int) Math.ceil(this.designWidth(new TextMetrics(Minecraft.getInstance().font, scale)) * scale.poseScale());
    }

    public int guiHeight() {
        return (int) Math.ceil(this.designHeight() * scale().poseScale());
    }

    private float designWidth(TextMetrics metrics) {
        float text = 0.0F;
        Font font = metrics.font();
        for (FormattedCharSequence line : this.title) {
            text = Math.max(text, font.width(line) * metrics.fontScale(TITLE));
        }
        for (FormattedCharSequence line : this.message) {
            text = Math.max(text, font.width(line) * metrics.fontScale(MESSAGE));
        }
        return Math.clamp(PADDING_LEFT + TILE + GAP + text + PADDING_RIGHT, MIN_WIDTH, MAX_WIDTH);
    }

    private float designHeight() {
        return Math.max(MIN_HEIGHT, PADDING_Y * 2.0F + this.title.size() * TITLE_LINE + this.message.size() * MESSAGE_LINE);
    }

    public void paint(GuiGraphicsExtractor graphics, Font font) {
        UiScale scale = scale();
        UiGraphics ui = new UiGraphics(graphics, scale, font, Util.getMillis());
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale.poseScale());
        TextMetrics metrics = ui.metrics();
        Box frame = new Box(0.0F, 0.0F, this.designWidth(metrics), this.designHeight());
        ui.shadow(frame, SHADOW_BLUR, SHADOW_OFFSET, SHADOW);
        ui.fill(frame, FILL);
        ui.border(frame, 1.0F, BORDER);
        ui.topHighlight(frame, ArkColors.INNER_HIGHLIGHT);
        Box tile = new Box(frame.x() + PADDING_LEFT, frame.centerY() - TILE * 0.5F, TILE, TILE);
        ui.fill(tile, TILE_FILL);
        ui.border(tile, 1.0F, TILE_BORDER);
        this.icon.draw(ui, tile);
        float textX = tile.right() + GAP;
        float block = this.title.size() * TITLE_LINE + this.message.size() * MESSAGE_LINE;
        float y = frame.centerY() - block * 0.5F;
        for (FormattedCharSequence line : this.title) {
            ui.textLine(line, textX, y + (TITLE_LINE - metrics.capHeight(TITLE)) * 0.5F, TITLE, this.titleColor);
            y += TITLE_LINE;
        }
        for (FormattedCharSequence line : this.message) {
            ui.textLine(line, textX, y + (MESSAGE_LINE - metrics.capHeight(MESSAGE)) * 0.5F, MESSAGE, ArkColors.TEXT_PRIMARY);
            y += MESSAGE_LINE;
        }
        if (!Float.isNaN(this.progress)) {
            float barY = frame.bottom() - PROGRESS_HEIGHT;
            ui.fill(frame.x(), barY, frame.width(), PROGRESS_HEIGHT, PROGRESS_TRACK);
            ui.fill(frame.x(), barY, frame.width() * Math.clamp(this.progress, 0.0F, 1.0F), PROGRESS_HEIGHT, Theme.accent().light());
        }
        graphics.pose().popMatrix();
    }

    private static UiScale scale() {
        Window window = Minecraft.getInstance().getWindow();
        return UiScale.compute(window.getWidth(), window.getHeight(), window.getGuiScale());
    }
}
