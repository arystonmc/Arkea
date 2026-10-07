package com.aryston.arkea.background;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FrameSlotsTest {
    @Test
    void sixtyFramesPerSecondKeepEveryOtherFrame() {
        double[] times = {0.0, 2.0 / 60.0, 1.0 / 60.0, 3.0 / 60.0};
        FrameSlots slots = FrameSlots.of(times, 4.0 / 60.0, 30, 100);

        assertEquals(0, slots.first(0.0));
        assertEquals(1, slots.end(0.0));
        assertEquals(slots.first(1.0 / 60.0), slots.end(1.0 / 60.0), "the frame between two slots is never shown");
        assertEquals(1, slots.first(2.0 / 60.0));
        assertEquals(2, slots.last());
    }

    @Test
    void slowFramesFillSeveralSlots() {
        FrameSlots slots = FrameSlots.of(new double[] {0.0, 0.1}, 0.2, 30, 100);

        assertEquals(0, slots.first(0.0));
        assertEquals(3, slots.end(0.0));
        assertEquals(6, slots.end(0.1));
    }

    @Test
    void slotsStopAtTheLimit() {
        FrameSlots slots = FrameSlots.of(new double[] {0.0, 1.0}, 2.0, 30, 45);

        assertEquals(30, slots.end(0.0));
        assertEquals(45, slots.end(1.0));
    }

    @Test
    void unknownTimesAreNotShown() {
        FrameSlots slots = FrameSlots.of(new double[] {0.5}, 1.0, 30, 100);

        assertEquals(0, slots.first(0.25));
        assertEquals(0, slots.end(0.25));
    }
}
