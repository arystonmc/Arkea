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
import net.minecraft.network.chat.Component;

public final class SettingRow {
    public static final float HEIGHT = 52.0F;
    public static final float PADDING_RIGHT = 12.0F;
    private static final float PADDING_LEFT = 14.0F;
    private static final float ICON_BOX = 28.0F;
    private static final float ICON_SIZE = 14.0F;
    private static final float GAP = 12.0F;
    private static final float LINE_GAP = 6.0F;
    private static final float CONTROL_SPACE = 160.0F;
    private static final int ICON_FILL = ArkColors.rgba(255, 255, 255, 0.04F);
    private static final int ICON_BORDER = ArkColors.rgba(255, 255, 255, 0.07F);
    private static final TextStyle NAME = TextStyle.of(13.0F);
    private static final TextStyle DESCRIPTION = TextStyle.of(10.0F);

    private final Icon icon;
    private final Component name;
    private final Component description;
    private final Transition hover = new Transition(0.0F, Motion.HOVER, Easing.EASE);
    private Box bounds = Box.EMPTY;

    public SettingRow(ItemContent content) {
        this.icon = content.icon();
        this.name = content.label();
        this.description = content.sub() != null ? content.sub() : Component.empty();
    }

    public void setBounds(Box newBounds) {
        this.bounds = newBounds;
    }

    public Box controlSlot(float controlWidth, float controlHeight) {
        float x = this.bounds.right() - PADDING_RIGHT - controlWidth;
        return new Box(x, this.bounds.centerY() - controlHeight * 0.5F, controlWidth, controlHeight);
    }

    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        Box box = this.bounds;
        this.hover.setTarget(graphics.canvasBox(box).contains(mouseX, mouseY) ? 1.0F : 0.0F, graphics.now());
        float hover = this.hover.value(graphics.now());
        graphics.fill(box, ArkColors.lerp(hover, ArkColors.ROW, ArkColors.ROW_HOVER));
        graphics.border(box, 1.0F, ArkColors.lerp(hover, ArkColors.BORDER_SUBTLE, ArkColors.BORDER_OVERLAY));
        Box iconBox = new Box(box.x() + PADDING_LEFT, box.centerY() - ICON_BOX * 0.5F, ICON_BOX, ICON_BOX);
        graphics.fill(iconBox, ICON_FILL);
        graphics.border(iconBox, 1.0F, ICON_BORDER);
        float iconOffset = (ICON_BOX - ICON_SIZE) * 0.5F;
        graphics.icon(this.icon, iconBox.x() + iconOffset, iconBox.y() + iconOffset, ICON_SIZE, ICON_SIZE, Theme.accent().light());
        TextMetrics metrics = graphics.metrics();
        float textX = iconBox.right() + GAP;
        float textWidth = box.right() - PADDING_RIGHT - CONTROL_SPACE - GAP - textX;
        float blockHeight = metrics.capHeight(NAME) + LINE_GAP + metrics.capHeight(DESCRIPTION);
        float nameY = box.centerY() - blockHeight * 0.5F;
        graphics.text(metrics.ellipsize(this.name.getString(), NAME, textWidth), textX, nameY, NAME, ArkColors.TEXT_PRIMARY);
        float descriptionY = nameY + metrics.capHeight(NAME) + LINE_GAP;
        String description = metrics.ellipsize(this.description.getString(), DESCRIPTION, textWidth);
        graphics.text(description, textX, descriptionY, DESCRIPTION, ArkColors.TEXT_DESCRIPTION);
    }
}
