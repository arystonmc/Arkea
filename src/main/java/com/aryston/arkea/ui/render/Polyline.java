package com.aryston.arkea.ui.render;

public record Polyline(float[] points, boolean closed) {
    public int size() {
        return this.points.length / 2;
    }

    public float x(int index) {
        return this.points[index * 2];
    }

    public float y(int index) {
        return this.points[index * 2 + 1];
    }
}
