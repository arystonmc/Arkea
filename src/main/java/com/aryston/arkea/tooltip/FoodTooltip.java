package com.aryston.arkea.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record FoodTooltip(int nutrition, float saturation) implements TooltipComponent {
}
