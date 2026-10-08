package com.aryston.arkea.screen.debug;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;

public final class DebugCards {
    private static final float SCALE_AT_1080P = 1.5F;
    private static final float TEXT_SIZE = 8.0F;
    private static final float MARGIN = 6.0F;
    private static final float CARD_GAP = 3.0F;
    private static final float PADDING_X = 6.0F;
    private static final float PADDING_Y = 3.0F;
    private static final float LINE_PER_SIZE = 1.375F;
    private static final float ACCENT_WIDTH = 2.0F;
    private static final float HALF = 0.5F;
    private static final int CARD_FILL = ArkColors.rgba(10, 10, 12, 0.78F);
    private static final int CARD_BORDER = ArkColors.rgba(255, 255, 255, 0.08F);
    private static final int TITLE_COLOR = ArkColors.rgb(0xFFFFFF);

    private DebugCards() {
    }

    public static boolean active() {
        return ArkeaConfig.SPEC.isLoaded() && ArkeaConfig.DEBUG_OVERLAY.get();
    }

    public static void draw(GuiGraphicsExtractor graphics, List<String> lines, boolean left, int screenWidth) {
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        int debugScale = Math.max(1, Math.round((float) window.getWidth() / screenWidth));
        UiScale scale = UiScale.reference(window.getWidth(), window.getHeight(), debugScale, SCALE_AT_1080P);
        UiGraphics ui = new UiGraphics(graphics, scale, minecraft.font, Util.getMillis());
        DebugFont font = DebugFont.pick(scale, TEXT_SIZE);
        TextStyle text = font.style();
        TextMetrics metrics = ui.metrics();
        float fontScale = metrics.fontScale(text);
        float line = text.size() * LINE_PER_SIZE;
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale.poseScale());
        float y = MARGIN;
        for (List<String> card : cards(lines)) {
            List<FormattedCharSequence> shaped = new ArrayList<>();
            float width = 0.0F;
            for (int index = 0; index < card.size(); index++) {
                FormattedCharSequence shapedLine = font.line(card.get(index), index == 0);
                shaped.add(shapedLine);
                width = Math.max(width, width(minecraft.font, shapedLine, fontScale));
            }
            width += PADDING_X * 2.0F;
            float height = card.size() * line + PADDING_Y * 2.0F;
            float x = left ? MARGIN : scale.canvasWidth() - MARGIN - width;
            Box box = new Box(x, y, width, height);
            ui.fill(box, CARD_FILL);
            ui.border(box, 1.0F, CARD_BORDER);
            ui.fill(left ? box.x() : box.right() - ACCENT_WIDTH, box.y(), ACCENT_WIDTH, box.height(), Theme.accent().base());
            float lineY = box.y() + PADDING_Y;
            for (int index = 0; index < shaped.size(); index++) {
                FormattedCharSequence shapedLine = shaped.get(index);
                float textX = left ? box.x() + PADDING_X : box.right() - PADDING_X - width(minecraft.font, shapedLine, fontScale);
                int color = index == 0 ? TITLE_COLOR : ArkColors.TEXT_SOFT;
                ui.trueTypeLine(shapedLine, textX, lineY + (line - metrics.capHeight(text)) * HALF, text, color);
                lineY += line;
            }
            y += height + CARD_GAP;
        }
        graphics.pose().popMatrix();
    }

    private static float width(Font font, FormattedCharSequence line, float fontScale) {
        return font.width(line) * fontScale;
    }

    private static List<List<String>> cards(List<String> lines) {
        List<List<String>> cards = new ArrayList<>();
        List<String> current = new ArrayList<>();
        for (String line : lines) {
            if (line == null || line.isEmpty()) {
                if (!current.isEmpty()) {
                    cards.add(current);
                    current = new ArrayList<>();
                }
                continue;
            }
            current.add(line);
        }
        if (!current.isEmpty()) {
            cards.add(current);
        }
        return cards;
    }
}
