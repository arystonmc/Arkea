package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Meter;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.ArkWidget;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;

public final class DialogProgress implements DialogBody {
    private static final float HEIGHT = 30.0F;
    private static final float BAR_HEIGHT = 4.0F;
    private static final float PERCENT = 100.0F;
    private static final int FILL_TIME = 250;
    private static final TextStyle LABEL = TextStyle.of(11.0F);

    private final Supplier<Component> label;
    private final DoubleSupplier progress;
    private final Transition shown = new Transition(0.0F, FILL_TIME, Easing.EASE_OUT);
    private Box area = Box.EMPTY;

    public DialogProgress(Supplier<Component> label, DoubleSupplier progress) {
        this.label = label;
        this.progress = progress;
    }

    @Override
    public float height(float width) {
        return HEIGHT;
    }

    @Override
    public void layout(Box newArea) {
        this.area = newArea;
    }

    @Override
    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        TextMetrics metrics = graphics.metrics();
        float target = (float) Math.clamp(this.progress.getAsDouble(), 0.0, 1.0);
        this.shown.setTarget(target, graphics.now());
        float fraction = this.shown.value(graphics.now());
        graphics.text(this.label.get().getString(), this.area.x(), this.area.y(), LABEL, ArkColors.TEXT_SOFT);
        String percent = Math.round(target * PERCENT) + "%";
        graphics.text(percent, this.area.right() - metrics.width(percent, LABEL), this.area.y(), LABEL, ArkColors.TEXT_MUTED);
        Box bar = new Box(this.area.x(), this.area.bottom() - BAR_HEIGHT, this.area.width(), BAR_HEIGHT);
        Meter.progress(graphics, bar, fraction, Theme.accent().base());
    }

    @Override
    public List<? extends ArkWidget> widgets() {
        return List.of();
    }
}
