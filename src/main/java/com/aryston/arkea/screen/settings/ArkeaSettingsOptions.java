package com.aryston.arkea.screen.settings;

import com.aryston.arkea.api.config.Binding;
import com.aryston.arkea.api.config.ChoiceStyle;
import com.aryston.arkea.api.config.ConfigOption;
import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.hud.CrosshairStyle;
import com.aryston.arkea.hud.HudSettings;
import com.aryston.arkea.hud.HudStyle;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.theme.Accent;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

final class ArkeaSettingsOptions {
    private static final String ID = "arkea.settings.option.";
    private static final String NAME = "arkea.configuration.";
    private static final String DESCRIPTION = "arkea.settings.";

    final ConfigOption<Boolean> backgroundPan = toggle("backgroundPan", ArkeaConfig.BACKGROUND_PAN, Icons.PANO, "pan");
    final ConfigOption<Accent> accent = describe(ConfigOption.choice(ID + "accent", binding(ArkeaConfig.ACCENT), List.of(Accent.values()),
            value -> Component.translatable("arkea.accent." + value.name().toLowerCase(Locale.ROOT))).style(ChoiceStyle.DROPDOWN),
        "accent", Icons.SPARKLE, "accent");
    final ConfigOption<Boolean> titleScreen = toggle("titleScreen", ArkeaConfig.TITLE_SCREEN, Icons.SLIDERS, "title");
    final ConfigOption<Boolean> optionsScreen = toggle("optionsScreen", ArkeaConfig.OPTIONS_SCREEN, Icons.LAYERS, "options");
    final ConfigOption<Boolean> menuScreens = toggle("menuScreens", ArkeaConfig.MENU_SCREENS, Icons.WORLD, "menus");
    final ConfigOption<Boolean> inGameScreens = toggle("inGameScreens", ArkeaConfig.IN_GAME_SCREENS, Icons.PLAY, "inGame");
    final ConfigOption<Boolean> modConfigScreens = toggle("modConfigScreens", ArkeaConfig.MOD_CONFIG_SCREENS, Icons.PACK, "modConfig");
    final ConfigOption<Boolean> vanillaTheme = toggle("vanillaTheme", ArkeaConfig.VANILLA_THEME, Icons.BLEND, "vanillaTheme");
    final ConfigOption<Boolean> joinSound = toggle("joinSound", ArkeaConfig.JOIN_SOUND, Icons.NOTE, "joinSound");
    final ConfigOption<Boolean> gameToasts = toggle("gameToasts", ArkeaConfig.GAME_TOASTS, Icons.BELL, "toasts");
    final ConfigOption<Boolean> debugOverlay = toggle("debugOverlay", ArkeaConfig.DEBUG_OVERLAY, Icons.CHUNKS, "debugOverlay");
    final ConfigOption<HudStyle> hudStyle = ConfigOption.choice(ID + "hudStyle", binding(ArkeaConfig.HUD_STYLE), List.of(HudStyle.values()), HudStyle::title)
        .style(ChoiceStyle.SEGMENTED)
        .name(Component.translatable(NAME + "hudStyle"))
        .description(Component.translatable("arkea.hud.style.hint"))
        .icon(Icons.MONITOR);
    final ConfigOption<Integer> hudOpacity = percent(ConfigOption.slider(ID + "hudOpacity", binding(ArkeaConfig.HUD_OPACITY),
        HudSettings.OPACITY_MIN, HudSettings.OPACITY_MAX, HudSettings.OPACITY_STEP), "hudOpacity", Icons.BLEND);
    final ConfigOption<Integer> hudSize = percent(ConfigOption.slider(ID + "hudSize", binding(ArkeaConfig.HUD_SIZE),
        HudSettings.SIZE_MIN, HudSettings.SIZE_MAX, HudSettings.SIZE_STEP), "hudSize", Icons.RESIZE);
    final ConfigOption<Boolean> hudEffects = part("hudEffects", ArkeaConfig.HUD_EFFECTS, Icons.FLASK);
    final ConfigOption<Boolean> hudBossBars = part("hudBossBars", ArkeaConfig.HUD_BOSS_BARS, Icons.HOSTILE);
    final ConfigOption<Boolean> hudScoreboard = part("hudScoreboard", ArkeaConfig.HUD_SCOREBOARD, Icons.RULES);
    final ConfigOption<Boolean> hudTabList = part("hudTabList", ArkeaConfig.HUD_TAB_LIST, Icons.USER);
    final ConfigOption<Boolean> chat = describe(ConfigOption.toggle(ID + "chat", Binding.of(ArkeaConfig.CHAT::get, ArkeaSettingsOptions::setChat,
        ArkeaConfig.CHAT.getDefault())), "chat", Icons.CHAT, "chat");
    final ConfigOption<Boolean> targetCard = part("targetCard", ArkeaConfig.TARGET_CARD, Icons.EYE);
    final ConfigOption<Boolean> pickupFeed = part("pickupFeed", ArkeaConfig.PICKUP_FEED, Icons.PLUS);
    final ConfigOption<Boolean> stackTotal = part("stackTotal", ArkeaConfig.STACK_TOTAL, Icons.LAYERS);
    final ConfigOption<Boolean> ammoCounter = part("ammoCounter", ArkeaConfig.AMMO_COUNTER, Icons.ARROW_R);
    final ConfigOption<Boolean> infoChip = part("infoChip", ArkeaConfig.INFO_CHIP, Icons.COMPASS);
    final ConfigOption<Boolean> deathPoint = part("deathPoint", ArkeaConfig.DEATH_POINT, Icons.WARNING);
    final ConfigOption<Boolean> durabilityWarning = part("durabilityWarning", ArkeaConfig.DURABILITY_WARNING, Icons.SWORD);
    final ConfigOption<Boolean> armorIcons = part("armorIcons", ArkeaConfig.ARMOR_ICONS, Icons.SHIRT);
    final ConfigOption<Boolean> foodPreview = part("foodPreview", ArkeaConfig.FOOD_PREVIEW, Icons.LEAF);
    final ConfigOption<Boolean> damageDirection = part("damageDirection", ArkeaConfig.DAMAGE_DIRECTION, Icons.HEART);
    final ConfigOption<Boolean> sleepReminder = part("sleepReminder", ArkeaConfig.SLEEP_REMINDER, Icons.CLOCK);
    final ConfigOption<CrosshairStyle> crosshair = describe(ConfigOption.choice(ID + "crosshair", binding(ArkeaConfig.CROSSHAIR),
        List.of(CrosshairStyle.values()), CrosshairStyle::title).style(ChoiceStyle.DROPDOWN), "crosshair", Icons.CROSS, "crosshair");
    final ConfigOption<Boolean> hitMarker = part("hitMarker", ArkeaConfig.HIT_MARKER, Icons.CROSS);
    final ConfigOption<Boolean> tooltips = part("tooltips", ArkeaConfig.TOOLTIPS, Icons.CHAT);
    final ConfigOption<Boolean> tooltipFood = part("tooltipFood", ArkeaConfig.TOOLTIP_FOOD, Icons.LEAF);
    final ConfigOption<Boolean> tooltipDurability = part("tooltipDurability", ArkeaConfig.TOOLTIP_DURABILITY, Icons.GAUGE);
    final ConfigOption<Boolean> tooltipContainers = part("tooltipContainers", ArkeaConfig.TOOLTIP_CONTAINERS, Icons.CHEST);
    final ConfigOption<Boolean> soundRadar = part("soundRadar", ArkeaConfig.SOUND_RADAR, Icons.SUBS);

