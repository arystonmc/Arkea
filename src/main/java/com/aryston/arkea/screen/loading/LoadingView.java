package com.aryston.arkea.screen.loading;

import java.util.List;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

record LoadingView(Mood mood, Component kicker, Component title, @Nullable Component detail, @Nullable Component stepLabel, float progress,
    List<Step> steps) {
    static final float NO_PROGRESS = Float.NaN;

    boolean hasProgressBar() {
        return !Float.isNaN(this.progress);
    }

    enum Mood {
        BUSY,
        ERROR
    }

    enum StepState {
        DONE,
        ACTIVE,
        WAITING
    }

    record Step(Component label, StepState state) {
    }
}
