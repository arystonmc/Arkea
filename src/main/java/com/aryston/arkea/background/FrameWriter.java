package com.aryston.arkea.background;

import com.aryston.arkea.Arkea;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

final class FrameWriter implements AutoCloseable {
    private static final int QUEUED_PER_ENCODER = 2;
    private static final long FINISH_TIMEOUT_MINUTES = 10L;
    private static final long STOP_TIMEOUT_SECONDS = 30L;

    private final Path target;
    private final ExecutorService encoders;
    private final Semaphore queue;
    private final AtomicReference<Throwable> failure = new AtomicReference<>();
    private final AtomicInteger frames = new AtomicInteger();
    private volatile int width;
    private volatile int height;

    FrameWriter(Path target, int threads) {
        this.target = target;
        this.encoders = Executors.newFixedThreadPool(threads, WorkerThreads.named("Arkea frame encoder"));
        this.queue = new Semaphore(threads * QUEUED_PER_ENCODER);
    }

    void submit(BufferedImage image, int first, int end) {
        try {
            this.queue.acquire();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new CancellationException();
        }
        this.encoders.execute(() -> {
            try {
                this.write(image, first, end);
            } catch (IOException | RuntimeException | OutOfMemoryError exception) {
                this.failure.compareAndSet(null, exception);
            } finally {
                this.queue.release();
            }
        });
    }

    private void write(BufferedImage image, int first, int end) throws IOException {
        if (this.failed()) {
            return;
        }
        byte[] jpeg = BackgroundImporter.encode(image);
        for (int slot = first; slot < end; slot++) {
            Files.write(BackgroundEntry.frame(this.target, slot), jpeg);
        }
        if (first == 0) {
            BackgroundImporter.writeThumbnail(image, this.target);
            this.width = image.getWidth();
            this.height = image.getHeight();
        }
        this.frames.accumulateAndGet(end, Math::max);
    }

    boolean failed() {
        return this.failure.get() != null;
    }

    int width() {
        return this.width;
    }

    int height() {
        return this.height;
    }

    int finish() throws IOException {
        this.encoders.shutdown();
        try {
            if (!this.encoders.awaitTermination(FINISH_TIMEOUT_MINUTES, TimeUnit.MINUTES)) {
                throw new IOException("Writing the frames took too long");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new CancellationException();
        }
        Throwable error = this.failure.get();
        if (error != null) {
            VideoConverter.rethrow(error);
        }
        if (this.width == 0) {
            throw new IOException("The file has no frames");
        }
        return this.frames.get();
    }

    @Override
    public void close() {
        this.encoders.shutdownNow();
        try {
            if (!this.encoders.awaitTermination(STOP_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                Arkea.LOGGER.warn("Frame encoders of {} did not stop in time", this.target);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
