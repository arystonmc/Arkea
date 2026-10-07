package com.aryston.arkea.ui.anim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PresenceTest {
    private static final int ENTER = 300;
    private static final int EXIT = 180;
    private static final float TOLERANCE = 1.0E-4F;

    @Test
    void startsHidden() {
        Presence presence = Presence.of(ENTER, EXIT);
        assertEquals(0.0F, presence.progress(1000L));
        assertTrue(presence.isGone(1000L));
        assertFalse(presence.isShown());
    }

    @Test
    void entersAndSettles() {
        Presence presence = Presence.of(ENTER, EXIT);
        presence.show(1000L);
        assertEquals(0.0F, presence.progress(1000L), TOLERANCE);
        assertTrue(presence.progress(1150L) > 0.5F);
        assertEquals(1.0F, presence.progress(1300L), TOLERANCE);
    }

    @Test
    void exitStaysMountedUntilItFinishes() {
        Presence presence = Presence.of(ENTER, EXIT);
        presence.show(0L);
        presence.hide(1000L);
        assertFalse(presence.isGone(1100L));
        assertTrue(presence.progress(1100L) > 0.0F);
        assertTrue(presence.isGone(1180L));
        assertEquals(0.0F, presence.progress(1180L), TOLERANCE);
    }

    @Test
    void reversingMidwayContinuesFromTheCurrentValue() {
        Presence presence = Presence.of(ENTER, EXIT);
        presence.show(0L);
        float midway = presence.progress(100L);
        presence.hide(100L);
        assertEquals(midway, presence.progress(100L), TOLERANCE);
        presence.show(150L);
        float resumed = presence.progress(150L);
        assertTrue(resumed > 0.0F && resumed < midway);
    }

    @Test
    void delayedEnterWaitsBeforeMoving() {
        Presence presence = Presence.of(ENTER, EXIT);
        presence.enter(0L, 200);
        assertEquals(0.0F, presence.progress(150L), TOLERANCE);
        assertTrue(presence.progress(350L) > 0.0F);
        assertTrue(presence.progress(400L) < 1.0F);
        assertEquals(1.0F, presence.progress(500L), TOLERANCE);
    }
}
