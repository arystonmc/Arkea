package com.aryston.arkea.background;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;

class NalUnitsTest {
    @Test
    void nonReferenceSliceIsDisposable() {
        assertTrue(NalUnits.isDisposableFrame(ByteBuffer.wrap(new byte[] {0, 0, 0, 1, 0x01, 0x11, 0x22})));
    }

    @Test
    void referenceSliceIsKept() {
        assertFalse(NalUnits.isDisposableFrame(ByteBuffer.wrap(new byte[] {0, 0, 0, 1, 0x41, 0x11, 0x22})));
    }

    @Test
    void parameterSetsBeforeTheSliceAreSkipped() {
        byte[] keyFrame = {0, 0, 0, 1, 0x67, 0x10, 0, 0, 0, 1, 0x68, 0x20, 0, 0, 1, 0x65, 0x30};
        assertFalse(NalUnits.isDisposableFrame(ByteBuffer.wrap(keyFrame)));
        byte[] seiThenBFrame = {0, 0, 0, 1, 0x06, 0x05, 0x01, 0, 0, 1, 0x01, 0x40};
        assertTrue(NalUnits.isDisposableFrame(ByteBuffer.wrap(seiThenBFrame)));
    }

    @Test
    void dataWithoutSliceIsKept() {
        assertFalse(NalUnits.isDisposableFrame(ByteBuffer.wrap(new byte[] {1, 2, 3, 4, 5})));
        assertFalse(NalUnits.isDisposableFrame(ByteBuffer.wrap(new byte[0])));
    }
}
