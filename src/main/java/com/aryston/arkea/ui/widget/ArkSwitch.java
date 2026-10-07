package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ArkSwitch extends ArkWidget {
    public static final float WIDTH = 64.0F;
    public static final float HEIGHT = 18.0F;
    private static final float LABEL_WIDTH = 26.0F;
    private static final float TRACK_WIDTH = 32.0F;
    private static final float PADDING = 3.0F;
    private static final float KNOB = 12.0F;
    private static final float KNOB_TRAVEL = 14.0F;
    private static final float HOVER_BRIGHTNESS = 0.08F;
    private static final int COLOR_TIME = 180;
    private static final int KNOB_TIME = 200;
    private static final TextStyle LABEL = TextStyle.of(11.0F);
    private static final Component ON = Component.translatable("arkea.switch.on");
    private static final Component OFF = Component.translatable("arkea.switch.off");

    private final Component name;
    private final BooleanSupplier getter;
    private final Consumer<Boolean> setter;
    private final Transition color;
    private final Transition knob;

    public ArkSwitch(UiHost host, Component name, BooleanSupplier getter, Consumer<Boolean> setter) {
        super(host);
        this.name = name;
        this.getter = getter;
        this.setter = setter;
        float initial = getter.getAsBoolean() ? 1.0F : 0.0F;
        this.color = new Transition(initial, COLOR_TIME, Easing.EASE);
        this.knob = new Transition(initial, KNOB_TIME, Easing.SPRING);
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        boolean on = this.getter.getAsBoolean();
        float target = on ? 1.0F : 0.0F;
        this.color.setTarget(target, graphics.now());
        this.knob.setTarget(target, graphics.now());
        float amount = this.color.value(graphics.now());
        String label = (on ? ON : OFF).getString();
        float textY = box.centerY() - graphics.metrics().capHeight(LABEL) * 0.5F;
        float labelRight = box.x() + LABEL_WIDTH;
        graphics.text(label, labelRight - graphics.metrics().width(label, LABEL), textY, LABEL,
            ArkColors.lerp(amount, ArkColors.TEXT_FAINT, ArkColors.TEXT_PRIMARY));
        Box track = new Box(box.right() - TRACK_WIDTH, box.centerY() - HEIGHT * 0.5F, TRACK_WIDTH, HEIGHT);
        int trackColor = ArkColors.lerp(amount, ArkColors.TRACK, Theme.accent().base());
        graphics.fill(track, ArkColors.brighten(trackColor, 1.0F + HOVER_BRIGHTNESS * this.hoverProgress()));
        float knobX = track.x() + PADDING + KNOB_TRAVEL * this.knob.value(graphics.now());
        graphics.fill(new Box(knobX, track.y() + PADDING, KNOB, KNOB), ArkColors.lerp(amount, ArkColors.CONTROL_IDLE, ArkColors.TEXT_PRIMARY));
    }

    @Override
    protected void onPress() {
        this.setter.accept(!this.getter.getAsBoolean());
    }

    @Override
    protected Component narrationMessage() {
        return CommonComponents.optionNameValue(this.name, this.getter.getAsBoolean() ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
    }
}
