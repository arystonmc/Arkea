package com.aryston.arkea.screen.loading;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.mixin.MinecraftAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Util;

public final class LoadingFrames {
    private static final long TASK_SLICE_NANOS = TimeUnit.MILLISECONDS.toNanos(2L);
    private static final long WAIT_SLICE_MILLIS = TimeUnit.NANOSECONDS.toMillis(TASK_SLICE_NANOS);

    private LoadingFrames() {
    }

    public static void await(Minecraft minecraft, BooleanSupplier done) {
        if (!drawsLoadingScreen(minecraft)) {
            minecraft.managedBlock(done);
            return;
        }
        while (!done.getAsBoolean()) {
            drawFrame(minecraft);
            long sliceEnd = Util.getNanos() + TASK_SLICE_NANOS;
            minecraft.managedBlock(() -> done.getAsBoolean() || Util.getNanos() >= sliceEnd);
        }
    }

    public static void await(Minecraft minecraft, Future<?> future) {
        if (!minecraft.isSameThread() || !drawsLoadingScreen(minecraft)) {
            return;
        }
        while (!future.isDone()) {
            drawFrame(minecraft);
            future.awaitUninterruptibly(WAIT_SLICE_MILLIS);
        }
    }

    private static boolean drawsLoadingScreen(Minecraft minecraft) {
        Screen screen = minecraft.gui.screen();
        return ArkeaConfig.MENU_SCREENS.get() && screen != null && LoadingSkin.handles(screen);
    }

    private static void drawFrame(Minecraft minecraft) {
        RenderSystem.pumpEvents(((MinecraftAccessor) minecraft).arkea$sdlEventHandler());
        minecraft.renderFrame(false);
    }
}
