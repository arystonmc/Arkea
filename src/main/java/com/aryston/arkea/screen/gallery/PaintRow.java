package com.aryston.arkea.screen.gallery;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.PanelRow;
import java.util.List;
import java.util.function.BiConsumer;

final class PaintRow implements PanelRow {
    private static final float PADDING = 14.0F;

    private final float height;
    private final BiConsumer<UiGraphics, Box> painter;
    private Box bounds = Box.EMPTY;

    PaintRow(float height, BiConsumer<UiGraphics, Box> painter) {
        this.height = height;
        this.painter = painter;
    }

    @Override
    public float height() {
        return this.height;
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
        graphics.fill(this.bounds, ArkColors.ROW);
        graphics.border(this.bounds, 1.0F, ArkColors.BORDER_SUBTLE);
        this.painter.accept(graphics, this.bounds.inset(PADDING));
    }

    @Override
    public boolean isHovered() {
        return false;
    }
}
