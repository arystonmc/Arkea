package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.theme.ArkColors;

public record IconButtonStyle(int fill, int border, int iconColor, float iconSize) {
    public static final IconButtonStyle SECONDARY = new IconButtonStyle(ArkColors.CONTROL_FILL, ArkColors.BORDER_STRONG, ArkColors.TEXT_SOFT, 14.0F);
}
