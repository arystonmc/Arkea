package com.aryston.arkea.hud.extra;

import net.minecraft.world.item.ItemStack;

public final class Durability {
    private static final float LOW_SHARE = 0.1F;

    private Durability() {
    }

    public static boolean low(ItemStack stack) {
        if (stack.isEmpty() || !stack.isDamageableItem() || stack.getMaxDamage() <= 0) {
            return false;
        }
        return remaining(stack) <= stack.getMaxDamage() * LOW_SHARE;
    }

    public static int remaining(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamageValue();
    }
}
