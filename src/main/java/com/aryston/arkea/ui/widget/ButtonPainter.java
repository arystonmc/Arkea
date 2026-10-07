package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class ButtonPainter {
    private static final float PADDING_X = 14.0F;
    private static final float ICON_SIZE = 14.0F;
    private static final float PIXEL_ICON_HEIGHT = 8.0F;
    private static final float ICON_GAP = 8.0F;
    private static final float GLOW_BLUR = 16.0F;
    private static final float GLOW_OFFSET = 4.0F;
    private static final float HOVER_BRIGHTNESS = 0.18F;
    private static final float DISABLED_OPACITY = 0.4F;
    private static final TextStyle LABEL = TextStyle.of(12.0F);

    private ButtonPainter() {
    }

    public static float width(TextMetrics metrics, Component label, @Nullable Icon leading, @Nullable Icon trailing) {
        return contentWidth(metrics.width(label.getString(), LABEL), leading, trailing) + PADDING_X * 2.0F;
    }

    public static void paint(UiGraphics graphics, Box box, Component label, ButtonVariant variant, float hover, boolean active, @Nullable Icon leading,
        @Nullable Icon trailing) {
        graphics.push();
        if (!active) {
            graphics.fade(DISABLED_OPACITY);
        }
        graphics.shadow(box, GLOW_BLUR, GLOW_OFFSET, variant.glow());
        float brightness = 1.0F + HOVER_BRIGHTNESS * hover;
        int fill = ArkColors.lerp(hover, variant.fill(), variant.hoverFill());
        graphics.fill(box, ArkColors.brighten(fill, brightness));
        graphics.border(box, 1.0F, ArkColors.brighten(variant.border(), brightness));
        int color = ArkColors.brighten(variant.text(), brightness);
        String text = label.getString();
        float textWidth = graphics.metrics().width(text, LABEL);
        float x = box.centerX() - contentWidth(textWidth, leading, trailing) * 0.5F;
        if (leading != null) {
            x = drawIcon(graphics, leading, x, box, color) + ICON_GAP;
        }
        graphics.text(text, x, box.centerY() - graphics.metrics().capHeight(LABEL) * 0.5F, LABEL, color);
        if (trailing != null) {
            drawIcon(graphics, trailing, x + textWidth + ICON_GAP, box, color);
        }
        graphics.pop();
    }

    private static float drawIcon(UiGraphics graphics, Icon icon, float x, Box box, int color) {
        float height = iconHeight(icon);
        float width = iconWidth(icon);
        graphics.icon(icon, x, box.centerY() - height * 0.5F, width, height, color);
        return x + width;
    }

    private static float contentWidth(float textWidth, @Nullable Icon leading, @Nullable Icon trailing) {
        float width = textWidth;
        if (leading != null) {
            width += iconWidth(leading) + ICON_GAP;
        }
        if (trailing != null) {
            width += iconWidth(trailing) + ICON_GAP;
        }
        return width;
    }

    private static float iconHeight(Icon icon) {
        return icon.viewWidth() == icon.viewHeight() ? ICON_SIZE : PIXEL_ICON_HEIGHT;
    }

    private static float iconWidth(Icon icon) {
        return iconHeight(icon) * icon.viewWidth() / icon.viewHeight();
    }
}
