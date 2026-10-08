package com.aryston.arkea.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

final class ClientFoodTooltip implements ClientTooltipComponent {
    private static final Identifier FULL = Identifier.withDefaultNamespace("hud/food_full");
    private static final Identifier HALF = Identifier.withDefaultNamespace("hud/food_half");
    private static final Identifier EMPTY = Identifier.withDefaultNamespace("hud/food_empty");
    private static final int ICON = 9;
    private static final int STEP = 8;
    private static final int MAX_ICONS = 10;
    private static final int GROUP_GAP = 6;
    private static final int TEXT_GAP = 2;
    private static final int HEIGHT = 12;
    private static final int SATURATION_TINT = 0xFFFFD34D;
    private static final int SATURATION_FADED = 0x59FFD34D;
    private static final int TEXT = 0xFFC8C8CA;
    private static final int TEXT_Y = 1;

    private final int nutrition;
    private final float saturation;

    ClientFoodTooltip(FoodTooltip tooltip) {
        this.nutrition = tooltip.nutrition();
        this.saturation = tooltip.saturation();
    }

    @Override
    public int getHeight(Font font) {
        return HEIGHT;
    }

    @Override
    public int getWidth(Font font) {
        int saturation = Mth.ceil(this.saturation);
        int width = this.groupWidth(font, this.nutrition);
        return saturation > 0 ? width + GROUP_GAP + this.groupWidth(font, saturation) : width;
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        int next = this.group(graphics, font, x, y, this.nutrition, false);
        int halves = Mth.ceil(this.saturation);
        if (halves > 0) {
            this.group(graphics, font, next + GROUP_GAP, y, halves, true);
        }
    }

    private int groupWidth(Font font, int halves) {
        int icons = Mth.positiveCeilDiv(Math.max(halves, 1), 2);
        if (icons > MAX_ICONS) {
            return ICON + TEXT_GAP + font.width("x" + icons);
        }
        return (icons - 1) * STEP + ICON;
    }

    private int group(GuiGraphicsExtractor graphics, Font font, int x, int y, int halves, boolean saturation) {
        int icons = Mth.positiveCeilDiv(Math.max(halves, 1), 2);
        if (icons > MAX_ICONS) {
            icon(graphics, x, y, false, saturation);
            String count = "x" + icons;
            graphics.text(font, count, x + ICON + TEXT_GAP, y + TEXT_Y, TEXT, true);
            return x + ICON + TEXT_GAP + font.width(count);
        }
        for (int index = 0; index < icons; index++) {
            icon(graphics, x + index * STEP, y, index * 2 + 1 == halves, saturation);
        }
        return x + (icons - 1) * STEP + ICON;
    }

    private static void icon(GuiGraphicsExtractor graphics, int x, int y, boolean half, boolean saturation) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EMPTY, x, y, ICON, ICON);
        if (saturation) {
            FoodOutline.draw(graphics, x, y, ICON, half, SATURATION_TINT, SATURATION_FADED);
            return;
        }
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, half ? HALF : FULL, x, y, ICON, ICON);
    }
}
