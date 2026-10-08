package com.aryston.arkea.hud.target;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

record TargetInfo(String key, ItemStack icon, Component name, Component mod, List<Line> lines, float health, float maxHealth, int armor, float breaking) {
    static final float NONE = -1.0F;

    boolean living() {
        return this.maxHealth > 0.0F;
    }

    enum Mark {
        NONE,
        YES,
        NO
    }

    record Line(Component text, int color, ItemStack icon, Mark mark) {
        Line(Component text, int color, ItemStack icon) {
            this(text, color, icon, Mark.NONE);
        }
    }
}
