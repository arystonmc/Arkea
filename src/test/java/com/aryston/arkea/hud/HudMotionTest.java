package com.aryston.arkea.hud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HudMotionTest {
    private static final float DELTA = 0.001F;

    @Test
    void barFillsSmoothlyAndLeavesAGhostOnDamage() {
        HudMotion.Bar bar = new HudMotion().health();
        bar.update(20.0F, 0L);
        assertEquals(20.0F, bar.value(0L), DELTA);
        bar.update(12.0F, 1000L);
        float middle = bar.value(1100L);
        assertTrue(middle < 20.0F && middle > 12.0F);
        assertEquals(12.0F, bar.value(2000L), DELTA);
        assertEquals(20.0F, bar.ghost(1300L), DELTA);
        assertEquals(12.0F, bar.ghost(3000L), DELTA);
    }

    @Test
    void healingLeavesNoGhost() {
        HudMotion.Bar bar = new HudMotion().food();
        bar.update(10.0F, 0L);
        bar.update(16.0F, 500L);
        assertEquals(16.0F, bar.ghost(600L), DELTA);
    }

    @Test
    void selectionSlidesAndPops() {
        HudMotion motion = new HudMotion();
        assertEquals(0.0F, motion.selection(0, 0L), DELTA);
        float sliding = motion.selection(4, 1000L);
        assertTrue(sliding < 4.0F);
        assertTrue(motion.selectionScale(1100L) > 1.0F);
        assertEquals(4.0F, motion.selection(4, 2000L), DELTA);
        assertEquals(1.0F, motion.selectionScale(2000L), DELTA);
    }

    @Test
    void levelUpPopsTheLevel() {
        HudMotion motion = new HudMotion();
        assertEquals(1.0F, motion.levelScale(5, 0L), DELTA);
        assertTrue(motion.levelScale(6, 1000L) >= 1.0F);
        assertTrue(motion.levelScale(6, 1150L) > 1.0F);
        assertEquals(1.0F, motion.levelScale(6, 3000L), DELTA);
    }
}
