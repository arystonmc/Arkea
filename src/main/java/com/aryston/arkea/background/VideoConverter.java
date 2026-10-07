package com.aryston.arkea.background;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.Codec;
import org.jcodec.common.DemuxerTrackMeta;
import org.jcodec.common.SeekableDemuxerTrack;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.Size;

final class VideoConverter {
    private static final int SPARE_CORES = 1;
    private static final int ENCODER_SHARE = 4;
    private static final int MIN_ENCODERS = 2;
    private static final int PICTURES_PER_DECODER = 16;
    private static final double BYTES_PER_PIXEL = 1.5;
    private static final int MEMORY_SHARE = 2;
    private static final int FALLBACK_WIDTH = 1920;
    private static final int FALLBACK_HEIGHT = 1080;

    private VideoConverter() {
    }

    static Result convert(Path source, Path target, BackgroundImporter.Progress progress, BooleanSupplier cancelled) throws IOException {
        Layout layout = layout(source);
        Files.createDirectories(target.resolve(BackgroundEntry.FRAMES));
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService decoders = Executors.newFixedThreadPool(decoderCount(cores, layout), WorkerThreads.named("Arkea video decoder"));
        try (FrameWriter writer = new FrameWriter(target, Math.max(MIN_ENCODERS, cores / ENCODER_SHARE))) {
            AtomicBoolean aborted = new AtomicBoolean();
            BooleanSupplier stopped = () -> cancelled.getAsBoolean() || aborted.get() || writer.failed();
            AtomicInteger decoded = new AtomicInteger();
            float packets = layout.segments().stream().mapToInt(VideoSegment::length).sum();
            Runnable packetDone = () -> progress.update(Math.min(1.0F, decoded.incrementAndGet() / packets));
            List<Future<?>> tasks = new ArrayList<>();
            for (VideoSegment segment : layout.segments()) {
                tasks.add(decoders.submit(() -> {
                    segment.decode(source, writer, packetDone, stopped);
                    return null;
                }));
            }
            try {
                await(tasks, aborted);
            } catch (CancellationException exception) {
                if (writer.failed()) {
                    writer.finish();
                }
                throw exception;
            }
            int frames = writer.finish();
            return new Result(writer.width(), writer.height(), layout.plan().fps(), frames);
        } finally {
            decoders.shutdownNow();
        }
    }

    private static Layout layout(Path source) throws IOException {
        try (FileChannelWrapper channel = NIOUtils.readableChannel(source.toFile())) {
            FrameGrab grab = VideoSegment.open(channel);
            SeekableDemuxerTrack track = grab.getVideoTrack();
            DemuxerTrackMeta meta = track.getMeta();
            int total = meta.getTotalFrames();
            Packet first = track.nextFrame();
            if (total <= 0 || first == null) {
                throw new IOException("The file has no frames");
            }
            double frameDuration = meta.getTotalDuration() / total;
            int fps = BackgroundImporter.frameRate(frameDuration);
            int maxFrames = (int) Math.round(BackgroundImporter.MAX_SECONDS * fps);
            VideoPlan plan = new VideoPlan(first.getPtsD(), fps, maxFrames, total, meta.getCodec() == Codec.H264);
            Size size = meta.getVideoCodecMeta() != null ? meta.getVideoCodecMeta().getSize() : null;
            long pixels = size != null ? (long) size.getWidth() * size.getHeight() : (long) FALLBACK_WIDTH * FALLBACK_HEIGHT;
            return new Layout(plan, segments(plan, meta.getSeekFrames(), frameDuration), pixels);
        }
    }

    private static List<VideoSegment> segments(VideoPlan plan, int[] syncFrames, double frameDuration) {
        List<Integer> starts = new ArrayList<>();
        starts.add(0);
        if (syncFrames != null) {
            for (int frame : syncFrames) {
                if (frame > starts.getLast() && frame < plan.totalFrames() && frame * frameDuration <= BackgroundImporter.MAX_SECONDS) {
                    starts.add(frame);
                }
            }
        }
        List<VideoSegment> segments = new ArrayList<>(starts.size());
        for (int index = 0; index < starts.size(); index++) {
            int end = index + 1 < starts.size() ? starts.get(index + 1) : plan.totalFrames();
            segments.add(new VideoSegment(plan, starts.get(index), end));
        }
        return segments;
    }

    private static int decoderCount(int cores, Layout layout) {
        Runtime runtime = Runtime.getRuntime();
        long available = runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory());
        long perDecoder = (long) (layout.pixels() * BYTES_PER_PIXEL * PICTURES_PER_DECODER);
        long byMemory = Math.max(1L, available / MEMORY_SHARE / Math.max(1L, perDecoder));
        return (int) Math.clamp(Math.min(Math.min(cores - SPARE_CORES, layout.segments().size()), byMemory), 1L, Integer.MAX_VALUE);
    }

    private static void await(List<Future<?>> tasks, AtomicBoolean aborted) throws IOException {
        Throwable failure = null;
        boolean interrupted = false;
        for (Future<?> task : tasks) {
            try {
                task.get();
            } catch (ExecutionException exception) {
                aborted.set(true);
                Throwable cause = exception.getCause();
                if (failure == null || failure instanceof CancellationException && !(cause instanceof CancellationException)) {
                    failure = cause;
                }
            } catch (InterruptedException exception) {
                aborted.set(true);
                interrupted = true;
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
            throw new CancellationException();
        }
        if (failure != null) {
            rethrow(failure);
        }
    }

    static void rethrow(Throwable error) throws IOException {
        if (error instanceof IOException io) {
            throw io;
        }
        if (error instanceof RuntimeException runtime) {
            throw runtime;
        }
        if (error instanceof Error fatal) {
            throw fatal;
        }
        throw new IOException(error);
    }

    record Result(int width, int height, int fps, int frames) {
    }

    private record Layout(VideoPlan plan, List<VideoSegment> segments, long pixels) {
    }
}
