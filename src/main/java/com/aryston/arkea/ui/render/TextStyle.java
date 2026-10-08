package com.aryston.arkea.ui.render;

public record TextStyle(float size, float letterSpacing, int shadowColor, float shadowOffset) {
    public static final float GLYPH_HEIGHT = 8.0F;
    public static final float CAP_HEIGHT = 7.0F;
    public static final float BASELINE = 7.0F;

    public static TextStyle of(float size) {
        return new TextStyle(size, 0.0F, 0, 0.0F);
    }

    public TextStyle spacing(float spacing) {
        return new TextStyle(this.size, spacing, this.shadowColor, this.shadowOffset);
    }

    public TextStyle shadow(int color, float offset) {
        return new TextStyle(this.size, this.letterSpacing, color, offset);
    }

    public boolean hasShadow() {
        return this.shadowOffset != 0.0F;
    }
}
