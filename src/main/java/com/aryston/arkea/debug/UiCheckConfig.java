package com.aryston.arkea.debug;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

final class UiCheckConfig {
    static final String SAMPLE_LINK = "https://github.com/arystonmc/Arkea";

    private UiCheckConfig() {
    }

    static Screen forMod(String modId, Screen parent) {
        ModContainer container = ModList.get().getModContainerById(modId).orElse(null);
        if (container == null) {
            return parent;
        }
        return container.getCustomExtension(IConfigScreenFactory.class)
            .map(factory -> factory.createScreen(container, parent))
            .orElse(parent);
    }
}
