package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import java.util.function.BooleanSupplier;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ArkChip extends ArkWidget {
    public static final float HEIGHT = 28.0F;
    public static final float GAP = 6.0F;
    private static final float PADDING = 10.0F;
    private static final float CHECK_WIDTH = 7.0F;
    private static final float CHECK_HEIGHT = 5.0F;
    private static final float CHECK_GAP = 8.0F;
    private static final float DASH = 3.0F;
    private static final int IDLE_FILL = ArkColors.rgba(255, 255, 255, 0.03F);
    private static final int IDLE_BORDER = ArkColors.rgba(255, 255, 255, 0.10F);
    private static final int ADD_BORDER = ArkColors.rgba(255, 255, 255, 0.20F);
    private static final TextStyle LABEL = TextStyle.of(11.0F);

    private final Component label;
    private final BooleanSupplier selected;
    private final Runnable action;
    private final boolean addChip;
    private final Transition amount;

    private ArkChip(UiHost host, Component label, BooleanSupplier selected, Runnable action, boolean addChip) {
        super(host);
        this.label = label;
        this.selected = selected;
        this.action = action;
        this.addChip = addChip;
        this.amount = new Transition(selected.getAsBoolean() ? 1.0F : 0.0F, Motion.CHECK_POP, Easing.EASE);
    }

    public static ArkChip toggle(UiHost host, Component label, BooleanSupplier selected, Runnable toggle) {
        return new ArkChip(host, label, selected, toggle, false);
    }

    public static ArkChip add(UiHost host, Component label, Runnable action) {
        return new ArkChip(host, Component.literal("+ ").append(label), () -> false, action, true);
    }

    public float preferredWidth() {
        float text = this.host.metrics().width(this.label.getString(), LABEL);
        return PADDING * 2.0F + text + (this.addChip ? 0.0F : CHECK_WIDTH + CHECK_GAP);
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        boolean on = this.selected.getAsBoolean();
        this.amount.setTarget(on ? 1.0F : 0.0F, graphics.now());
        float selection = this.amount.value(graphics.now());
        float hover = this.hoverProgress();
        int idleFill = ArkColors.lerp(hover, IDLE_FILL, ArkColors.ROW_HOVER);
        graphics.fill(box, ArkColors.lerp(selection, idleFill, Theme.accent().tint()));
        if (this.addChip) {
            graphics.dashedBorder(box, DASH, ArkColors.lerp(hover, ADD_BORDER, ArkColors.TEXT_MUTED));
        } else {
            int idleBorder = ArkColors.lerp(hover, IDLE_BORDER, ArkColors.BORDER_TOOLTIP);
            graphics.border(box, 1.0F, ArkColors.lerp(selection, idleBorder, Theme.accent().base()));
        }
        float x = box.x() + PADDING;
        if (!this.addChip) {
            if (selection > 0.0F) {
                graphics.icon(Icons.CHECK, x, box.centerY() - CHECK_HEIGHT * 0.5F, CHECK_WIDTH, CHECK_HEIGHT,
                    ArkColors.multiplyAlpha(Theme.accent().light(), selection));
            }
            x += (CHECK_WIDTH + CHECK_GAP) * selection;
        }
        int textColor = ArkColors.lerp(Math.max(selection, hover), ArkColors.TEXT_MUTED, ArkColors.TEXT_PRIMARY);
        graphics.text(this.label.getString(), x, box.centerY() - graphics.metrics().capHeight(LABEL) * 0.5F, LABEL, textColor);
    }

    @Override
    protected void onPress() {
        this.action.run();
    }

    @Override
    protected Component narrationMessage() {
        if (this.addChip) {
            return this.label;
        }
        return CommonComponents.optionNameValue(this.label, this.selected.getAsBoolean() ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
    }
}
