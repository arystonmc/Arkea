package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ArkCycle extends ArkWidget {
    public static final float WIDTH = 160.0F;
    public static final float HEIGHT = 30.0F;
    private static final float ARROW_WIDTH = 26.0F;
    private static final float ARROW_ICON_WIDTH = 5.0F;
    private static final float ARROW_ICON_HEIGHT = 8.0F;
    private static final float VALUE_PADDING = 4.0F;
    private static final float DOTS_INSET = 32.0F;
    private static final float DOTS_BOTTOM = 3.0F;
    private static final float DOT_HEIGHT = 2.0F;
    private static final float DOT_GAP = 2.0F;
    private static final int MAX_DOTS = 16;
    private static final float VALUE_SLIDE = 12.0F;
    private static final int VALUE_TIME = 200;
    private static final int ARROW_TIME = 120;
    private static final TextStyle VALUE = TextStyle.of(12.0F);

    private final Component name;
    private final CycleModel model;
    private final Transition valueIn = new Transition(1.0F, VALUE_TIME, Easing.EASE_OUT);
    private final Transition leftHover = new Transition(0.0F, ARROW_TIME, Easing.EASE);
    private final Transition rightHover = new Transition(0.0F, ARROW_TIME, Easing.EASE);
    private int shownIndex;
    private int direction = 1;
    private int pendingDirection = 1;

    public ArkCycle(UiHost host, Component name, CycleModel model) {
        super(host);
        this.name = name;
        this.model = model;
        this.shownIndex = model.index();
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        long now = graphics.now();
        int index = this.model.index();
        if (index != this.shownIndex) {
            this.shownIndex = index;
            this.direction = this.pendingDirection;
            this.pendingDirection = 1;
            this.valueIn.snap(0.0F);
            this.valueIn.setTarget(1.0F, now);
        }
        float hover = this.hoverProgress();
        graphics.fill(box, ArkColors.CONTROL_FILL);
        graphics.border(box, 1.0F, ArkColors.lerp(hover, ArkColors.BORDER_DEFAULT, ArkColors.BORDER_OVERLAY));
        float mouse = this.isHovered() ? this.localMouseX() : Float.NaN;
        this.leftHover.setTarget(mouse < box.x() + ARROW_WIDTH ? 1.0F : 0.0F, now);
        this.rightHover.setTarget(mouse >= box.right() - ARROW_WIDTH ? 1.0F : 0.0F, now);
        this.renderArrow(graphics, new Box(box.x(), box.y(), ARROW_WIDTH, box.height()), this.leftHover.value(now), true);
        this.renderArrow(graphics, new Box(box.right() - ARROW_WIDTH, box.y(), ARROW_WIDTH, box.height()), this.rightHover.value(now), false);
        this.renderValue(graphics, box, index);
        this.renderDots(graphics, box, index);
    }

    private void renderArrow(UiGraphics graphics, Box area, float hover, boolean left) {
        float line = graphics.scale().snapThickness(1.0F);
        graphics.fill(area.inset(line), ArkColors.multiplyAlpha(ArkColors.CONTROL_HOVER, hover));
        float x = area.centerX() - ARROW_ICON_WIDTH * 0.5F;
        float y = area.centerY() - ARROW_ICON_HEIGHT * 0.5F;
        graphics.icon(left ? Icons.CHEVRON_LEFT : Icons.CHEVRON_RIGHT, x, y, ARROW_ICON_WIDTH, ARROW_ICON_HEIGHT,
            ArkColors.lerp(hover, ArkColors.CONTROL_IDLE, ArkColors.TEXT_PRIMARY));
    }

    private void renderValue(UiGraphics graphics, Box box, int index) {
        TextMetrics metrics = graphics.metrics();
        float available = box.width() - (ARROW_WIDTH + VALUE_PADDING) * 2.0F;
        String text = metrics.ellipsize(this.model.label(index).getString(), VALUE, available);
        float progress = this.valueIn.value(graphics.now());
        float x = box.centerX() - metrics.width(text, VALUE) * 0.5F + this.direction * VALUE_SLIDE * (1.0F - progress);
        float y = box.centerY() - metrics.capHeight(VALUE) * 0.5F - DOT_HEIGHT * 0.5F;
        graphics.push();
        graphics.fade(progress);
        graphics.text(text, x, y, VALUE, ArkColors.TEXT_PRIMARY);
        graphics.pop();
    }

    private void renderDots(UiGraphics graphics, Box box, int index) {
        int size = this.model.size();
        if (size <= 1) {
            return;
        }
        float left = box.x() + DOTS_INSET;
        float width = box.width() - DOTS_INSET * 2.0F;
        float y = box.bottom() - DOTS_BOTTOM - DOT_HEIGHT;
        if (size > MAX_DOTS) {
            graphics.fill(left, y, width, DOT_HEIGHT, ArkColors.TRACK_EMPTY);
            graphics.fill(left, y, width * (index + 1) / size, DOT_HEIGHT, Theme.accent().base());
            return;
        }
        float dot = (width - DOT_GAP * (size - 1)) / size;
        for (int dotIndex = 0; dotIndex < size; dotIndex++) {
            int color = dotIndex <= index ? Theme.accent().base() : ArkColors.TRACK_EMPTY;
            graphics.fill(left + dotIndex * (dot + DOT_GAP), y, dot, DOT_HEIGHT, color);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!super.mouseClicked(event, doubleClick)) {
            return false;
        }
        float x = this.localX(this.host.uiScale().toDesign(event.x()));
        this.pendingDirection = x < this.bounds().x() + ARROW_WIDTH ? -1 : 1;
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.isActive() && this.isFocused() && (event.isLeft() || event.isRight())) {
            this.pendingDirection = event.isLeft() ? -1 : 1;
            this.activate();
            return true;
        }
        if (event.isSelection()) {
            this.pendingDirection = event.hasShiftDown() ? -1 : 1;
        }
        return super.keyPressed(event);
    }

    @Override
    protected void onPress() {
        int size = this.model.size();
        if (size > 0) {
            this.model.select(Math.floorMod(this.model.index() + this.pendingDirection, size));
        }
    }

    @Override
    protected Component narrationMessage() {
        return CommonComponents.optionNameValue(this.name, this.model.label(this.model.index()));
    }
}
