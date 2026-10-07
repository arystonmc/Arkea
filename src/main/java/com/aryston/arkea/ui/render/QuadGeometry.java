package com.aryston.arkea.ui.render;

final class QuadGeometry {
    private static final int CORNERS = 4;
    private final float[] xs = new float[CORNERS];
    private final float[] ys = new float[CORNERS];
    private final float[] us = new float[CORNERS];
    private final float[] vs = new float[CORNERS];
    private final int[] colors = new int[CORNERS];

    QuadGeometry corner(int index, float x, float y, int color) {
        return this.corner(index, x, y, 0.0F, 0.0F, color);
    }

    QuadGeometry corner(int index, float x, float y, float u, float v, int color) {
        this.xs[index] = x;
        this.ys[index] = y;
        this.us[index] = u;
        this.vs[index] = v;
        this.colors[index] = color;
        return this;
    }

    float x(int index) {
        return this.xs[index];
    }

    float y(int index) {
        return this.ys[index];
    }

    float u(int index) {
        return this.us[index];
    }

    float v(int index) {
        return this.vs[index];
    }

    int color(int index) {
        return this.colors[index];
    }

    float signedArea() {
        float area = 0.0F;
        for (int index = 0; index < CORNERS; index++) {
            int next = (index + 1) % CORNERS;
            area += this.xs[index] * this.ys[next] - this.xs[next] * this.ys[index];
        }
        return area * 0.5F;
    }

    boolean isDegenerate() {
        return Math.abs(this.signedArea()) < 1.0E-6F;
    }
}
