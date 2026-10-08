package com.aryston.arkea.debug;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

final class UiCheckConfig {
    private static final String HELION = "helion";

    private UiCheckConfig() {
    }

    static Screen helion(Screen parent) {
        ModContainer container = ModList.get().getModContainerById(HELION).orElse(null);
        if (container == null) {
            return parent;
        }
        return container.getCustomExtension(IConfigScreenFactory.class)
            .map(factory -> factory.createScreen(container, parent))
            .orElse(parent);
    }
}
