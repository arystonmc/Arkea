package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ArkSlider extends ArkWidget {
    public static final float WIDTH = 160.0F;
    public static final float HEIGHT = 14.0F;
    private static final float VALUE_WIDTH = 52.0F;
    private static final float VALUE_GAP = 10.0F;
    private static final float TRACK_HEIGHT = 2.0F;
    private static final float HANDLE_WIDTH = 6.0F;
    private static final float TICK_HEIGHT = 8.0F;
    private static final float GLOW_BLUR = 8.0F;
    private static final float HANDLE_SHADOW_BLUR = 6.0F;
    private static final float HANDLE_SHADOW_OFFSET = 2.0F;
    private static final float HANDLE_HOVER_BRIGHTNESS = 0.06F;
    private static final int FILL_ANIMATION = 250;
    private static final int HANDLE = ArkColors.rgb(0xF0F0F0);
    private static final int HANDLE_SHADOW = ArkColors.rgba(0, 0, 0, 0.50F);
    private static final TextStyle VALUE = TextStyle.of(11.0F);

    private final Component name;
    private final SliderModel model;
    private final Transition position;
    private boolean dragging;

    public ArkSlider(UiHost host, Component name, SliderModel model) {
        super(host);
        this.name = name;
        this.model = model;
        this.position = new Transition(model.fraction(), FILL_ANIMATION, Easing.EASE_OUT);
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        float target = this.model.fraction();
        if (this.dragging) {
            this.position.snap(target);
        } else {
            this.position.setTarget(target, graphics.now());
        }
        float fraction = this.position.value(graphics.now());
        Box track = this.trackBox(graphics.metrics());
        float trackY = box.centerY() - TRACK_HEIGHT * 0.5F;
        float filled = track.width() * fraction;
        graphics.fill(track.x(), trackY, track.width(), TRACK_HEIGHT, ArkColors.TRACK);
        graphics.shadow(new Box(track.x(), trackY, filled, TRACK_HEIGHT), GLOW_BLUR, 0.0F, Theme.accent().glow());
        graphics.fill(track.x(), trackY, filled, TRACK_HEIGHT, Theme.accent().base());
        this.renderTicks(graphics, track, box.centerY(), fraction);
        Box handle = new Box(track.x() + filled - HANDLE_WIDTH * 0.5F, box.centerY() - HEIGHT * 0.5F, HANDLE_WIDTH, HEIGHT);
        graphics.shadow(handle, HANDLE_SHADOW_BLUR, HANDLE_SHADOW_OFFSET, HANDLE_SHADOW);
        graphics.fill(handle, ArkColors.brighten(HANDLE, 1.0F + HANDLE_HOVER_BRIGHTNESS * this.hoverProgress()));
        String text = this.model.label().getString();
        float textX = box.right() - graphics.metrics().width(text, VALUE);
        graphics.text(text, textX, box.centerY() - graphics.metrics().capHeight(VALUE) * 0.5F, VALUE, ArkColors.TEXT_PRIMARY);
    }

    private void renderTicks(UiGraphics graphics, Box track, float centerY, float fraction) {
        int ticks = this.model.ticks();
        if (ticks < 2) {
            return;
        }
        float line = graphics.scale().snapThickness(1.0F);
        for (int index = 0; index < ticks; index++) {
            float position = index / (float) (ticks - 1);
            int color = position <= fraction ? Theme.accent().light() : ArkColors.TRACK;
            graphics.fill(track.x() + track.width() * position - line * 0.5F, centerY - TICK_HEIGHT * 0.5F, line, TICK_HEIGHT, color);
        }
    }

    private Box trackBox(TextMetrics metrics) {
        Box box = this.bounds();
        float valueWidth = Math.max(VALUE_WIDTH, Math.max(this.labelWidth(metrics, 0.0F), this.labelWidth(metrics, 1.0F)));
        return new Box(box.x(), box.y(), box.width() - valueWidth - VALUE_GAP, box.height());
    }

    private float labelWidth(TextMetrics metrics, float fraction) {
        return metrics.width(this.model.labelAt(fraction).getString(), VALUE);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!super.mouseClicked(event, doubleClick)) {
            return false;
        }
        this.dragging = true;
        this.setFromMouse(event.x());
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (!this.dragging || event.button() != InputConstants.MOUSE_BUTTON_LEFT) {
            return false;
        }
        this.setFromMouse(event.x());
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        boolean wasDragging = this.dragging;
        this.dragging = false;
        super.mouseReleased(event);
        if (wasDragging) {
            this.model.release();
        }
        return wasDragging;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!this.isActive() || !this.isFocused()) {
            return false;
        }
        int direction = event.isLeft() ? -1 : event.isRight() ? 1 : 0;
        if (direction == 0) {
            return false;
        }
        this.model.step(direction, event.hasShiftDown());
        return true;
    }

    private void setFromMouse(double guiX) {
        float localX = this.localX(this.host.uiScale().toDesign(guiX));
        Box track = this.trackBox(this.host.metrics());
        this.model.drag(Math.clamp((localX - track.x()) / track.width(), 0.0F, 1.0F));
    }

    @Override
    public void activate() {
    }

    @Override
    protected void onPress() {
    }

    @Override
    protected Component narrationMessage() {
        return this.name;
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
        Component value = this.model.label();
        output.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.slider", Component.empty().append(this.name).append(": ").append(value)));
        if (this.isActive()) {
            String usage = this.isFocused() ? "narration.slider.usage.focused" : "narration.slider.usage.hovered";
            output.add(NarratedElementType.USAGE, Component.translatable(usage));
        }
    }
}
