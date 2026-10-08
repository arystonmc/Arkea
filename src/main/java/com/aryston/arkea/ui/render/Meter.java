package com.aryston.arkea.ui.render;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.theme.ArkColors;

public final class Meter {
    public static final int SEGMENTS = 10;
    public static final float HEIGHT = 6.0F;
    private static final float SEGMENT_GAP = 3.0F;
    private static final float PIP = 6.0F;
    private static final float PIP_GAP = 2.0F;
    private static final int EMPTY = ArkColors.rgba(255, 255, 255, 0.10F);

    private Meter() {
    }

    public static void segments(UiGraphics graphics, Box box, float fraction, int color) {
        float width = (box.width() - SEGMENT_GAP * (SEGMENTS - 1)) / SEGMENTS;
        int filled = Math.round(Math.clamp(fraction, 0.0F, 1.0F) * SEGMENTS);
        for (int index = 0; index < SEGMENTS; index++) {
            graphics.fill(box.x() + index * (width + SEGMENT_GAP), box.y(), width, box.height(), index < filled ? color : EMPTY);
        }
    }

    public static float pipsWidth(int count) {
        return count * PIP + (count - 1) * PIP_GAP;
    }

    public static void pips(UiGraphics graphics, float x, float centerY, int level, int count, int color) {
        for (int index = 0; index < count; index++) {
            graphics.fill(x + index * (PIP + PIP_GAP), centerY - PIP * 0.5F, PIP, PIP, index < level ? color : EMPTY);
        }
    }

    public static void progress(UiGraphics graphics, Box box, float fraction, int color) {
        graphics.fill(box, ArkColors.TRACK_EMPTY);
        graphics.fill(box.x(), box.y(), box.width() * Math.clamp(fraction, 0.0F, 1.0F), box.height(), color);
    }

    public static void stepDots(UiGraphics graphics, float x, float y, int current, int count, int color) {
        float position = x;
        for (int index = 0; index < count; index++) {
            float width = index == current ? PIP * 2.0F + PIP_GAP * 2.0F : PIP + PIP_GAP;
            graphics.fill(position, y, width, PIP_GAP * 2.0F, index == current ? color : EMPTY);
            position += width + PIP_GAP * 2.0F;
        }
    }

    public static void divider(UiGraphics graphics, float x, float y, float width) {
        graphics.fill(x, y, width, graphics.scale().snapThickness(1.0F), ArkColors.BORDER_DEFAULT);
    }

    public static void dashedDivider(UiGraphics graphics, float x, float y, float width, float dash) {
        float line = graphics.scale().snapThickness(1.0F);
        for (float position = x; position < x + width; position += dash * 2.0F) {
            graphics.fill(position, y, Math.min(dash, x + width - position), line, ArkColors.BORDER_TOOLTIP);
        }
    }

    public static void labeledDivider(UiGraphics graphics, float x, float y, float width, String label, TextStyle style) {
        TextMetrics metrics = graphics.metrics();
        float labelWidth = metrics.width(label, style);
        float gap = PIP + PIP_GAP * 2.0F;
        float side = (width - labelWidth) * 0.5F - gap;
        divider(graphics, x, y, side);
        graphics.text(label, x + side + gap, y - metrics.capHeight(style) * 0.5F, style, ArkColors.TEXT_LABEL);
        divider(graphics, x + width - side, y, side);
    }
}
