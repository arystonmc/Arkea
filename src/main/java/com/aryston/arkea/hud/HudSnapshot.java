package com.aryston.arkea.hud;

import com.aryston.arkea.mixin.HudAccessor;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.contextualbar.ExperienceBar;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.ClientHooks;
import org.jspecify.annotations.Nullable;

public record HudSnapshot(
    @Nullable Player owner,
    float health,
    float maxHealth,
    float absorption,
    Hud.HeartType heart,
    boolean hardcore,
    int armor,
    int food,
    float saturation,
    boolean hungerEffect,
    float air,
    boolean airVisible,
    float vehicleHealth,
    float vehicleMaxHealth,
    boolean survival,
    boolean experience,
    int level,
    float progress,
    List<ItemStack> hotbar,
    int selected,
    ItemStack offhand,
    boolean offhandLeft,
    float attack,
    @Nullable Component itemName,
    float itemNameAlpha,
    List<ItemStack> armorItems,
    int previewFood,
    float previewSaturation
) {
    public static final float MAX_FOOD = 20.0F;
    public static final float MAX_ARMOR = 20.0F;
    public static final float NO_ATTACK = -1.0F;
    private static final float NAME_FADE_TICKS = 10.0F;
    private static final float SAMPLE_HEALTH = 16.0F;
    private static final float SAMPLE_MAX_HEALTH = 20.0F;
    private static final int SAMPLE_ARMOR = 12;
    private static final int SAMPLE_FOOD = 17;
    private static final float SAMPLE_SATURATION = 4.0F;
    private static final int SAMPLE_LEVEL = 27;
    private static final float SAMPLE_PROGRESS = 0.62F;
    private static final int STEAK = 23;
    private static final int BREAD = 9;
    private static final int STACK = 64;
    private static final int PLANKS = 32;
    private static final int TORCHES = 48;
    private static final float WORN = 0.66F;
    private static final float USED = 0.3F;
    private static final List<EquipmentSlot> ARMOR_SLOTS = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
    private static @Nullable HudSnapshot sample;
    private static @Nullable HudSnapshot fullSample;

    public boolean riding() {
        return this.vehicleMaxHealth > 0.0F;
    }

    public boolean low() {
        return this.health <= this.maxHealth * HudPainter.LOW_SHARE;
    }

    public boolean full() {
        return this.health >= this.maxHealth && this.absorption <= 0.0F && this.food >= MAX_FOOD && !this.airVisible && !this.riding();
    }

    public static HudSnapshot capture(Minecraft minecraft, Player player) {
        Inventory inventory = player.getInventory();
        List<ItemStack> hotbar = new ArrayList<>(Inventory.getSelectionSize());
        for (int slot = 0; slot < Inventory.getSelectionSize(); slot++) {
            hotbar.add(inventory.getItem(slot));
        }
        HudAccessor hud = (HudAccessor) minecraft.gui.hud;
        boolean experience = hud.arkea$contextualInfoBar().getSecond() instanceof ExperienceBar;
        LivingEntity vehicle = vehicle(player);
        int maxAir = player.getMaxAirSupply();
        int air = Math.clamp(player.getAirSupply(), 0, maxAir);
        boolean underWater = player.getFluidInteraction().isEyeInFluidMatching(player, (entity, type, unused) -> entity.canDrownInFluidType(type));
        FoodProperties food = edible(player);
        float attack = NO_ATTACK;
        if (minecraft.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
            float strength = player.getAttackStrengthScale(0.0F);
            attack = strength < 1.0F ? strength : NO_ATTACK;
        }
        return new HudSnapshot(player, player.getHealth(), Math.max(player.getMaxHealth(), player.getHealth()), player.getAbsorptionAmount(), heart(player),
            player.level().getLevelData().isHardcore(), player.getArmorValue(), player.getFoodData().getFoodLevel(), player.getFoodData().getSaturationLevel(),
            player.hasEffect(MobEffects.HUNGER), maxAir > 0 ? air / (float) maxAir : 1.0F, underWater || air < maxAir,
            vehicle != null ? vehicle.getHealth() : 0.0F, vehicle != null ? vehicle.getMaxHealth() : 0.0F, minecraft.gameMode != null && minecraft.gameMode.canHurtPlayer(),
            experience, player.experienceLevel, player.experienceProgress, hotbar, inventory.getSelectedSlot(), player.getOffhandItem(),
            player.getMainArm().getOpposite() == HumanoidArm.LEFT, attack, itemName(hud), Math.min(1.0F, hud.arkea$toolHighlightTimer() / NAME_FADE_TICKS),
            armor(player), food != null ? food.nutrition() : 0, food != null ? food.saturation() : 0.0F);
    }

    public static HudSnapshot sample(boolean full) {
        if (full) {
            if (fullSample == null) {
                HudSnapshot base = sample(false);
                fullSample = new HudSnapshot(null, base.maxHealth, base.maxHealth, 0.0F, base.heart, false, base.armor, (int) MAX_FOOD, base.saturation, false,
                    1.0F, false, 0.0F, 0.0F, true, true, base.level, base.progress, base.hotbar, base.selected, base.offhand, base.offhandLeft, NO_ATTACK,
                    base.itemName, base.itemNameAlpha, base.armorItems, 0, 0.0F);
            }
            return fullSample;
        }
        if (sample == null) {
            List<ItemStack> hotbar = List.of(new ItemStack(Items.DIAMOND_SWORD), worn(Items.IRON_PICKAXE, USED), worn(Items.STONE_AXE, WORN),
                new ItemStack(Items.BOW), new ItemStack(Items.COOKED_BEEF, STEAK), new ItemStack(Items.BREAD, BREAD), new ItemStack(Items.COBBLESTONE, STACK),
                new ItemStack(Items.OAK_PLANKS, PLANKS), ItemStack.EMPTY);
            sample = new HudSnapshot(null, SAMPLE_HEALTH, SAMPLE_MAX_HEALTH, 0.0F, Hud.HeartType.NORMAL, false, SAMPLE_ARMOR, SAMPLE_FOOD, SAMPLE_SATURATION,
                false, 1.0F, false, 0.0F, 0.0F, true, true, SAMPLE_LEVEL, SAMPLE_PROGRESS, hotbar, 0, new ItemStack(Items.TORCH, TORCHES), true, NO_ATTACK,
                styledName(hotbar.getFirst()), 1.0F, List.of(new ItemStack(Items.IRON_HELMET), worn(Items.IRON_CHESTPLATE, USED)), 0, 0.0F);
        }
        return sample;
    }

    private static ItemStack worn(Item item, float share) {
        ItemStack stack = new ItemStack(item);
        stack.setDamageValue((int) (stack.getMaxDamage() * share));
        return stack;
    }

    private static List<ItemStack> armor(Player player) {
        List<ItemStack> armor = new ArrayList<>(ARMOR_SLOTS.size());
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                armor.add(stack);
            }
        }
        return armor;
    }

    private static @Nullable FoodProperties edible(Player player) {
        for (ItemStack stack : List.of(player.getMainHandItem(), player.getOffhandItem())) {
            FoodProperties food = stack.get(DataComponents.FOOD);
            if (food != null && (food.canAlwaysEat() || player.getFoodData().needsFood())) {
                return food;
            }
        }
        return null;
    }

    private static Hud.HeartType heart(Player player) {
        Hud.HeartType type = Hud.HeartType.NORMAL;
        if (player.hasEffect(MobEffects.POISON)) {
            type = Hud.HeartType.POISIONED;
        } else if (player.hasEffect(MobEffects.WITHER)) {
            type = Hud.HeartType.WITHERED;
        } else if (player.isFullyFrozen()) {
            type = Hud.HeartType.FROZEN;
        }
        return ClientHooks.firePlayerHeartTypeEvent(player, type);
    }

    private static @Nullable LivingEntity vehicle(Player player) {
        Entity vehicle = player.getVehicle();
        return vehicle instanceof LivingEntity living && living.showVehicleHealth() ? living : null;
    }

    private static @Nullable Component itemName(HudAccessor hud) {
        ItemStack stack = hud.arkea$lastToolHighlight();
        return hud.arkea$toolHighlightTimer() > 0 && !stack.isEmpty() ? styledName(stack) : null;
    }

    private static Component styledName(ItemStack stack) {
        MutableComponent name = Component.empty().append(stack.getHoverName()).withStyle(stack.getRarity().getStyleModifier());
        if (stack.has(DataComponents.CUSTOM_NAME)) {
            name.withStyle(ChatFormatting.ITALIC);
        }
        return stack.getHighlightTip(name);
    }
}
