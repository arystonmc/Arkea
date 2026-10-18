package com.aryston.arkea.screen.loading;

import com.aryston.arkea.ui.anim.ProgressMotion;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

final class LoadingSession {
    static final long MAX_STEP_MS = 34L;

    private static final long SESSION_GAP_MS = 1500L;
    private static final long RECENT_MS = 250L;
    private static final int TIP_COUNT = 13;
    private static final long LONG_AGO = Long.MIN_VALUE / 4;

    private final List<LoadingView.StepState> stepStates = new ArrayList<>();
    private final List<Long> stepChangedAt = new ArrayList<>();
    private long lastFrameAt = LONG_AGO;
    private long clock;
    private long lastStep;
    private int tip;
    private boolean active;
    private boolean worldJoin;
    private float shownProgress;
    private @Nullable String spanKey;
    private long spanStartedAt;
    private float spanStart;
    private @Nullable String titleText;
    private long titleChangedAt;
    private long readyAt = LONG_AGO;
    private @Nullable LoadingView view;

    boolean frame(long now) {
        boolean fresh = !this.active || now - this.lastFrameAt > SESSION_GAP_MS;
        if (fresh) {
            this.start();
        } else {
            this.lastStep = Math.min(now - this.lastFrameAt, MAX_STEP_MS);
            this.clock += this.lastStep;
        }
        this.lastFrameAt = now;
        return fresh;
    }

    private void start() {
        this.active = true;
        this.clock = 0L;
        this.lastStep = 0L;
        this.tip = ThreadLocalRandom.current().nextInt(TIP_COUNT);
        this.worldJoin = false;
        this.shownProgress = 0.0F;
        this.spanKey = null;
        this.titleText = null;
        this.titleChangedAt = LONG_AGO;
        this.readyAt = LONG_AGO;
        this.view = null;
        this.stepStates.clear();
        this.stepChangedAt.clear();
    }

    void show(LoadingView next) {
        this.worldJoin |= next.worldJoin();
        String title = next.title().getString();
        if (this.titleText != null && !this.titleText.equals(title)) {
            this.titleChangedAt = this.clock;
        }
        this.titleText = title;
        this.trackSteps(next.steps());
        LoadingView.Span span = next.span();
        if (span != null) {
            if (!span.key().equals(this.spanKey)) {
                if (this.spanKey != null && (span.start() < this.spanStart || span.end() <= this.shownProgress)) {
                    this.shownProgress = span.start();
                }
                this.spanKey = span.key();
                this.spanStart = span.start();
                this.spanStartedAt = this.clock;
            }
            float target = ProgressMotion.target(span.start(), span.end(), next.progress(), this.clock - this.spanStartedAt);
            this.shownProgress = ProgressMotion.follow(this.shownProgress, target, this.lastStep);
        }
        this.view = next;
    }

    private void trackSteps(List<LoadingView.Step> steps) {
        if (steps.size() != this.stepStates.size()) {
            this.stepStates.clear();
            this.stepChangedAt.clear();
            for (LoadingView.Step step : steps) {
                this.stepStates.add(step.state());
                this.stepChangedAt.add(LONG_AGO);
            }
            return;
        }
        for (int index = 0; index < steps.size(); index++) {
            if (steps.get(index).state() != this.stepStates.get(index)) {
                this.stepStates.set(index, steps.get(index).state());
                this.stepChangedAt.set(index, this.clock);
            }
        }
    }

    boolean drewRecently(long now) {
        return this.active && now - this.lastFrameAt <= RECENT_MS;
    }

    boolean finish(Component readyLabel) {
        this.active = false;
        if (this.view == null || this.view.mood() != LoadingView.Mood.BUSY) {
            return false;
        }
        this.view = this.view.ready(readyLabel);
        this.readyAt = this.clock;
        this.trackSteps(this.view.steps());
        return true;
    }

    void tick(long now) {
        this.lastStep = Math.min(Math.max(0L, now - this.lastFrameAt), MAX_STEP_MS);
        this.clock += this.lastStep;
        this.lastFrameAt = now;
        this.shownProgress = ProgressMotion.follow(this.shownProgress, 1.0F, this.lastStep);
    }

    long sinceReady() {
        return this.clock - this.readyAt;
    }

    void end() {
        this.active = false;
    }

    long clock() {
        return this.clock;
    }

    int tip() {
        return this.tip;
    }

    boolean worldJoin() {
        return this.worldJoin;
    }

    float shownProgress() {
        return this.shownProgress;
    }

    long sinceTitleChange() {
        return this.clock - this.titleChangedAt;
    }

    long sinceStepChange(int index) {
        return index < this.stepChangedAt.size() ? this.clock - this.stepChangedAt.get(index) : Long.MAX_VALUE;
    }

    @Nullable LoadingView view() {
        return this.view;
    }
}
