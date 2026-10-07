package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.UiGraphics;
import java.util.ArrayList;
import java.util.List;

public final class ToolbarRow implements PanelRow {
    private static final float GAP = 8.0F;

    private final float height;
    private final List<Item> items = new ArrayList<>();
    private Box bounds = Box.EMPTY;

    public ToolbarRow(float height) {
        this.height = height;
    }

    public ToolbarRow add(ArkWidget widget, float width, float widgetHeight, boolean alignRight) {
        this.items.add(new Item(widget, width, widgetHeight, alignRight));
        return this;
    }

    @Override
    public float height() {
        return this.height;
    }

    @Override
    public void place(Box newBounds) {
        this.bounds = newBounds;
        float left = newBounds.x();
        float right = newBounds.right();
        for (Item item : this.items) {
            float y = newBounds.centerY() - item.height() * 0.5F;
            if (item.alignRight()) {
                right -= item.width();
                item.widget().setBounds(new Box(right, y, item.width(), item.height()));
                right -= GAP;
            } else {
                item.widget().setBounds(new Box(left, y, item.width(), item.height()));
                left += item.width() + GAP;
            }
        }
    }

    @Override
    public Box bounds() {
        return this.bounds;
    }

    @Override
    public List<ArkWidget> widgets() {
        return this.items.stream().map(Item::widget).toList();
    }

    @Override
    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        for (Item item : this.items) {
            item.widget().render(graphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean isHovered() {
        return false;
    }

    private record Item(ArkWidget widget, float width, float height, boolean alignRight) {
    }
}
