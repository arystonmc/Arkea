package com.aryston.arkea.ui.layout;

public record UiScale(float scale, int guiScale, float canvasWidth, float canvasHeight) {
    public static final float DESIGN_WIDTH = 1366.0F;
    public static final float DESIGN_HEIGHT = 768.0F;
    private static final float MIN_SCALE = 0.5F;
    private static final float MAX_SCALE = 3.0F;
    private static final float STEP = 0.25F;
    private static final float REFERENCE_WIDTH = 1920.0F;
    private static final float REFERENCE_HEIGHT = 1080.0F;
    private static final float MIN_REFERENCE_SCALE = 0.5F;

    public static UiScale compute(int windowWidth, int windowHeight, int guiScale) {
        float fit = Math.min(windowWidth / DESIGN_WIDTH, windowHeight / DESIGN_HEIGHT);
        float stepped = Math.max(STEP, (float) Math.floor(fit / STEP) * STEP);
        float scale = Math.clamp(stepped, MIN_SCALE, MAX_SCALE);
        return new UiScale(scale, guiScale, windowWidth / scale, windowHeight / scale);
    }

    public static UiScale reference(int windowWidth, int windowHeight, int guiScale, float scaleAtReference) {
        float fit = Math.min(windowWidth / REFERENCE_WIDTH, windowHeight / REFERENCE_HEIGHT);
        float scale = Math.max(MIN_REFERENCE_SCALE, fit * scaleAtReference);
        return new UiScale(scale, guiScale, windowWidth / scale, windowHeight / scale);
    }

    public float poseScale() {
        return this.scale / this.guiScale;
    }

    public float toDesign(double guiCoordinate) {
        return (float) (guiCoordinate / this.poseScale());
    }

    public float toGui(float designCoordinate) {
        return designCoordinate * this.poseScale();
    }

    public float snapThickness(float designThickness) {
        return Math.max(1.0F, Math.round(designThickness * this.scale)) / this.scale;
    }
}
