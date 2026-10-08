package com.aryston.arkea.hud.target;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

record TargetInfo(String key, ItemStack icon, Component name, Component mod, List<Line> lines, float health, float maxHealth, int armor, float breaking) {
    static final float NONE = -1.0F;

    boolean living() {
        return this.maxHealth > 0.0F;
    }

    record Line(Component text, int color, ItemStack icon) {
    }
}
