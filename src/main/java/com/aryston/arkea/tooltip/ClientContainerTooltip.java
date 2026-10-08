package com.aryston.arkea.tooltip;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

final class ClientContainerTooltip implements ClientTooltipComponent {
    private static final int COLUMNS = 9;
    private static final int SLOT = 18;
    private static final int ITEM_INSET = 1;
    private static final int TOP_GAP = 2;
    private static final int SLOT_FILL = 0x14FFFFFF;
    private static final int SLOT_EDGE = 0x1FFFFFFF;

    private final List<ItemStack> items;

    ClientContainerTooltip(ContainerTooltip tooltip) {
        this.items = tooltip.items();
    }

    @Override
    public int getHeight(Font font) {
        return this.rows() * SLOT + TOP_GAP * 2;
    }

    @Override
    public int getWidth(Font font) {
        return Math.min(COLUMNS, this.items.size()) * SLOT;
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        for (int index = 0; index < this.items.size(); index++) {
            int slotX = x + index % COLUMNS * SLOT;
            int slotY = y + TOP_GAP + index / COLUMNS * SLOT;
            graphics.fill(slotX, slotY, slotX + SLOT - 1, slotY + SLOT - 1, SLOT_FILL);
            graphics.fill(slotX, slotY + SLOT - 2, slotX + SLOT - 1, slotY + SLOT - 1, SLOT_EDGE);
            ItemStack stack = this.items.get(index);
            graphics.fakeItem(stack, slotX + ITEM_INSET, slotY + ITEM_INSET);
            graphics.itemDecorations(font, stack, slotX + ITEM_INSET, slotY + ITEM_INSET);
        }
    }

    private int rows() {
        return (this.items.size() + COLUMNS - 1) / COLUMNS;
    }
}
