package com.aryston.arkea.ui.render;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class SampleItems {
    private SampleItems() {
    }

    public static boolean ready(Item item) {
        return item.builtInRegistryHolder().areComponentsBound();
    }

    public static ItemStack of(Item item) {
        return ready(item) ? new ItemStack(item) : ItemStack.EMPTY;
    }
}
