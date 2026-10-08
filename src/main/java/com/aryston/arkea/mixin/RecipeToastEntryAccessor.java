package com.aryston.arkea.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.gui.components.toasts.RecipeToast$Entry")
public interface RecipeToastEntryAccessor {
    @Accessor("unlockedItem")
    ItemStack arkea$unlockedItem();
}
