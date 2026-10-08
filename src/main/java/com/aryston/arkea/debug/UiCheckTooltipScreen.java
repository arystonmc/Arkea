package com.aryston.arkea.debug;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

final class UiCheckTooltipScreen extends Screen {
    private static final int TOOLTIP_X = 60;
    private static final int TOOLTIP_Y = 40;

    private final ItemStack stack;

    UiCheckTooltipScreen(ItemStack stack) {
        super(Component.empty());
        this.stack = stack;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.setTooltipForNextFrame(this.font, this.stack, TOOLTIP_X, TOOLTIP_Y);
    }
}
