package com.aryston.arkea.background;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.function.BooleanSupplier;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.jspecify.annotations.Nullable;

public final class BackgroundImporter {
    static final int VIDEO_WIDTH = 1280;
    static final int VIDEO_HEIGHT = 720;
    static final int IMAGE_WIDTH = 1920;
    static final int IMAGE_HEIGHT = 1080;
    static final int MAX_FPS = 30;
    static final double MAX_SECONDS = 60.0;
    static final int THUMBNAIL_WIDTH = 384;
    static final int THUMBNAIL_HEIGHT = 216;
    private static final float QUALITY = 0.85F;

    private BackgroundImporter() {
    }

    public static @Nullable Source detect(Path file) {
        String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
        int dot = name.lastIndexOf('.');
        String extension = dot < 0 ? "" : name.substring(dot + 1);
        return switch (extension) {
            case "mp4", "m4v", "mov" -> Source.VIDEO;
            case "gif" -> Source.GIF;
            case "png", "jpg", "jpeg", "bmp" -> Source.IMAGE;
            default -> null;
        };
    }

    public static BackgroundEntry convert(Path source, Path target, String name, Progress progress, BooleanSupplier cancelled) throws IOException {
        Source type = detect(source);
        if (type == null) {
            throw new IOException("Unsupported file type");
        }
        Files.createDirectories(target);
        if (type == Source.IMAGE) {
            return convertImage(ImageIO.read(source.toFile()), target, name);
        }
        if (type == Source.VIDEO) {
            return convertVideo(source, target, name, progress, cancelled);
        }
        try (MediaFrames frames = new GifFrames(source)) {
            return convertFrames(frames, target, name, progress, cancelled);
        }
    }

    private static BackgroundEntry convertImage(@Nullable BufferedImage image, Path target, String name) throws IOException {
        if (image == null) {
            throw new IOException("Could not read the image");
        }
        BufferedImage scaled = fit(image, IMAGE_WIDTH, IMAGE_HEIGHT);
        byte[] jpeg = encode(scaled);
        Files.write(target.resolve(BackgroundEntry.IMAGE), jpeg);
        writeThumbnail(scaled, target);
        BackgroundEntry entry = new BackgroundEntry(target, name, BackgroundEntry.Kind.IMAGE, scaled.getWidth(), scaled.getHeight(), 0, 1,
            folderSize(target), System.currentTimeMillis());
        entry.write();
        return entry;
    }

    private static BackgroundEntry convertVideo(Path source, Path target, String name, Progress progress, BooleanSupplier cancelled)
        throws IOException {
        VideoConverter.Result video = VideoConverter.convert(source, target, progress, cancelled);
        BackgroundEntry entry = new BackgroundEntry(target, name, BackgroundEntry.Kind.VIDEO, video.width(), video.height(), video.fps(), video.frames(),
            folderSize(target), System.currentTimeMillis());
        entry.write();
        return entry;
    }

    private static BackgroundEntry convertFrames(MediaFrames frames, Path target, String name, Progress progress, BooleanSupplier cancelled)
        throws IOException {
        MediaFrames.Frame first = frames.next();
        if (first == null) {
            throw new IOException("The file has no frames");
        }
        MediaFrames.Frame next = frames.next();
        if (next == null) {
            return convertImage(first.image(), target, name);
        }
        int fps = frameRate(next.time() - first.time());
        Files.createDirectories(target.resolve(BackgroundEntry.FRAMES));
        MediaFrames.Frame current = first;
        BufferedImage scaled = fit(current.image(), VIDEO_WIDTH, VIDEO_HEIGHT);
        writeThumbnail(scaled, target);
        int maxFrames = (int) Math.round(MAX_SECONDS * fps);
        int written = 0;
        while (true) {
            if (cancelled.getAsBoolean()) {
                throw new CancellationException();
            }
            double end = next != null ? next.time() - first.time() : current.time() - first.time() + 1.0 / fps;
            int until = Math.min((int) Math.round(end * fps), maxFrames);
            byte[] jpeg = null;
            while (written < until) {
                if (jpeg == null) {
                    jpeg = encode(scaled);
                }
                Files.write(BackgroundEntry.frame(target, written), jpeg);
                written++;
            }
            if (next == null || written >= maxFrames) {
                break;
            }
            current = next;
            next = frames.next();
            scaled = fit(current.image(), VIDEO_WIDTH, VIDEO_HEIGHT);
            progress.update(Math.clamp(Math.max(frames.progress(current.time()), (float) ((current.time() - first.time()) / MAX_SECONDS)), 0.0F, 1.0F));
        }
        BackgroundEntry entry = new BackgroundEntry(target, name, BackgroundEntry.Kind.VIDEO, scaled.getWidth(), scaled.getHeight(), fps, written,
            folderSize(target), System.currentTimeMillis());
        entry.write();
        return entry;
    }

    static int frameRate(double frameDuration) {
        if (frameDuration <= 0.0) {
            return MAX_FPS;
        }
        return Math.clamp(Math.round(1.0 / frameDuration), 1, MAX_FPS);
    }

    static BufferedImage fit(BufferedImage source, int maxWidth, int maxHeight) {
        float scale = Math.min(1.0F, Math.min(maxWidth / (float) source.getWidth(), maxHeight / (float) source.getHeight()));
        int width = Math.max(1, Math.round(source.getWidth() * scale));
        int height = Math.max(1, Math.round(source.getHeight() * scale));
        return draw(source, width, height, 0, 0, source.getWidth(), source.getHeight());
    }

    static void writeThumbnail(BufferedImage source, Path target) throws IOException {
        float sourceAspect = source.getWidth() / (float) source.getHeight();
        float targetAspect = THUMBNAIL_WIDTH / (float) THUMBNAIL_HEIGHT;
        int cropWidth = sourceAspect > targetAspect ? Math.round(source.getHeight() * targetAspect) : source.getWidth();
        int cropHeight = sourceAspect > targetAspect ? source.getHeight() : Math.round(source.getWidth() / targetAspect);
        int x = (source.getWidth() - cropWidth) / 2;
        int y = (source.getHeight() - cropHeight) / 2;
        BufferedImage thumbnail = draw(source, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, x, y, cropWidth, cropHeight);
        Files.write(target.resolve(BackgroundEntry.THUMBNAIL), encode(thumbnail));
    }

    private static BufferedImage draw(BufferedImage source, int width, int height, int sourceX, int sourceY, int sourceWidth, int sourceHeight) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = result.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, width, height);
        graphics.drawImage(source, 0, 0, width, height, sourceX, sourceY, sourceX + sourceWidth, sourceY + sourceHeight, null);
        graphics.dispose();
        return result;
    }

    static byte[] encode(BufferedImage image) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageWriteParam parameters = writer.getDefaultWriteParam();
        parameters.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        parameters.setCompressionQuality(QUALITY);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ImageOutputStream output = ImageIO.createImageOutputStream(bytes)) {
            writer.setOutput(output);
            writer.write(null, new IIOImage(image, null, null), parameters);
        } finally {
            writer.dispose();
        }
        return bytes.toByteArray();
    }

    static long folderSize(Path folder) throws IOException {
        try (var paths = Files.walk(folder)) {
            return paths.filter(Files::isRegularFile).mapToLong(path -> {
                try {
                    return Files.size(path);
                } catch (IOException exception) {
                    return 0L;
                }
            }).sum();
        }
    }

    public enum Source {
        VIDEO,
        GIF,
        IMAGE
    }

    @FunctionalInterface
    public interface Progress {
        void update(float fraction);
    }
}
