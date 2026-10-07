package com.aryston.arkea.ui.render;

import com.aryston.arkea.ui.theme.ArkColors;

public final class PixelSpinner {
    public static final float SIZE = 24.0F;
    private static final float DOT = 6.0F;
    private static final long CYCLE_MS = 800L;
    private static final float MIN_ALPHA = 0.2F;
    private static final float[][] DOTS = {{9.0F, 0.0F}, {16.0F, 3.0F}, {18.0F, 9.0F}, {16.0F, 15.0F}, {9.0F, 18.0F}, {2.0F, 15.0F}, {0.0F, 9.0F},
        {2.0F, 3.0F}};

    private PixelSpinner() {
    }

    public static void render(UiGraphics graphics, float x, float y, int color) {
        float phase = (graphics.now() % CYCLE_MS) / (float) CYCLE_MS * DOTS.length;
        for (int index = 0; index < DOTS.length; index++) {
            float distance = (phase - index + DOTS.length) % DOTS.length;
            float alpha = Math.max(MIN_ALPHA, 1.0F - distance / DOTS.length);
            graphics.fill(x + DOTS[index][0], y + DOTS[index][1], DOT, DOT, ArkColors.multiplyAlpha(color, alpha));
        }
    }
}
