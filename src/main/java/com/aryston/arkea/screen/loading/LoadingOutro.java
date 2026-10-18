package com.aryston.arkea.screen.loading;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import java.util.List;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public final class LoadingOutro {
    private static final long READY_MS = 280L;
    private static final long DIP_MS = 240L;
    private static final long REVEAL_MS = 640L;
    private static final long COVERED_MS = READY_MS + DIP_MS;
    private static final long TOTAL_MS = COVERED_MS + REVEAL_MS;
    private static final float EXIT_RISE = 10.0F;
    private static final float EXIT_SCALE = 0.03F;
    private static final int DIM = ArkColors.rgba(8, 8, 9, 0.55F);
    private static final int NO_MOUSE = -1;
    private static final float JOIN_SOUND_PITCH = 1.0F;
    private static final float JOIN_SOUND_VOLUME = 0.5F;

    private final LoadingSkin skin;
    private boolean running;
    private long startedAt;

    public LoadingOutro(LoadingSkin skin) {
        this.skin = skin;
    }

    public void update(Minecraft minecraft) {
        long now = Util.getMillis();
        LoadingSession session = this.skin.session();
        if (session.drewRecently(now) && !LoadingSkin.handles(minecraft.gui.screen())) {
            boolean ready = session.finish(Component.translatable("arkea.loading.ready"));
            if (ready && minecraft.level != null && minecraft.player != null && this.skin.lastScreen() != null) {
                this.running = true;
                this.startedAt = session.clock();
                this.playJoinSound(minecraft, session.view());
            }
        }
        if (LoadingSkin.handles(minecraft.gui.screen())) {
            this.running = false;
        }
        if (this.running) {
            session.tick(now);
            if (session.clock() - this.startedAt >= TOTAL_MS || minecraft.level == null) {
                this.running = false;
            }
        }
    }

    public boolean hidesHud() {
        return this.running && this.elapsed() < COVERED_MS;
    }

    public void render(GuiGraphicsExtractor graphics, DeltaTracker delta) {
        if (!this.running) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        long elapsed = this.elapsed();
        Screen background = this.skin.lastScreen();
        boolean covered = elapsed < COVERED_MS && minecraft.gui.screen() == null && background != null;
        if (covered) {
            background.extractBackground(graphics, NO_MOUSE, NO_MOUSE, delta.getGameTimeDeltaPartialTick(false));
            graphics.nextStratum();
        }
        UiGraphics ui = this.skin.graphics(graphics);
        UiScale scale = ui.scale();
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale.poseScale());
        float dip = Math.clamp((elapsed - READY_MS) / (float) DIP_MS, 0.0F, 1.0F);
        if (covered) {
            ui.fill(0.0F, 0.0F, scale.canvasWidth(), scale.canvasHeight(), DIM);
            LoadingView view = this.skin.session().view();
            if (view != null) {
                float content = 1.0F - Easing.EXIT.apply(dip);
                ui.push();
                ui.fade(content);
                ui.translate(0.0F, -EXIT_RISE * (1.0F - content));
                ui.scaleAround(1.0F + EXIT_SCALE * (1.0F - content), scale.canvasWidth() * 0.5F, scale.canvasHeight() * 0.5F);
                this.skin.draw(ui, view, List.of(), false);
                ui.pop();
            }
        }
        float black = covered ? Easing.EXIT.apply(dip) : 1.0F - Easing.STANDARD.apply(Math.clamp((elapsed - COVERED_MS) / (float) REVEAL_MS, 0.0F, 1.0F));
        ui.fill(0.0F, 0.0F, scale.canvasWidth(), scale.canvasHeight(), ArkColors.rgba(0, 0, 0, black));
        graphics.pose().popMatrix();
    }

    private void playJoinSound(Minecraft minecraft, @Nullable LoadingView view) {
        if (ArkeaConfig.JOIN_SOUND.get() && view != null && view.joinsWorld()) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, JOIN_SOUND_PITCH, JOIN_SOUND_VOLUME));
        }
    }

    private long elapsed() {
        return this.skin.session().clock() - this.startedAt;
    }
}
