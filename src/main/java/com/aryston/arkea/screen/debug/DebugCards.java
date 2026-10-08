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
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Util;

public final class DebugCards {
    private static final float SCALE_AT_1080P = 1.5F;
    private static final float MARGIN = 6.0F;
    private static final float CARD_GAP = 3.0F;
    private static final float PADDING_X = 6.0F;
    private static final float PADDING_Y = 3.0F;
    private static final float LINE = 10.0F;
    private static final float ACCENT_WIDTH = 2.0F;
    private static final int CARD_FILL = ArkColors.rgba(10, 10, 12, 0.72F);
    private static final int CARD_BORDER = ArkColors.rgba(255, 255, 255, 0.08F);
    private static final int TITLE_COLOR = ArkColors.rgb(0xFFFFFF);
    private static final TextStyle TEXT = TextStyle.of(8.0F);

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
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale.poseScale());
        TextMetrics metrics = ui.metrics();
        float y = MARGIN;
        for (List<String> card : cards(lines)) {
            float width = 0.0F;
            for (String line : card) {
                width = Math.max(width, metrics.width(line, TEXT));
            }
            width += PADDING_X * 2.0F;
            float height = card.size() * LINE + PADDING_Y * 2.0F;
            float x = left ? MARGIN : scale.canvasWidth() - MARGIN - width;
            Box box = new Box(x, y, width, height);
            ui.fill(box, CARD_FILL);
            ui.border(box, 1.0F, CARD_BORDER);
            ui.fill(left ? box.x() : box.right() - ACCENT_WIDTH, box.y(), ACCENT_WIDTH, box.height(), Theme.accent().base());
            float lineY = box.y() + PADDING_Y;
            for (int index = 0; index < card.size(); index++) {
                String line = card.get(index);
                float textX = left ? box.x() + PADDING_X : box.right() - PADDING_X - metrics.width(line, TEXT);
                int color = index == 0 ? TITLE_COLOR : ArkColors.TEXT_SOFT;
                ui.text(line, textX, lineY + (LINE - metrics.capHeight(TEXT)) * 0.5F, TEXT, color);
                lineY += LINE;
            }
            y += height + CARD_GAP;
        }
        graphics.pose().popMatrix();
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
