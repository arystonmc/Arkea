package com.aryston.arkea.debug;

import com.aryston.arkea.Arkea;
import com.aryston.arkea.api.config.ModConfigScreens;
import com.aryston.arkea.hud.HudStyle;
import com.aryston.arkea.screen.gallery.ArkGalleryScreen;
import com.aryston.arkea.screen.options.hud.ArkHudScreen;
import com.aryston.arkea.screen.palette.CommandPaletteScreen;
import com.aryston.arkea.ui.screen.ArkScreen;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.mojang.blaze3d.platform.InputConstants;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.telemetry.TelemetryInfoScreen;
import net.minecraft.client.gui.screens.CreditsAndAttributionScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.modlist.ModListScreen;

public final class UiCheck {
    private static final String PROPERTY = "arkea.uiCheck";
    private static final String ALL = "all";
    private static final int START_DELAY_TICKS = 100;
    private static final int SETTLE_TICKS = 30;
    private static final int ACTION_SETTLE_TICKS = 20;
    private static final int AWAIT_TICKS = 20;
    private static final int SCREENSHOT_DOWNSCALE = 1;
    private static final String SCREENSHOT_PREFIX = "arkea_";

    private final List<Step> steps;
    private int next;
    private int wait = START_DELAY_TICKS;
    private boolean finished;

    private UiCheck(List<Step> steps) {
        this.steps = steps;
    }

    public static Optional<UiCheck> requested() {
        String value = System.getProperty(PROPERTY);
        if (FMLEnvironment.isProduction() || value == null || value.isBlank()) {
            return Optional.empty();
        }
        Set<String> names = Arrays.stream(value.toLowerCase(Locale.ROOT).split(",")).map(String::strip).collect(Collectors.toSet());
        List<Step> steps = UiCheckSteps.ALL.stream().filter(step -> names.contains(ALL) || names.contains(step.group())).toList();
        Arkea.LOGGER.info("Arkea interface check runs {} steps", steps.size());
        return Optional.of(new UiCheck(steps));
    }

    public void tick(Minecraft minecraft) {
        if (this.finished || minecraft.gui.overlay() != null) {
            return;
        }
        if (--this.wait > 0) {
            return;
        }
        if (this.next > 0) {
            Step shot = this.steps.get(this.next - 1);
            Screenshot.grab(minecraft.gameDirectory, SCREENSHOT_PREFIX + shot.name() + ".png", minecraft.gameRenderer.mainRenderTarget(),
                SCREENSHOT_DOWNSCALE, message -> { });
        }
        if (this.next >= this.steps.size()) {
            this.finished = true;
            Arkea.LOGGER.info("Arkea interface check finished");
            minecraft.stop();
            return;
        }
        Step step = this.steps.get(this.next);
        if (!step.ready().test(minecraft)) {
            this.wait = AWAIT_TICKS;
            return;
        }
        this.next++;
        Arkea.LOGGER.info("Arkea interface check: {}", step.name());
        step.action().accept(minecraft);
        this.wait = step.settle() > 0 ? step.settle() : step.opensScreen() ? SETTLE_TICKS : ACTION_SETTLE_TICKS;
    }

    record Step(String group, String name, boolean opensScreen, Consumer<Minecraft> action, Predicate<Minecraft> ready, int settle) {
        Step(String group, String name, boolean opensScreen, Consumer<Minecraft> action) {
            this(group, name, opensScreen, action, minecraft -> true, 0);
        }

        Step(String group, String name, boolean opensScreen, Consumer<Minecraft> action, Predicate<Minecraft> ready) {
            this(group, name, opensScreen, action, ready, 0);
        }

        static Step quick(String group, String name, int settle, Consumer<Minecraft> action) {
            return new Step(group, name, false, action, minecraft -> true, settle);
        }

        static Step await(String group, String name, Predicate<Minecraft> condition) {
            return new Step(group, name, true, minecraft -> { }, condition);
        }

        static Step screen(String group, String name, Function<Screen, Screen> factory) {
            return new Step(group, name, true, minecraft -> minecraft.gui.setScreen(factory.apply(new TitleScreen())));
        }

        static Step press(String group, String name, String widgetKey) {
            return on(group, name, ArkScreen.class, screen -> screen.findWidget(widgetKey).ifPresentOrElse(widget -> {
                Arkea.LOGGER.info("Arkea interface check: {} activates {} active={} bounds={}", name, widget.getClass().getSimpleName(), widget.isActive(),
                    widget.bounds());
                widget.activate();
            },
                () -> Arkea.LOGGER.warn("Arkea interface check: {} found no widget {}", name, widgetKey)));
        }

        static Step key(String group, String name, int key) {
            return on(group, name, Screen.class, screen -> screen.keyPressed(new KeyEvent(key, 0, 0)));
        }

        static <T> Step on(String group, String name, Class<T> type, Consumer<T> action) {
            return new Step(group, name, false, minecraft -> {
                if (type.isInstance(minecraft.gui.screen())) {
                    action.accept(type.cast(minecraft.gui.screen()));
                } else {
                    Arkea.LOGGER.warn("Arkea interface check: {} expected {} but found {}", name, type.getSimpleName(), minecraft.gui.screen());
                }
            });
        }
    }

