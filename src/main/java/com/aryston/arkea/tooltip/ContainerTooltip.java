package com.aryston.arkea.tooltip;

import java.util.List;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record ContainerTooltip(List<ItemStack> items) implements TooltipComponent {
}
