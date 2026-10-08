package com.aryston.arkea.ui.render;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.theme.ArkColors;
import net.minecraft.world.item.ItemStack;

public final class ItemSlot {
    public static final float SIZE = 40.0F;
    private static final float ITEM_NATIVE = 16.0F;
    private static final float ITEM_SIZE = 24.0F;
    private static final float BEVEL = 2.0F;
    private static final int FILL = ArkColors.rgba(0, 0, 0, 0.35F);
    private static final int SHADE = ArkColors.rgba(0, 0, 0, 0.50F);
    private static final int LIGHT = ArkColors.rgba(255, 255, 255, 0.08F);

    private ItemSlot() {
    }

    public static void draw(UiGraphics graphics, Box box, ItemStack stack, boolean selected) {
        graphics.fill(box, FILL);
        graphics.fill(box.x(), box.y(), box.width(), BEVEL, SHADE);
        graphics.fill(box.x(), box.y() + BEVEL, BEVEL, box.height() - BEVEL, SHADE);
        graphics.fill(box.x() + BEVEL, box.bottom() - BEVEL, box.width() - BEVEL, BEVEL, LIGHT);
        graphics.fill(box.right() - BEVEL, box.y() + BEVEL, BEVEL, box.height() - BEVEL * 2.0F, LIGHT);
        if (selected) {
            graphics.border(box, BEVEL, ArkColors.TEXT_PRIMARY);
        }
        if (stack.isEmpty()) {
            return;
        }
        Box item = new Box(box.centerX() - ITEM_SIZE * 0.5F, box.centerY() - ITEM_SIZE * 0.5F, ITEM_SIZE, ITEM_SIZE);
        graphics.vanilla(item, ITEM_NATIVE, vanilla -> vanilla.fakeItem(stack, 0, 0));
    }
}
