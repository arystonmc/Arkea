package com.aryston.arkea.hud;

import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;

public final class HudBossBar {
    private static final float WIDTH = 320.0F;
    private static final float DROP = 8.0F;
    private static final float HEIGHT = 4.0F;
    private static final float NAME_GAP = 5.0F;
    private static final float NOTCH = 1.0F;
    private static final float HALF = 0.5F;
    private static final int NOTCHES_6 = 6;
    private static final int NOTCHES_10 = 10;
    private static final int NOTCHES_12 = 12;
    private static final int NOTCHES_20 = 20;
    private static final int TRACK = ArkColors.rgba(0, 0, 0, 0.35F);
    private static final int NOTCH_COLOR = ArkColors.rgba(0, 0, 0, 0.45F);
    private static final int TEXT_SHADOW = ArkColors.rgba(0, 0, 0, 0.55F);
    private static final TextStyle NAME = TextStyle.of(10.0F);

    private HudBossBar() {
    }

    public static float draw(UiGraphics graphics, float centerX, float vanillaY, BossEvent event) {
        float barY = vanillaY + DROP;
        TextMetrics metrics = graphics.metrics();
        Component name = event.getName();
        float nameWidth = metrics.width(name.getString(), NAME);
        float nameX = centerX - nameWidth * HALF;
        float nameY = barY - NAME_GAP - metrics.capHeight(NAME);
        graphics.text(name.getString(), nameX + 1.0F, nameY + 1.0F, NAME, TEXT_SHADOW);
        graphics.richText(name, nameX, nameY, nameWidth + 1.0F, NAME, ArkColors.TEXT_PRIMARY);
        float x = centerX - WIDTH * HALF;
        graphics.fill(x, barY, WIDTH, HEIGHT, TRACK);
        graphics.fill(x, barY, WIDTH * Math.clamp(event.getProgress(), 0.0F, 1.0F), HEIGHT, color(event.getColor()));
        int notches = notches(event.getOverlay());
        for (int index = 1; index < notches; index++) {
            graphics.fill(x + WIDTH * index / notches - NOTCH * HALF, barY, NOTCH, HEIGHT, NOTCH_COLOR);
        }
        return barY + HEIGHT;
    }

    private static int color(BossEvent.BossBarColor color) {
        return switch (color) {
            case PINK -> ArkColors.rgba(230, 92, 180, 0.9F);
            case BLUE -> ArkColors.rgba(79, 163, 224, 0.9F);
            case RED -> ArkColors.rgba(224, 83, 75, 0.9F);
            case GREEN -> ArkColors.rgba(123, 201, 95, 0.9F);
            case YELLOW -> ArkColors.rgba(230, 200, 74, 0.9F);
            case PURPLE -> ArkColors.rgba(156, 107, 219, 0.9F);
            case WHITE -> ArkColors.rgba(232, 232, 232, 0.9F);
        };
    }

    private static int notches(BossEvent.BossBarOverlay overlay) {
        return switch (overlay) {
            case PROGRESS -> 0;
            case NOTCHED_6 -> NOTCHES_6;
            case NOTCHED_10 -> NOTCHES_10;
            case NOTCHED_12 -> NOTCHES_12;
            case NOTCHED_20 -> NOTCHES_20;
        };
    }
}
