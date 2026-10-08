package com.aryston.arkea.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record DurabilityTooltip(int remaining, int max, int color) implements TooltipComponent {
}
