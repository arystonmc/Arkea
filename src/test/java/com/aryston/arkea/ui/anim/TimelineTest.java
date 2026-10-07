package com.aryston.arkea.ui.anim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TimelineTest {
    @Test
    void enterHoldsUntilItsDelayHasPassed() {
        assertEquals(0.0F, Timeline.enter(100L, 250, 600));
        assertTrue(Timeline.enter(400L, 250, 600) > 0.0F);
        assertEquals(1.0F, Timeline.enter(850L, 250, 600));
    }

    @Test
    void exitGoesFromOneToZero() {
        assertEquals(1.0F, Timeline.exit(0L, 200));
        assertTrue(Timeline.exit(100L, 200) < 1.0F);
        assertEquals(0.0F, Timeline.exit(200L, 200));
    }

    @Test
    void oscillationLoopsBetweenZeroAndOne() {
        assertEquals(0.0F, Oscillation.wave(0L, 900), 1.0E-4F);
        assertEquals(1.0F, Oscillation.wave(450L, 900), 1.0E-4F);
        assertEquals(0.0F, Oscillation.wave(900L, 900), 1.0E-4F);
    }
}
