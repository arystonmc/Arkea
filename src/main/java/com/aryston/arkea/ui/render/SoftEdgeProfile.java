package com.aryston.arkea.ui.render;

public final class SoftEdgeProfile {
    private static final double SIGMAS_PER_HALF_BAND = 2.0;
    private static final double ERF_A1 = 0.254829592;
    private static final double ERF_A2 = -0.284496736;
    private static final double ERF_A3 = 1.421413741;
    private static final double ERF_A4 = -1.453152027;
    private static final double ERF_A5 = 1.061405429;
    private static final double ERF_P = 0.3275911;
    private static final double SQRT_TWO = Math.sqrt(2.0);

    private SoftEdgeProfile() {
    }

    public static float coverage(float bandPosition) {
        double distance = (bandPosition * 2.0 - 1.0) * SIGMAS_PER_HALF_BAND;
        return (float) (0.5 * (1.0 + erf(distance / SQRT_TWO)));
    }

    public static float radial(float normalizedX, float normalizedY, float innerRadius) {
        float distance = (float) (Math.sqrt(normalizedX * normalizedX + normalizedY * normalizedY) / SQRT_TWO);
        return Math.clamp((distance - innerRadius) / (1.0F - innerRadius), 0.0F, 1.0F);
    }

    private static double erf(double value) {
        double sign = Math.signum(value);
        double absolute = Math.abs(value);
        double t = 1.0 / (1.0 + ERF_P * absolute);
        double polynomial = ((((ERF_A5 * t + ERF_A4) * t + ERF_A3) * t + ERF_A2) * t + ERF_A1) * t;
        return sign * (1.0 - polynomial * Math.exp(-absolute * absolute));
    }
}
