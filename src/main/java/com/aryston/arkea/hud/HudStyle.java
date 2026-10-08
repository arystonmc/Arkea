package com.aryston.arkea.hud;

import java.util.Locale;
import net.minecraft.network.chat.Component;

public enum HudStyle {
    VANILLA,
    COMPACT,
    CLASSIC,
    MINIMAL;

    public boolean arkea() {
        return this != VANILLA;
    }

    public boolean icons() {
        return this == CLASSIC;
    }

    public boolean hidesWhenFull() {
        return this == MINIMAL;
    }

    public Component title() {
        return Component.translatable(this.key());
    }

    public Component description() {
        return Component.translatable(this.key() + ".description");
    }

    private String key() {
        return "arkea.hud.style." + this.name().toLowerCase(Locale.ROOT);
    }
}
