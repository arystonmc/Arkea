package com.aryston.arkea.screen.options.packs;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.PanelRow;
import java.util.List;
import net.minecraft.network.chat.Component;

final class DropHintRow implements PanelRow {
    private static final float HEIGHT = 56.0F;
    private static final float DASH = 4.0F;
    private static final float ICON = 14.0F;
    private static final float GAP = 10.0F;
    private static final int BORDER = ArkColors.rgba(255, 255, 255, 0.14F);
    private static final TextStyle TEXT = TextStyle.of(11.0F);

    private final Icon icon;
    private final Component text;
    private Box bounds = Box.EMPTY;

    DropHintRow(Icon icon, Component text) {
        this.icon = icon;
        this.text = text;
    }

    @Override
    public float height() {
        return HEIGHT;
    }

    @Override
    public void place(Box newBounds) {
        this.bounds = newBounds;
    }

    @Override
    public Box bounds() {
        return this.bounds;
    }

    @Override
    public List<ArkWidget> widgets() {
        return List.of();
    }

    @Override
    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        Box box = this.bounds;
        float line = graphics.scale().snapThickness(1.0F);
        for (float x = box.x(); x < box.right(); x += DASH * 2.0F) {
            float width = Math.min(DASH, box.right() - x);
            graphics.fill(x, box.y(), width, line, BORDER);
            graphics.fill(x, box.bottom() - line, width, line, BORDER);
        }
        for (float y = box.y(); y < box.bottom(); y += DASH * 2.0F) {
            float height = Math.min(DASH, box.bottom() - y);
            graphics.fill(box.x(), y, line, height, BORDER);
            graphics.fill(box.right() - line, y, line, height, BORDER);
        }
        TextMetrics metrics = graphics.metrics();
        String text = metrics.ellipsize(this.text.getString(), TEXT, box.width() - ICON - GAP * 3.0F);
        float width = ICON + GAP + metrics.width(text, TEXT);
        float x = box.centerX() - width * 0.5F;
        graphics.icon(this.icon, x, box.centerY() - ICON * 0.5F, ICON, ICON, ArkColors.TEXT_FAINT);
        graphics.text(text, x + ICON + GAP, box.centerY() - metrics.capHeight(TEXT) * 0.5F, TEXT, ArkColors.TEXT_FAINT);
    }

    @Override
    public boolean isHovered() {
        return false;
    }
}
