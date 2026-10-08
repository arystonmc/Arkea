package com.aryston.arkea.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

final class ClientDurabilityTooltip implements ClientTooltipComponent {
    private static final int BAR_WIDTH = 40;
    private static final int BAR_HEIGHT = 3;
    private static final int BAR_Y = 3;
    private static final int GAP = 5;
    private static final int HEIGHT = 11;
    private static final int TRACK = 0x33FFFFFF;
    private static final int TEXT = 0xFFC8C8CA;

    private final DurabilityTooltip tooltip;
    private final Component text;

    ClientDurabilityTooltip(DurabilityTooltip tooltip) {
        this.tooltip = tooltip;
        this.text = Component.translatable("arkea.tooltip.durability", tooltip.remaining(), tooltip.max());
    }

    @Override
    public int getHeight(Font font) {
        return HEIGHT;
    }

    @Override
    public int getWidth(Font font) {
        return BAR_WIDTH + GAP + font.width(this.text);
    }

    @Override
    public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
        graphics.text(font, this.text, x + BAR_WIDTH + GAP, y, TEXT, true);
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        graphics.fill(x, y + BAR_Y, x + BAR_WIDTH, y + BAR_Y + BAR_HEIGHT, TRACK);
        int filled = Math.round(BAR_WIDTH * this.tooltip.remaining() / (float) Math.max(1, this.tooltip.max()));
        graphics.fill(x, y + BAR_Y, x + filled, y + BAR_Y + BAR_HEIGHT, ARGB.opaque(this.tooltip.color()));
    }
}
