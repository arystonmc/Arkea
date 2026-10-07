package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.theme.ArkColors;

public record IconButtonStyle(int fill, int hoverFill, int border, int iconColor, int hoverIconColor, float iconWidth, float iconHeight) {
    private static final float HOVER_BRIGHTNESS = 1.18F;
    private static final int ARROW_HOVER_FILL = ArkColors.rgba(255, 255, 255, 0.08F);
    public static final IconButtonStyle BACK = new IconButtonStyle(ArkColors.rgba(255, 255, 255, 0.04F), ARROW_HOVER_FILL,
        ArkColors.BORDER_DEFAULT, ArkColors.TEXT_SOFT, ArkColors.TEXT_PRIMARY, 6.0F, 10.0F);
    public static final IconButtonStyle CLOSE = new IconButtonStyle(ArkColors.TRANSPARENT, ARROW_HOVER_FILL,
        ArkColors.TRANSPARENT, ArkColors.TEXT_MUTED, ArkColors.TEXT_PRIMARY, 10.0F, 10.0F);

    public static IconButtonStyle brightening(int fill, int border, int iconColor, float iconSize) {
        return new IconButtonStyle(fill, ArkColors.brighten(fill, HOVER_BRIGHTNESS), border, iconColor,
            ArkColors.brighten(iconColor, HOVER_BRIGHTNESS), iconSize, iconSize);
    }
}
