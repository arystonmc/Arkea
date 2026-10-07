package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class SettingRow {
    public static final float HEIGHT = 52.0F;
    public static final float PADDING_RIGHT = 12.0F;
    private static final float PADDING_LEFT = 14.0F;
    private static final float ICON_BOX = 28.0F;
    private static final float ICON_SIZE = 14.0F;
    private static final float GAP = 12.0F;
    private static final float LINE_GAP = 6.0F;
    private static final float CONTROL_SPACE = 160.0F;
    private static final int ICON_TIME = 200;
    private static final int ICON_FILL = ArkColors.rgba(255, 255, 255, 0.04F);
    private static final int ICON_BORDER = ArkColors.rgba(255, 255, 255, 0.07F);
    private static final TextStyle NAME = TextStyle.of(13.0F);
    private static final TextStyle DESCRIPTION = TextStyle.of(10.0F);

    private final Icon icon;
    private final Component name;
    private final Component description;
    private final Transition hover = new Transition(0.0F, Motion.HOVER, Easing.EASE);
    private final Transition lit;
    private BooleanSupplier litWhen = () -> true;
    private Supplier<@Nullable Component> tooltip = () -> null;
    private Box bounds = Box.EMPTY;
    private float controlSpace = CONTROL_SPACE;
    private boolean hovered;

    public SettingRow(ItemContent content) {
        this.icon = content.icon();
        this.name = content.label();
        this.description = content.sub() != null ? content.sub() : Component.empty();
        this.lit = new Transition(1.0F, ICON_TIME, Easing.EASE);
    }

    public SettingRow litWhen(BooleanSupplier condition) {
        this.litWhen = condition;
        this.lit.snap(condition.getAsBoolean() ? 1.0F : 0.0F);
        return this;
    }

    public SettingRow tooltip(Supplier<@Nullable Component> text) {
        this.tooltip = text;
        return this;
    }

    public @Nullable Component tooltip() {
        return this.tooltip.get();
    }

    public Component name() {
        return this.name;
    }

    public boolean isHovered() {
        return this.hovered;
    }

    public Box bounds() {
        return this.bounds;
    }

    public void setBounds(Box newBounds) {
        this.bounds = newBounds;
    }

    public Box controlSlot(float controlWidth, float controlHeight) {
        this.controlSpace = Math.max(CONTROL_SPACE, controlWidth);
        float x = this.bounds.right() - PADDING_RIGHT - controlWidth;
        return new Box(x, this.bounds.centerY() - controlHeight * 0.5F, controlWidth, controlHeight);
    }

    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        this.render(graphics, mouseX, mouseY, 1.0F);
    }

    public void render(UiGraphics graphics, float mouseX, float mouseY, float contentOpacity) {
        Box box = this.bounds;
        long now = graphics.now();
        this.hovered = graphics.visible(graphics.canvasBox(box)).contains(mouseX, mouseY);
        this.hover.setTarget(this.hovered ? 1.0F : 0.0F, now);
        this.lit.setTarget(this.litWhen.getAsBoolean() ? 1.0F : 0.0F, now);
        float hover = this.hover.value(now);
        graphics.fill(box, ArkColors.lerp(hover, ArkColors.ROW, ArkColors.ROW_HOVER));
        graphics.border(box, 1.0F, ArkColors.lerp(hover, ArkColors.BORDER_SUBTLE, ArkColors.BORDER_OVERLAY));
        graphics.push();
        graphics.fade(contentOpacity);
        Box iconBox = new Box(box.x() + PADDING_LEFT, box.centerY() - ICON_BOX * 0.5F, ICON_BOX, ICON_BOX);
        graphics.fill(iconBox, ICON_FILL);
        graphics.border(iconBox, 1.0F, ICON_BORDER);
        float iconOffset = (ICON_BOX - ICON_SIZE) * 0.5F;
        int iconColor = ArkColors.lerp(this.lit.value(now), ArkColors.TEXT_LABEL, Theme.accent().light());
        graphics.icon(this.icon, iconBox.x() + iconOffset, iconBox.y() + iconOffset, ICON_SIZE, ICON_SIZE, iconColor);
        TextMetrics metrics = graphics.metrics();
        float textX = iconBox.right() + GAP;
        float textWidth = box.right() - PADDING_RIGHT - this.controlSpace - GAP - textX;
        float blockHeight = metrics.capHeight(NAME) + LINE_GAP + metrics.capHeight(DESCRIPTION);
        float nameY = box.centerY() - blockHeight * 0.5F;
        graphics.text(metrics.ellipsize(this.name.getString(), NAME, textWidth), textX, nameY, NAME, ArkColors.TEXT_PRIMARY);
        float descriptionY = nameY + metrics.capHeight(NAME) + LINE_GAP;
        String description = metrics.ellipsize(this.description.getString(), DESCRIPTION, textWidth);
        graphics.text(description, textX, descriptionY, DESCRIPTION, ArkColors.TEXT_DESCRIPTION);
        graphics.pop();
    }
}
