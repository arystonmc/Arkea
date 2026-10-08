package com.aryston.arkea.debug;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.hud.HudStyle;
import com.aryston.arkea.hud.HudClock;
import com.aryston.arkea.mixin.HudAccessor;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

final class UiCheckHud {
    private static final int SAMPLE_FOOD = 15;
    static final int MID_ANIMATION = 20;
    private static final long SLOW_MOTION = 10L;
    private static final int OTHER_SLOT = 8;
    private static final int STACK_SLOT = 6;
    private static final float HURT = 4.0F;
    private static final double SCROLL = -10.0;
    private static final double HALF = 0.5;
    private static final String SAMPLE_HEADER = "Aryston SMP";
    private static final String SAMPLE_FOOTER = "play.aryston.net";
    private static final List<String> SETUP = List.of(
        "gamemode survival @a",
        "item replace entity @a hotbar.0 with diamond_sword",
        "item replace entity @a hotbar.1 with iron_pickaxe[damage=180]",
        "item replace entity @a hotbar.2 with stone_axe[damage=110]",
        "item replace entity @a hotbar.3 with bow",
        "item replace entity @a hotbar.4 with cooked_beef 23",
        "item replace entity @a hotbar.5 with bread 9",
        "item replace entity @a hotbar.6 with cobblestone 64",
        "item replace entity @a hotbar.7 with oak_planks 32",
        "item replace entity @a weapon.offhand with torch 48",
        "item replace entity @a armor.head with iron_helmet",
        "item replace entity @a armor.chest with iron_chestplate",
        "xp set @a 27 levels",
        "xp set @a 60 points",
        "effect give @a speed 161 1",
        "effect give @a night_vision 9",
        "effect give @a strength infinite",
        "scoreboard objectives add arkea dummy \"Aryston SMP\"",
        "scoreboard objectives setdisplay sidebar arkea",
        "scoreboard objectives setdisplay list arkea",
        "scoreboard players set Can arkea 1284",
        "scoreboard players set Alex arkea 960",
        "scoreboard players set Deniz arkea 415",
        "bossbar add arkea:check \"Ender Dragon\"",
        "bossbar set arkea:check players @a",
        "bossbar set arkea:check value 72",
        "bossbar set arkea:check color pink",
        "bossbar set arkea:check style notched_20");
    private static final List<String> DANGER = List.of("effect give @a poison 20");
    private static final float SAMPLE_HEALTH = 13.0F;
    private static final float DANGER_HEALTH = 5.0F;
    private static final int DANGER_FOOD = 5;

    private UiCheckHud() {
    }

    static void setup(Minecraft minecraft) {
        minecraft.gui.setScreen(null);
        run(minecraft, SETUP);
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server != null) {
            server.execute(() -> server.getPlayerList().getPlayers().forEach(player -> {
                player.getFoodData().setFoodLevel(SAMPLE_FOOD);
                player.setHealth(SAMPLE_HEALTH);
            }));
        }
    }

    static Consumer<Minecraft> style(HudStyle style) {
        return minecraft -> {
            ArkeaConfig.set(ArkeaConfig.HUD_STYLE, style);
            if (minecraft.player != null) {
                int selected = minecraft.player.getInventory().getSelectedSlot();
                minecraft.player.getInventory().setSelectedSlot(selected == 0 ? OTHER_SLOT : 0);
            }
        };
    }

    static void slowMotion(Minecraft minecraft) {
        style(HudStyle.COMPACT).accept(minecraft);
        long start = Util.getMillis();
        HudClock.use(() -> start + (Util.getMillis() - start) / SLOW_MOTION);
    }

    static void realTime(Minecraft minecraft) {
        HudClock.use(Util::getMillis);
    }

    static void selectNext(Minecraft minecraft) {
        if (minecraft.player != null) {
            minecraft.player.getInventory().setSelectedSlot(STACK_SLOT);
        }
    }

    static void useOne(Minecraft minecraft) {
        if (minecraft.player != null) {
            minecraft.player.getInventory().getItem(STACK_SLOT).shrink(1);
        }
    }

    static void hurt(Minecraft minecraft) {
        if (minecraft.player != null) {
            minecraft.player.setHealth(minecraft.player.getHealth() - HURT);
        }
    }

    static void tabList(Minecraft minecraft) {
        PlayerTabOverlay overlay = ((HudAccessor) minecraft.gui.hud).arkea$tabList();
        overlay.setHeader(Component.literal(SAMPLE_HEADER));
        overlay.setFooter(Component.literal(SAMPLE_FOOTER));
        minecraft.options.keyPlayerList.setDown(true);
    }

    static void releaseTabList(Minecraft minecraft) {
        minecraft.options.keyPlayerList.setDown(false);
    }

    static void scroll(Screen screen) {
        screen.mouseScrolled(screen.width * HALF, screen.height * HALF, 0.0, SCROLL);
    }

    static void danger(Minecraft minecraft) {
        style(HudStyle.COMPACT).accept(minecraft);
        run(minecraft, DANGER);
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server != null) {
            server.execute(() -> server.getPlayerList().getPlayers().forEach(player -> {
                player.getFoodData().setFoodLevel(DANGER_FOOD);
                player.setHealth(DANGER_HEALTH);
            }));
        }
    }

    private static void run(Minecraft minecraft, List<String> commands) {
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server == null) {
            return;
        }
        server.execute(() -> {
            CommandSourceStack source = server.createCommandSourceStack();
            commands.forEach(command -> server.getCommands().performPrefixedCommand(source, command));
        });
    }
}
