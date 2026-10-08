package com.aryston.arkea.screen.options.hud;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.PanelRow;
import java.util.List;

final class HudStyleRow implements PanelRow {
    private final HudStyleCard card;

    HudStyleRow(HudStyleCard card) {
        this.card = card;
    }

    @Override
    public float height() {
        return HudStyleCard.TEXT_AREA;
    }

    @Override
    public float height(float width) {
        return HudStyleCard.height(width);
    }

    @Override
    public void place(Box bounds) {
        this.card.setBounds(bounds);
    }

    @Override
    public Box bounds() {
        return this.card.bounds();
    }

    @Override
    public List<ArkWidget> widgets() {
        return List.of(this.card);
    }

    @Override
    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        this.card.render(graphics, mouseX, mouseY);
    }

    @Override
    public boolean isHovered() {
        return this.card.isHovered();
    }
}
