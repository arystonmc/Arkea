package com.aryston.arkea.hud.extra;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.hud.CrosshairStyle;
import java.util.Locale;
import net.minecraft.resources.Identifier;

public final class Crosshair {
    private Crosshair() {
    }

    public static Identifier sprite(Identifier vanilla) {
        if (!ArkeaConfig.SPEC.isLoaded() || ArkeaConfig.CROSSHAIR.get() == CrosshairStyle.VANILLA) {
            return vanilla;
        }
        return Identifier.fromNamespaceAndPath("arkea", "crosshair/" + ArkeaConfig.CROSSHAIR.get().name().toLowerCase(Locale.ROOT));
    }
}
