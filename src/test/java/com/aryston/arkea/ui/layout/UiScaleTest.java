package com.aryston.arkea.ui.layout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UiScaleTest {
    private static final float TOLERANCE = 1.0E-4F;

    @Test
    void designResolutionMapsOneToOne() {
        UiScale scale = UiScale.compute(1366, 768, 3);
        assertEquals(1.0F, scale.scale(), TOLERANCE);
        assertEquals(1366.0F, scale.canvasWidth(), TOLERANCE);
        assertEquals(768.0F, scale.canvasHeight(), TOLERANCE);
    }

    @Test
    void scaleSnapsDownToQuarterSteps() {
        assertEquals(1.25F, UiScale.compute(1920, 1080, 4).scale(), TOLERANCE);
        assertEquals(1.75F, UiScale.compute(2560, 1440, 4).scale(), TOLERANCE);
        assertEquals(0.75F, UiScale.compute(1280, 720, 2).scale(), TOLERANCE);
    }

    @Test
    void canvasAlwaysFitsTheDesign() {
        int[][] windows = {{854, 480}, {1280, 720}, {1600, 900}, {1920, 1080}, {2560, 1080}, {3840, 2160}, {1024, 768}};
        for (int[] window : windows) {
            UiScale scale = UiScale.compute(window[0], window[1], 2);
            assertTrue(scale.canvasWidth() >= UiScale.DESIGN_WIDTH - TOLERANCE, () -> "width at " + window[0]);
            assertTrue(scale.canvasHeight() >= UiScale.DESIGN_HEIGHT - TOLERANCE, () -> "height at " + window[1]);
        }
    }

    @Test
    void referenceScaleFollowsTheWindowNotTheGuiScale() {
        assertEquals(1.5F, UiScale.reference(1920, 1080, 2, 1.5F).scale(), TOLERANCE);
        assertEquals(1.5F, UiScale.reference(1920, 1080, 4, 1.5F).scale(), TOLERANCE);
        assertEquals(2.0F, UiScale.reference(2560, 1440, 3, 1.5F).scale(), TOLERANCE);
        assertEquals(1.5F, UiScale.reference(2560, 1080, 2, 1.5F).scale(), TOLERANCE);
        assertEquals(0.5F, UiScale.reference(320, 240, 1, 1.5F).scale(), TOLERANCE);
    }

    @Test
    void guiAndDesignCoordinatesRoundTrip() {
        UiScale scale = UiScale.compute(1920, 1080, 3);
        assertEquals(500.0F, scale.toDesign(scale.toGui(500.0F)), TOLERANCE);
    }

    @Test
    void thicknessNeverDropsBelowOnePhysicalPixel() {
        UiScale scale = UiScale.compute(854, 480, 2);
        assertEquals(1.0F, scale.snapThickness(1.0F) * scale.scale(), TOLERANCE);
    }
}
