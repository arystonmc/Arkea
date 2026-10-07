package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import net.minecraft.network.chat.Component;

public record Tag(Component text, int color, int fill) {
    public static final float HEIGHT = 12.0F;
    public static final float GAP = 8.0F;
    private static final float PADDING = 4.0F;
    private static final float TONE_FILL = 0.14F;
    private static final TextStyle STYLE = TextStyle.of(8.0F).spacing(1.0F);

    public static Tag tone(Component text, int color) {
        return new Tag(text, color, ArkColors.withAlpha(color, TONE_FILL));
    }

    public float width(TextMetrics metrics) {
        return metrics.width(this.text.getString(), STYLE) + PADDING * 2.0F;
    }

    public float draw(UiGraphics graphics, float x, float centerY) {
        TextMetrics metrics = graphics.metrics();
        Box box = new Box(x, centerY - HEIGHT * 0.5F, this.width(metrics), HEIGHT);
        graphics.fill(box, this.fill);
        graphics.text(this.text.getString(), box.x() + PADDING, box.centerY() - metrics.capHeight(STYLE) * 0.5F, STYLE, this.color);
        return box.right();
    }
}