    static final class UiCheckSteps {
        static final List<Step> ALL = List.of(
            Step.screen("title", "title", parent -> new TitleScreen()),
            Step.screen("options", "options", parent -> new OptionsScreen(parent, Minecraft.getInstance().options)),
            Step.screen("worlds", "worlds", SelectWorldScreen::new),
            Step.press("worlds", "worlds_edit", "details:edit"),
            Step.key("worlds", "worlds_after_edit", InputConstants.KEY_ESCAPE),
            Step.key("worlds", "worlds_delete", InputConstants.KEY_DELETE),
            Step.key("worlds", "worlds_after_delete", InputConstants.KEY_ESCAPE),
            new Step("palette", "palette", false, minecraft -> minecraft.gui.pushScreenLayer(new CommandPaletteScreen())),
            Step.key("palette", "palette_down", InputConstants.KEY_DOWN),
            new Step("palette", "palette_close", false, minecraft -> minecraft.gui.popScreenLayer()),
            new Step("toasts", "toasts", false, minecraft -> UiCheckToasts.show()),
            new Step("toasts", "game_toasts", false, UiCheckGameToasts::show),
            Step.screen("loading", "loading_message", parent -> new GenericMessageScreen(Component.translatable("menu.savingLevel"))),
            Step.screen("loading", "loading_progress", parent -> UiCheckLoading.progress()),
            Step.screen("loading", "loading_disconnected", parent -> new DisconnectedScreen(parent, Component.translatable("connect.failed"),
                Component.translatable("disconnect.genericReason", Component.translatable("disconnect.unknownHost")))),
            new Step("loading", "loading_connect", true, UiCheckLoading::connect),
            new Step("loading", "loading_cancel", false, UiCheckLoading::cancel),
            new Step("servers", "servers_setup", false, UiCheckServers::add),
            new Step("create", "create", true, minecraft -> CreateWorldScreen.openFresh(minecraft, () -> minecraft.gui.setScreen(new TitleScreen()))),
            Step.press("create", "create_world", "tab:WORLD"),
            Step.press("create", "create_more", "tab:MORE"),
            Step.press("create", "create_game", "tab:GAME"),
            Step.screen("mods", "mods", ModListScreen::create),
            Step.press("mods", "mods_arkea", "mod:arkea"),
            Step.screen("servers", "servers", JoinMultiplayerScreen::new),
            Step.press("servers", "servers_add", "add"),
            Step.key("servers", "servers_after_add", InputConstants.KEY_ESCAPE),
            Step.press("servers", "servers_lan", "tab:LAN"),
            new Step("servers", "servers_cleanup", false, UiCheckServers::remove),
            Step.screen("gallery", "gallery", ArkGalleryScreen::new),
            Step.press("gallery", "gallery_whatsnew", "gallery:whatsnew"),
            Step.key("gallery", "gallery_after_whatsnew", InputConstants.KEY_ESCAPE),
            Step.press("gallery", "gallery_preview", "gallery:preview"),
            Step.key("gallery", "gallery_after_preview", InputConstants.KEY_ESCAPE),
            Step.press("gallery", "gallery_sheet", "gallery:sheet"),
            Step.key("gallery", "gallery_after_sheet", InputConstants.KEY_ESCAPE),
            Step.press("gallery", "gallery_progress", "gallery:progress"),
            Step.key("gallery", "gallery_after_progress", InputConstants.KEY_ESCAPE),
            Step.screen("config", "config_neoforge", parent -> ModConfigScreens.forMod(ModList.get().getModContainerById("neoforge").orElseThrow(), parent)),
            new Step("game", "game_create", true, minecraft -> CreateWorldScreen.openFresh(minecraft, () -> minecraft.gui.setScreen(new TitleScreen()))),
            Step.press("game", "game_start", "create"),
            Step.await("game", "game_loaded", minecraft -> minecraft.level != null && minecraft.player != null && minecraft.gui.screen() == null),
            new Step("game", "game_pause", true, minecraft -> minecraft.gui.setScreen(new PauseScreen(true))),
            new Step("game", "game_stats", true, UiCheckGame::stats),
            new Step("game", "game_death", true, UiCheckGame::death),
            new Step("game", "game_debug", false, UiCheckGame::debug),
            new Step("game", "game_chat_messages", false, UiCheckGame::messages),
            Step.quick("game", "game_chat_entering", UiCheckHud.MID_ANIMATION, UiCheckGame::lateMessage),
            new Step("game", "game_chat", true, UiCheckGame::chat),
            Step.on("game", "game_chat_suggestions", ChatScreen.class, UiCheckGame::typeSuggestion),
            Step.on("game", "game_chat_history", ChatScreen.class, UiCheckGame::history),
            Step.quick("game", "game_chat_scrolling", UiCheckHud.MID_ANIMATION, UiCheckGame::scroll),
            new Step("game", "game_chat_scrolled", false, UiCheckHud::realTime),
            new Step("features", "features_create", true, minecraft -> CreateWorldScreen.openFresh(minecraft, () -> minecraft.gui.setScreen(new TitleScreen()))),
            Step.press("features", "features_start", "create"),
            Step.await("features", "features_loaded", minecraft -> minecraft.level != null && minecraft.player != null && minecraft.gui.screen() == null),
            new Step("features", "features_setup", false, UiCheckFeatures::setup),
            new Step("features", "features_chat_open", true, UiCheckFeatures::openChat),
            Step.key("features", "features_chat_closed", InputConstants.KEY_ESCAPE),
            new Step("features", "features_pickups", false, UiCheckFeatures::pickups),
            new Step("features", "features_bow", false, UiCheckFeatures::bow),
            Step.quick("features", "features_hurt", UiCheckFeatures.HURT_SETTLE, UiCheckFeatures::hurt),
            new Step("features", "features_horse", false, UiCheckFeatures::horse),
            Step.screen("features", "features_tooltip_food", parent -> new UiCheckTooltipScreen(UiCheckFeatures.food())),
            Step.screen("features", "features_tooltip_tool", parent -> new UiCheckTooltipScreen(UiCheckFeatures.tool())),
            Step.screen("features", "features_tooltip_shulker", parent -> new UiCheckTooltipScreen(UiCheckFeatures.shulker())),
            new Step("features", "features_night", false, UiCheckFeatures::night),
            new Step("features", "features_death", true, UiCheckFeatures::die),
            new Step("features", "features_respawned", false, UiCheckFeatures::respawn),
            new Step("features", "features_death_point", false, UiCheckFeatures::walkAway),
            new Step("features", "features_restored", false, UiCheckFeatures::restore),
            new Step("hud", "hud_create", true, minecraft -> CreateWorldScreen.openFresh(minecraft, () -> minecraft.gui.setScreen(new TitleScreen()))),
            Step.press("hud", "hud_start", "create"),
            Step.await("hud", "hud_loaded", minecraft -> minecraft.level != null && minecraft.player != null && minecraft.gui.screen() == null),
            new Step("hud", "hud_vanilla", false, UiCheckHud::setup),
            new Step("hud", "hud_compact", false, UiCheckHud.style(HudStyle.COMPACT)),
            new Step("hud", "hud_classic", false, UiCheckHud.style(HudStyle.CLASSIC)),
            new Step("hud", "hud_minimal", false, UiCheckHud.style(HudStyle.MINIMAL)),
            new Step("hud", "hud_anim_ready", false, UiCheckHud::slowMotion),
            Step.quick("hud", "hud_anim_select", UiCheckHud.MID_ANIMATION, UiCheckHud::selectNext),
            Step.quick("hud", "hud_anim_count", UiCheckHud.MID_ANIMATION, UiCheckHud::useOne),
            Step.quick("hud", "hud_anim_damage", UiCheckHud.MID_ANIMATION, UiCheckHud::hurt),
            new Step("hud", "hud_anim_done", false, UiCheckHud::realTime),
            new Step("hud", "hud_danger", false, UiCheckHud::danger),
            new Step("hud", "hud_tab_list", false, UiCheckHud::tabList),
            new Step("hud", "hud_tab_released", false, UiCheckHud::releaseTabList),
            Step.screen("hud", "hud_settings", ArkHudScreen::new),
            Step.press("hud", "hud_settings_classic", "hud:classic"),
            Step.on("hud", "hud_settings_cards", Screen.class, UiCheckHud::scroll),
            Step.key("hud", "hud_settings_closed", InputConstants.KEY_ESCAPE),
            new Step("hud", "hud_restored", false, UiCheckHud.style(HudStyle.VANILLA)),
            Step.screen("vanilla", "vanilla_telemetry", parent -> new TelemetryInfoScreen(parent, Minecraft.getInstance().options)),
            Step.screen("vanilla", "vanilla_credits", CreditsAndAttributionScreen::new),
            Step.screen("vanilla", "vanilla_safety", SafetyScreen::new),
            Step.screen("vanilla", "vanilla_link", parent -> new ConfirmLinkScreen(accepted -> { }, URI.create(UiCheckConfig.SAMPLE_LINK), true)),
            Step.screen("helion", "helion_general", parent -> UiCheckConfig.helion(parent)),
            Step.press("helion", "helion_rendering", "nav:rendering"),
            Step.press("helion", "helion_lighting", "nav:lighting"),
            Step.press("helion", "helion_effects", "nav:effects"),
            Step.press("helion", "helion_performance", "nav:performance"),
            Step.press("helion", "helion_advanced", "nav:advanced"),
            Step.press("helion", "helion_back_general", "nav:general"),
            Step.press("helion", "helion_preset_quality", "preset:2"),
            Step.press("helion", "helion_applied", "apply"),
            Step.press("helion", "helion_preset_balanced", "preset:1"),
            Step.press("helion", "helion_restored", "apply")
        );

        private UiCheckSteps() {
        }
    }
}
