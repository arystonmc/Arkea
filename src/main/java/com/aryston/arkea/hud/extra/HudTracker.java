package com.aryston.arkea.hud.extra;

import com.aryston.arkea.hud.HudClock;
import com.aryston.arkea.config.ArkeaConfig;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class HudTracker {
    private static final long TOAST_TIME = 5000L;
    private static final SystemToast.SystemToastId DURABILITY_TOAST = new SystemToast.SystemToastId(TOAST_TIME);
    private static final SystemToast.SystemToastId SLEEP_TOAST = new SystemToast.SystemToastId(TOAST_TIME);
    private static final List<EquipmentSlot> WATCHED = List.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST,
        EquipmentSlot.LEGS, EquipmentSlot.FEET);
    private static final long TICKS_PER_DAY = 24000L;
    private static final long NIGHT_START = 12542L;
    private static final long NIGHT_END = 23459L;
    private static final float FALL_DAMAGE = 3.0F;
    private static final Set<String> WARNED = new HashSet<>();
    private static long remindedNight = -1L;
    private static int lastHurtTime;
    private static double lastFall;
    private static boolean wasDead;
    private static long diedAt = -1L;

    private HudTracker() {
    }

    public static void tick(Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null) {
            return;
        }
        durability(minecraft, player);
        sleep(minecraft, player);
        hurt(player);
        boolean dead = player.isDeadOrDying();
        if (dead && !wasDead) {
            diedAt = HudClock.now();
        }
        wasDead = dead;
    }

    public static long diedAt() {
        return diedAt;
    }

    private static void durability(Minecraft minecraft, LocalPlayer player) {
        if (!ArkeaConfig.on(ArkeaConfig.DURABILITY_WARNING)) {
            return;
        }
        Set<String> low = new HashSet<>();
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < Inventory.getSelectionSize(); slot++) {
            check(minecraft, inventory.getItem(slot), "hotbar" + slot, low);
        }
        for (EquipmentSlot slot : WATCHED) {
            if (slot != EquipmentSlot.MAINHAND) {
                check(minecraft, player.getItemBySlot(slot), slot.getName(), low);
            }
        }
        WARNED.retainAll(low);
    }

    private static void check(Minecraft minecraft, ItemStack stack, String slot, Set<String> low) {
        if (!Durability.low(stack)) {
            return;
        }
        String key = slot + ":" + BuiltInRegistries.ITEM.getKey(stack.getItem());
        low.add(key);
        if (WARNED.add(key)) {
            SystemToast.add(minecraft.gui.toastManager(), DURABILITY_TOAST, Component.translatable("arkea.warning.durability", stack.getHoverName()),
                Component.translatable("arkea.warning.durability.uses", Durability.remaining(stack)));
        }
    }

    private static void sleep(Minecraft minecraft, LocalPlayer player) {
        if (!ArkeaConfig.on(ArkeaConfig.SLEEP_REMINDER) || minecraft.level == null || minecraft.level.dimension() != Level.OVERWORLD) {
            return;
        }
        long clock = minecraft.level.getOverworldClockTime();
        long time = clock % TICKS_PER_DAY;
        boolean night = time >= NIGHT_START && time <= NIGHT_END || minecraft.level.isThundering();
        long day = clock / TICKS_PER_DAY;
        if (night && !player.isSleeping() && remindedNight != day) {
            remindedNight = day;
            SystemToast.add(minecraft.gui.toastManager(), SLEEP_TOAST, Component.translatable("arkea.warning.sleep"),
                Component.translatable("arkea.warning.sleep.message"));
        }
    }

    private static void hurt(LocalPlayer player) {
        boolean environmental = lastFall > FALL_DAMAGE || player.isOnFire() || player.isInLava() || player.getAirSupply() <= 0 || player.isFullyFrozen()
            || player.getFoodData().getFoodLevel() == 0;
        if (player.hurtTime > lastHurtTime && !environmental && ArkeaConfig.on(ArkeaConfig.DAMAGE_DIRECTION)) {
            DamageIndicator.add(player.getHurtDir(), player.getYRot());
        }
        lastHurtTime = player.hurtTime;
        lastFall = player.fallDistance;
    }
}
