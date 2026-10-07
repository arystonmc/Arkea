package com.aryston.arkea.background;

import com.aryston.arkea.Arkea;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

final class BackgroundPlayer implements AutoCloseable {
    private static final int BUFFERED_FRAMES = 3;
    private static final long MAX_LAG = 250L;
    private static final long MILLIS = 1000L;

    private final BackgroundEntry entry;
    private final DynamicTexture texture;
    private final BlockingQueue<NativeImage> ready = new ArrayBlockingQueue<>(BUFFERED_FRAMES);
    private final @Nullable Thread decoder;
    private volatile boolean running = true;
    private @Nullable NativeImage pending;
    private long nextFrameAt;
    private boolean hasFrame;

    BackgroundPlayer(BackgroundEntry entry) throws IOException {
        this.entry = entry;
        if (entry.kind() == BackgroundEntry.Kind.IMAGE) {
            this.texture = new DynamicTexture(() -> "Arkea background " + entry.id(), JpegImages.read(entry.image()));
            this.hasFrame = true;
            this.decoder = null;
            return;
        }
        this.texture = new DynamicTexture(() -> "Arkea background " + entry.id(), entry.width(), entry.height(), true);
        this.texture.upload();
        this.decoder = new Thread(this::decode, "Arkea background decoder");
        this.decoder.setDaemon(true);
        this.decoder.start();
    }

    BackgroundEntry entry() {
        return this.entry;
    }

    DynamicTexture texture() {
        return this.texture;
    }

    boolean hasFrame() {
        return this.hasFrame;
    }

    private void decode() {
        int index = 0;
        while (this.running) {
            try {
                NativeImage image = JpegImages.read(this.entry.frame(index));
                if (!this.ready.offer(image, 1, TimeUnit.SECONDS)) {
                    image.close();
                    continue;
                }
                index = (index + 1) % this.entry.frames();
            } catch (InterruptedException exception) {
                return;
            } catch (IOException | RuntimeException exception) {
                Arkea.LOGGER.warn("Stopping background {}: {}", this.entry.id(), exception.getMessage());
                return;
            }
        }
    }

    void update() {
        if (this.decoder == null) {
            return;
        }
        long now = Util.getMillis();
        if (this.pending == null) {
            this.pending = this.ready.poll();
        }
        if (this.pending == null || this.hasFrame && now < this.nextFrameAt) {
            return;
        }
        NativeImage pixels = this.texture.getPixels();
        if (this.pending.getWidth() == pixels.getWidth() && this.pending.getHeight() == pixels.getHeight()) {
            MemoryUtil.memCopy(this.pending.getPointer(), pixels.getPointer(), (long) pixels.getWidth() * pixels.getHeight() * 4L);
            this.texture.upload();
            this.hasFrame = true;
        }
        this.pending.close();
        this.pending = null;
        long frameTime = MILLIS / Math.max(1, this.entry.fps());
        this.nextFrameAt = now - this.nextFrameAt > MAX_LAG ? now + frameTime : this.nextFrameAt + frameTime;
    }

    @Override
    public void close() {
        this.running = false;
        if (this.decoder != null) {
            this.decoder.interrupt();
            try {
                this.decoder.join(MILLIS);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
        if (this.pending != null) {
            this.pending.close();
            this.pending = null;
        }
        NativeImage image;
        while ((image = this.ready.poll()) != null) {
            image.close();
        }
        this.texture.close();
    }
}
