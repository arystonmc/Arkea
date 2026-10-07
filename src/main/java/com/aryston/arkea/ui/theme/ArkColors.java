package com.aryston.arkea.ui.theme;

public final class ArkColors {
    public static final int BACKDROP = rgb(0x0A0B0A);
    public static final int WINDOW = rgba(20, 20, 22, 0.82F);
    public static final int WINDOW_SOLID = rgb(0x141416);
    public static final int SIDEBAR = rgba(0, 0, 0, 0.22F);
    public static final int DIALOG = rgb(0x161618);
    public static final int MENU = rgb(0x141416);
    public static final int ROW = rgba(255, 255, 255, 0.03F);
    public static final int ROW_HOVER = rgba(255, 255, 255, 0.055F);
    public static final int CONTROL_FILL = rgba(0, 0, 0, 0.30F);
    public static final int TRACK = rgb(0x38383B);
    public static final int TRACK_EMPTY = rgba(255, 255, 255, 0.10F);

    public static final int BORDER_SUBTLE = rgba(255, 255, 255, 0.06F);
    public static final int BORDER_DEFAULT = rgba(255, 255, 255, 0.08F);
    public static final int BORDER_STRONG = rgba(255, 255, 255, 0.10F);
    public static final int BORDER_OVERLAY = rgba(255, 255, 255, 0.12F);
    public static final int BORDER_TOOLTIP = rgba(255, 255, 255, 0.14F);
    public static final int INNER_HIGHLIGHT = rgba(255, 255, 255, 0.05F);

    public static final int TEXT_PRIMARY = rgb(0xFFFFFF);
    public static final int TEXT_SOFT = rgb(0xC8C8CA);
    public static final int TEXT_MUTED = rgb(0x9A9A9D);
    public static final int TEXT_DESCRIPTION = rgb(0x8E8E91);
    public static final int TEXT_LABEL = rgb(0x6E6E71);
    public static final int TEXT_FAINT = rgb(0x77777A);
    public static final int TEXT_ICON_IDLE = rgb(0xB5B5B8);
    public static final int TEXT_DISABLED = rgb(0x5E5E61);

    public static final int WARNING = rgb(0xE0A63A);
    public static final int WARNING_TEXT = rgb(0xE6C27A);
    public static final int DANGER = rgb(0xB8463A);
    public static final int DANGER_BORDER = rgb(0xD0584A);
    public static final int DANGER_TEXT = rgb(0xF08A7A);
    public static final int INFO = rgb(0x9DB4D6);
    public static final int INFO_TEXT = rgb(0xC2D1E6);
    public static final int ERROR = rgb(0xE0705F);
    public static final int MINECRAFT_YELLOW = rgb(0xFFE066);
    public static final int XP = rgb(0x8FD14F);

    public static final int SCRIM_MODAL = rgba(8, 8, 9, 0.78F);
    public static final int SCRIM_IN_GAME = rgba(8, 8, 9, 0.50F);
    public static final int SCRIM_PAUSE = rgba(8, 8, 9, 0.60F);

    public static final int SHADOW_WINDOW = rgba(0, 0, 0, 0.60F);
    public static final int SHADOW_MENU = rgba(0, 0, 0, 0.55F);
    public static final int SHADOW_DIALOG = rgba(0, 0, 0, 0.70F);
    public static final int TRANSPARENT = 0;

    private static final float CHANNEL_MAX = 255.0F;
    private static final int ALPHA_SHIFT = 24;
    private static final int RED_SHIFT = 16;
    private static final int GREEN_SHIFT = 8;
    private static final int CHANNEL_MASK = 0xFF;
    private static final int RGB_MASK = 0xFFFFFF;

    private ArkColors() {
    }

    public static int rgb(int rgb) {
        return CHANNEL_MASK << ALPHA_SHIFT | rgb & RGB_MASK;
    }

    public static int rgba(int red, int green, int blue, float alpha) {
        return channel(alpha) << ALPHA_SHIFT | red << RED_SHIFT | green << GREEN_SHIFT | blue;
    }

    public static int withAlpha(int color, float alpha) {
        return channel(alpha) << ALPHA_SHIFT | color & RGB_MASK;
    }

    public static float alpha(int color) {
        return (color >>> ALPHA_SHIFT) / CHANNEL_MAX;
    }

    public static int multiplyAlpha(int color, float factor) {
        if (factor >= 1.0F) {
            return color;
        }
        int alpha = Math.round((color >>> ALPHA_SHIFT) * Math.max(0.0F, factor));
        return alpha << ALPHA_SHIFT | color & RGB_MASK;
    }

    public static int brighten(int color, float factor) {
        int red = scaleChannel(color >> RED_SHIFT & CHANNEL_MASK, factor);
        int green = scaleChannel(color >> GREEN_SHIFT & CHANNEL_MASK, factor);
        int blue = scaleChannel(color & CHANNEL_MASK, factor);
        return color & CHANNEL_MASK << ALPHA_SHIFT | red << RED_SHIFT | green << GREEN_SHIFT | blue;
    }

    public static int lerp(float amount, int from, int to) {
        float clamped = Math.clamp(amount, 0.0F, 1.0F);
        int alpha = lerpChannel(clamped, from >>> ALPHA_SHIFT, to >>> ALPHA_SHIFT);
        int red = lerpChannel(clamped, from >> RED_SHIFT & CHANNEL_MASK, to >> RED_SHIFT & CHANNEL_MASK);
        int green = lerpChannel(clamped, from >> GREEN_SHIFT & CHANNEL_MASK, to >> GREEN_SHIFT & CHANNEL_MASK);
        int blue = lerpChannel(clamped, from & CHANNEL_MASK, to & CHANNEL_MASK);
        return alpha << ALPHA_SHIFT | red << RED_SHIFT | green << GREEN_SHIFT | blue;
    }

    private static int channel(float alpha) {
        return Math.round(Math.clamp(alpha, 0.0F, 1.0F) * CHANNEL_MAX);
    }

    private static int scaleChannel(int value, float factor) {
        return Math.min(CHANNEL_MASK, Math.round(value * factor));
    }

    private static int lerpChannel(float amount, int from, int to) {
        return Math.round(from + (to - from) * amount);
    }
}
