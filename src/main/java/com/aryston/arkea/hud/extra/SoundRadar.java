package com.aryston.arkea.hud.extra;

import com.aryston.arkea.hud.HudClock;
import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.hud.HudPainter;
import com.aryston.arkea.hud.HudSettings;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class SoundRadar implements SoundEventListener {
    private static final long LIFE = 2400L;
    private static final long FADE = 600L;
    private static final int MAX_SOUNDS = 8;
    private static final float RADIUS = 150.0F;
    private static final float HEIGHT = 18.0F;
    private static final float PAD = 7.0F;
    private static final float DOT = 4.0F;
    private static final float DOT_GAP = 5.0F;
    private static final float FRONT = 90.0F;
    private static final float MIN_OPACITY = 0.55F;
    private static final float HALF = 0.5F;
    private static final TextStyle TEXT = TextStyle.of(10.0F);
    private static final List<Heard> HEARD = new ArrayList<>();
    private static boolean registered;

    private SoundRadar() {
    }

    public static void ensureRegistered(Minecraft minecraft) {
        if (!registered) {
            registered = true;
            minecraft.getSoundManager().addListener(new SoundRadar());
        }
    }

    @Override
    public void onPlaySound(SoundInstance sound, WeighedSoundEvents events, float range) {
        Component subtitle = events.getSubtitle();
        if (subtitle == null || sound.isRelative() || !ArkeaConfig.on(ArkeaConfig.SOUND_RADAR)) {
            return;
        }
        Vec3 position = new Vec3(sound.getX(), sound.getY(), sound.getZ());
        long now = HudClock.now();
        HEARD.removeIf(heard -> heard.subtitle().getString().equals(subtitle.getString()));
        HEARD.add(new Heard(subtitle, position, now));
        while (HEARD.size() > MAX_SOUNDS) {
            HEARD.removeFirst();
        }
    }

    public static void draw(UiGraphics graphics, Box screen, HudSettings settings, LocalPlayer player) {
        long now = graphics.now();
        HEARD.removeIf(heard -> now - heard.at() > LIFE);
        TextMetrics metrics = graphics.metrics();
        int plate = ArkColors.withAlpha(HudPainter.plate(settings), Math.max(settings.opacity(), MIN_OPACITY));
        for (Heard heard : HEARD) {
            float alpha = Math.clamp((LIFE - (now - heard.at())) / (float) FADE, 0.0F, 1.0F);
            double dx = heard.position().x - player.getX();
            double dz = heard.position().z - player.getZ();
            float angle = ((float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - player.getYRot() - FRONT) * Mth.DEG_TO_RAD;
            float centerX = screen.centerX() + Mth.sin(angle) * RADIUS;
            float centerY = screen.centerY() - Mth.cos(angle) * RADIUS;
            String text = heard.subtitle().getString();
            float width = PAD * 2.0F + DOT + DOT_GAP + metrics.width(text, TEXT);
            Box chip = new Box(centerX - width * HALF, centerY - HEIGHT * HALF, width, HEIGHT);
            graphics.push();
            graphics.fade(alpha);
            graphics.fill(chip, plate);
            graphics.fill(chip.x() + PAD, chip.centerY() - DOT * HALF, DOT, DOT, Theme.accent().light());
            graphics.text(text, chip.x() + PAD + DOT + DOT_GAP, chip.centerY() - metrics.capHeight(TEXT) * HALF, TEXT, ArkColors.TEXT_PRIMARY);
            graphics.pop();
        }
    }

    private record Heard(Component subtitle, Vec3 position, long at) {
    }
}
