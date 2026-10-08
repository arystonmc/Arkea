package com.aryston.arkea.hud;

import java.util.Locale;
import net.minecraft.network.chat.Component;

public enum CrosshairStyle {
    VANILLA,
    CROSS,
    GAP,
    DOT,
    CIRCLE;

    public Component title() {
        return Component.translatable("arkea.crosshair." + this.name().toLowerCase(Locale.ROOT));
    }
}
