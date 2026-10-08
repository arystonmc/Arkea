package com.aryston.arkea.config;

import com.aryston.arkea.hud.CrosshairStyle;
import com.aryston.arkea.hud.HudSettings;
import com.aryston.arkea.hud.HudStyle;
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

    public static final ModConfigSpec.BooleanValue IN_GAME_SCREENS = BUILDER
        .translation("arkea.configuration.inGameScreens")
        .define("inGameScreens", true);

    public static final ModConfigSpec.BooleanValue MOD_CONFIG_SCREENS = BUILDER
        .translation("arkea.configuration.modConfigScreens")
        .define("modConfigScreens", true);

    public static final ModConfigSpec.BooleanValue VANILLA_THEME = BUILDER
        .translation("arkea.configuration.vanillaTheme")
        .define("vanillaTheme", true);

    public static final ModConfigSpec.BooleanValue DEBUG_OVERLAY = BUILDER
        .translation("arkea.configuration.debugOverlay")
        .define("debugOverlay", true);

    public static final ModConfigSpec.BooleanValue GAME_TOASTS = BUILDER
        .translation("arkea.configuration.gameToasts")
        .define("gameToasts", true);

    public static final ModConfigSpec.EnumValue<HudStyle> HUD_STYLE = BUILDER
        .translation("arkea.configuration.hudStyle")
        .defineEnum("hudStyle", HudStyle.VANILLA);

    public static final ModConfigSpec.IntValue HUD_OPACITY = BUILDER
        .translation("arkea.configuration.hudOpacity")
        .defineInRange("hudOpacity", HudSettings.OPACITY_DEFAULT, HudSettings.OPACITY_MIN, HudSettings.OPACITY_MAX);

    public static final ModConfigSpec.IntValue HUD_SIZE = BUILDER
        .translation("arkea.configuration.hudSize")
        .defineInRange("hudSize", HudSettings.SIZE_DEFAULT, HudSettings.SIZE_MIN, HudSettings.SIZE_MAX);

    public static final ModConfigSpec.BooleanValue HUD_EFFECTS = BUILDER
        .translation("arkea.configuration.hudEffects")
        .define("hudEffects", true);

    public static final ModConfigSpec.BooleanValue HUD_BOSS_BARS = BUILDER
        .translation("arkea.configuration.hudBossBars")
        .define("hudBossBars", true);

    public static final ModConfigSpec.BooleanValue HUD_SCOREBOARD = BUILDER
        .translation("arkea.configuration.hudScoreboard")
        .define("hudScoreboard", true);

    public static final ModConfigSpec.BooleanValue HUD_TAB_LIST = BUILDER
        .translation("arkea.configuration.hudTabList")
        .define("hudTabList", true);

    public static final ModConfigSpec.BooleanValue CHAT = BUILDER
        .translation("arkea.configuration.chat")
        .define("chat", true);

    public static final ModConfigSpec.BooleanValue TOOLTIPS = BUILDER
        .translation("arkea.configuration.tooltips")
        .define("tooltips", true);

    public static final ModConfigSpec.BooleanValue TOOLTIP_FOOD = BUILDER
        .translation("arkea.configuration.tooltipFood")
        .define("tooltipFood", true);

    public static final ModConfigSpec.BooleanValue TOOLTIP_DURABILITY = BUILDER
        .translation("arkea.configuration.tooltipDurability")
        .define("tooltipDurability", true);

    public static final ModConfigSpec.BooleanValue TOOLTIP_CONTAINERS = BUILDER
        .translation("arkea.configuration.tooltipContainers")
        .define("tooltipContainers", true);

    public static final ModConfigSpec.BooleanValue TARGET_CARD = BUILDER
        .translation("arkea.configuration.targetCard")
        .define("targetCard", true);

    public static final ModConfigSpec.BooleanValue PICKUP_FEED = BUILDER
        .translation("arkea.configuration.pickupFeed")
        .define("pickupFeed", true);

    public static final ModConfigSpec.BooleanValue STACK_TOTAL = BUILDER
        .translation("arkea.configuration.stackTotal")
        .define("stackTotal", true);

    public static final ModConfigSpec.BooleanValue AMMO_COUNTER = BUILDER
        .translation("arkea.configuration.ammoCounter")
        .define("ammoCounter", true);

    public static final ModConfigSpec.BooleanValue DURABILITY_WARNING = BUILDER
        .translation("arkea.configuration.durabilityWarning")
        .define("durabilityWarning", true);

    public static final ModConfigSpec.BooleanValue ARMOR_ICONS = BUILDER
        .translation("arkea.configuration.armorIcons")
        .define("armorIcons", true);

    public static final ModConfigSpec.BooleanValue FOOD_PREVIEW = BUILDER
        .translation("arkea.configuration.foodPreview")
        .define("foodPreview", true);

    public static final ModConfigSpec.BooleanValue INFO_CHIP = BUILDER
        .translation("arkea.configuration.infoChip")
        .define("infoChip", false);

    public static final ModConfigSpec.BooleanValue DAMAGE_DIRECTION = BUILDER
        .translation("arkea.configuration.damageDirection")
        .define("damageDirection", true);

    public static final ModConfigSpec.BooleanValue DEATH_POINT = BUILDER
        .translation("arkea.configuration.deathPoint")
        .define("deathPoint", true);

    public static final ModConfigSpec.BooleanValue SLEEP_REMINDER = BUILDER
        .translation("arkea.configuration.sleepReminder")
        .define("sleepReminder", false);

    public static final ModConfigSpec.BooleanValue HIT_MARKER = BUILDER
        .translation("arkea.configuration.hitMarker")
        .define("hitMarker", false);

    public static final ModConfigSpec.BooleanValue SOUND_RADAR = BUILDER
        .translation("arkea.configuration.soundRadar")
        .define("soundRadar", false);

    public static final ModConfigSpec.EnumValue<CrosshairStyle> CROSSHAIR = BUILDER
        .translation("arkea.configuration.crosshair")
        .defineEnum("crosshair", CrosshairStyle.VANILLA);

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

    public static Accent accent() {
        return SPEC.isLoaded() ? ACCENT.get() : Accent.GREEN;
    }

    public static boolean on(ModConfigSpec.BooleanValue value) {
        return SPEC.isLoaded() && value.get();
    }

    public static <T> void set(ModConfigSpec.ConfigValue<T> value, T newValue) {
        if (!newValue.equals(value.get())) {
            value.set(newValue);
            SPEC.save();
        }
    }
}
