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
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
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
    private static final float GLOW_BLUR = 8.0F;
    private static final float HANDLE_SHADOW_BLUR = 6.0F;
    private static final float HANDLE_SHADOW_OFFSET = 2.0F;
    private static final float HANDLE_HOVER_BRIGHTNESS = 0.06F;
    private static final int FILL_ANIMATION = 250;
    private static final int LARGE_STEP = 10;
    private static final int HANDLE = ArkColors.rgb(0xF0F0F0);
    private static final int HANDLE_SHADOW = ArkColors.rgba(0, 0, 0, 0.50F);
    private static final TextStyle VALUE = TextStyle.of(11.0F);

    private final Component name;
    private final SliderRange range;
    private final IntSupplier getter;
    private final IntConsumer setter;
    private final IntFunction<Component> label;
    private final Transition position;
    private boolean dragging;

    public ArkSlider(UiHost host, Component name, SliderRange range, SliderBinding binding) {
        super(host);
        this.name = name;
        this.range = range;
        this.getter = binding.getter();
        this.setter = binding.setter();
        this.label = binding.label();
        this.position = new Transition(range.fraction(this.getter.getAsInt()), FILL_ANIMATION, Easing.EASE_OUT);
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        float target = this.range.fraction(this.getter.getAsInt());
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
        Box handle = new Box(track.x() + filled - HANDLE_WIDTH * 0.5F, box.centerY() - HEIGHT * 0.5F, HANDLE_WIDTH, HEIGHT);
        graphics.shadow(handle, HANDLE_SHADOW_BLUR, HANDLE_SHADOW_OFFSET, HANDLE_SHADOW);
        graphics.fill(handle, ArkColors.brighten(HANDLE, 1.0F + HANDLE_HOVER_BRIGHTNESS * this.hoverProgress()));
        String text = this.label.apply(this.getter.getAsInt()).getString();
        float textX = box.right() - graphics.metrics().width(text, VALUE);
        graphics.text(text, textX, box.centerY() - graphics.metrics().capHeight(VALUE) * 0.5F, VALUE, ArkColors.TEXT_PRIMARY);
    }

    private Box trackBox(TextMetrics metrics) {
        Box box = this.bounds();
        float valueWidth = Math.max(VALUE_WIDTH, Math.max(this.labelWidth(metrics, this.range.min()), this.labelWidth(metrics, this.range.max())));
        return new Box(box.x(), box.y(), box.width() - valueWidth - VALUE_GAP, box.height());
    }

    private float labelWidth(TextMetrics metrics, int value) {
        return metrics.width(this.label.apply(value).getString(), VALUE);
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
        int step = this.range.step() * (event.hasShiftDown() ? LARGE_STEP : 1);
        this.setter.accept(this.range.clamp(this.getter.getAsInt() + direction * step));
        return true;
    }

    private void setFromMouse(double guiX) {
        float canvasX = this.host.uiScale().toDesign(guiX);
        Box hit = this.hitBox();
        Box track = this.trackBox(this.host.metrics());
        float scale = hit.width() / this.bounds().width();
        float trackLeft = hit.x() + (track.x() - this.bounds().x()) * scale;
        float fraction = (canvasX - trackLeft) / (track.width() * scale);
        this.setter.accept(this.range.valueAt(fraction));
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
        Component value = this.label.apply(this.getter.getAsInt());
        output.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.slider", Component.empty().append(this.name).append(": ").append(value)));
        if (this.isActive()) {
            String usage = this.isFocused() ? "narration.slider.usage.focused" : "narration.slider.usage.hovered";
            output.add(NarratedElementType.USAGE, Component.translatable(usage));
        }
    }
}
