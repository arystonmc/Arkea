package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Presence;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public final class MenuToast {
    static final int ENTER_MS = 320;
    static final int EXIT_MS = 200;
    static final int STAY_MS = 2400;
    private static final int MOVE_MS = 260;
    private static final float BUSY = -1.0F;

    private ToastTone tone;
    private Component message;
    private final @Nullable Component action;
    private final @Nullable Runnable onAction;
    private final Presence presence = Presence.of(ENTER_MS, EXIT_MS);
    private final Transition lift = new Transition(0.0F, MOVE_MS, Easing.STANDARD);
    private float progress = Float.NaN;
    private long shownAt;
    private Box frame = Box.EMPTY;
    private Box actionBox = Box.EMPTY;

    MenuToast(ToastTone tone, Component message, @Nullable Component action, @Nullable Runnable onAction) {
        this.tone = tone;
        this.message = message;
        this.action = action;
        this.onAction = onAction;
    }

    public void progress(float value) {
        this.progress = Math.clamp(value, 0.0F, 1.0F);
    }

    void busy() {
        this.progress = BUSY;
    }

    boolean isBusy() {
        return this.progress == BUSY;
    }

    public void finish(ToastTone newTone, Component newMessage) {
        this.tone = newTone;
        this.message = newMessage;
        this.progress = Float.NaN;
        this.shownAt = Util.getMillis();
    }

    public void dismiss(long now) {
        this.presence.hide(now);
    }

    void show(long now) {
        this.presence.enter(now, 0);
        this.shownAt = now;
    }

    boolean expired(long now) {
        boolean waits = this.tone == ToastTone.ERROR || this.hasProgress();
        return this.presence.isShown() && !waits && now - this.shownAt >= STAY_MS;
    }

    boolean isGone(long now) {
        return this.presence.isGone(now);
    }

    boolean isShown() {
        return this.presence.isShown();
    }

    boolean hasProgress() {
        return !Float.isNaN(this.progress);
    }

    float progressValue() {
        return this.progress;
    }

    float visibility(long now) {
        return this.presence.progress(now);
    }

    Transition lift() {
        return this.lift;
    }

    ToastTone tone() {
        return this.tone;
    }

    Component message() {
        return this.message;
    }

    @Nullable Component action() {
        return this.action;
    }

    void layout(Box newFrame, Box newActionBox) {
        this.frame = newFrame;
        this.actionBox = newActionBox;
    }

    Box frame() {
        return this.frame;
    }

    boolean click(float x, float y, long now) {
        if (!this.presence.isShown() || !this.frame.contains(x, y)) {
            return false;
        }
        if (this.onAction != null && this.actionBox.contains(x, y)) {
            this.onAction.run();
        }
        this.dismiss(now);
        return true;
    }
}
