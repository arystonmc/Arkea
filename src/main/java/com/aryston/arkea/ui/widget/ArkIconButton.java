package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import net.minecraft.network.chat.Component;

public class ArkIconButton extends ArkWidget {
    private static final float HOVER_BRIGHTNESS = 0.18F;

    private final Icon icon;
    private final Component label;
    private final Runnable action;
    private final int fill;
    private final int border;
    private final int iconColor;
    private final float iconSize;

    public ArkIconButton(UiHost host, Icon icon, Component label, IconButtonStyle style, Runnable action) {
        super(host);
        this.icon = icon;
        this.label = label;
        this.action = action;
        this.fill = style.fill();
        this.border = style.border();
        this.iconColor = style.iconColor();
        this.iconSize = style.iconSize();
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        float hover = this.hoverProgress();
        float brightness = 1.0F + HOVER_BRIGHTNESS * hover;
        graphics.push();
        graphics.translate(0.0F, this.pressProgress());
        if (!this.isActive()) {
            graphics.fade(DISABLED_OPACITY);
        }
        graphics.fill(box, ArkColors.brighten(this.fill, brightness));
        graphics.border(box, 1.0F, ArkColors.brighten(this.border, brightness));
        float iconX = box.centerX() - this.iconSize * 0.5F;
        float iconY = box.centerY() - this.iconSize * 0.5F;
        graphics.icon(this.icon, iconX, iconY, this.iconSize, this.iconSize, ArkColors.brighten(this.iconColor, brightness));
        graphics.pop();
    }

    @Override
    protected void onPress() {
        this.action.run();
    }

    @Override
    protected Component narrationMessage() {
        return this.label;
    }
}
