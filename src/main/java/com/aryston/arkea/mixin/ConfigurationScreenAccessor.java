package com.aryston.arkea.mixin;

import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ConfigurationScreen.class)
public interface ConfigurationScreenAccessor {
    @Accessor("mod")
    ModContainer arkea$mod();
}
