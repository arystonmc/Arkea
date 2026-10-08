package com.aryston.arkea.screen.game;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jspecify.annotations.Nullable;

public final class PauseExtras {
    static final Component HELION = Component.translatable("arkea.pause.helion");
    private static final String HELION_ID = "helion";

    private PauseExtras() {
    }

    public static @Nullable Button button(Screen screen) {
        if (!(screen instanceof PauseScreen pause) || !pause.showsPauseMenu()) {
            return null;
        }
        Optional<? extends ModContainer> helion = ModList.get().getModContainerById(HELION_ID);
        if (helion.isEmpty()) {
            return null;
        }
        ModContainer container = helion.get();
        return container.getCustomExtension(IConfigScreenFactory.class)
            .map(factory -> Button.builder(HELION, button -> Minecraft.getInstance().gui.setScreen(factory.createScreen(container, screen))).build())
            .orElse(null);
    }
}
