package com.aryston.arkea.background;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rect;
import org.junit.jupiter.api.Test;

class PictureScalerTest {
    private static final int TOLERANCE = 2;

    @Test
    void limitedRangeGrayStaysGray() {
        BufferedImage image = PictureScaler.scale(solid(ColorSpace.YUV420, 64, 64, 126, 128, 128), 64, 64);

        assertChannels(image.getRGB(10, 10), 128, 128, 128);
    }

    @Test
    void fullRangeRedConvertsToRed() {
        BufferedImage image = PictureScaler.scale(solid(ColorSpace.YUV420J, 32, 32, 76, 85, 255), 32, 32);

        assertChannels(image.getRGB(5, 5), 254, 0, 0);
    }

    @Test
    void largePicturesShrinkToFitAndKeepTheirAspect() {
        BufferedImage image = PictureScaler.scale(solid(ColorSpace.YUV420, 3840, 2160, 120, 128, 128), 1280, 720);

        assertEquals(1280, image.getWidth());
        assertEquals(720, image.getHeight());
    }

    @Test
    void cropRemovesCodedPadding() {
        Picture picture = solid(ColorSpace.YUV420, 64, 48, 200, 128, 128);
        byte[] luma = picture.getPlaneData(0);
        Arrays.fill(luma, 64 * 32, luma.length, (byte) (16 - 128));
        picture.setCrop(new Rect(0, 0, 64, 32));

        BufferedImage image = PictureScaler.scale(picture, 64, 64);

        assertEquals(32, image.getHeight());
        assertTrue((image.getRGB(32, 31) & 0xFF) > 200, "padding rows below the crop must not darken the last row");
    }

    @Test
    void averagingBlendsNeighbors() {
        Picture picture = solid(ColorSpace.YUV420J, 4, 2, 0, 128, 128);
        byte[] luma = picture.getPlaneData(0);
        for (int row = 0; row < 2; row++) {
            luma[row * 4] = (byte) (255 - 128);
            luma[row * 4 + 1] = (byte) (255 - 128);
        }

        BufferedImage image = PictureScaler.scale(picture, 2, 1);

        assertChannels(image.getRGB(0, 0), 255, 255, 255);
        assertChannels(image.getRGB(1, 0), 0, 0, 0);
    }

    private static Picture solid(ColorSpace color, int width, int height, int y, int u, int v) {
        Picture picture = Picture.create(width, height, color);
        Arrays.fill(picture.getPlaneData(0), (byte) (y - 128));
        Arrays.fill(picture.getPlaneData(1), (byte) (u - 128));
        Arrays.fill(picture.getPlaneData(2), (byte) (v - 128));
        return picture;
    }

    private static void assertChannels(int rgb, int red, int green, int blue) {
        String actual = Integer.toHexString(rgb);
        assertTrue(Math.abs((rgb >> 16 & 0xFF) - red) <= TOLERANCE, "red of " + actual);
        assertTrue(Math.abs((rgb >> 8 & 0xFF) - green) <= TOLERANCE, "green of " + actual);
        assertTrue(Math.abs((rgb & 0xFF) - blue) <= TOLERANCE, "blue of " + actual);
    }
}
