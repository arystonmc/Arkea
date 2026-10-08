package com.aryston.arkea.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.gui.contextualbar.ContextualBar;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Hud.class)
public interface HudAccessor {
    @Accessor("toolHighlightTimer")
    int arkea$toolHighlightTimer();

    @Accessor("lastToolHighlight")
    ItemStack arkea$lastToolHighlight();

    @Accessor("bossOverlay")
    BossHealthOverlay arkea$bossOverlay();

    @Accessor("tabList")
    PlayerTabOverlay arkea$tabList();

    @Accessor("contextualInfoBar")
    Pair<?, ContextualBar> arkea$contextualInfoBar();
}
