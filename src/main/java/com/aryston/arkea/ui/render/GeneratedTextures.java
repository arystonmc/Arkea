package com.aryston.arkea.ui.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.FilterMode;
import java.util.function.IntBinaryOperator;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.jspecify.annotations.Nullable;

final class GeneratedTextures {
    private static final int SOFT_EDGE_SIZE = 64;
    private static final int VIGNETTE_SIZE = 128;
    private static final float VIGNETTE_INNER_RADIUS = 0.45F;
    private static final int WHITE = 0xFFFFFF;
    private static final int ALPHA_SHIFT = 24;
    private static final float CHANNEL_MAX = 255.0F;
    private static @Nullable TextureSetup softEdge;
    private static @Nullable TextureSetup vignette;

    private GeneratedTextures() {
    }

    static TextureSetup softEdge() {
        if (softEdge == null) {
            softEdge = upload("Arkea soft edge", SOFT_EDGE_SIZE, GeneratedTextures::softEdgePixel);
        }
        return softEdge;
    }

    static TextureSetup vignette() {
        if (vignette == null) {
            vignette = upload("Arkea vignette", VIGNETTE_SIZE, GeneratedTextures::vignettePixel);
        }
        return vignette;
    }

    private static TextureSetup upload(String label, int size, IntBinaryOperator pixel) {
        NativeImage image = new NativeImage(size, size, false);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                image.setPixel(x, y, pixel.applyAsInt(x, y));
            }
        }
        DynamicTexture texture = new DynamicTexture(() -> label, image);
        return TextureSetup.singleTexture(texture.getTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
    }

    private static int softEdgePixel(int x, int y) {
        float coverage = bandCoverage(x) * bandCoverage(y);
        return white(coverage);
    }

    private static float bandCoverage(int texel) {
        float position = (texel + 0.5F) / SOFT_EDGE_SIZE;
        float band = position < 0.5F ? position * 2.0F : (1.0F - position) * 2.0F;
        float floor = SoftEdgeProfile.coverage(0.0F);
        float ceiling = SoftEdgeProfile.coverage(1.0F);
        return (SoftEdgeProfile.coverage(band) - floor) / (ceiling - floor);
    }

    private static int vignettePixel(int x, int y) {
        float normalizedX = (x + 0.5F) / VIGNETTE_SIZE * 2.0F - 1.0F;
        float normalizedY = (y + 0.5F) / VIGNETTE_SIZE * 2.0F - 1.0F;
        return white(SoftEdgeProfile.radial(normalizedX, normalizedY, VIGNETTE_INNER_RADIUS));
    }

    private static int white(float alpha) {
        return Math.round(Math.clamp(alpha, 0.0F, 1.0F) * CHANNEL_MAX) << ALPHA_SHIFT | WHITE;
    }
}
