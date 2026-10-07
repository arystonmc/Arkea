package com.aryston.arkea.background;

import java.awt.image.BufferedImage;
import java.io.IOException;
import org.jspecify.annotations.Nullable;

interface MediaFrames extends AutoCloseable {
    @Nullable Frame next() throws IOException;

    float progress(double time);

    @Override
    void close() throws IOException;

    record Frame(BufferedImage image, double time) {
    }
}
