package com.aryston.arkea.ui.theme;

import java.util.function.Supplier;

public final class Theme {
    private static Supplier<Accent> accentSource = () -> Accent.GREEN;

    private Theme() {
    }

    public static Accent accent() {
        return accentSource.get();
    }

    public static void setAccentSource(Supplier<Accent> source) {
        accentSource = source;
    }
}
