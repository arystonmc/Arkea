package com.aryston.arkea.ui.anim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TransitionTest {
    private static final float TOLERANCE = 1.0E-4F;

    @Test
    void reachesTheTargetAfterItsDuration() {
        Transition transition = new Transition(0.0F, 150, Easing.EASE);
        transition.setTarget(1.0F, 1000L);
        assertEquals(0.0F, transition.value(1000L), TOLERANCE);
        assertTrue(transition.value(1075L) > 0.5F);
        assertEquals(1.0F, transition.value(1150L), TOLERANCE);
    }

    @Test
    void retargetingStartsFromTheCurrentValue() {
        Transition transition = new Transition(0.0F, 100, Easing.LINEAR);
        transition.setTarget(1.0F, 0L);
        float current = transition.value(50L);
        transition.setTarget(0.0F, 50L);
        assertEquals(current, transition.value(50L), TOLERANCE);
        assertEquals(0.0F, transition.value(150L), TOLERANCE);
    }

    @Test
    void snapJumpsWithoutAnimation() {
        Transition transition = new Transition(0.0F, 100, Easing.LINEAR);
        transition.snap(1.0F);
        assertEquals(1.0F, transition.value(0L));
        assertEquals(1.0F, transition.target());
    }
}
