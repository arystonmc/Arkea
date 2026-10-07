package com.aryston.arkea.ui.anim;

@FunctionalInterface
public interface Easing {
    Easing LINEAR = progress -> progress;
    Easing EASE = new CubicBezier(0.25F, 0.1F, 0.25F, 1.0F);
    Easing STANDARD = new CubicBezier(0.2F, 0.8F, 0.2F, 1.0F);
    Easing EXIT = new CubicBezier(0.42F, 0.0F, 1.0F, 1.0F);
    Easing EASE_OUT = new CubicBezier(0.0F, 0.0F, 0.58F, 1.0F);
    Easing EASE_IN_OUT = new CubicBezier(0.42F, 0.0F, 0.58F, 1.0F);
    Easing SPRING = new CubicBezier(0.3F, 1.4F, 0.5F, 1.0F);

    float apply(float progress);
}
