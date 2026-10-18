package com.aryston.arkea.screen.loading;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

record LoadingView(Mood mood, Component kicker, Component title, @Nullable Component detail, @Nullable Component stepLabel, @Nullable Span span,
    float progress, List<Step> steps, boolean worldJoin) {
    static final float NO_PROGRESS = Float.NaN;
    static final Span FULL = new Span("full", 0.0F, 1.0F);

    static LoadingView of(Mood mood, Component kicker, Component title, @Nullable Component detail, @Nullable Component stepLabel, float progress,
        List<Step> steps) {
        return new LoadingView(mood, kicker, title, detail, stepLabel, Float.isNaN(progress) ? null : FULL, progress, steps, false);
    }

    boolean hasProgressBar() {
        return this.span != null;
    }

    boolean joinsWorld() {
        return !this.steps.isEmpty();
    }

    LoadingView ready(Component label) {
        List<Step> done = new ArrayList<>(this.steps.size());
        for (Step step : this.steps) {
            done.add(new Step(step.label(), StepState.DONE));
        }
        return new LoadingView(Mood.READY, this.kicker, this.title, null, label, FULL, 1.0F, done, this.worldJoin);
    }

    enum Mood {
        BUSY,
        READY,
        ERROR
    }

    enum StepState {
        DONE,
        ACTIVE,
        WAITING
    }

    record Step(Component label, StepState state) {
    }

    record Span(String key, float start, float end) {
    }
}
