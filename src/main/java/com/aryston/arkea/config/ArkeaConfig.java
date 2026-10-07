package com.aryston.arkea.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ArkeaConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue TITLE_SCREEN = BUILDER
        .translation("arkea.configuration.titleScreen")
        .define("titleScreen", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private ArkeaConfig() {
    }
}
