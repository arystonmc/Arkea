package com.aryston.arkea.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.gui.modlist.ModListScreen;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModListScreen.class)
public interface ModListScreenAccessor {
    @Accessor("lastScreen")
    @Nullable Screen arkea$lastScreen();
}
