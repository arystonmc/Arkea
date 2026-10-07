package com.aryston.arkea.background;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rect;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform;

final class PictureScaler {
    private static final int SIGNED_OFFSET = 128;
    private static final int CHANNELS = 3;
    private static final int WEIGHT_BITS = 14;
    private static final int WEIGHT_ONE = 1 << WEIGHT_BITS;
    private static final int COLUMN_SHIFT = 6;
    private static final int ROW_SHIFT = WEIGHT_BITS * 2 - COLUMN_SHIFT;
    private static final int MATRIX_BITS = 16;
    private static final int MAX_CHANNEL = 255;
    private static final int HD_HEIGHT = 720;

    private PictureScaler() {
    }

    static BufferedImage scale(Picture picture, int maxWidth, int maxHeight) {
        ColorSpace color = picture.getColor();
        if (color != ColorSpace.YUV420 && color != ColorSpace.YUV420J) {
            return BackgroundImporter.fit(toRgbImage(picture), maxWidth, maxHeight);
        }
        Rect crop = cropOf(picture);
        float factor = Math.min(1.0F, Math.min(maxWidth / (float) crop.getWidth(), maxHeight / (float) crop.getHeight()));
        int width = Math.max(1, Math.round(crop.getWidth() * factor));
        int height = Math.max(1, Math.round(crop.getHeight() * factor));
        YuvMatrix matrix = color == ColorSpace.YUV420J ? YuvMatrix.BT601_FULL
            : crop.getHeight() >= HD_HEIGHT ? YuvMatrix.BT709_LIMITED : YuvMatrix.BT601_LIMITED;
        int[] luma = resample(picture, 0, crop.getX(), crop.getY(), crop.getWidth(), crop.getHeight(), width, height);
        int chromaX = crop.getX() / 2;
        int chromaY = crop.getY() / 2;
        int chromaWidth = Math.max(1, (crop.getWidth() + 1) / 2);
        int chromaHeight = Math.max(1, (crop.getHeight() + 1) / 2);
        int[] blue = resample(picture, 1, chromaX, chromaY, chromaWidth, chromaHeight, width, height);
        int[] red = resample(picture, 2, chromaX, chromaY, chromaWidth, chromaHeight, width, height);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        for (int index = 0; index < pixels.length; index++) {
            pixels[index] = matrix.rgb(luma[index], blue[index], red[index]);
        }
        return image;
    }

    private static Rect cropOf(Picture picture) {
        Rect crop = picture.getCrop();
        return crop != null ? crop : new Rect(0, 0, picture.getWidth(), picture.getHeight());
    }

    private static int[] resample(Picture picture, int plane, int sourceX, int sourceY, int sourceWidth, int sourceHeight, int width, int height) {
        byte[] data = picture.getPlaneData(plane);
        int stride = picture.getPlaneWidth(plane);
        int rows = picture.getPlaneHeight(plane);
        int columnsAvailable = Math.min(sourceWidth, stride - sourceX);
        int rowsAvailable = Math.min(sourceHeight, rows - sourceY);
        Taps columns = Taps.of(sourceWidth, columnsAvailable, width);
        Taps rowTaps = Taps.of(sourceHeight, rowsAvailable, height);
        int[] result = new int[width * height];
        int[] column = new int[columnsAvailable];
        for (int y = 0; y < height; y++) {
            Arrays.fill(column, 0);
            int first = rowTaps.start(y);
            for (int tap = 0; tap < rowTaps.count(y); tap++) {
                int weight = rowTaps.weight(y, tap);
                int offset = (sourceY + first + tap) * stride + sourceX;
                for (int x = 0; x < columnsAvailable; x++) {
                    column[x] += weight * (data[offset + x] + SIGNED_OFFSET);
                }
            }
            for (int x = 0; x < columnsAvailable; x++) {
                column[x] = (column[x] + (1 << (COLUMN_SHIFT - 1))) >> COLUMN_SHIFT;
            }
            int row = y * width;
            for (int x = 0; x < width; x++) {
                int start = columns.start(x);
                int sum = 0;
                for (int tap = 0; tap < columns.count(x); tap++) {
                    sum += columns.weight(x, tap) * column[start + tap];
                }
                result[row + x] = (sum + (1 << (ROW_SHIFT - 1))) >> ROW_SHIFT;
            }
        }
        return result;
    }

