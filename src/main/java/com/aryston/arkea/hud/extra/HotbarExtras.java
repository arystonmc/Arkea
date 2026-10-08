package com.aryston.arkea.hud.extra;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.hud.HudPainter;
import com.aryston.arkea.hud.HudSettings;
import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public final class HotbarExtras {
    private static final float BADGE_HEIGHT = 13.0F;
    private static final float BADGE_PAD = 5.0F;
    private static final float BADGE_GAP = 3.0F;
    private static final float LEVEL_CLEARANCE = 4.0F;
    private static final float AMMO_GAP = 8.0F;
    private static final float AMMO_PAD = 6.0F;
    private static final float AMMO_ICON = 16.0F;
    private static final int ITEM_PIXELS = 16;
    private static final float OFFHAND_WIDTH = 42.0F;
    private static final float MIN_OPACITY = 0.6F;
    private static final int BUMP = 280;
    private static final float BUMP_SCALE = 0.22F;
    private static final float HALF = 0.5F;
    private static final String INFINITE = "∞";
    private static final TextStyle BADGE = TextStyle.of(9.0F);
    private static final TextStyle AMMO = TextStyle.of(10.0F);
    private static int lastTotal;
    private static long totalAt;

    private HotbarExtras() {
    }

    public static void draw(UiGraphics graphics, HotbarLayout layout, HudSettings settings, Minecraft minecraft, Player player) {
        Box hotbar = layout.hotbar();
        int plate = ArkColors.withAlpha(HudPainter.plate(settings), Math.max(settings.opacity(), MIN_OPACITY));
        TextMetrics metrics = graphics.metrics();
        ItemStack held = player.getInventory().getSelectedItem();
        if (ArkeaConfig.on(ArkeaConfig.STACK_TOTAL) && !held.isEmpty() && held.getMaxStackSize() > 1) {
            int total = count(player.getInventory(), held, true);
            if (total != lastTotal) {
                lastTotal = total;
                totalAt = graphics.now();
            }
            if (total > held.getCount()) {
                String text = Component.translatable("arkea.hud.total", total).getString();
                float width = metrics.width(text, BADGE) + BADGE_PAD * 2.0F;
                Box badge = badge(layout, width);
                float bump = bump(graphics.now() - totalAt);
                graphics.push();
                graphics.scaleAround(1.0F + BUMP_SCALE * bump, badge.centerX(), badge.centerY());
                graphics.fill(badge, plate);
                graphics.border(badge, 1.0F, ArkColors.BORDER_DEFAULT);
                graphics.text(text, badge.x() + BADGE_PAD, badge.centerY() - metrics.capHeight(BADGE) * HALF, BADGE, ArkColors.TEXT_PRIMARY);
                graphics.pop();
            }
        }
        if (ArkeaConfig.on(ArkeaConfig.AMMO_COUNTER)) {
            ItemStack weapon = player.getMainHandItem().getItem() instanceof ProjectileWeaponItem ? player.getMainHandItem()
                : player.getOffhandItem().getItem() instanceof ProjectileWeaponItem ? player.getOffhandItem() : ItemStack.EMPTY;
            if (!weapon.isEmpty()) {
                ItemStack ammo = player.getProjectile(weapon);
                boolean infinite = player.hasInfiniteMaterials() || ammo.is(Items.ARROW) && infinity(minecraft, weapon);
                int count = ammo.isEmpty() ? 0 : count(player.getInventory(), ammo, false);
                String text = infinite ? INFINITE : String.valueOf(count);
                float width = AMMO_PAD * 2.0F + AMMO_ICON + AMMO_PAD + metrics.width(text, AMMO);
                boolean offhandRight = !player.getOffhandItem().isEmpty() && player.getMainArm() == HumanoidArm.LEFT;
                float x = hotbar.right() + AMMO_GAP + (offhandRight ? OFFHAND_WIDTH + AMMO_GAP : 0.0F);
                Box chip = new Box(x, hotbar.centerY() - hotbar.height() * HALF, width, hotbar.height());
                graphics.fill(chip, plate);
                ItemStack icon = ammo.isEmpty() ? new ItemStack(Items.ARROW) : ammo;
                graphics.vanilla(new Box(chip.x() + AMMO_PAD, chip.centerY() - AMMO_ICON * HALF, AMMO_ICON, AMMO_ICON), ITEM_PIXELS,
                    vanilla -> vanilla.fakeItem(icon, 0, 0));
                int color = !infinite && count == 0 ? ArkColors.ERROR : ArkColors.TEXT_PRIMARY;
                graphics.text(text, chip.x() + AMMO_PAD * 2.0F + AMMO_ICON, chip.centerY() - metrics.capHeight(AMMO) * HALF, AMMO, color);
            }
        }
    }

    private static Box badge(HotbarLayout layout, float width) {
        float x = layout.selected().centerX() - width * HALF;
        float bottom = layout.experience().y() - BADGE_GAP;
        Box level = layout.level();
        if (level != null && x < level.right() + LEVEL_CLEARANCE && x + width > level.x() - LEVEL_CLEARANCE) {
            bottom = Math.min(bottom, level.y() - BADGE_GAP);
        }
        return new Box(x, bottom - BADGE_HEIGHT, width, BADGE_HEIGHT);
    }

    private static int count(Inventory inventory, ItemStack match, boolean sameComponents) {
        int total = 0;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (sameComponents ? ItemStack.isSameItemSameComponents(stack, match) : ItemStack.isSameItem(stack, match)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private static boolean infinity(Minecraft minecraft, ItemStack weapon) {
        if (minecraft.level == null) {
            return false;
        }
        Holder<Enchantment> infinity = minecraft.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.INFINITY);
        return EnchantmentHelper.getItemEnchantmentLevel(infinity, weapon) > 0;
    }

    private static float bump(long elapsed) {
        if (elapsed < 0L || elapsed >= BUMP) {
            return 0.0F;
        }
        return Mth.sin(Easing.EASE_OUT.apply(elapsed / (float) BUMP) * Mth.PI);
    }
}
