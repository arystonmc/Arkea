package com.aryston.arkea.config;

import com.aryston.arkea.screen.worlds.WorldSort;
import com.aryston.arkea.ui.theme.Accent;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ArkeaConfig {
    public static final String VANILLA_BACKGROUND = "vanilla";

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue TITLE_SCREEN = BUILDER
        .translation("arkea.configuration.titleScreen")
        .define("titleScreen", true);

    public static final ModConfigSpec.BooleanValue OPTIONS_SCREEN = BUILDER
        .translation("arkea.configuration.optionsScreen")
        .define("optionsScreen", true);

    public static final ModConfigSpec.BooleanValue MENU_SCREENS = BUILDER
        .translation("arkea.configuration.menuScreens")
        .define("menuScreens", true);

    public static final ModConfigSpec.EnumValue<Accent> ACCENT = BUILDER
        .translation("arkea.configuration.accent")
        .defineEnum("accent", Accent.GREEN);

    public static final ModConfigSpec.ConfigValue<String> BACKGROUND = BUILDER
        .translation("arkea.configuration.background")
        .define("background", VANILLA_BACKGROUND);

    public static final ModConfigSpec.BooleanValue BACKGROUND_PAN = BUILDER
        .translation("arkea.configuration.backgroundPan")
        .define("backgroundPan", true);

    public static final ModConfigSpec.EnumValue<WorldSort> WORLD_SORT = BUILDER
        .translation("arkea.configuration.worldSort")
        .defineEnum("worldSort", WorldSort.LAST_PLAYED);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private ArkeaConfig() {
    }

    public static <T> void set(ModConfigSpec.ConfigValue<T> value, T newValue) {
        if (!newValue.equals(value.get())) {
            value.set(newValue);
            SPEC.save();
        }
    }
}
