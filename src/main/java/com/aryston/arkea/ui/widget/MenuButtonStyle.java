package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;

public record MenuButtonStyle(
    int fill,
    int border,
    int glow,
    int topHighlight,
    int iconFill,
    int iconColor,
    int labelColor,
    float labelSize,
    int subColor,
    MenuButtonLayout layout
) {
    private static final float PRIMARY_GLOW_ALPHA = 0.40F;

    public static MenuButtonStyle primary() {
        return new MenuButtonStyle(
            Theme.accent().base(),
            Theme.accent().border(),
            ArkColors.withAlpha(Theme.accent().base(), PRIMARY_GLOW_ALPHA),
            ArkColors.rgba(255, 255, 255, 0.18F),
            ArkColors.rgba(0, 0, 0, 0.20F),
            ArkColors.TEXT_PRIMARY,
            ArkColors.TEXT_PRIMARY,
            16.0F,
            ArkColors.rgba(255, 255, 255, 0.75F),
            MenuButtonLayout.TILE
        );
    }

    public static MenuButtonStyle secondary() {
        return new MenuButtonStyle(
            ArkColors.rgba(16, 16, 18, 0.72F),
            ArkColors.BORDER_STRONG,
            ArkColors.TRANSPARENT,
            ArkColors.TRANSPARENT,
            ArkColors.rgba(255, 255, 255, 0.06F),
            ArkColors.TEXT_SOFT,
            ArkColors.TEXT_PRIMARY,
            13.0F,
            ArkColors.TEXT_DESCRIPTION,
            MenuButtonLayout.TILE
        );
    }

    public static MenuButtonStyle dangerInline() {
        return new MenuButtonStyle(
            ArkColors.rgba(16, 16, 18, 0.60F),
            ArkColors.BORDER_DEFAULT,
            ArkColors.TRANSPARENT,
            ArkColors.TRANSPARENT,
            ArkColors.TRANSPARENT,
            ArkColors.DANGER_TEXT,
            ArkColors.DANGER_TEXT,
            12.0F,
            ArkColors.DANGER_TEXT,
            MenuButtonLayout.INLINE
        );
    }
}
