package com.aryston.arkea.ui.anim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CubicBezierTest {
    private static final float TOLERANCE = 1.0E-3F;

    @Test
    void endpointsAreFixed() {
        assertEquals(0.0F, Easing.STANDARD.apply(0.0F));
        assertEquals(1.0F, Easing.STANDARD.apply(1.0F));
        assertEquals(0.0F, Easing.STANDARD.apply(-0.5F));
        assertEquals(1.0F, Easing.STANDARD.apply(1.5F));
    }

    @Test
    void linearControlPointsGiveIdentity() {
        CubicBezier linear = new CubicBezier(0.0F, 0.0F, 1.0F, 1.0F);
        for (float progress = 0.0F; progress <= 1.0F; progress += 0.1F) {
            assertEquals(progress, linear.apply(progress), TOLERANCE);
        }
    }

    @Test
    void easeMatchesTheCssReferenceValue() {
        assertEquals(0.8024F, Easing.EASE.apply(0.5F), TOLERANCE);
    }

    @Test
    void standardCurveIsMonotonicAndFrontLoaded() {
        float previous = 0.0F;
        for (int step = 1; step <= 100; step++) {
            float value = Easing.STANDARD.apply(step / 100.0F);
            assertTrue(value >= previous);
            previous = value;
        }
        assertTrue(Easing.STANDARD.apply(0.25F) > 0.5F);
    }

    @Test
    void springOvershoots() {
        float peak = 0.0F;
        for (int step = 0; step <= 100; step++) {
            peak = Math.max(peak, Easing.SPRING.apply(step / 100.0F));
        }
        assertTrue(peak > 1.0F);
    }
}
