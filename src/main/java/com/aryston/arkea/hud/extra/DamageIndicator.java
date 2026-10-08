package com.aryston.arkea.hud.extra;

import com.aryston.arkea.hud.HudClock;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;

public final class DamageIndicator {
    private static final long LIFE = 1100L;
    private static final float RADIUS = 92.0F;
    private static final float SEGMENT = 18.0F;
    private static final float THICKNESS = 4.0F;
    private static final float SPREAD = 9.0F;
    private static final float FRONT = 90.0F;
    private static final int SEGMENTS = 3;
    private static final float SIDE_ALPHA = 0.55F;
    private static final int MAX_HITS = 4;
    private static final float HALF = 0.5F;
    private static final int COLOR = ArkColors.rgba(240, 88, 74, 0.9F);
    private static final List<Hit> HITS = new ArrayList<>();

    private DamageIndicator() {
    }

    static void add(float hurtDirection, float yaw) {
        HITS.add(new Hit(hurtDirection - FRONT + yaw, HudClock.now()));
        while (HITS.size() > MAX_HITS) {
            HITS.removeFirst();
        }
    }

    public static void draw(UiGraphics graphics, Box screen, float yaw) {
        long now = graphics.now();
        HITS.removeIf(hit -> now - hit.at() > LIFE);
        for (Hit hit : HITS) {
            float life = 1.0F - (now - hit.at()) / (float) LIFE;
            float angle = (hit.world() - yaw) * Mth.DEG_TO_RAD;
            for (int index = 0; index < SEGMENTS; index++) {
                float offset = (index - (SEGMENTS - 1) * HALF) * SPREAD * Mth.DEG_TO_RAD;
                float alpha = life * (index == (SEGMENTS - 1) / 2 ? 1.0F : SIDE_ALPHA);
                graphics.push();
                graphics.rotateAround(angle + offset, screen.centerX(), screen.centerY());
                graphics.fill(screen.centerX() - SEGMENT * HALF, screen.centerY() - RADIUS - THICKNESS, SEGMENT, THICKNESS, ArkColors.multiplyAlpha(COLOR, alpha));
                graphics.pop();
            }
        }
    }

    private record Hit(float world, long at) {
    }
}
