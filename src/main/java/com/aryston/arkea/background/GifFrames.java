package com.aryston.arkea.background;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Node;

final class GifFrames implements MediaFrames {
    private static final String IMAGE_FORMAT = "javax_imageio_gif_image_1.0";
    private static final String STREAM_FORMAT = "javax_imageio_gif_stream_1.0";
    private static final double MIN_DELAY = 0.02;
    private static final double DEFAULT_DELAY = 0.1;
    private static final double CENTISECONDS = 100.0;

    private final ImageInputStream input;
    private final ImageReader reader;
    private final int count;
    private final BufferedImage canvas;
    private int index;
    private double time;
    private @Nullable Disposal pending;

    GifFrames(Path file) throws IOException {
        this.input = ImageIO.createImageInputStream(file.toFile());
        this.reader = ImageIO.getImageReadersByFormatName("gif").next();
        this.reader.setInput(this.input, false);
        this.count = this.reader.getNumImages(true);
        int width = this.reader.getWidth(0);
        int height = this.reader.getHeight(0);
        IIOMetadata stream = this.reader.getStreamMetadata();
        if (stream != null) {
            Node screen = child((IIOMetadataNode) stream.getAsTree(STREAM_FORMAT), "LogicalScreenDescriptor");
            if (screen != null) {
                width = Math.max(width, intAttribute(screen, "logicalScreenWidth", width));
                height = Math.max(height, intAttribute(screen, "logicalScreenHeight", height));
            }
        }
        this.canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public @Nullable Frame next() throws IOException {
        if (this.index >= this.count) {
            return null;
        }
        Graphics2D graphics = this.canvas.createGraphics();
        if (this.pending != null) {
            this.pending.apply(this.canvas, graphics);
            this.pending = null;
        }
        BufferedImage frame = this.reader.read(this.index);
        IIOMetadataNode root = (IIOMetadataNode) this.reader.getImageMetadata(this.index).getAsTree(IMAGE_FORMAT);
        Node descriptor = child(root, "ImageDescriptor");
        Node control = child(root, "GraphicControlExtension");
        int x = descriptor != null ? intAttribute(descriptor, "imageLeftPosition", 0) : 0;
        int y = descriptor != null ? intAttribute(descriptor, "imageTopPosition", 0) : 0;
        String disposal = control != null ? control.getAttributes().getNamedItem("disposalMethod").getNodeValue() : "none";
        double delay = control != null ? intAttribute(control, "delayTime", 0) / CENTISECONDS : 0.0;
        if (delay < MIN_DELAY) {
            delay = DEFAULT_DELAY;
        }
        BufferedImage previous = "restoreToPrevious".equals(disposal) ? copy(this.canvas) : null;
        graphics.drawImage(frame, x, y, null);
        graphics.dispose();
        Frame result = new Frame(copy(this.canvas), this.time);
        if ("restoreToBackgroundColor".equals(disposal)) {
            this.pending = new Disposal(x, y, frame.getWidth(), frame.getHeight(), null);
        } else if (previous != null) {
            this.pending = new Disposal(0, 0, 0, 0, previous);
        }
        this.time += delay;
        this.index++;
        return result;
    }

    private static BufferedImage copy(BufferedImage source) {
        BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        copy.getRaster().setRect(source.getRaster());
        return copy;
    }

    private static @Nullable Node child(IIOMetadataNode root, String name) {
        for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (name.equals(node.getNodeName())) {
                return node;
            }
        }
        return null;
    }

    private static int intAttribute(Node node, String name, int fallback) {
        Node attribute = node.getAttributes().getNamedItem(name);
        if (attribute == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(attribute.getNodeValue());
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    @Override
    public float progress(double time) {
        return this.count > 0 ? this.index / (float) this.count : 0.0F;
    }

    @Override
    public void close() throws IOException {
        this.reader.dispose();
        this.input.close();
    }

    private record Disposal(int x, int y, int width, int height, @Nullable BufferedImage previous) {
        void apply(BufferedImage canvas, Graphics2D graphics) {
            if (this.previous != null) {
                canvas.getRaster().setRect(this.previous.getRaster());
                return;
            }
            graphics.setComposite(AlphaComposite.Clear);
            graphics.fillRect(this.x, this.y, this.width, this.height);
            graphics.setComposite(AlphaComposite.SrcOver);
        }
    }
}
