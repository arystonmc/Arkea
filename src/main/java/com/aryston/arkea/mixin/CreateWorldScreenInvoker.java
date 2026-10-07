package com.aryston.arkea.mixin;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.world.level.WorldDataConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CreateWorldScreen.class)
public interface CreateWorldScreenInvoker {
    @Invoker("onCreate")
    void arkea$create();

    @Invoker("openExperimentsScreen")
    void arkea$openExperiments(WorldDataConfiguration configuration);

    @Invoker("openDataPackSelectionScreen")
    void arkea$openDataPacks(WorldDataConfiguration configuration);
}
