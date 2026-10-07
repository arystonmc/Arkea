package com.aryston.arkea.ui.widget;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SliderRangeTest {
    private static final SliderRange FOV = new SliderRange(30, 110, 1);

    @Test
    void fractionMapsTheRangeToZeroOne() {
        assertEquals(0.0F, FOV.fraction(30));
        assertEquals(0.5F, FOV.fraction(70), 1.0E-6F);
        assertEquals(1.0F, FOV.fraction(110));
        assertEquals(1.0F, FOV.fraction(500));
    }

    @Test
    void valueAtSnapsToTheStepAndClamps() {
        SliderRange stepped = new SliderRange(0, 100, 5);
        assertEquals(50, stepped.valueAt(0.51F));
        assertEquals(55, stepped.valueAt(0.53F));
        assertEquals(0, stepped.valueAt(-1.0F));
        assertEquals(100, stepped.valueAt(2.0F));
    }

    @Test
    void emptyRangeStaysAtItsMinimum() {
        SliderRange single = new SliderRange(4, 4, 1);
        assertEquals(0.0F, single.fraction(4));
        assertEquals(4, single.valueAt(0.7F));
    }
}
