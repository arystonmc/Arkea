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
    private final IconButtonStyle style;
    private final Runnable action;

    public ArkIconButton(UiHost host, Icon icon, Component label, IconButtonStyle style, Runnable action) {
        super(host);
        this.icon = icon;
        this.label = label;
        this.style = style;
        this.action = action;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        float hover = this.hoverProgress();
        graphics.push();
        if (!this.isActive()) {
            graphics.fade(DISABLED_OPACITY);
        }
        graphics.fill(box, ArkColors.lerp(hover, this.style.fill(), this.style.hoverFill()));
        graphics.border(box, 1.0F, ArkColors.brighten(this.style.border(), 1.0F + HOVER_BRIGHTNESS * hover));
        float iconX = box.centerX() - this.style.iconWidth() * 0.5F;
        float iconY = box.centerY() - this.style.iconHeight() * 0.5F;
        int iconColor = ArkColors.lerp(hover, this.style.iconColor(), this.style.hoverIconColor());
        graphics.icon(this.icon, iconX, iconY, this.style.iconWidth(), this.style.iconHeight(), iconColor);
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
