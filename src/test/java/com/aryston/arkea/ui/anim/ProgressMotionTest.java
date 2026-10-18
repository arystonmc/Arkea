package com.aryston.arkea.ui.anim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ProgressMotionTest {
    private static final float DELTA = 1.0E-5F;

    @Test
    void mapsAKnownFractionIntoItsSpan() {
        assertEquals(0.2F, ProgressMotion.target(0.2F, 1.0F, 0.0F, 0L), DELTA);
        assertEquals(0.6F, ProgressMotion.target(0.2F, 1.0F, 0.5F, 0L), DELTA);
        assertEquals(1.0F, ProgressMotion.target(0.2F, 1.0F, 2.0F, 0L), DELTA);
    }

    @Test
    void creepsThroughAnUnknownSpanWithoutReachingItsEnd() {
        float early = ProgressMotion.target(0.0F, 0.1F, Float.NaN, 200L);
        float late = ProgressMotion.target(0.0F, 0.1F, Float.NaN, 60_000L);
        assertTrue(early > 0.0F);
        assertTrue(late > early);
        assertTrue(late < 0.1F);
    }

    @Test
    void followsTheTargetSmoothlyAndNeverMovesBack() {
        float shown = ProgressMotion.follow(0.0F, 1.0F, 16L);
        assertTrue(shown > 0.0F && shown < 0.5F);
        assertEquals(shown, ProgressMotion.follow(shown, 0.0F, 16L), DELTA);
        assertEquals(1.0F, ProgressMotion.follow(0.99999F, 1.0F, 10_000L), DELTA);
    }
}
