package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.UiGraphics;
import java.util.List;

public final class WidgetRow implements PanelRow {
    private final ArkWidget widget;
    private final float height;

    public WidgetRow(ArkWidget widget, float height) {
        this.widget = widget;
        this.height = height;
    }

    @Override
    public float height() {
        return this.height;
    }

    @Override
    public void place(Box bounds) {
        this.widget.setBounds(bounds);
    }

    @Override
    public Box bounds() {
        return this.widget.bounds();
    }

    @Override
    public List<ArkWidget> widgets() {
        return List.of(this.widget);
    }

    @Override
    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        this.widget.render(graphics, mouseX, mouseY);
    }

    @Override
    public boolean isHovered() {
        return this.widget.isHovered();
    }
}
