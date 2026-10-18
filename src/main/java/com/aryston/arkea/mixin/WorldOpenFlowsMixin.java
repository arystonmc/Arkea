package com.aryston.arkea.mixin;

import com.aryston.arkea.screen.loading.LoadingFrames;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldOpenFlows.class)
abstract class WorldOpenFlowsMixin {
    @Redirect(
        method = "loadWorldDataBlocking",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;managedBlock(Ljava/util/function/BooleanSupplier;)V")
    )
    private void arkea$drawWhileDataLoads(Minecraft minecraft, BooleanSupplier done) {
        LoadingFrames.await(minecraft, done);
    }
}
