package com.aryston.arkea.ui.theme;

public enum Accent {
    GREEN(0x3C8527, 0x7BC95F, 0x52A535, ArkColors.rgba(60, 133, 39, 0.35F), ArkColors.rgba(60, 133, 39, 0.16F)),
    EMERALD(0x2E7D57, 0x5FD08F, 0x3FA06E, ArkColors.rgba(46, 125, 87, 0.35F), ArkColors.rgba(46, 125, 87, 0.16F)),
    GOLD(0x9C6B17, 0xE6B04A, 0xB88224, ArkColors.rgba(156, 107, 23, 0.35F), ArkColors.rgba(156, 107, 23, 0.16F)),
    STONE(0x55555A, 0xC8C8CC, 0x6E6E74, ArkColors.rgba(85, 85, 90, 0.30F), ArkColors.rgba(255, 255, 255, 0.08F));

    private final int base;
    private final int light;
    private final int border;
    private final int glow;
    private final int tint;

    Accent(int base, int light, int border, int glow, int tint) {
        this.base = ArkColors.rgb(base);
        this.light = ArkColors.rgb(light);
        this.border = ArkColors.rgb(border);
        this.glow = glow;
        this.tint = tint;
    }

    public int base() {
        return this.base;
    }

    public int light() {
        return this.light;
    }

    public int border() {
        return this.border;
    }

    public int glow() {
        return this.glow;
    }

    public int tint() {
        return this.tint;
    }
}
