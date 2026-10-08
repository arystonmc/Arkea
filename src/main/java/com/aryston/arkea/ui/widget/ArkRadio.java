package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import java.util.function.BooleanSupplier;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ArkRadio extends ArkWidget {
    public static final float BOX = 16.0F;
    public static final float HEIGHT = 20.0F;
    private static final float GAP = 10.0F;
    private static final float DOT = 8.0F;
    private static final float POP_START = 0.4F;
    private static final int IDLE_FILL = ArkColors.rgba(0, 0, 0, 0.30F);
    private static final int HOVER_FILL = ArkColors.rgba(255, 255, 255, 0.06F);
    private static final int IDLE_BORDER = ArkColors.rgba(255, 255, 255, 0.25F);
    private static final TextStyle LABEL = TextStyle.of(12.0F);

    private final Component label;
    private final BooleanSupplier selected;
    private final Runnable select;
    private final Transition dot;

    public ArkRadio(UiHost host, Component label, BooleanSupplier selected, Runnable select) {
        super(host);
        this.label = label;
        this.selected = selected;
        this.select = select;
        this.dot = new Transition(selected.getAsBoolean() ? 1.0F : 0.0F, Motion.CHECK_POP, Easing.SPRING);
    }

    public float preferredWidth() {
        return BOX + GAP + this.host.metrics().width(this.label.getString(), LABEL);
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box bounds = this.bounds();
        boolean on = this.selected.getAsBoolean();
        this.dot.setTarget(on ? 1.0F : 0.0F, graphics.now());
        float amount = this.dot.value(graphics.now());
        float hover = this.hoverProgress();
        Box box = new Box(bounds.x(), bounds.centerY() - BOX * 0.5F, BOX, BOX);
        graphics.fill(box, ArkColors.lerp(hover, IDLE_FILL, HOVER_FILL));
        int idleBorder = ArkColors.lerp(hover, IDLE_BORDER, Theme.accent().light());
        graphics.border(box, 1.0F, ArkColors.lerp(Math.clamp(amount, 0.0F, 1.0F), idleBorder, Theme.accent().base()));
        if (amount > 0.0F) {
            float size = DOT * (POP_START + (1.0F - POP_START) * amount);
            Box inner = new Box(box.centerX() - size * 0.5F, box.centerY() - size * 0.5F, size, size);
            graphics.fill(inner, ArkColors.multiplyAlpha(Theme.accent().light(), Math.clamp(amount, 0.0F, 1.0F)));
        }
        int textColor = on ? ArkColors.TEXT_PRIMARY : ArkColors.lerp(hover, ArkColors.TEXT_SOFT, ArkColors.TEXT_PRIMARY);
        graphics.text(this.label.getString(), box.right() + GAP, bounds.centerY() - graphics.metrics().capHeight(LABEL) * 0.5F, LABEL, textColor);
    }

    @Override
    protected void onPress() {
        if (!this.selected.getAsBoolean()) {
            this.select.run();
        }
    }

    @Override
    protected Component narrationMessage() {
        return CommonComponents.optionNameValue(this.label, this.selected.getAsBoolean() ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
    }
}
