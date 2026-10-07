package com.aryston.arkea.background;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.PriorityQueue;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.api.PictureWithMetadata;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rect;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform;
import org.jspecify.annotations.Nullable;

final class VideoFrames implements MediaFrames {
    private static final int SIGNED_OFFSET = 128;
    private static final int CHANNELS = 3;
    private static final int REORDER_DEPTH = 8;

    private final FileChannelWrapper channel;
    private final FrameGrab grab;
    private final double duration;
    private @Nullable Transform transform;
    private @Nullable Picture rgb;
    private final PriorityQueue<Frame> reorder = new PriorityQueue<>(Comparator.comparingDouble(Frame::time));
    private boolean ended;

    VideoFrames(Path file) throws IOException {
        this.channel = NIOUtils.readableChannel(file.toFile());
        try {
            this.grab = FrameGrab.createFrameGrab(this.channel);
        } catch (JCodecException | RuntimeException exception) {
            this.channel.close();
            throw new IOException("Unsupported video: " + exception.getMessage(), exception);
        }
        double total = this.grab.getVideoTrack().getMeta().getTotalDuration();
        this.duration = total > 0.0 ? total : 0.0;
    }

    @Override
    public @Nullable Frame next() throws IOException {
        while (!this.ended && this.reorder.size() < REORDER_DEPTH) {
            Frame decoded = this.decode();
            if (decoded == null) {
                this.ended = true;
            } else {
                this.reorder.add(decoded);
            }
        }
        return this.reorder.poll();
    }

    private @Nullable Frame decode() throws IOException {
        PictureWithMetadata frame;
        try {
            frame = this.grab.getNativeFrameWithMetadata();
        } catch (RuntimeException exception) {
            throw new IOException("Could not decode the video: " + exception.getMessage(), exception);
        }
        if (frame == null) {
            return null;
        }
        return new Frame(this.toImage(frame.getPicture()), frame.getTimestamp());
    }

    private BufferedImage toImage(Picture source) {
        Picture rgb = this.rgb;
        Transform transform = this.transform;
        if (source.getColor() != ColorSpace.RGB && (transform == null || rgb == null || rgb.getWidth() != source.getWidth()
            || rgb.getHeight() != source.getHeight())) {
            transform = ColorUtil.getTransform(source.getColor(), ColorSpace.RGB);
            if (transform == null) {
                throw new IllegalStateException("Unsupported pixel format " + source.getColor());
            }
            rgb = Picture.create(source.getWidth(), source.getHeight(), ColorSpace.RGB);
            this.transform = transform;
            this.rgb = rgb;
        }
        if (source.getColor() == ColorSpace.RGB) {
            rgb = source;
        } else {
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

    @Override
    public float progress(double time) {
        return this.duration > 0.0 ? (float) (time / this.duration) : 0.0F;
    }

    @Override
    public void close() throws IOException {
        this.channel.close();
    }
}
