package com.aryston.arkea.mixin;

import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelLoadingScreen.class)
public interface LevelLoadingScreenAccessor {
    @Accessor("loadTracker")
    LevelLoadTracker arkea$loadTracker();

    @Accessor("reason")
    LevelLoadingScreen.Reason arkea$reason();
}
