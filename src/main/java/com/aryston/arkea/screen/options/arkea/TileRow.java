package com.aryston.arkea.screen.options.arkea;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.PanelRow;
import java.util.List;
import org.jspecify.annotations.Nullable;

final class TileRow implements PanelRow {
    private static final float BUTTON = 28.0F;
    private static final float INSET = 8.0F;

    private final MediaTile tile;
    private final @Nullable ArkWidget corner;
    private final float height;

    TileRow(MediaTile tile, @Nullable ArkWidget corner, float height) {
        this.tile = tile;
        this.corner = corner;
        this.height = height;
        tile.corner(corner);
    }

    @Override
    public float height() {
        return this.height;
    }

    @Override
    public void place(Box bounds) {
        this.tile.setBounds(bounds);
        if (this.corner != null) {
            this.corner.setBounds(new Box(bounds.right() - INSET - BUTTON, bounds.y() + INSET, BUTTON, BUTTON));
        }
    }

    @Override
    public Box bounds() {
        return this.tile.bounds();
    }

    @Override
    public List<ArkWidget> widgets() {
        return this.corner == null ? List.of(this.tile) : List.of(this.tile, this.corner);
    }

    @Override
    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        this.tile.render(graphics, mouseX, mouseY);
        if (this.corner != null) {
            this.corner.render(graphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean isHovered() {
        return this.tile.isHovered();
    }
}
