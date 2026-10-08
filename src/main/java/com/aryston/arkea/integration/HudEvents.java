package com.aryston.arkea.integration;

import com.aryston.arkea.hud.HudBossBar;
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
import java.util.function.LongSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.gui.contextualbar.ExperienceBar;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

public final class HudEvents {
    private static final HudPainter PAINTER = new HudPainter();
    private static LongSupplier clock = Util::getMillis;

    private HudEvents() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(HudEvents::onLayer);
        NeoForge.EVENT_BUS.addListener(HudEvents::onBossBar);
    }

    public static void clock(LongSupplier source) {
        clock = source;
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
            return;
        }
        event.setCanceled(true);
        paint(event.getGuiGraphics(), settings, (ui, screen) -> HudBossBar.draw(ui, screen.centerX(), ui.scale().toDesign(event.getY()), event.getBossEvent()));
    }

    private static void paint(GuiGraphicsExtractor graphics, HudSettings settings, BiConsumer<UiGraphics, Box> painter) {
        Minecraft minecraft = Minecraft.getInstance();
        UiScale scale = settings.scale(minecraft.getWindow());
        UiGraphics ui = new UiGraphics(graphics, scale, minecraft.font, clock.getAsLong());
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale.poseScale());
        painter.accept(ui, new Box(0.0F, 0.0F, scale.canvasWidth(), scale.canvasHeight()));
        graphics.pose().popMatrix();
    }
}
