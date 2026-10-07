package com.aryston.arkea.background;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CancellationException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BackgroundImporterTest {
    private static final BackgroundImporter.Progress NO_PROGRESS = fraction -> {
    };

    @TempDir
    Path folder;

    @Test
    void detectsSupportedFilesByExtension() {
        assertEquals(BackgroundImporter.Source.VIDEO, BackgroundImporter.detect(Path.of("clip.MP4")));
        assertEquals(BackgroundImporter.Source.VIDEO, BackgroundImporter.detect(Path.of("clip.mov")));
        assertEquals(BackgroundImporter.Source.GIF, BackgroundImporter.detect(Path.of("loop.gif")));
        assertEquals(BackgroundImporter.Source.IMAGE, BackgroundImporter.detect(Path.of("shot.jpeg")));
        assertNull(BackgroundImporter.detect(Path.of("clip.webm")));
        assertNull(BackgroundImporter.detect(Path.of("noextension")));
    }

    @Test
    void frameRateIsRoundedAndCapped() {
        assertEquals(10, BackgroundImporter.frameRate(0.1));
        assertEquals(24, BackgroundImporter.frameRate(1.0 / 23.976));
        assertEquals(BackgroundImporter.MAX_FPS, BackgroundImporter.frameRate(1.0 / 60.0));
        assertEquals(BackgroundImporter.MAX_FPS, BackgroundImporter.frameRate(0.0));
        assertEquals(1, BackgroundImporter.frameRate(5.0));
    }

    @Test
    void fitKeepsAspectAndNeverUpscales() {
        BufferedImage large = BackgroundImporter.fit(new BufferedImage(3840, 1600, BufferedImage.TYPE_INT_RGB), 1920, 1080);
        assertEquals(1920, large.getWidth());
        assertEquals(800, large.getHeight());
        BufferedImage small = BackgroundImporter.fit(new BufferedImage(640, 360, BufferedImage.TYPE_INT_RGB), 1920, 1080);
        assertEquals(640, small.getWidth());
        assertEquals(360, small.getHeight());
    }

    @Test
    void convertsImageIntoScaledEntry() throws IOException {
        Path source = this.folder.resolve("wide.png");
        ImageIO.write(solid(3840, 2160, Color.RED), "png", source.toFile());
        Path target = this.folder.resolve("entry");

        BackgroundEntry entry = BackgroundImporter.convert(source, target, "Wide", NO_PROGRESS, () -> false);

        assertEquals(BackgroundEntry.Kind.IMAGE, entry.kind());
        assertEquals(1920, entry.width());
        assertEquals(1080, entry.height());
        assertTrue(Files.isRegularFile(entry.image()));
        assertTrue(Files.isRegularFile(entry.thumbnail()));
        BufferedImage thumbnail = ImageIO.read(entry.thumbnail().toFile());
        assertEquals(BackgroundImporter.THUMBNAIL_WIDTH, thumbnail.getWidth());
        assertEquals(BackgroundImporter.THUMBNAIL_HEIGHT, thumbnail.getHeight());
        assertEquals(entry, BackgroundEntry.read(target));
    }

    @Test
    void convertsAnimatedGifIntoFramePack() throws IOException {
        Path source = this.folder.resolve("loop.gif");
        writeGif(source, 10, Color.RED, Color.GREEN, Color.BLUE, Color.WHITE);
        Path target = this.folder.resolve("entry");

        BackgroundEntry entry = BackgroundImporter.convert(source, target, "Loop", NO_PROGRESS, () -> false);

        assertEquals(BackgroundEntry.Kind.VIDEO, entry.kind());
        assertEquals(10, entry.fps());
        assertEquals(4, entry.frames());
        assertEquals(0.4F, entry.seconds(), 1.0E-4F);
        for (int index = 0; index < entry.frames(); index++) {
            assertTrue(Files.isRegularFile(entry.frame(index)), "frame " + index);
        }
        int blue = ImageIO.read(entry.frame(2).toFile()).getRGB(32, 32);
        assertTrue((blue & 0xFF) > 200 && (blue >> 16 & 0xFF) < 60, "third frame should be blue");
        assertEquals(entry, BackgroundEntry.read(target));
    }

    @Test
    void convertsVideoIntoFramePack() throws IOException {
        Path source = this.folder.resolve("clip.mp4");
        writeVideo(source, 5, Color.RED, Color.RED, Color.BLUE, Color.BLUE, Color.GREEN);
        Path target = this.folder.resolve("entry");

        BackgroundEntry entry = BackgroundImporter.convert(source, target, "Clip", NO_PROGRESS, () -> false);

        assertEquals(BackgroundEntry.Kind.VIDEO, entry.kind());
        assertEquals(5, entry.fps());
        assertEquals(5, entry.frames());
        assertEquals(64, entry.width());
        assertEquals(64, entry.height());
        int red = ImageIO.read(entry.frame(0).toFile()).getRGB(32, 32);
        assertTrue((red >> 16 & 0xFF) > 200 && (red & 0xFF) < 60, "first frame should be red, was " + Integer.toHexString(red));
        int blue = ImageIO.read(entry.frame(2).toFile()).getRGB(32, 32);
        assertTrue((blue & 0xFF) > 200 && (blue >> 16 & 0xFF) < 60, "third frame should be blue, was " + Integer.toHexString(blue));
    }

    @Test
    void videoWithBidirectionalFramesPlaysInPresentationOrder() throws IOException {
        Path source = this.folder.resolve("ramp.mp4");
        try (var input = BackgroundImporterTest.class.getResourceAsStream("/background/ramp.mp4")) {
            assertNotNull(input);
            Files.copy(input, source);
        }

        BackgroundEntry entry = BackgroundImporter.convert(source, this.folder.resolve("entry"), "Ramp", NO_PROGRESS, () -> false);

        assertEquals(10, entry.fps());
        assertEquals(12, entry.frames());
        int previous = -1;
        for (int index = 0; index < entry.frames(); index++) {
            int brightness = ImageIO.read(entry.frame(index).toFile()).getRGB(32, 32) & 0xFF;
            assertTrue(brightness > previous, "frame " + index + " should be brighter than the one before");
            previous = brightness;
        }
    }

    @Test
    void singleFrameGifBecomesImage() throws IOException {
        Path source = this.folder.resolve("still.gif");
        writeGif(source, 10, Color.ORANGE);

        BackgroundEntry entry = BackgroundImporter.convert(source, this.folder.resolve("entry"), "Still", NO_PROGRESS, () -> false);

        assertEquals(BackgroundEntry.Kind.IMAGE, entry.kind());
        assertTrue(Files.isRegularFile(entry.image()));
    }

    @Test
    void cancellationStopsConversion() throws IOException {
        Path source = this.folder.resolve("loop.gif");
        writeGif(source, 10, Color.RED, Color.GREEN);

        assertThrows(CancellationException.class,
            () -> BackgroundImporter.convert(source, this.folder.resolve("entry"), "Loop", NO_PROGRESS, () -> true));
    }

    @Test
    void unreadableManifestIsIgnored() throws IOException {
        Files.writeString(this.folder.resolve(BackgroundEntry.MANIFEST), "{ broken");
        assertNull(BackgroundEntry.read(this.folder));
        assertNull(BackgroundEntry.read(this.folder.resolve("missing")));
    }

    @Test
    void manifestRoundTrips() throws IOException {
        BackgroundEntry entry = new BackgroundEntry(this.folder, "Ocean Loop", BackgroundEntry.Kind.VIDEO, 1280, 720, 30, 900, 12345L, 42L);
        entry.write();
        BackgroundEntry read = BackgroundEntry.read(this.folder);
        assertNotNull(read);
        assertEquals(entry, read);
        assertEquals(30.0F, read.seconds(), 1.0E-4F);
    }

    private static BufferedImage solid(int width, int height, Color color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(color);
        graphics.fillRect(0, 0, width, height);
        graphics.dispose();
        return image;
    }

    private static void writeVideo(Path file, int fps, Color... colors) throws IOException {
        SequenceEncoder encoder = SequenceEncoder.createSequenceEncoder(file.toFile(), fps);
        for (Color color : colors) {
            Picture picture = Picture.create(64, 64, ColorSpace.RGB);
            byte[] data = picture.getPlaneData(0);
            for (int index = 0; index < data.length; index += 3) {
                data[index] = (byte) (color.getRed() - 128);
                data[index + 1] = (byte) (color.getGreen() - 128);
                data[index + 2] = (byte) (color.getBlue() - 128);
            }
            encoder.encodeNativeFrame(picture);
        }
        encoder.finish();
    }

    private static void writeGif(Path file, int delayCentiseconds, Color... colors) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("gif").next();
        try (ImageOutputStream output = ImageIO.createImageOutputStream(file.toFile())) {
            writer.setOutput(output);
            writer.prepareWriteSequence(null);
            for (Color color : colors) {
                BufferedImage frame = solid(64, 64, color);
                IIOMetadata metadata = writer.getDefaultImageMetadata(ImageTypeSpecifier.createFromRenderedImage(frame), null);
                String format = metadata.getNativeMetadataFormatName();
                IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(format);
                IIOMetadataNode control = new IIOMetadataNode("GraphicControlExtension");
                control.setAttribute("disposalMethod", "none");
                control.setAttribute("userInputFlag", "FALSE");
                control.setAttribute("transparentColorFlag", "FALSE");
                control.setAttribute("delayTime", Integer.toString(delayCentiseconds));
                control.setAttribute("transparentColorIndex", "0");
                root.appendChild(control);
                metadata.setFromTree(format, root);
                writer.writeToSequence(new IIOImage(frame, null, metadata), null);
            }
            writer.endWriteSequence();
        } finally {
            writer.dispose();
        }
    }
}
