package com.aryston.arkea.ui.theme;

import com.aryston.arkea.config.ArkeaConfig;

public final class Theme {
    private Theme() {
    }

    public static Accent accent() {
        return ArkeaConfig.SPEC.isLoaded() ? ArkeaConfig.ACCENT.get() : Accent.GREEN;
    }
}
