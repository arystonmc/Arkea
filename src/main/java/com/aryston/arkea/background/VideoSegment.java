package com.aryston.arkea.background;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.CancellationException;
import java.util.function.BooleanSupplier;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.api.specific.ContainerAdaptor;
import org.jcodec.common.SeekableDemuxerTrack;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.Picture;
import org.jspecify.annotations.Nullable;

final class VideoSegment {
    private final VideoPlan plan;
    private final int start;
    private final int end;

    VideoSegment(VideoPlan plan, int start, int end) {
        this.plan = plan;
        this.start = start;
        this.end = end;
    }

    int length() {
        return this.end - this.start;
    }

    static FrameGrab open(FileChannelWrapper channel) throws IOException {
        try {
            return FrameGrab.createFrameGrab(channel);
        } catch (JCodecException | RuntimeException exception) {
            throw new IOException("Unsupported video: " + exception.getMessage(), exception);
        }
    }

    void decode(Path file, FrameWriter writer, Runnable packetDone, BooleanSupplier stopped) throws IOException {
        try (FileChannelWrapper channel = NIOUtils.readableChannel(file.toFile())) {
            FrameGrab grab = open(channel);
            SeekableDemuxerTrack track = grab.getVideoTrack();
            FrameSlots slots = this.slots(track);
            track.gotoFrame(this.start);
            ContainerAdaptor decoder = grab.getDecoder();
            byte[][] buffer = decoder.allocatePicture();
            for (int index = this.start; index < this.end; index++) {
                if (stopped.getAsBoolean()) {
                    throw new CancellationException();
                }
                Packet packet = track.nextFrame();
                if (packet == null) {
                    return;
                }
                double time = this.plan.time(packet);
                int first = slots.first(time);
                int last = slots.end(time);
                boolean shown = first < last;
                if (shown || !this.plan.canSkip(packet)) {
                    Picture picture = decodeFrame(decoder, packet, buffer);
                    if (shown && picture != null) {
                        writer.submit(PictureScaler.scale(picture, BackgroundImporter.VIDEO_WIDTH, BackgroundImporter.VIDEO_HEIGHT), first, last);
                    }
                }
                packetDone.run();
            }
        }
    }

    private FrameSlots slots(SeekableDemuxerTrack track) throws IOException {
        track.gotoFrame(this.start);
        double[] times = new double[this.length()];
        int count = 0;
        double syncTime = 0.0;
        double end = 0.0;
        for (int index = this.start; index < this.end; index++) {
            Packet packet = track.nextFrame();
            if (packet == null) {
                break;
            }
            double time = this.plan.time(packet);
            if (index == this.start) {
                syncTime = time;
            }
            end = Math.max(end, time + packet.getDurationD());
            if (time >= syncTime) {
                times[count++] = time;
            }
        }
        if (this.end < this.plan.totalFrames()) {
            Packet next = track.nextFrame();
            if (next != null) {
                end = this.plan.time(next);
            }
        }
        return FrameSlots.of(Arrays.copyOf(times, count), end, this.plan.fps(), this.plan.maxFrames());
    }

    private static @Nullable Picture decodeFrame(ContainerAdaptor decoder, Packet packet, byte[][] buffer) throws IOException {
        try {
            return decoder.decodeFrame(packet, buffer);
        } catch (RuntimeException exception) {
            throw new IOException("Could not decode the video: " + exception.getMessage(), exception);
        }
    }
}
