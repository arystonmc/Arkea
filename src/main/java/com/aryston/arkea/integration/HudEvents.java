package com.aryston.arkea.integration;

import com.aryston.arkea.Arkea;
import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.hud.extra.DamageIndicator;
import com.aryston.arkea.hud.extra.HitMarker;
import com.aryston.arkea.hud.extra.HotbarExtras;
import com.aryston.arkea.hud.extra.HotbarLayout;
import com.aryston.arkea.hud.extra.HudTracker;
import com.aryston.arkea.hud.extra.InfoChip;
import com.aryston.arkea.hud.extra.PickupFeed;
import com.aryston.arkea.hud.extra.SoundRadar;
import com.aryston.arkea.hud.target.TargetCard;
import com.aryston.arkea.hud.BossBarStack;
import com.aryston.arkea.hud.HudBossBar;
import com.aryston.arkea.hud.HudClock;
import com.aryston.arkea.hud.HudEffects;
import com.aryston.arkea.hud.HudPainter;
import com.aryston.arkea.hud.HudSettings;
import com.aryston.arkea.hud.HudSidebar;
import com.aryston.arkea.hud.HudSnapshot;
import com.aryston.arkea.hud.HudTabList;
import com.aryston.arkea.mixin.HudAccessor;
import com.aryston.arkea.mixin.PlayerTabOverlayAccessor;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.UiGraphics;
import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.gui.contextualbar.ContextualBar;
import net.minecraft.client.gui.contextualbar.ExperienceBar;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

public final class HudEvents {
    private static final HudPainter PAINTER = new HudPainter();
    private static final TargetCard TARGET = new TargetCard();
    private static final float PICKUPS_ABOVE_STATUS = 82.0F;
    private static final float PICKUPS_ABOVE_EDGE = 16.0F;
    private static final int VANILLA_HALF = 91;
    private static final int VANILLA_HEIGHT = 22;
    private static final int VANILLA_SLOT = 20;
    private static final int VANILLA_LEVEL_TOP = 35;
    private static final int VANILLA_BOSS_BAR_HEIGHT = 5;
    private static final float HALF = 0.5F;

