package com.aryston.arkea;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.integration.ClientEvents;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;

@Mod(value = Arkea.MOD_ID, dist = Dist.CLIENT)
public final class Arkea {
    public static final String MOD_ID = "arkea";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Arkea(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, ArkeaConfig.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        ClientEvents.register();
        LOGGER.info("Arkea initialized");
    }
}
