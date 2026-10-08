package com.aryston.arkea.hud.extra;

import com.aryston.arkea.hud.HudClock;
import com.aryston.arkea.config.ArkeaConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

public final class HitMarker {
    private static final Identifier SPRITE = Identifier.fromNamespaceAndPath("arkea", "crosshair/hit");
    private static final int SIZE = 15;
    private static final long DURATION = 260L;
    private static long hitAt = -DURATION;

    private HitMarker() {
    }

    public static void onAttack(AttackEntityEvent event) {
        if (event.getEntity().level().isClientSide() && ArkeaConfig.on(ArkeaConfig.HIT_MARKER)) {
            hitAt = HudClock.now();
        }
    }

    public static void draw(GuiGraphicsExtractor graphics) {
        long elapsed = HudClock.now() - hitAt;
        if (elapsed >= DURATION) {
            return;
        }
        float alpha = 1.0F - elapsed / (float) DURATION;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE, (graphics.guiWidth() - SIZE) / 2, (graphics.guiHeight() - SIZE) / 2, SIZE, SIZE,
            ARGB.white(alpha));
    }
}
