package com.aryston.arkea.ui.anim;

public record CubicBezier(float x1, float y1, float x2, float y2) implements Easing {
    private static final int NEWTON_STEPS = 8;
    private static final int BISECTION_STEPS = 24;
    private static final float EPSILON = 1.0E-5F;
    private static final float MIN_SLOPE = 1.0E-4F;

    @Override
    public float apply(float progress) {
        if (progress <= 0.0F) {
            return 0.0F;
        }
        if (progress >= 1.0F) {
            return 1.0F;
        }
        return curve(solveCurveParameter(progress), this.y1, this.y2);
    }

    private float solveCurveParameter(float x) {
        float parameter = x;
        for (int step = 0; step < NEWTON_STEPS; step++) {
            float error = curve(parameter, this.x1, this.x2) - x;
            if (Math.abs(error) < EPSILON) {
                return parameter;
            }
            float slope = slope(parameter, this.x1, this.x2);
            if (Math.abs(slope) < MIN_SLOPE) {
                break;
            }
            parameter -= error / slope;
        }
        return bisect(x);
    }

    private float bisect(float x) {
        float low = 0.0F;
        float high = 1.0F;
        float parameter = x;
        for (int step = 0; step < BISECTION_STEPS; step++) {
            parameter = (low + high) * 0.5F;
            if (curve(parameter, this.x1, this.x2) < x) {
                low = parameter;
            } else {
                high = parameter;
            }
        }
        return parameter;
    }

    private static float curve(float parameter, float first, float second) {
        float inverse = 1.0F - parameter;
        return 3.0F * inverse * inverse * parameter * first + 3.0F * inverse * parameter * parameter * second + parameter * parameter * parameter;
    }

    private static float slope(float parameter, float first, float second) {
        float inverse = 1.0F - parameter;
        return 3.0F * inverse * inverse * first + 6.0F * inverse * parameter * (second - first) + 3.0F * parameter * parameter * (1.0F - second);
    }
}
