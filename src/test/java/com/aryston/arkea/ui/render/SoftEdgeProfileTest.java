package com.aryston.arkea.ui.render;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SoftEdgeProfileTest {
    @Test
    void edgeCoverageIsHalfAtTheEdgeAndRisesInside() {
        assertEquals(0.5F, SoftEdgeProfile.coverage(0.5F), 1.0E-3F);
        assertTrue(SoftEdgeProfile.coverage(0.0F) < 0.05F);
        assertTrue(SoftEdgeProfile.coverage(1.0F) > 0.95F);
        assertTrue(SoftEdgeProfile.coverage(0.75F) > SoftEdgeProfile.coverage(0.25F));
    }

    @Test
    void radialFadeStartsAtTheInnerRadiusAndPeaksInTheCorners() {
        assertEquals(0.0F, SoftEdgeProfile.radial(0.0F, 0.0F, 0.45F));
        assertEquals(0.0F, SoftEdgeProfile.radial(0.5F, 0.0F, 0.45F));
        assertEquals(1.0F, SoftEdgeProfile.radial(1.0F, 1.0F, 0.45F), 1.0E-4F);
    }
}
