package com.aryston.arkea.ui.widget;

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

public class ArkRangeSlider extends ArkWidget {
    public static final float WIDTH = 240.0F;
    public static final float HEIGHT = 14.0F;
    private static final float VALUE_WIDTH = 72.0F;
    private static final float VALUE_GAP = 10.0F;
    private static final float TRACK_HEIGHT = 2.0F;
    private static final float HANDLE_WIDTH = 6.0F;
    private static final float GLOW_BLUR = 8.0F;
    private static final float HANDLE_SHADOW_BLUR = 6.0F;
    private static final float HANDLE_SHADOW_OFFSET = 2.0F;
    private static final float ACTIVE_BRIGHTNESS = 0.08F;
    private static final int HANDLE = ArkColors.rgb(0xF0F0F0);
    private static final int HANDLE_SHADOW = ArkColors.rgba(0, 0, 0, 0.50F);
    private static final TextStyle VALUE = TextStyle.of(11.0F);

    private final Component name;
    private final RangeModel model;
    private boolean dragging;
    private boolean highActive;

    public ArkRangeSlider(UiHost host, Component name, RangeModel model) {
        super(host);
        this.name = name;
        this.model = model;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        Box track = this.trackBox(graphics.metrics());
        float trackY = box.centerY() - TRACK_HEIGHT * 0.5F;
        float lowX = track.x() + track.width() * this.model.low();
        float highX = track.x() + track.width() * this.model.high();
        graphics.fill(track.x(), trackY, track.width(), TRACK_HEIGHT, ArkColors.TRACK);
        graphics.shadow(new Box(lowX, trackY, highX - lowX, TRACK_HEIGHT), GLOW_BLUR, 0.0F, Theme.accent().glow());
        graphics.fill(lowX, trackY, highX - lowX, TRACK_HEIGHT, Theme.accent().base());
        this.renderHandle(graphics, lowX, box.centerY(), !this.highActive);
        this.renderHandle(graphics, highX, box.centerY(), this.highActive);
        String text = this.model.label().getString();
        float textX = box.right() - graphics.metrics().width(text, VALUE);
        graphics.text(text, textX, box.centerY() - graphics.metrics().capHeight(VALUE) * 0.5F, VALUE, ArkColors.TEXT_MUTED);
    }

    private void renderHandle(UiGraphics graphics, float x, float centerY, boolean active) {
        Box handle = new Box(x - HANDLE_WIDTH * 0.5F, centerY - HEIGHT * 0.5F, HANDLE_WIDTH, HEIGHT);
        graphics.shadow(handle, HANDLE_SHADOW_BLUR, HANDLE_SHADOW_OFFSET, HANDLE_SHADOW);
        float glow = active ? this.hoverProgress() : 0.0F;
        graphics.fill(handle, ArkColors.brighten(HANDLE, 1.0F + ACTIVE_BRIGHTNESS * glow));
    }

    private Box trackBox(TextMetrics metrics) {
        Box box = this.bounds();
        float valueWidth = Math.max(VALUE_WIDTH, metrics.width(this.model.label().getString(), VALUE));
        return new Box(box.x() + HANDLE_WIDTH * 0.5F, box.y(), box.width() - valueWidth - VALUE_GAP - HANDLE_WIDTH, box.height());
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!super.mouseClicked(event, doubleClick)) {
            return false;
        }
        float fraction = this.fractionAt(event.x());
        this.highActive = Math.abs(fraction - this.model.high()) < Math.abs(fraction - this.model.low())
            || fraction > this.model.high();
        this.dragging = true;
        this.apply(fraction);
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (!this.dragging || event.button() != InputConstants.MOUSE_BUTTON_LEFT) {
            return false;
        }
        this.apply(this.fractionAt(event.x()));
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        boolean wasDragging = this.dragging;
        this.dragging = false;
        super.mouseReleased(event);
        return wasDragging;
    }

    private void apply(float fraction) {
        if (this.highActive) {
            this.model.setHigh(fraction);
        } else {
            this.model.setLow(fraction);
        }
    }

    private float fractionAt(double guiX) {
        float localX = this.localX(this.host.uiScale().toDesign(guiX));
        Box track = this.trackBox(this.host.metrics());
        return Math.clamp((localX - track.x()) / track.width(), 0.0F, 1.0F);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!this.isActive() || !this.isFocused()) {
            return false;
        }
        if (event.isSelection()) {
            this.highActive = !this.highActive;
            return true;
        }
        int direction = event.isLeft() ? -1 : event.isRight() ? 1 : 0;
        if (direction == 0) {
            return false;
        }
        this.model.step(this.highActive, direction, event.hasShiftDown());
        return true;
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
        output.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.slider", Component.empty().append(this.name).append(": ").append(this.model.label())));
        if (this.isActive()) {
            output.add(NarratedElementType.USAGE, Component.translatable("arkea.range.usage"));
        }
    }
}
