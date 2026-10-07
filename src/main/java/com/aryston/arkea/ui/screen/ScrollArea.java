package com.aryston.arkea.ui.screen;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;

public final class ScrollArea {
    public static final float WHEEL_STEP = 64.0F;
    private static final int SCROLL_TIME = 180;
    private static final float BAR_WIDTH = 4.0F;
    private static final float BAR_INSET = 4.0F;
    private static final float MIN_THUMB = 24.0F;

    private final Transition offset = new Transition(0.0F, SCROLL_TIME, Easing.EASE_OUT);
    private final Transition thumbHover = new Transition(0.0F, Motion.HOVER, Easing.EASE);
    private Box viewport = Box.EMPTY;
    private Box canvasViewport = Box.EMPTY;
    private Box canvasTrack = Box.EMPTY;
    private Box canvasThumb = Box.EMPTY;
    private float contentHeight;
    private float target;
    private boolean dragging;
    private float grab;

    public void layout(Box newViewport, float newContentHeight) {
        this.viewport = newViewport;
        this.contentHeight = newContentHeight;
        this.target = this.clamp(this.target);
        this.offset.snap(this.target);
        this.canvasTrack = Box.EMPTY;
        this.canvasThumb = Box.EMPTY;
    }

    public Box viewport() {
        return this.viewport;
    }

    public boolean isScrollable() {
        return this.max() > 0.0F;
    }

    public float max() {
        return Math.max(0.0F, this.contentHeight - this.viewport.height());
    }

    public float offset(long now) {
        return this.offset.value(now);
    }

    public void scrollBy(float delta, long now) {
        this.scrollTo(this.target + delta, now);
    }

    public void scrollTo(float value, long now) {
        this.target = this.clamp(value);
        this.offset.setTarget(this.target, now);
    }

    public void finish() {
        this.offset.snap(this.target);
    }

    public void reveal(Box content, float margin, long now) {
        if (content.y() - margin < this.viewport.y() + this.target) {
            this.scrollTo(content.y() - margin - this.viewport.y(), now);
        } else if (content.bottom() + margin > this.viewport.bottom() + this.target) {
            this.scrollTo(content.bottom() + margin - this.viewport.bottom(), now);
        }
    }

    private float clamp(float value) {
        return Math.clamp(value, 0.0F, this.max());
    }

    public boolean isOver(float x, float y) {
        return this.isScrollable() && this.canvasViewport.contains(x, y);
    }

    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        this.canvasViewport = graphics.canvasBox(this.viewport);
        if (!this.isScrollable()) {
            this.canvasTrack = Box.EMPTY;
            this.canvasThumb = Box.EMPTY;
            return;
        }
        Box track = new Box(this.viewport.right() - BAR_INSET - BAR_WIDTH, this.viewport.y() + BAR_INSET, BAR_WIDTH,
            this.viewport.height() - BAR_INSET * 2.0F);
        float thumbHeight = Math.max(MIN_THUMB, track.height() * this.viewport.height() / this.contentHeight);
        float thumbY = track.y() + (track.height() - thumbHeight) * this.offset(graphics.now()) / this.max();
        Box thumb = new Box(track.x(), thumbY, BAR_WIDTH, thumbHeight);
        this.canvasTrack = graphics.canvasBox(track);
        this.canvasThumb = graphics.canvasBox(thumb);
        boolean hot = this.dragging || this.canvasTrack.expand(BAR_INSET).contains(mouseX, mouseY);
        this.thumbHover.setTarget(hot ? 1.0F : 0.0F, graphics.now());
        graphics.fill(thumb, ArkColors.lerp(this.thumbHover.value(graphics.now()), ArkColors.SCROLL_THUMB, ArkColors.SCROLL_THUMB_HOVER));
    }

    public boolean press(float x, float y, long now) {
        if (!this.isScrollable() || !this.canvasTrack.expand(BAR_INSET).contains(x, y)) {
            return false;
        }
        if (y < this.canvasThumb.y() || y > this.canvasThumb.bottom()) {
            float page = this.viewport.height() * (y < this.canvasThumb.y() ? -1.0F : 1.0F);
            this.scrollBy(page, now);
            return true;
        }
        this.dragging = true;
        this.grab = y - this.canvasThumb.y();
        return true;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public void drag(float y) {
        float travel = this.canvasTrack.height() - this.canvasThumb.height();
        if (travel <= 0.0F) {
            return;
        }
        this.target = this.clamp((y - this.grab - this.canvasTrack.y()) / travel * this.max());
        this.offset.snap(this.target);
    }

    public void release() {
        this.dragging = false;
    }
}
