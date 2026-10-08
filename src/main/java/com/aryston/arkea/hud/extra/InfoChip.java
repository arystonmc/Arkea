package com.aryston.arkea.hud.extra;

import com.aryston.arkea.hud.HudClock;
import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.hud.HudPainter;
import com.aryston.arkea.hud.HudSettings;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public final class InfoChip {
    private static final float MARGIN = 16.0F;
    private static final float HEIGHT = 20.0F;
    private static final float PAD = 8.0F;
    private static final float GAP = 6.0F;
    private static final float ICON = 10.0F;
    private static final float MIN_OPACITY = 0.45F;
    private static final float HALF = 0.5F;
    private static final String DOT = "  ·  ";
    private static final String[] DIRECTIONS = {"s", "sw", "w", "nw", "n", "ne", "e", "se"};
    private static final float SECTOR = 45.0F;
    private static final float FULL_TURN = 360.0F;
    private static final float FRONT = 90.0F;
    private static final String COMMA = ", ";
    private static final long TICKS_PER_DAY = 24000L;
    private static final long TICKS_PER_HOUR = 1000L;
    private static final long DAWN_HOUR = 6L;
    private static final long HOURS_PER_DAY = 24L;
    private static final long MINUTES_PER_HOUR = 60L;
    private static final long DEATH_POINT_LIFE = 600000L;
    private static final double REACHED = 4.0;
    private static final TextStyle TEXT = TextStyle.of(10.0F);
    private static float stackBottom;

    private InfoChip() {
    }

    public static void draw(UiGraphics graphics, Box screen, HudSettings settings, Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;
        if (player == null || level == null) {
            return;
        }
        int plate = ArkColors.withAlpha(HudPainter.plate(settings), Math.max(settings.opacity(), MIN_OPACITY));
        float y = screen.y() + MARGIN;
        stackBottom = y;
        if (ArkeaConfig.on(ArkeaConfig.INFO_CHIP)) {
            List<String> parts = new ArrayList<>();
            BlockPos pos = player.blockPosition();
            if (!minecraft.showOnlyReducedInfo()) {
                parts.add(pos.getX() + COMMA + pos.getY() + COMMA + pos.getZ());
            }
            parts.add(Component.translatable("arkea.info.direction." + direction(player.getYRot())).getString());
            level.getBiome(pos).unwrapKey().ifPresent(key -> parts.add(Component.translatable(key.identifier().toLanguageKey("biome")).getString()));
            parts.add(clock(level.getOverworldClockTime()));
            String text = String.join(DOT, parts);
            y = chip(graphics, screen.x() + MARGIN, y, text, plate, false, 0.0F) + GAP;
            stackBottom = y;
        }
        if (ArkeaConfig.on(ArkeaConfig.DEATH_POINT)) {
            deathPoint(graphics, screen, minecraft, player, plate, y);
        }
    }

    public static float stackBottom() {
        return stackBottom;
    }

    private static void deathPoint(UiGraphics graphics, Box screen, Minecraft minecraft, LocalPlayer player, int plate, float y) {
        long diedAt = HudTracker.diedAt();
        Optional<GlobalPos> death = player.getLastDeathLocation();
        if (diedAt < 0L || HudClock.now() - diedAt > DEATH_POINT_LIFE || death.isEmpty() || player.isDeadOrDying()
            || minecraft.level == null || death.get().dimension() != minecraft.level.dimension()) {
            return;
        }
        BlockPos pos = death.get().pos();
        double dx = pos.getX() + HALF - player.getX();
        double dz = pos.getZ() + HALF - player.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        if (distance < REACHED) {
            return;
        }
        float bearing = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - player.getYRot() - FRONT;
        String text = Component.translatable("arkea.info.death", Math.round(distance)).getString();
        stackBottom = chip(graphics, screen.x() + MARGIN, y, text, plate, true, bearing * Mth.DEG_TO_RAD) + GAP;
    }

    private static float chip(UiGraphics graphics, float x, float y, String text, int plate, boolean arrow, float angle) {
        TextMetrics metrics = graphics.metrics();
        float width = PAD * 2.0F + metrics.width(text, TEXT) + (arrow ? ICON + GAP : 0.0F);
        Box chip = new Box(x, y, width, HEIGHT);
        graphics.fill(chip, plate);
        float textX = chip.x() + PAD;
        if (arrow) {
            float centerX = textX + ICON * HALF;
            graphics.push();
            graphics.rotateAround(angle, centerX, chip.centerY());
            graphics.icon(Icons.UP, centerX - ICON * HALF, chip.centerY() - ICON * HALF, ICON, ICON, Theme.accent().light());
            graphics.pop();
            textX += ICON + GAP;
        }
        graphics.text(text, textX, chip.centerY() - metrics.capHeight(TEXT) * HALF, TEXT, ArkColors.TEXT_SOFT);
        return chip.bottom();
    }

    private static String direction(float yaw) {
        float turned = Mth.positiveModulo(yaw + SECTOR * HALF, FULL_TURN);
        return DIRECTIONS[(int) (turned / SECTOR) % DIRECTIONS.length];
    }

    private static String clock(long ticks) {
        long tick = ticks % TICKS_PER_DAY;
        long hour = (tick / TICKS_PER_HOUR + DAWN_HOUR) % HOURS_PER_DAY;
        long minute = tick % TICKS_PER_HOUR * MINUTES_PER_HOUR / TICKS_PER_HOUR;
        return String.format(Locale.ROOT, "%02d:%02d", hour, minute);
    }
}
