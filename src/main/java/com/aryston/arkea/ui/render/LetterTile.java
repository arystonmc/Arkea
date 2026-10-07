package com.aryston.arkea.ui.render;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.theme.ArkColors;
import java.util.Locale;

public final class LetterTile {
    private static final float SHADE_SHARE = 1.0F / 12.0F;
    private static final float SHINE_SHARE = 1.0F / 20.0F;
    private static final float LETTER_SHARE = 0.45F;
    private static final String UNKNOWN = "?";
    private static final int SHADE = ArkColors.rgba(0, 0, 0, 0.25F);
    private static final int SHINE = ArkColors.rgba(255, 255, 255, 0.15F);
    private static final int[] COLORS = {ArkColors.rgb(0x3C8527), ArkColors.rgb(0x2F7A4A), ArkColors.rgb(0xA8741A), ArkColors.rgb(0x5C5C61),
        ArkColors.rgb(0x7A4B2A)};

    private LetterTile() {
    }

    public static void draw(UiGraphics graphics, Box tile, String name, Locale locale) {
        graphics.fill(tile, COLORS[Math.floorMod(name.hashCode(), COLORS.length)]);
        float shade = Math.max(1.0F, tile.height() * SHADE_SHARE);
        graphics.fill(tile.x(), tile.bottom() - shade, tile.width(), shade, SHADE);
        graphics.fill(tile.x(), tile.y(), tile.width(), Math.max(1.0F, tile.height() * SHINE_SHARE), SHINE);
        String letter = name.isBlank() ? UNKNOWN : name.strip().substring(0, 1).toUpperCase(locale);
        TextStyle style = TextStyle.of(tile.height() * LETTER_SHARE);
        TextMetrics metrics = graphics.metrics();
        graphics.text(letter, tile.centerX() - metrics.width(letter, style) * 0.5F, tile.centerY() - (metrics.capHeight(style) + shade) * 0.5F, style,
            ArkColors.TEXT_PRIMARY);
    }
}