    private static BufferedImage toRgbImage(Picture source) {
        Picture rgb = source;
        if (source.getColor() != ColorSpace.RGB) {
            Transform transform = ColorUtil.getTransform(source.getColor(), ColorSpace.RGB);
            if (transform == null) {
                throw new IllegalStateException("Unsupported pixel format " + source.getColor());
            }
            rgb = Picture.create(source.getWidth(), source.getHeight(), ColorSpace.RGB);
            transform.transform(source, rgb);
        }
        BufferedImage full = new BufferedImage(rgb.getWidth(), rgb.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        byte[] target = ((DataBufferByte) full.getRaster().getDataBuffer()).getData();
        byte[] data = rgb.getPlaneData(0);
        for (int index = 0; index < target.length; index += CHANNELS) {
            target[index] = (byte) (data[index + 2] + SIGNED_OFFSET);
            target[index + 1] = (byte) (data[index + 1] + SIGNED_OFFSET);
            target[index + 2] = (byte) (data[index] + SIGNED_OFFSET);
        }
        Rect crop = source.getCrop();
        if (crop == null) {
            return full;
        }
        return full.getSubimage(crop.getX(), crop.getY(), crop.getWidth(), crop.getHeight());
    }

    private record Taps(int[] starts, int[] counts, int[] offsets, int[] weights) {
        static Taps of(int sourceLength, int available, int length) {
            double ratio = sourceLength / (double) length;
            boolean enlarging = ratio < 1.0;
            int[] starts = new int[length];
            int[] counts = new int[length];
            int[] offsets = new int[length];
            int[] weights = new int[length * ((int) Math.ceil(ratio) + 2)];
            int used = 0;
            for (int index = 0; index < length; index++) {
                double center = Math.max(0.0, (index + 0.5) * ratio - 0.5);
                double from = enlarging ? Math.floor(center) : index * ratio;
                double to = enlarging ? from + 2.0 : from + ratio;
                int first = Math.min((int) Math.floor(from), available - 1);
                int last = Math.max(first, Math.min((int) Math.ceil(to) - 1, available - 1));
                double[] coverage = new double[last - first + 1];
                double total = 0.0;
                for (int tap = 0; tap < coverage.length; tap++) {
                    int position = first + tap;
                    coverage[tap] = enlarging ? Math.max(0.0, 1.0 - Math.abs(position - center))
                        : Math.max(0.0, Math.min(to, position + 1.0) - Math.max(from, position));
                    total += coverage[tap];
                }
                starts[index] = first;
                counts[index] = coverage.length;
                offsets[index] = used;
                int assigned = 0;
                for (int tap = 0; tap < coverage.length; tap++) {
                    int weight = total > 0.0 ? (int) Math.round(coverage[tap] / total * WEIGHT_ONE) : 0;
                    weights[used + tap] = weight;
                    assigned += weight;
                }
                weights[used] += WEIGHT_ONE - assigned;
                used += coverage.length;
            }
            return new Taps(starts, counts, offsets, weights);
        }

        int start(int index) {
            return this.starts[index];
        }

        int count(int index) {
            return this.counts[index];
        }

        int weight(int index, int tap) {
            return this.weights[this.offsets[index] + tap];
        }
    }

    private enum YuvMatrix {
        BT709_LIMITED(1.164, 16, 1.793, 0.213, 0.533, 2.112),
        BT601_LIMITED(1.164, 16, 1.596, 0.392, 0.813, 2.017),
        BT601_FULL(1.0, 0, 1.402, 0.344, 0.714, 1.772);

        private final int lumaScale;
        private final int lumaOffset;
        private final int redFromV;
        private final int greenFromU;
        private final int greenFromV;
        private final int blueFromU;

        YuvMatrix(double lumaScale, int lumaOffset, double redFromV, double greenFromU, double greenFromV, double blueFromU) {
            this.lumaScale = fixed(lumaScale);
            this.lumaOffset = lumaOffset;
            this.redFromV = fixed(redFromV);
            this.greenFromU = fixed(greenFromU);
            this.greenFromV = fixed(greenFromV);
            this.blueFromU = fixed(blueFromU);
        }

        private static int fixed(double value) {
            return (int) Math.round(value * (1 << MATRIX_BITS));
        }

        int rgb(int luma, int blue, int red) {
            int y = (luma - this.lumaOffset) * this.lumaScale;
            int u = blue - SIGNED_OFFSET;
            int v = red - SIGNED_OFFSET;
            int r = channel(y + this.redFromV * v);
            int g = channel(y - this.greenFromU * u - this.greenFromV * v);
            int b = channel(y + this.blueFromU * u);
            return r << 16 | g << 8 | b;
        }

        private static int channel(int value) {
            return Math.clamp((value + (1 << (MATRIX_BITS - 1))) >> MATRIX_BITS, 0, MAX_CHANNEL);
        }
    }
}
