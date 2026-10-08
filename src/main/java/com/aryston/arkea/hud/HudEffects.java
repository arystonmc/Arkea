package com.aryston.arkea.hud;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.google.common.collect.Ordering;
import java.util.Collection;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;

public final class HudEffects {
    private static final float MARGIN = 16.0F;
    private static final float TOP = 14.0F;
    private static final float CHIP_HEIGHT = 24.0F;
    private static final float CHIP_GAP = 4.0F;
    private static final float PAD = 6.0F;
    private static final float ICON = 16.0F;
    private static final int ICON_PIXELS = 18;
    private static final float TEXT_GAP = 6.0F;
    private static final float LEVEL_GAP = 4.0F;
    private static final float AMBIENT = 0.6F;
    private static final int ENDING_TICKS = 200;
    private static final int TICKS_PER_SECOND = 20;
    private static final float BLINK_SECONDS = 10.0F;
    private static final float BLINK_FADE = 5.0F;
    private static final float BLINK_BASE = 0.5F;
    private static final float BLINK_SWING = 0.25F;
    private static final float HALF = 0.5F;
    private static final int TEXT_SHADOW = ArkColors.rgba(0, 0, 0, 0.55F);
    private static final TextStyle TIME = TextStyle.of(10.0F).shadow(TEXT_SHADOW, 1.0F);

    private HudEffects() {
    }

    public static void draw(UiGraphics graphics, Box screen, Player player, Hud hud, HudSettings settings) {
        Collection<MobEffectInstance> effects = player.getActiveEffects();
        if (effects.isEmpty()) {
            return;
        }
        TextMetrics metrics = graphics.metrics();
        float tickRate = player.level().tickRateManager().tickrate();
        float beneficialRight = screen.right() - MARGIN;
        float harmfulRight = screen.right() - MARGIN;
        for (MobEffectInstance instance : Ordering.natural().reverse().sortedCopy(effects)) {
            IClientMobEffectExtensions extensions = IClientMobEffectExtensions.of(instance);
            if (!extensions.isVisibleInGui(instance) || !instance.showIcon()) {
                continue;
            }
            Holder<MobEffect> effect = instance.getEffect();
            boolean beneficial = effect.value().isBeneficial();
            String time = MobEffectUtil.formatDuration(instance, 1.0F, tickRate).getString();
            String level = instance.getAmplifier() > 0 ? Component.translatable("enchantment.level." + (instance.getAmplifier() + 1)).getString() : "";
            float levelWidth = level.isEmpty() ? 0.0F : metrics.width(level, TIME) + LEVEL_GAP;
            float width = PAD * 2.0F + ICON + TEXT_GAP + levelWidth + metrics.width(time, TIME);
            float right = beneficial ? beneficialRight : harmfulRight;
            Box chip = new Box(right - width, beneficial ? TOP : TOP + CHIP_HEIGHT + CHIP_GAP, width, CHIP_HEIGHT);
            if (beneficial) {
                beneficialRight -= width + CHIP_GAP;
            } else {
                harmfulRight -= width + CHIP_GAP;
            }
            float alpha = alpha(instance);
            graphics.fill(chip, HudPainter.plate(settings));
            if (!beneficial) {
                graphics.fill(chip.x(), chip.bottom() - 1.0F, chip.width(), 1.0F, ArkColors.withAlpha(ArkColors.ERROR, alpha));
            }
            Box icon = new Box(chip.x() + PAD, chip.centerY() - ICON * HALF, ICON, ICON);
            int tint = ARGB.white(alpha);
            graphics.vanilla(icon, ICON_PIXELS, vanilla -> {
                if (!extensions.extractHudIcon(instance, hud, vanilla, 0, 0, ICON_PIXELS, ICON_PIXELS, tint)) {
                    vanilla.blitSprite(RenderPipelines.GUI_TEXTURED, Hud.getMobEffectSprite(effect), 0, 0, ICON_PIXELS, ICON_PIXELS, tint);
                }
            });
            float textX = icon.right() + TEXT_GAP;
            float textY = chip.centerY() - metrics.capHeight(TIME) * HALF;
            graphics.push();
            graphics.fade(alpha);
            if (!level.isEmpty()) {
                graphics.text(level, textX, textY, TIME, Theme.accent().light());
                textX += levelWidth;
            }
            graphics.text(time, textX, textY, TIME, ArkColors.TEXT_SOFT);
            graphics.pop();
        }
    }

    private static float alpha(MobEffectInstance instance) {
        if (instance.isAmbient()) {
            return AMBIENT;
        }
        if (!instance.endsWithin(ENDING_TICKS)) {
            return 1.0F;
        }
        int remaining = instance.getDuration();
        float used = BLINK_SECONDS - remaining / (float) TICKS_PER_SECOND;
        float alpha = Mth.clamp(remaining / BLINK_SECONDS / BLINK_FADE * BLINK_BASE, 0.0F, BLINK_BASE)
            + Mth.cos(remaining * Mth.PI / BLINK_FADE) * Mth.clamp(used / BLINK_SECONDS * BLINK_SWING, 0.0F, BLINK_SWING);
        return Mth.clamp(alpha, 0.0F, 1.0F);
    }
}
