package com.aryston.arkea.background;

import com.aryston.arkea.Arkea;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.jspecify.annotations.Nullable;

public final class BackgroundThumbnails {
    private static final Map<String, DynamicTexture> TEXTURES = new HashMap<>();
    private static final Map<String, Boolean> FAILED = new HashMap<>();

    private BackgroundThumbnails() {
    }

    public static @Nullable DynamicTexture get(BackgroundEntry entry) {
        DynamicTexture texture = TEXTURES.get(entry.id());
        if (texture != null || FAILED.containsKey(entry.id())) {
            return texture;
        }
        try {
            texture = new DynamicTexture(() -> "Arkea background thumbnail " + entry.id(), JpegImages.read(entry.thumbnail()));
            TEXTURES.put(entry.id(), texture);
        } catch (IOException | RuntimeException exception) {
            Arkea.LOGGER.warn("Could not load the thumbnail of {}", entry.id(), exception);
            FAILED.put(entry.id(), Boolean.TRUE);
        }
        return texture;
    }

    public static void clear() {
        TEXTURES.values().forEach(DynamicTexture::close);
        TEXTURES.clear();
        FAILED.clear();
    }
}
