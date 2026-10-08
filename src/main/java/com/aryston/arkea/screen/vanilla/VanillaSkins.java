package com.aryston.arkea.screen.vanilla;

import com.aryston.arkea.screen.game.DeathSkin;
import com.aryston.arkea.screen.game.PauseSkin;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.screens.Screen;

public final class VanillaSkins {
    private static final List<VanillaSkin> ALL = List.of(new DialogSkin(), new PauseSkin(), new DeathSkin());

    private VanillaSkins() {
    }

    public static Optional<VanillaSkin> find(Screen screen) {
        return ALL.stream().filter(skin -> skin.enabled() && skin.handles(screen)).findFirst();
    }
}
