package com.aryston.arkea.debug;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.hud.CrosshairStyle;
import com.aryston.arkea.hud.HudClock;
import com.aryston.arkea.hud.HudStyle;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;

final class UiCheckFeatures {
    static final int HURT_SETTLE = 20;
    private static final long SLOW_MOTION = 10L;
    private static final float LOOK_DOWN = 15.0F;
    private static final int HUNGRY = 9;
    private static final int WORN_PICKAXE = 1540;
    private static final int FOOD = 12;
    private static final int GOLD = 23;
    private static final int TORCHES = 48;
    private static final List<String> SETUP = List.of(
        "gamemode survival @a",
        "time set 1000",
        "item replace entity @a hotbar.0 with iron_pickaxe[damage=245]",
        "item replace entity @a hotbar.1 with cobblestone 64",
        "item replace entity @a hotbar.2 with bread 6",
        "item replace entity @a hotbar.3 with bow",
        "item replace entity @a armor.chest with iron_chestplate[damage=230]",
        "item replace entity @a armor.head with iron_helmet",
        "give @a cobblestone 200",
        "give @a arrow 37",
        "fill ^-2 ^ ^3 ^2 ^3 ^3 iron_ore",
        "bossbar add arkea:features \"Wither\"",
        "bossbar set arkea:features players @a",
        "bossbar set arkea:features value 45",
        "say Welcome to the Arkea test world",
        "tellraw @a [\"\",{\"text\":\"<Alex> \",\"color\":\"green\"},\"found a village at \",{\"text\":\"[120, 64, -340]\",\"color\":\"aqua\",\"underlined\":true}]",
        "tellraw @a {\"text\":\"<Deniz> who wants to trade some diamonds for a stack of emeralds tonight?\"}");
    private static final List<String> PICKUPS = List.of(
        "summon item ~ ~ ~ {Item:{id:\"minecraft:iron_ingot\",count:5}}",
        "summon item ~ ~ ~ {Item:{id:\"minecraft:oak_log\",count:12}}",
        "summon experience_orb ~ ~ ~ {Value:7}");
    private static final List<String> HURT = List.of("damage @p 2 minecraft:generic at ^4 ^ ^", "playsound minecraft:entity.zombie.ambient hostile @a ^-5 ^ ^1");
    private static final List<String> NIGHT = List.of("time set 13000");
    private static final List<String> DIE = List.of("gamerule keep_inventory true", "kill @p");
    private static final List<String> AWAY = List.of("tp @p ~12 ~ ~5");
    private static final List<String> HORSE = List.of("kill @e[type=item]", "fill ^-2 ^ ^3 ^2 ^3 ^3 air", "summon horse ^ ^ ^3 {NoAI:1b,Tame:1b}");

    private UiCheckFeatures() {
    }

    static void setup(Minecraft minecraft) {
        minecraft.gui.setScreen(null);
        ArkeaConfig.set(ArkeaConfig.HUD_STYLE, HudStyle.COMPACT);
        ArkeaConfig.set(ArkeaConfig.INFO_CHIP, true);
        ArkeaConfig.set(ArkeaConfig.SOUND_RADAR, true);
        ArkeaConfig.set(ArkeaConfig.HIT_MARKER, true);
        ArkeaConfig.set(ArkeaConfig.CROSSHAIR, CrosshairStyle.GAP);
        if (minecraft.player != null) {
            minecraft.player.setXRot(0.0F);
            minecraft.player.getInventory().setSelectedSlot(1);
        }
        run(minecraft, SETUP);
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server != null) {
            server.execute(() -> server.getPlayerList().getPlayers().forEach(player -> player.getFoodData().setFoodLevel(HUNGRY)));
        }
    }

    static void pickups(Minecraft minecraft) {
        run(minecraft, PICKUPS);
    }

    static void bow(Minecraft minecraft) {
        if (minecraft.player != null) {
            minecraft.player.getInventory().setSelectedSlot(3);
        }
    }

    static void hurt(Minecraft minecraft) {
        long start = Util.getMillis();
        HudClock.use(() -> start + (Util.getMillis() - start) / SLOW_MOTION);
        if (minecraft.player != null) {
            minecraft.player.getInventory().setSelectedSlot(2);
        }
        run(minecraft, HURT);
    }

    static void horse(Minecraft minecraft) {
        HudClock.use(Util::getMillis);
        if (minecraft.player != null) {
            minecraft.player.setXRot(LOOK_DOWN);
        }
        run(minecraft, HORSE);
    }

    static void night(Minecraft minecraft) {
        minecraft.gui.setScreen(null);
        ArkeaConfig.set(ArkeaConfig.SLEEP_REMINDER, true);
        run(minecraft, NIGHT);
    }

    static void die(Minecraft minecraft) {
        run(minecraft, DIE);
    }

    static void respawn(Minecraft minecraft) {
        if (minecraft.player != null) {
            minecraft.player.respawn();
        }
        minecraft.gui.setScreen(null);
    }

    static void walkAway(Minecraft minecraft) {
        run(minecraft, AWAY);
    }

    static void openChat(Minecraft minecraft) {
        minecraft.gui.setScreen(new ChatScreen("", false));
    }

    static void restore(Minecraft minecraft) {
        ArkeaConfig.set(ArkeaConfig.HUD_STYLE, HudStyle.VANILLA);
        ArkeaConfig.set(ArkeaConfig.INFO_CHIP, false);
        ArkeaConfig.set(ArkeaConfig.SOUND_RADAR, false);
        ArkeaConfig.set(ArkeaConfig.HIT_MARKER, false);
        ArkeaConfig.set(ArkeaConfig.CROSSHAIR, CrosshairStyle.VANILLA);
        ArkeaConfig.set(ArkeaConfig.SLEEP_REMINDER, false);
        minecraft.gui.setScreen(null);
    }

    static ItemStack food() {
        return new ItemStack(Items.COOKED_BEEF, FOOD);
    }

    static ItemStack tool() {
        ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
        stack.setDamageValue(WORN_PICKAXE);
        return stack;
    }

    static ItemStack shulker() {
        ItemStack stack = new ItemStack(Items.SHULKER_BOX);
        stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(new ItemStack(Items.DIAMOND, FOOD), new ItemStack(Items.GOLD_INGOT, GOLD),
            new ItemStack(Items.TORCH, TORCHES), new ItemStack(Items.ENCHANTED_GOLDEN_APPLE), new ItemStack(Items.ELYTRA))));
        return stack;
    }

    private static void run(Minecraft minecraft, List<String> commands) {
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server == null) {
            return;
        }
        server.execute(() -> {
            CommandSourceStack source = server.createCommandSourceStack();
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                source = source.withEntity(player).withPosition(player.position()).withRotation(player.getRotationVector());
            }
            CommandSourceStack positioned = source;
            commands.forEach(command -> server.getCommands().performPrefixedCommand(positioned, command));
        });
    }
}
