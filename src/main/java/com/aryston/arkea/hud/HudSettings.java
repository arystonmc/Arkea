package com.aryston.arkea.hud;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.ui.layout.UiScale;
import com.mojang.blaze3d.platform.Window;

public record HudSettings(HudStyle style, float opacity, float size, boolean effects, boolean bossBars, boolean scoreboard,
    boolean tabList) {
    public static final int OPACITY_MIN = 0;
    public static final int OPACITY_MAX = 80;
    public static final int OPACITY_DEFAULT = 30;
    public static final int OPACITY_STEP = 5;
    public static final int SIZE_MIN = 75;
    public static final int SIZE_MAX = 150;
    public static final int SIZE_DEFAULT = 100;
    public static final int SIZE_STEP = 5;
    private static final float PERCENT = 100.0F;
    private static final float GUI_UNIT = 0.45F;
    private static final HudSettings DEFAULTS = new HudSettings(HudStyle.VANILLA, OPACITY_DEFAULT / PERCENT, SIZE_DEFAULT / PERCENT, true, true, true, true);

    public static HudSettings current() {
        if (!ArkeaConfig.SPEC.isLoaded()) {
            return DEFAULTS;
        }
        return new HudSettings(ArkeaConfig.HUD_STYLE.get(), ArkeaConfig.HUD_OPACITY.get() / PERCENT, ArkeaConfig.HUD_SIZE.get() / PERCENT,
            ArkeaConfig.HUD_EFFECTS.get(), ArkeaConfig.HUD_BOSS_BARS.get(), ArkeaConfig.HUD_SCOREBOARD.get(), ArkeaConfig.HUD_TAB_LIST.get());
    }

    public UiScale scale(Window window) {
        float scale = window.getGuiScale() * GUI_UNIT * this.size;
        return new UiScale(scale, window.getGuiScale(), window.getWidth() / scale, window.getHeight() / scale);
    }

    public HudSettings withStyle(HudStyle newStyle) {
        return new HudSettings(newStyle, this.opacity, this.size, this.effects, this.bossBars, this.scoreboard, this.tabList);
    }
}
