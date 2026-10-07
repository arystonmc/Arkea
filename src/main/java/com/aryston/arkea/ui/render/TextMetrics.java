package com.aryston.arkea.ui.render;

import com.aryston.arkea.ui.layout.UiScale;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public record TextMetrics(Font font, UiScale scale) {
    private static final String ELLIPSIS = "...";
    private static final float SNAP_TOLERANCE = 0.15F;

    public float fontScale(TextStyle style) {
        float exact = style.size() / TextStyle.GLYPH_HEIGHT * this.scale.scale();
        float nearest = Math.max(1.0F, Math.round(exact));
        float pixels = Math.abs(exact - nearest) <= SNAP_TOLERANCE ? nearest : Math.max(1.0F, exact);
        return pixels / this.scale.scale();
    }

    public float capHeight(TextStyle style) {
        return TextStyle.CAP_HEIGHT * this.fontScale(style);
    }

    public float width(String text, TextStyle style) {
        if (text.isEmpty()) {
            return 0.0F;
        }
        float spacing = style.letterSpacing() * (text.codePointCount(0, text.length()) - 1);
        return (this.font.width(text) - 1) * this.fontScale(style) + spacing;
    }

    public List<String> wrap(String text, TextStyle style, float maxWidth) {
        int fontWidth = Math.max(1, (int) (maxWidth / this.fontScale(style)));
        return this.font.getSplitter().splitLines(text, fontWidth, Style.EMPTY).stream().map(FormattedText::getString).toList();
    }

    public String ellipsize(String text, TextStyle style, float maxWidth) {
        if (this.width(text, style) <= maxWidth) {
            return text;
        }
        float available = maxWidth - this.width(ELLIPSIS, style);
        int fontWidth = Math.max(0, (int) (available / this.fontScale(style)));
        return this.font.plainSubstrByWidth(text, fontWidth) + ELLIPSIS;
    }
}
