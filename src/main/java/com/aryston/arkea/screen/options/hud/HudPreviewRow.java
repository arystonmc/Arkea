package com.aryston.arkea.screen.options.hud;

import com.aryston.arkea.hud.HudPainter;
import com.aryston.arkea.hud.HudPreview;
import com.aryston.arkea.hud.HudSettings;
import com.aryston.arkea.hud.HudSnapshot;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.PanelRow;
import java.util.List;

final class HudPreviewRow implements PanelRow {
    private static final float ASPECT = 3.2F;
    private static final float MIN_HEIGHT = 120.0F;
    private static final long MINIMAL_CYCLE = 2500L;

    private final HudPainter painter = new HudPainter();
    private Box bounds = Box.EMPTY;

    @Override
    public float height() {
        return MIN_HEIGHT;
    }

    @Override
    public float height(float width) {
        return Math.max(MIN_HEIGHT, width / ASPECT);
    }

    @Override
    public void place(Box bounds) {
        this.bounds = bounds;
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
        HudSettings settings = HudSettings.current();
        boolean full = settings.style().hidesWhenFull() && graphics.now() / MINIMAL_CYCLE % 2 == 0;
        HudPreview.draw(graphics, this.bounds, settings, this.painter, HudSnapshot.sample(full));
        graphics.border(this.bounds, 1.0F, ArkColors.BORDER_DEFAULT);
    }

    @Override
    public boolean isHovered() {
        return false;
    }
}
