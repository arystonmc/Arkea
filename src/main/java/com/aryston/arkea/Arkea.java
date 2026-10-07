package com.aryston.arkea;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(value = Arkea.MOD_ID, dist = Dist.CLIENT)
public final class Arkea {
    public static final String MOD_ID = "arkea";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Arkea() {
        LOGGER.info("Arkea initialized");
    }
}
