package com.aryston.arkea.background;

import java.nio.ByteBuffer;

final class NalUnits {
    private static final int TYPE_MASK = 0x1F;
    private static final int REFERENCE_SHIFT = 5;
    private static final int REFERENCE_MASK = 0x03;
    private static final int CODED_SLICE = 1;
    private static final int IDR_SLICE = 5;
    private static final int START_CODE_PREFIX = 2;

    private NalUnits() {
    }

    static boolean isDisposableFrame(ByteBuffer annexB) {
        ByteBuffer data = annexB.duplicate();
        int limit = data.limit();
        for (int index = data.position(); index + START_CODE_PREFIX < limit; index++) {
            if (data.get(index) != 0 || data.get(index + 1) != 0 || data.get(index + 2) != 1) {
                continue;
            }
            int headerIndex = index + START_CODE_PREFIX + 1;
            if (headerIndex >= limit) {
                return false;
            }
            int header = data.get(headerIndex) & 0xFF;
            int type = header & TYPE_MASK;
            if (type == CODED_SLICE || type == IDR_SLICE) {
                return (header >> REFERENCE_SHIFT & REFERENCE_MASK) == 0;
            }
            index = headerIndex;
        }
        return false;
    }
}
