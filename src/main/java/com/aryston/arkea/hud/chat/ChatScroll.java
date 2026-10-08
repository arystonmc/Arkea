package com.aryston.arkea.hud.chat;

import com.aryston.arkea.hud.HudClock;
import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Transition;

final class ChatScroll {
    private static final Transition OFFSET = new Transition(0.0F, Motion.CHAT_SCROLL, Easing.STANDARD);
    private static float offset;
    private static int below;
    private static int above;

    private ChatScroll() {
    }

    static void scrolled(int lines) {
        if (lines == 0) {
            return;
        }
        long now = HudClock.now();
        OFFSET.snap(OFFSET.value(now) + lines);
        OFFSET.setTarget(0.0F, now);
    }

    static void reset() {
        OFFSET.snap(0.0F);
    }

    static void frame(int position) {
        offset = OFFSET.value(HudClock.now());
        below = offset > 0.0F ? Math.min((int) Math.ceil(offset), position) : 0;
        above = offset < 0.0F ? (int) Math.ceil(-offset) : 0;
    }

    static boolean moving() {
        return offset != 0.0F;
    }

    static int firstLine(int position) {
        return position - below;
    }

    static int lineCount(int perPage) {
        return perPage + below + above;
    }

    static float shift(int entryHeight) {
        return (below - offset) * entryHeight;
    }
}