    private static void setChat(boolean value) {
        ArkeaConfig.set(ArkeaConfig.CHAT, value);
        Minecraft.getInstance().gui.hud.getChat().rescaleChat();
    }

    private static ConfigOption<Boolean> part(String key, ModConfigSpec.BooleanValue value, Icon icon) {
        return toggle(key, value, icon, key);
    }

    private static ConfigOption<Boolean> toggle(String key, ModConfigSpec.BooleanValue value, Icon icon, String descriptionKey) {
        return describe(ConfigOption.toggle(ID + key, binding(value)), key, icon, descriptionKey);
    }

    private static ConfigOption<Integer> percent(ConfigOption<Integer> slider, String key, Icon icon) {
        return describe(slider, key, icon, key).format((int value) -> Component.translatable("arkea.hud.percent", value));
    }

    private static <T> ConfigOption<T> describe(ConfigOption<T> option, String nameKey, Icon icon, String descriptionKey) {
        return option.name(Component.translatable(NAME + nameKey))
            .description(Component.translatable(DESCRIPTION + descriptionKey + ".description"))
            .icon(icon);
    }

    private static <T> Binding<T> binding(ModConfigSpec.ConfigValue<T> value) {
        return Binding.of(value::get, newValue -> ArkeaConfig.set(value, newValue), value.getDefault());
    }
}
