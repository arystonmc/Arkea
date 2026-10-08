package com.aryston.arkea.mixin;

import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AlertScreen.class)
public interface AlertScreenAccessor {
    @Accessor("messageText")
    Component arkea$message();
}
