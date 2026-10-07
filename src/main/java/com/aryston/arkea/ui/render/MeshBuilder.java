package com.aryston.arkea.ui.render;

import java.util.Arrays;

final class MeshBuilder {
    private static final int CORNERS = 4;
    private static final int INITIAL_QUADS = 16;
    private float[] xs = new float[INITIAL_QUADS * CORNERS];
    private float[] ys = new float[INITIAL_QUADS * CORNERS];
    private float[] us = new float[INITIAL_QUADS * CORNERS];
    private float[] vs = new float[INITIAL_QUADS * CORNERS];
    private int[] colors = new int[INITIAL_QUADS * CORNERS];
    private int vertexCount;

    void clear() {
        this.vertexCount = 0;
    }

    boolean isEmpty() {
        return this.vertexCount == 0;
    }

    void add(QuadGeometry quad) {
        if (quad.isDegenerate()) {
            return;
        }
        this.ensureCapacity(this.vertexCount + CORNERS);
        boolean reverse = quad.signedArea() > 0.0F;
        for (int corner = 0; corner < CORNERS; corner++) {
            int source = reverse ? CORNERS - 1 - corner : corner;
            this.xs[this.vertexCount] = quad.x(source);
            this.ys[this.vertexCount] = quad.y(source);
            this.us[this.vertexCount] = quad.u(source);
            this.vs[this.vertexCount] = quad.v(source);
            this.colors[this.vertexCount] = quad.color(source);
            this.vertexCount++;
        }
    }


    float[] xs() {
        return Arrays.copyOf(this.xs, this.vertexCount);
    }

    float[] ys() {
        return Arrays.copyOf(this.ys, this.vertexCount);
    }

    float[] us() {
        return Arrays.copyOf(this.us, this.vertexCount);
    }

    float[] vs() {
        return Arrays.copyOf(this.vs, this.vertexCount);
    }

    int[] colors() {
        return Arrays.copyOf(this.colors, this.vertexCount);
    }

    private void ensureCapacity(int required) {
        if (required <= this.xs.length) {
            return;
        }
        int capacity = Math.max(required, this.xs.length * 2);
        this.xs = Arrays.copyOf(this.xs, capacity);
        this.ys = Arrays.copyOf(this.ys, capacity);
        this.us = Arrays.copyOf(this.us, capacity);
        this.vs = Arrays.copyOf(this.vs, capacity);
        this.colors = Arrays.copyOf(this.colors, capacity);
    }
}
