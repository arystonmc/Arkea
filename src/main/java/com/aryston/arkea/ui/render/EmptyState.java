package com.aryston.arkea.ui.render;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import net.minecraft.network.chat.Component;

public final class EmptyState {
    public static final float HEIGHT = 280.0F;
    private static final float GAP = 14.0F;
    private static final float DASH = 4.0F;
    private static final int DASH_COLOR = ArkColors.rgba(255, 255, 255, 0.12F);
    private static final TextStyle TITLE = TextStyle.of(13.0F);
    private static final TextStyle HINT = TextStyle.of(11.0F);

    private EmptyState() {
    }

    public static void render(UiGraphics graphics, Box box, Component title, Component hint, boolean busy) {
        graphics.dashedBorder(box, DASH, DASH_COLOR);
        TextMetrics metrics = graphics.metrics();
        float block = metrics.capHeight(TITLE) + GAP + metrics.capHeight(HINT) + (busy ? PixelSpinner.SIZE + GAP : 0.0F);
        float y = box.centerY() - block * 0.5F;
        if (busy) {
            PixelSpinner.render(graphics, box.centerX() - PixelSpinner.SIZE * 0.5F, y, Theme.accent().light());
            y += PixelSpinner.SIZE + GAP;
        }
        String titleText = metrics.ellipsize(title.getString(), TITLE, box.width());
        graphics.text(titleText, box.centerX() - metrics.width(titleText, TITLE) * 0.5F, y, TITLE, ArkColors.TEXT_PRIMARY);
        y += metrics.capHeight(TITLE) + GAP;
        String hintText = metrics.ellipsize(hint.getString(), HINT, box.width());
        graphics.text(hintText, box.centerX() - metrics.width(hintText, HINT) * 0.5F, y, HINT, ArkColors.TEXT_FAINT);
    }
}
