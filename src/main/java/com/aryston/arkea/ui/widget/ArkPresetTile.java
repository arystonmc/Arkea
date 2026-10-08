package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import java.util.function.BooleanSupplier;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ArkPresetTile extends ArkWidget {
    public static final float HEIGHT = 84.0F;
    private static final float PADDING = 14.0F;
    private static final float ICON_BOX = 28.0F;
    private static final float ICON = 14.0F;
    private static final float TITLE_GAP = 12.0F;
    private static final float LINE_GAP = 6.0F;
    private static final float LIFT = 2.0F;
    private static final float BADGE = 16.0F;
    private static final float BADGE_INSET = 8.0F;
    private static final float CHECK_WIDTH = 8.0F;
    private static final float CHECK_HEIGHT = 6.0F;
    private static final float GLOW_BLUR = 14.0F;
    private static final float GLOW_OFFSET = 4.0F;
    private static final int ICON_FILL = ArkColors.rgba(255, 255, 255, 0.04F);
    private static final TextStyle TITLE = TextStyle.of(14.0F);
    private static final TextStyle DESCRIPTION = TextStyle.of(10.0F);

    private final ItemContent content;
    private final BooleanSupplier selected;
    private final Runnable action;
    private final Transition selection;

    public ArkPresetTile(UiHost host, ItemContent content, BooleanSupplier selected, Runnable action) {
        super(host);
        this.content = content;
        this.selected = selected;
        this.action = action;
        this.selection = new Transition(selected.getAsBoolean() ? 1.0F : 0.0F, Motion.CHECK_POP, Easing.EASE);
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        float hover = this.hoverProgress();
        Box box = this.bounds().offset(0.0F, -LIFT * hover);
        this.selection.setTarget(this.selected.getAsBoolean() ? 1.0F : 0.0F, graphics.now());
        float selected = this.selection.value(graphics.now());
        if (selected > 0.0F) {
            graphics.shadow(box, GLOW_BLUR, GLOW_OFFSET, ArkColors.multiplyAlpha(Theme.accent().glow(), selected));
        }
        int fill = ArkColors.lerp(hover, ArkColors.ROW, ArkColors.ROW_HOVER);
        graphics.fill(box, ArkColors.lerp(selected, fill, Theme.accent().tint()));
        int border = ArkColors.lerp(hover, ArkColors.BORDER_SUBTLE, ArkColors.BORDER_OVERLAY);
        graphics.border(box, 1.0F, ArkColors.lerp(selected, border, Theme.accent().base()));
        Box iconBox = new Box(box.x() + PADDING, box.y() + PADDING, ICON_BOX, ICON_BOX);
        graphics.fill(iconBox, ArkColors.lerp(selected, ICON_FILL, Theme.accent().tint()));
        graphics.border(iconBox, 1.0F, ArkColors.BORDER_DEFAULT);
        int iconColor = ArkColors.lerp(selected, ArkColors.TEXT_ICON_IDLE, Theme.accent().light());
        graphics.icon(this.content.icon(), iconBox.centerX() - ICON * 0.5F, iconBox.centerY() - ICON * 0.5F, ICON, ICON, iconColor);
        TextMetrics metrics = graphics.metrics();
        float textWidth = box.width() - PADDING * 2.0F;
        float titleY = iconBox.bottom() + TITLE_GAP;
        graphics.text(metrics.ellipsize(this.content.label().getString(), TITLE, textWidth), box.x() + PADDING, titleY, TITLE, ArkColors.TEXT_PRIMARY);
        Component sub = this.content.sub();
        if (sub != null) {
            graphics.text(metrics.ellipsize(sub.getString(), DESCRIPTION, textWidth), box.x() + PADDING, titleY + metrics.capHeight(TITLE) + LINE_GAP,
                DESCRIPTION, ArkColors.TEXT_DESCRIPTION);
        }
        if (selected > 0.0F) {
            Box badge = new Box(box.right() - BADGE_INSET - BADGE, box.y() + BADGE_INSET, BADGE, BADGE);
            graphics.fill(badge, ArkColors.multiplyAlpha(Theme.accent().base(), selected));
            graphics.icon(Icons.CHECK, badge.centerX() - CHECK_WIDTH * 0.5F, badge.centerY() - CHECK_HEIGHT * 0.5F, CHECK_WIDTH, CHECK_HEIGHT,
                ArkColors.multiplyAlpha(ArkColors.TEXT_PRIMARY, selected));
        }
    }

    @Override
    protected void onPress() {
        this.action.run();
    }

    @Override
    protected Component narrationMessage() {
        return CommonComponents.optionNameValue(this.content.label(), this.selected.getAsBoolean() ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
    }
}
