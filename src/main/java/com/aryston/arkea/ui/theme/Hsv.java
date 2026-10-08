package com.aryston.arkea.ui.theme;

public record Hsv(float hue, float saturation, float value) {
    private static final float CHANNEL_MAX = 255.0F;
    private static final int SECTORS = 6;
    private static final int RED_SHIFT = 16;
    private static final int GREEN_SHIFT = 8;
    private static final int CHANNEL_MASK = 0xFF;

    public static Hsv fromRgb(int rgb) {
        float red = (rgb >> RED_SHIFT & CHANNEL_MASK) / CHANNEL_MAX;
        float green = (rgb >> GREEN_SHIFT & CHANNEL_MASK) / CHANNEL_MAX;
        float blue = (rgb & CHANNEL_MASK) / CHANNEL_MAX;
        float max = Math.max(red, Math.max(green, blue));
        float min = Math.min(red, Math.min(green, blue));
        float delta = max - min;
        float hue = 0.0F;
        if (delta > 0.0F) {
            if (max == red) {
                hue = ((green - blue) / delta) % SECTORS;
            } else if (max == green) {
                hue = (blue - red) / delta + 2.0F;
            } else {
                hue = (red - green) / delta + 4.0F;
            }
            hue = (hue / SECTORS + 1.0F) % 1.0F;
        }
        return new Hsv(hue, max <= 0.0F ? 0.0F : delta / max, max);
    }

    public static int hueColor(float hue) {
        return new Hsv(hue, 1.0F, 1.0F).toRgb();
    }

    public int toRgb() {
        float scaled = (this.hue % 1.0F + 1.0F) % 1.0F * SECTORS;
        int sector = (int) Math.floor(scaled) % SECTORS;
        float fraction = scaled - (float) Math.floor(scaled);
        float low = this.value * (1.0F - this.saturation);
        float falling = this.value * (1.0F - this.saturation * fraction);
        float rising = this.value * (1.0F - this.saturation * (1.0F - fraction));
        return switch (sector) {
            case 0 -> pack(this.value, rising, low);
            case 1 -> pack(falling, this.value, low);
            case 2 -> pack(low, this.value, rising);
            case 3 -> pack(low, falling, this.value);
            case 4 -> pack(rising, low, this.value);
            default -> pack(this.value, low, falling);
        };
    }

    public Hsv withHue(float newHue) {
        return new Hsv(newHue, this.saturation, this.value);
    }

    public Hsv withSaturationValue(float newSaturation, float newValue) {
        return new Hsv(this.hue, newSaturation, newValue);
    }

    private static int pack(float red, float green, float blue) {
        return channel(red) << RED_SHIFT | channel(green) << GREEN_SHIFT | channel(blue);
    }

    private static int channel(float value) {
        return Math.round(Math.clamp(value, 0.0F, 1.0F) * CHANNEL_MAX);
    }
}
