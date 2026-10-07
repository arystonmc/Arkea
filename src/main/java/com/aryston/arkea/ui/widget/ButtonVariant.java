package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.theme.Accent;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;

public enum ButtonVariant {
    PRIMARY,
    SECONDARY,
    SUBTLE,
    GHOST,
    DANGER;

    private static final int SUBTLE_FILL = ArkColors.rgba(255, 255, 255, 0.05F);
    private static final int GHOST_HOVER = ArkColors.ROW;

    public int fill() {
        Accent accent = Theme.accent();
        return switch (this) {
            case PRIMARY -> accent.base();
            case SECONDARY -> ArkColors.CONTROL_FILL;
            case SUBTLE -> SUBTLE_FILL;
            case GHOST -> ArkColors.TRANSPARENT;
            case DANGER -> ArkColors.DANGER;
        };
    }

    public int hoverFill() {
        return this == GHOST ? GHOST_HOVER : this.fill();
    }

    public int border() {
        Accent accent = Theme.accent();
        return switch (this) {
            case PRIMARY -> accent.border();
            case SECONDARY, SUBTLE -> ArkColors.BORDER_STRONG;
            case GHOST -> ArkColors.TRANSPARENT;
            case DANGER -> ArkColors.DANGER_BORDER;
        };
    }

    public int text() {
        return switch (this) {
            case PRIMARY, SUBTLE, DANGER -> ArkColors.TEXT_PRIMARY;
            case SECONDARY -> ArkColors.TEXT_SOFT;
            case GHOST -> ArkColors.TEXT_MUTED;
        };
    }

    public int glow() {
        return this == PRIMARY ? Theme.accent().glow() : ArkColors.TRANSPARENT;
    }
}
