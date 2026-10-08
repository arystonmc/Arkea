package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import java.util.function.BooleanSupplier;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class ArkDisclosure extends ArkWidget {
    public static final float HEIGHT = 40.0F;
    public static final float INDENT = 18.0F;
    private static final float PADDING = 14.0F;
    private static final float CHEVRON_WIDTH = 8.0F;
    private static final float CHEVRON_HEIGHT = 5.0F;
    private static final float GAP = 10.0F;
    private static final int ROTATE_TIME = 180;
    private static final float HALF_TURN = (float) Math.PI;
    private static final TextStyle LABEL = TextStyle.of(12.0F);
    private static final TextStyle COUNT = TextStyle.of(10.0F);

    private final Component label;
    private final BooleanSupplier expanded;
    private final Runnable toggle;
    private final Transition rotation;
    private @Nullable Component detail;
    private @Nullable Tag tag;

    public ArkDisclosure(UiHost host, Component label, BooleanSupplier expanded, Runnable toggle) {
        super(host);
        this.label = label;
        this.expanded = expanded;
        this.toggle = toggle;
        this.rotation = new Transition(expanded.getAsBoolean() ? 1.0F : 0.0F, ROTATE_TIME, Easing.EASE);
    }

    public ArkDisclosure detail(Component text) {
        this.detail = text;
        return this;
    }

    public ArkDisclosure tag(Tag value) {
        this.tag = value;
        return this;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        float hover = this.hoverProgress();
        this.rotation.setTarget(this.expanded.getAsBoolean() ? 1.0F : 0.0F, graphics.now());
        graphics.fill(box, ArkColors.lerp(hover, ArkColors.ROW, ArkColors.ROW_HOVER));
        graphics.border(box, 1.0F, ArkColors.lerp(hover, ArkColors.BORDER_SUBTLE, ArkColors.BORDER_OVERLAY));
        float chevronX = box.x() + PADDING;
        graphics.push();
        graphics.rotateAround(-HALF_TURN * 0.5F * (1.0F - this.rotation.value(graphics.now())), chevronX + CHEVRON_WIDTH * 0.5F, box.centerY());
        graphics.icon(Icons.CHEVRON_DOWN, chevronX, box.centerY() - CHEVRON_HEIGHT * 0.5F, CHEVRON_WIDTH, CHEVRON_HEIGHT, ArkColors.TEXT_MUTED);
        graphics.pop();
        TextMetrics metrics = graphics.metrics();
        float textX = chevronX + CHEVRON_WIDTH + GAP;
        int textColor = ArkColors.lerp(hover, ArkColors.TEXT_SOFT, ArkColors.TEXT_PRIMARY);
        graphics.text(this.label.getString(), textX, box.centerY() - metrics.capHeight(LABEL) * 0.5F, LABEL, textColor);
        float right = box.right() - PADDING;
        if (this.tag != null) {
            this.tag.draw(graphics, right - this.tag.width(metrics), box.centerY());
        } else if (this.detail != null) {
            String detailText = this.detail.getString();
            graphics.text(detailText, right - metrics.width(detailText, COUNT), box.centerY() - metrics.capHeight(COUNT) * 0.5F, COUNT, ArkColors.TEXT_FAINT);
        }
    }

    @Override
    protected void onPress() {
        this.toggle.run();
    }

    @Override
    protected Component narrationMessage() {
        return CommonComponents.optionNameValue(this.label, this.expanded.getAsBoolean() ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
    }
}
