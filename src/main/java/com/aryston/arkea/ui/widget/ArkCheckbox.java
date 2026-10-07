package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ArkCheckbox extends ArkWidget {
    public static final float BOX = 16.0F;
    private static final float GAP = 10.0F;
    private static final float CHECK_WIDTH = 10.0F;
    private static final float CHECK_HEIGHT = 8.0F;
    private static final int FILL_TIME = 200;
    private static final int IDLE_FILL = ArkColors.rgba(0, 0, 0, 0.30F);
    private static final int HOVER_FILL = ArkColors.rgba(255, 255, 255, 0.06F);
    private static final int IDLE_BORDER = ArkColors.rgba(255, 255, 255, 0.25F);
    private static final int CHECKED_HIGHLIGHT = ArkColors.rgba(255, 255, 255, 0.20F);
    private static final TextStyle LABEL = TextStyle.of(11.0F);

    private final Component label;
    private final BooleanSupplier getter;
    private final Consumer<Boolean> setter;
    private final Transition fill;

    public ArkCheckbox(UiHost host, Component label, BooleanSupplier getter, Consumer<Boolean> setter) {
        super(host);
        this.label = label;
        this.getter = getter;
        this.setter = setter;
        this.fill = new Transition(getter.getAsBoolean() ? 1.0F : 0.0F, FILL_TIME, Easing.EASE);
    }

    public float preferredWidth() {
        return BOX + GAP + this.host.metrics().width(this.label.getString(), LABEL);
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box bounds = this.bounds();
        boolean checked = this.getter.getAsBoolean();
        this.fill.setTarget(checked ? 1.0F : 0.0F, graphics.now());
        float amount = this.fill.value(graphics.now());
        float hover = this.hoverProgress();
        Box box = new Box(bounds.x(), bounds.centerY() - BOX * 0.5F, BOX, BOX);
        int accent = Theme.accent().base();
        int idle = ArkColors.lerp(hover, IDLE_FILL, HOVER_FILL);
        graphics.fill(box, ArkColors.lerp(amount, idle, accent));
        if (amount < 1.0F) {
            int border = ArkColors.lerp(hover, IDLE_BORDER, Theme.accent().light());
            graphics.border(box, 1.0F, ArkColors.multiplyAlpha(border, 1.0F - amount));
        }
        if (amount > 0.0F) {
            graphics.fill(box.x(), box.y(), BOX, 1.0F, ArkColors.multiplyAlpha(CHECKED_HIGHLIGHT, amount));
            graphics.icon(Icons.CHECK, box.centerX() - CHECK_WIDTH * 0.5F, box.centerY() - CHECK_HEIGHT * 0.5F, CHECK_WIDTH, CHECK_HEIGHT,
                ArkColors.multiplyAlpha(ArkColors.TEXT_PRIMARY, amount));
        }
        int textColor = ArkColors.lerp(hover, ArkColors.TEXT_SOFT, ArkColors.TEXT_PRIMARY);
        graphics.text(this.label.getString(), box.right() + GAP, bounds.centerY() - graphics.metrics().capHeight(LABEL) * 0.5F, LABEL, textColor);
    }

    @Override
    protected void onPress() {
        this.setter.accept(!this.getter.getAsBoolean());
    }

    @Override
    protected Component narrationMessage() {
        return CommonComponents.optionNameValue(this.label, this.getter.getAsBoolean() ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
    }
}
