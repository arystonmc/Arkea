package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface GameToastIcon {
    float ITEM_SIZE = 24.0F;
    float ITEM_NATIVE = 16.0F;
    float TONE_ICON = 16.0F;
    float TONE_FILL = 0.14F;

    void draw(UiGraphics graphics, Box tile);

    static GameToastIcon item(ItemStack stack) {
        return (graphics, tile) -> graphics.vanilla(new Box(tile.centerX() - ITEM_SIZE * 0.5F, tile.centerY() - ITEM_SIZE * 0.5F, ITEM_SIZE, ITEM_SIZE),
            ITEM_NATIVE, raw -> raw.fakeItem(stack, 0, 0));
    }

    static GameToastIcon tone(ToastTone tone) {
        return (graphics, tile) -> {
            int color = tone.color();
            graphics.fill(tile, ArkColors.withAlpha(color, TONE_FILL));
            graphics.icon(tone.icon(), tile.centerX() - TONE_ICON * 0.5F, tile.centerY() - TONE_ICON * 0.5F, TONE_ICON, TONE_ICON, color);
        };
    }
}
