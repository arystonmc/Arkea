package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ArkStepper extends ArkWidget {
    public static final float WIDTH = 140.0F;
    public static final float HEIGHT = 30.0F;
    private static final float BUTTON_WIDTH = 30.0F;
    private static final float SIGN_LENGTH = 8.0F;
    private static final float SIGN_THICKNESS = 1.5F;
    private static final float VALUE_PADDING = 4.0F;
    private static final int REPEAT_DELAY = 400;
    private static final int REPEAT_INTERVAL = 60;
    private static final int BUTTON_TIME = 120;
    private static final TextStyle VALUE = TextStyle.of(12.0F);

    private final Component name;
    private final SliderModel model;
    private final Transition minusHover = new Transition(0.0F, BUTTON_TIME, Easing.EASE);
    private final Transition plusHover = new Transition(0.0F, BUTTON_TIME, Easing.EASE);
    private int direction;
    private long holdStart;
    private long lastRepeat;
    private boolean repeated;

    public ArkStepper(UiHost host, Component name, SliderModel model) {
        super(host);
        this.name = name;
        this.model = model;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        long now = graphics.now();
        this.repeatWhileHeld(now);
        float hover = this.hoverProgress();
        graphics.fill(box, ArkColors.CONTROL_FILL);
        graphics.border(box, 1.0F, ArkColors.lerp(hover, ArkColors.BORDER_DEFAULT, ArkColors.BORDER_OVERLAY));
        float mouse = this.isHovered() ? this.localMouseX() : Float.NaN;
        this.minusHover.setTarget(mouse < box.x() + BUTTON_WIDTH ? 1.0F : 0.0F, now);
        this.plusHover.setTarget(mouse >= box.right() - BUTTON_WIDTH ? 1.0F : 0.0F, now);
        float line = graphics.scale().snapThickness(1.0F);
        Box minus = new Box(box.x(), box.y(), BUTTON_WIDTH, box.height());
        Box plus = new Box(box.right() - BUTTON_WIDTH, box.y(), BUTTON_WIDTH, box.height());
        this.renderButton(graphics, minus, this.minusHover.value(now), false);
        this.renderButton(graphics, plus, this.plusHover.value(now), true);
        graphics.fill(minus.right() - line, box.y() + line, line, box.height() - line * 2.0F, ArkColors.BORDER_DEFAULT);
        graphics.fill(plus.x(), box.y() + line, line, box.height() - line * 2.0F, ArkColors.BORDER_DEFAULT);
        TextMetrics metrics = graphics.metrics();
        String text = metrics.ellipsize(this.model.label().getString(), VALUE, box.width() - (BUTTON_WIDTH + VALUE_PADDING) * 2.0F);
        graphics.text(text, box.centerX() - metrics.width(text, VALUE) * 0.5F, box.centerY() - metrics.capHeight(VALUE) * 0.5F, VALUE, ArkColors.TEXT_PRIMARY);
    }

    private void renderButton(UiGraphics graphics, Box area, float hover, boolean plus) {
        float line = graphics.scale().snapThickness(1.0F);
        graphics.fill(area.inset(line), ArkColors.multiplyAlpha(ArkColors.CONTROL_HOVER, hover));
        int color = ArkColors.lerp(hover, ArkColors.TEXT_MUTED, ArkColors.TEXT_PRIMARY);
        graphics.fill(area.centerX() - SIGN_LENGTH * 0.5F, area.centerY() - SIGN_THICKNESS * 0.5F, SIGN_LENGTH, SIGN_THICKNESS, color);
        if (plus) {
            graphics.fill(area.centerX() - SIGN_THICKNESS * 0.5F, area.centerY() - SIGN_LENGTH * 0.5F, SIGN_THICKNESS, SIGN_LENGTH, color);
        }
    }

    private void repeatWhileHeld(long now) {
        if (this.direction == 0 || now - this.holdStart < REPEAT_DELAY || now - this.lastRepeat < REPEAT_INTERVAL) {
            return;
        }
        this.lastRepeat = now;
        this.repeated = true;
        this.model.step(this.direction, false);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!super.mouseClicked(event, doubleClick)) {
            return false;
        }
        float x = this.localX(this.host.uiScale().toDesign(event.x()));
        Box box = this.bounds();
        this.direction = x < box.x() + BUTTON_WIDTH ? -1 : x >= box.right() - BUTTON_WIDTH ? 1 : 0;
        this.holdStart = this.host.now();
        this.lastRepeat = this.holdStart;
        this.repeated = false;
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        boolean handled = super.mouseReleased(event);
        this.direction = 0;
        return handled;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!this.isActive() || !this.isFocused()) {
            return false;
        }
        int step = event.isLeft() || event.isDown() ? -1 : event.isRight() || event.isUp() ? 1 : 0;
        if (step == 0) {
            return event.key() == InputConstants.KEY_RETURN || event.key() == InputConstants.KEY_SPACE;
        }
        this.model.step(step, event.hasShiftDown());
        return true;
    }

    @Override
    protected void onPress() {
        if (this.direction != 0 && !this.repeated) {
            this.model.step(this.direction, false);
        }
    }

    @Override
    protected Component narrationMessage() {
        return CommonComponents.optionNameValue(this.name, this.model.label());
    }
}