    private HudEvents() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(HudEvents::registerLayers);
        NeoForge.EVENT_BUS.addListener(HudEvents::onLayer);
        NeoForge.EVENT_BUS.addListener(HudEvents::onBossBar);
        NeoForge.EVENT_BUS.addListener(HudEvents::onTick);
        NeoForge.EVENT_BUS.addListener(HitMarker::onAttack);
    }

    private static void registerLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CAMERA_OVERLAYS, layer("damage_direction"), (graphics, delta) -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (visible() && player != null && ArkeaConfig.on(ArkeaConfig.DAMAGE_DIRECTION)) {
                paint(graphics, HudSettings.current(), (ui, screen) -> DamageIndicator.draw(ui, screen, player.getYRot()));
            }
        });
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, layer("hit_marker"), (graphics, delta) -> {
            if (visible() && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                HitMarker.draw(graphics);
            }
        });
        event.registerAbove(VanillaGuiLayers.HOTBAR, layer("hotbar_extras"), (graphics, delta) -> hotbarExtras(graphics));
        event.registerAbove(VanillaGuiLayers.SELECTED_ITEM_NAME, layer("pickups"), (graphics, delta) -> {
            if (visible()) {
                HudSettings settings = HudSettings.current();
                float bottom = settings.style().arkea() ? PICKUPS_ABOVE_STATUS : PICKUPS_ABOVE_EDGE;
                paint(graphics, settings, (ui, screen) -> PickupFeed.draw(ui, screen, settings, screen.bottom() - bottom));
            }
        });
        event.registerAbove(VanillaGuiLayers.EFFECTS, layer("info"), (graphics, delta) -> {
            if (visible()) {
                HudSettings settings = HudSettings.current();
                paint(graphics, settings, (ui, screen) -> InfoChip.draw(ui, screen, settings, Minecraft.getInstance()));
            }
        });
        event.registerBelow(VanillaGuiLayers.BOSS_OVERLAY, layer("boss_bar_reset"), (graphics, delta) -> BossBarStack.clear());
        event.registerAbove(VanillaGuiLayers.BOSS_OVERLAY, layer("target"), (graphics, delta) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (visible() && minecraft.gui.screen() == null && !minecraft.debugEntries.isOverlayVisible() && ArkeaConfig.on(ArkeaConfig.TARGET_CARD)) {
                HudSettings settings = HudSettings.current();
                paint(graphics, settings, (ui, screen) -> TARGET.draw(ui, screen, minecraft, settings,
                    BossBarStack.any() ? ui.scale().toDesign(BossBarStack.bottom()) : 0.0F));
            }
        });
        event.registerAbove(VanillaGuiLayers.SUBTITLE_OVERLAY, layer("sound_radar"), (graphics, delta) -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (visible() && player != null && ArkeaConfig.on(ArkeaConfig.SOUND_RADAR)) {
                HudSettings settings = HudSettings.current();
                paint(graphics, settings, (ui, screen) -> SoundRadar.draw(ui, screen, settings, player));
            }
        });
    }

    private static void hotbarExtras(GuiGraphicsExtractor graphics) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (!visible() || player == null || minecraft.gameMode == null || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        HudSettings settings = HudSettings.current();
        int selected = player.getInventory().getSelectedSlot();
        int level = minecraft.gameMode.hasExperience() ? player.experienceLevel : 0;
        paint(graphics, settings, (ui, screen) -> {
            HotbarLayout layout = settings.style().arkea() ? new HotbarLayout(HudPainter.hotbarBox(screen), HudPainter.slot(screen, selected),
                HudPainter.experienceBar(screen), level > 0 ? HudPainter.levelText(ui.metrics(), screen, level) : null)
                : vanillaHotbar(minecraft, ui.scale(), selected, level);
            HotbarExtras.draw(ui, layout, settings, minecraft, player);
        });
    }

    private static HotbarLayout vanillaHotbar(Minecraft minecraft, UiScale scale, int selected, int level) {
        int center = minecraft.getWindow().getGuiScaledWidth() / 2;
        int bottom = minecraft.getWindow().getGuiScaledHeight();
        Box hotbar = new Box(scale.toDesign(center - VANILLA_HALF), scale.toDesign(bottom - VANILLA_HEIGHT), scale.toDesign(VANILLA_HALF * 2),
            scale.toDesign(VANILLA_HEIGHT));
        Box slot = new Box(scale.toDesign(center - VANILLA_HALF + 1 + selected * VANILLA_SLOT), scale.toDesign(bottom - VANILLA_HEIGHT + 1),
            scale.toDesign(VANILLA_SLOT), scale.toDesign(VANILLA_SLOT));
        Box experience = new Box(hotbar.x(), scale.toDesign(bottom - ContextualBar.MARGIN_BOTTOM - ContextualBar.HEIGHT), hotbar.width(),
            scale.toDesign(ContextualBar.HEIGHT));
        if (level <= 0) {
            return new HotbarLayout(hotbar, slot, experience, null);
        }
        int width = minecraft.font.width(String.valueOf(level));
        Box text = new Box(scale.toDesign(center - width * HALF), scale.toDesign(bottom - VANILLA_LEVEL_TOP), scale.toDesign(width),
            scale.toDesign(minecraft.font.lineHeight));
        return new HotbarLayout(hotbar, slot, experience, text);
    }

    private static boolean visible() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.player != null && minecraft.level != null && !minecraft.gui.hud.isHidden();
    }

    private static Identifier layer(String name) {
        return Identifier.fromNamespaceAndPath(Arkea.MOD_ID, name);
    }

    private static void onTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        SoundRadar.ensureRegistered(minecraft);
        HudTracker.tick(minecraft);
    }

    private static void onLayer(RenderGuiLayerEvent.Pre event) {
        HudSettings settings = HudSettings.current();
        Minecraft minecraft = Minecraft.getInstance();
        if (!settings.style().arkea() || minecraft.gameMode == null || minecraft.level == null || minecraft.gui.hud.isHidden()
            || !(minecraft.getCameraEntity() instanceof Player player)) {
            return;
        }
        Identifier name = event.getName();
        GuiGraphicsExtractor graphics = event.getGuiGraphics();
        boolean spectator = minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR;
        boolean survival = minecraft.gameMode.canHurtPlayer();
        boolean experience = ((HudAccessor) minecraft.gui.hud).arkea$contextualInfoBar().getSecond() instanceof ExperienceBar;
        if (name.equals(VanillaGuiLayers.HOTBAR) && !spectator) {
            event.setCanceled(true);
            paint(graphics, settings, (ui, screen) -> PAINTER.drawHotbar(ui, screen, HudSnapshot.capture(minecraft, player), settings));
        } else if (name.equals(VanillaGuiLayers.PLAYER_HEALTH) || name.equals(VanillaGuiLayers.VEHICLE_HEALTH)) {
            event.setCanceled(true);
            if (survival == name.equals(VanillaGuiLayers.PLAYER_HEALTH)) {
                paint(graphics, settings, (ui, screen) -> PAINTER.drawStatus(ui, screen, HudSnapshot.capture(minecraft, player), settings));
            }
        } else if (name.equals(VanillaGuiLayers.ARMOR_LEVEL) || name.equals(VanillaGuiLayers.FOOD_LEVEL) || name.equals(VanillaGuiLayers.AIR_LEVEL)) {
            event.setCanceled(true);
        } else if (experience && (name.equals(VanillaGuiLayers.CONTEXTUAL_INFO_BAR_BACKGROUND) || name.equals(VanillaGuiLayers.EXPERIENCE_LEVEL))) {
            event.setCanceled(true);
        } else if (experience && name.equals(VanillaGuiLayers.CONTEXTUAL_INFO_BAR)) {
            event.setCanceled(true);
            paint(graphics, settings, (ui, screen) -> PAINTER.drawExperience(ui, screen, HudSnapshot.capture(minecraft, player)));
        } else if (name.equals(VanillaGuiLayers.SELECTED_ITEM_NAME) && !spectator) {
            event.setCanceled(true);
            paint(graphics, settings, (ui, screen) -> PAINTER.drawItemName(ui, screen, HudSnapshot.capture(minecraft, player), settings));
        } else if (name.equals(VanillaGuiLayers.EFFECTS) && settings.effects()) {
            event.setCanceled(true);
            Screen screen = minecraft.gui.screen();
            if (screen == null || !screen.showsActiveEffects()) {
                paint(graphics, settings, (ui, canvas) -> HudEffects.draw(ui, canvas, player, minecraft.gui.hud, settings));
            }
        } else if (name.equals(VanillaGuiLayers.SCOREBOARD_SIDEBAR) && settings.scoreboard()) {
            event.setCanceled(true);
            Objective objective = HudSidebar.objective(minecraft.level.getScoreboard(), player);
            if (objective != null) {
                graphics.nextStratum();
                paint(graphics, settings, (ui, screen) -> HudSidebar.draw(ui, screen, objective, settings));
            }
        } else if (name.equals(VanillaGuiLayers.TAB_LIST) && settings.tabList()) {
            event.setCanceled(true);
            tabList(minecraft, graphics, settings);
        }
    }

    private static void tabList(Minecraft minecraft, GuiGraphicsExtractor graphics, HudSettings settings) {
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null) {
            return;
        }
        Scoreboard scoreboard = minecraft.level.getScoreboard();
        Objective objective = scoreboard.getDisplayObjective(DisplaySlot.LIST);
        PlayerTabOverlay overlay = ((HudAccessor) minecraft.gui.hud).arkea$tabList();
        boolean alone = minecraft.isLocalServer() && player.connection.getListedOnlinePlayers().size() <= 1 && objective == null;
        boolean shown = minecraft.options.keyPlayerList.isDown() && !alone;
        overlay.setVisible(shown);
        if (shown) {
            PlayerTabOverlayAccessor texts = (PlayerTabOverlayAccessor) overlay;
            graphics.nextStratum();
            paint(graphics, settings, (ui, screen) -> HudTabList.draw(ui, screen, minecraft, overlay, scoreboard, objective, texts.arkea$header(),
                texts.arkea$footer(), settings));
        }
    }

    private static void onBossBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
        HudSettings settings = HudSettings.current();
        if (!settings.style().arkea() || !settings.bossBars()) {
            BossBarStack.add(event.getY() + VANILLA_BOSS_BAR_HEIGHT);
            return;
        }
        event.setCanceled(true);
        paint(event.getGuiGraphics(), settings, (ui, screen) -> BossBarStack.add(
            ui.scale().toGui(HudBossBar.draw(ui, screen.centerX(), ui.scale().toDesign(event.getY()), event.getBossEvent()))));
    }

    private static void paint(GuiGraphicsExtractor graphics, HudSettings settings, BiConsumer<UiGraphics, Box> painter) {
        Minecraft minecraft = Minecraft.getInstance();
        UiScale scale = settings.scale(minecraft.getWindow());
        UiGraphics ui = new UiGraphics(graphics, scale, minecraft.font, HudClock.now());
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale.poseScale());
        painter.accept(ui, new Box(0.0F, 0.0F, scale.canvasWidth(), scale.canvasHeight()));
        graphics.pose().popMatrix();
    }
}
