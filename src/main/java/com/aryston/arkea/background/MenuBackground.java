package com.aryston.arkea.background;

import com.aryston.arkea.Arkea;
import com.aryston.arkea.config.ArkeaConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.FilterMode;
import java.io.IOException;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public final class MenuBackground {
    private static final long IDLE_RELEASE = 3000L;
    private static final long PAN_PERIOD = 50000L;
    private static final float PAN_START_SCALE = 1.04F;
    private static final float PAN_END_SCALE = 1.10F;
    private static final float PAN_OFFSET = 0.03F;
    private static @Nullable BackgroundPlayer player;
    private static @Nullable String failedId;
    private static long lastDrawn;

    private MenuBackground() {
    }

    public static boolean isCustom() {
        return BackgroundLibrary.get().selected() != null;
    }

    public static boolean draw(GuiGraphicsExtractor graphics, int width, int height) {
        BackgroundPlayer current = player(BackgroundLibrary.get().selected());
        if (current == null) {
            return false;
        }
        lastDrawn = Util.getMillis();
        current.update();
        if (!current.hasFrame()) {
            graphics.fill(0, 0, width, height, 0xFF000000);
            return true;
        }
        BackgroundEntry entry = current.entry();
        float screenAspect = width / (float) height;
        float textureAspect = entry.width() / (float) entry.height();
        float spanU = textureAspect > screenAspect ? screenAspect / textureAspect : 1.0F;
        float spanV = textureAspect > screenAspect ? 1.0F : textureAspect / screenAspect;
        float centerU = 0.5F;
        if (entry.kind() == BackgroundEntry.Kind.IMAGE && ArkeaConfig.BACKGROUND_PAN.get()) {
            float phase = (Util.getMillis() % (PAN_PERIOD * 2L)) / (float) PAN_PERIOD;
            float travel = phase <= 1.0F ? phase : 2.0F - phase;
            float eased = travel * travel * (3.0F - 2.0F * travel);
            float scale = PAN_START_SCALE + (PAN_END_SCALE - PAN_START_SCALE) * eased;
            spanU /= scale;
            spanV /= scale;
            centerU += -PAN_OFFSET + PAN_OFFSET * 2.0F * eased;
        }
        centerU = Math.clamp(centerU, spanU * 0.5F, 1.0F - spanU * 0.5F);
        float u0 = centerU - spanU * 0.5F;
        float v0 = 0.5F - spanV * 0.5F;
        graphics.blit(current.texture().getTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR), 0, 0, width, height,
            u0, u0 + spanU, v0, v0 + spanV);
        return true;
    }

    private static @Nullable BackgroundPlayer player(@Nullable BackgroundEntry selected) {
        if (selected == null) {
            close();
            return null;
        }
        if (player != null && player.entry().equals(selected)) {
            return player;
        }
        close();
        if (selected.id().equals(failedId)) {
            return null;
        }
        try {
            player = new BackgroundPlayer(selected);
        } catch (IOException | RuntimeException exception) {
            Arkea.LOGGER.warn("Could not play background {}", selected.id(), exception);
            failedId = selected.id();
        }
        return player;
    }

    public static void tick() {
        if (player != null && Util.getMillis() - lastDrawn > IDLE_RELEASE) {
            close();
        }
    }

    static void release(BackgroundEntry entry) {
        if (player != null && player.entry().equals(entry)) {
            close();
        }
        if (entry.id().equals(failedId)) {
            failedId = null;
        }
    }

    private static void close() {
        if (player != null) {
            player.close();
            player = null;
        }
    }
}
