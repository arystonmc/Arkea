package com.aryston.arkea.background;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

final class JpegImages {
    private static final int RGBA = 4;

    private JpegImages() {
    }

    static NativeImage read(Path file) throws IOException {
        byte[] bytes = Files.readAllBytes(file);
        ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            buffer.put(bytes).flip();
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer components = stack.mallocInt(1);
            ByteBuffer pixels = STBImage.stbi_load_from_memory(buffer, width, height, components, RGBA);
            if (pixels == null) {
                throw new IOException("Could not decode " + file.getFileName() + ": " + STBImage.stbi_failure_reason());
            }
            return new NativeImage(NativeImage.Format.RGBA, width.get(0), height.get(0), true, MemoryUtil.memAddress(pixels));
        } finally {
            MemoryUtil.memFree(buffer);
        }
    }
}
