package com.aryston.arkea.mixin;

import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ProgressScreen.class)
public interface ProgressScreenAccessor {
    @Accessor("header")
    @Nullable Component arkea$header();

    @Accessor("stage")
    @Nullable Component arkea$stage();

    @Accessor("progress")
    int arkea$progress();
}
