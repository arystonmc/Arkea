package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;

public enum ToastTone {
    SUCCESS(Icons.TICK),
    INFO(Icons.INFO),
    WARNING(Icons.WARNING),
    ERROR(Icons.ERROR);

    private final Icon icon;

    ToastTone(Icon icon) {
        this.icon = icon;
    }

    public Icon icon() {
        return this.icon;
    }

    public int color() {
        return switch (this) {
            case SUCCESS -> Theme.accent().light();
            case INFO -> ArkColors.INFO;
            case WARNING -> ArkColors.WARNING;
            case ERROR -> ArkColors.ERROR;
        };
    }
}
