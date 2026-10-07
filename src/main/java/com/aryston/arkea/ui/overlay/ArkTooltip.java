package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Presence;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class ArkTooltip {
    private static final float MAX_WIDTH = 280.0F;
    private static final float PADDING_X = 10.0F;
    private static final float PADDING_Y = 8.0F;
    private static final float LINE_HEIGHT = 15.0F;
    private static final float CURSOR_GAP = 12.0F;
    private static final float EDGE_MARGIN = 8.0F;
    private static final float ENTER_OFFSET = 4.0F;
    private static final float SHADOW_BLUR = 30.0F;
    private static final float SHADOW_OFFSET = 12.0F;
    private static final TextStyle TEXT = TextStyle.of(11.0F);

    private final Presence presence = new Presence(Motion.TOOLTIP_IN, Motion.TOOLTIP_OUT, Easing.EASE_OUT, Easing.EXIT);
    private @Nullable Object target;
    private @Nullable Component text;
    private long hoverStart;
    private float anchorX;
    private float anchorY;

    public void track(@Nullable TooltipHint hint, long now, float mouseX, float mouseY) {
        Object candidate = hint != null ? hint.owner() : null;
        if (candidate != this.target) {
            this.presence.hide(now);
            this.target = candidate;
            this.hoverStart = now;
        }
        if (hint == null) {
            return;
        }
        this.text = hint.text();
        if (!this.presence.isShown()) {
            this.anchorX = mouseX;
            this.anchorY = mouseY;
            if (now - this.hoverStart >= Motion.TOOLTIP_DELAY) {
                this.presence.show(now);
            }
        }
    }

    public void render(UiGraphics graphics) {
        long now = graphics.now();
        float progress = this.presence.progress(now);
        if (progress <= 0.0F || this.text == null) {
            return;
        }
        TextMetrics metrics = graphics.metrics();
        List<String> lines = metrics.wrap(this.text.getString(), TEXT, MAX_WIDTH - PADDING_X * 2.0F);
        float textWidth = 0.0F;
        for (String line : lines) {
            textWidth = Math.max(textWidth, metrics.width(line, TEXT));
        }
        Box box = this.place(graphics.scale(), textWidth + PADDING_X * 2.0F, lines.size() * LINE_HEIGHT + PADDING_Y * 2.0F);
        graphics.push();
        graphics.fade(progress);
        graphics.translate(0.0F, ENTER_OFFSET * (1.0F - progress));
        graphics.shadow(box, SHADOW_BLUR, SHADOW_OFFSET, ArkColors.SHADOW_MENU);
        graphics.fill(box, ArkColors.MENU);
        graphics.border(box, 1.0F, ArkColors.BORDER_TOOLTIP);
        float y = box.y() + PADDING_Y;
        for (String line : lines) {
            graphics.text(line, box.x() + PADDING_X, y + (LINE_HEIGHT - metrics.capHeight(TEXT)) * 0.5F, TEXT, ArkColors.TEXT_SOFT);
            y += LINE_HEIGHT;
        }
        graphics.pop();
    }

    private Box place(UiScale scale, float width, float height) {
        float x = this.anchorX + CURSOR_GAP;
        float y = this.anchorY + CURSOR_GAP;
        if (x + width > scale.canvasWidth() - EDGE_MARGIN) {
            x = this.anchorX - CURSOR_GAP - width;
        }
        if (y + height > scale.canvasHeight() - EDGE_MARGIN) {
            y = this.anchorY - CURSOR_GAP - height;
        }
        return new Box(Math.max(EDGE_MARGIN, x), Math.max(EDGE_MARGIN, y), width, height);
    }
}
