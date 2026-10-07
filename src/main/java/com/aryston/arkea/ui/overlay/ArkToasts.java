package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public final class ArkToasts {
    private static final int MAX_SHOWN = 3;
    private static final float HEIGHT = 40.0F;
    private static final float PROGRESS_HEIGHT = 2.0F;
    private static final float TONE_COLUMN = 38.0F;
    private static final float ICON_SIZE = 14.0F;
    private static final float GAP = 12.0F;
    private static final float PADDING_RIGHT = 16.0F;
    private static final float ACTION_PADDING_RIGHT = 6.0F;
    private static final float ACTION_HEIGHT = 28.0F;
    private static final float ACTION_PADDING = 10.0F;
    private static final float MIN_WIDTH = 240.0F;
    private static final float MAX_WIDTH = 520.0F;
    private static final float BOTTOM = 48.0F;
    private static final float STACK_GAP = 8.0F;
    private static final float ENTER_SLIDE = 16.0F;
    private static final float EXIT_SLIDE = 12.0F;
    private static final float ENTER_SCALE = 0.95F;
    private static final float SHADOW_BLUR = 30.0F;
    private static final float SHADOW_OFFSET = 10.0F;
    private static final float TONE_FILL = 0.14F;
    private static final float ACTION_FILL = 0.10F;
    private static final float BORDER_ALPHA = 0.55F;
    private static final int PERCENT = 100;
    private static final long BUSY_CYCLE_MS = 1400L;
    private static final float BUSY_SEGMENT = 0.3F;
    private static final int FILL = ArkColors.rgba(14, 14, 15, 0.96F);
    private static final int SHADOW = ArkColors.rgba(0, 0, 0, 0.5F);
    private static final int PROGRESS_TRACK = ArkColors.rgba(255, 255, 255, 0.06F);
    private static final TextStyle MESSAGE = TextStyle.of(12.0F);
    private static final TextStyle SMALL = TextStyle.of(11.0F);
    private static final List<MenuToast> TOASTS = new ArrayList<>();

    private ArkToasts() {
    }

    public static MenuToast show(ToastTone tone, Component message) {
        return add(new MenuToast(tone, message, null, null));
    }

    public static MenuToast show(ToastTone tone, Component message, Component action, Runnable onAction) {
        return add(new MenuToast(tone, message, action, onAction));
    }

    public static MenuToast progress(Component message) {
        MenuToast toast = new MenuToast(ToastTone.INFO, message, null, null);
        toast.progress(0.0F);
        return add(toast);
    }

    public static MenuToast busy(Component message) {
        MenuToast toast = new MenuToast(ToastTone.INFO, message, null, null);
        toast.busy();
        return add(toast);
    }

    private static MenuToast add(MenuToast toast) {
        long now = Util.getMillis();
        toast.show(now);
        TOASTS.add(toast);
        List<MenuToast> shown = TOASTS.stream().filter(MenuToast::isShown).toList();
        for (int index = 0; index < shown.size() - MAX_SHOWN; index++) {
            shown.get(index).dismiss(now);
        }
        return toast;
    }

    public static boolean click(float x, float y) {
        long now = Util.getMillis();
        for (MenuToast toast : TOASTS) {
            if (toast.click(x, y, now)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(float x, float y) {
        return TOASTS.stream().anyMatch(toast -> toast.isShown() && toast.frame().contains(x, y));
    }

    public static void render(UiGraphics graphics) {
        long now = graphics.now();
        TOASTS.removeIf(toast -> toast.isGone(now));
        for (MenuToast toast : TOASTS) {
            if (toast.expired(now)) {
                toast.dismiss(now);
            }
        }
        UiScale scale = graphics.scale();
        float stack = 0.0F;
        for (int index = TOASTS.size() - 1; index >= 0; index--) {
            MenuToast toast = TOASTS.get(index);
            toast.lift().setTarget(stack, now);
            if (toast.isShown()) {
                stack += HEIGHT + STACK_GAP;
            }
            float width = width(graphics.metrics(), toast);
            float y = scale.canvasHeight() - BOTTOM - HEIGHT - toast.lift().value(now);
            Box frame = new Box((scale.canvasWidth() - width) * 0.5F, y, width, HEIGHT);
            render(graphics, toast, frame, now);
        }
    }

    private static float width(TextMetrics metrics, MenuToast toast) {
        float width = TONE_COLUMN + GAP + metrics.width(toast.message().getString(), MESSAGE) + PADDING_RIGHT;
        Component action = toast.action();
        if (action != null) {
            width += GAP + metrics.width(action.getString(), SMALL) + ACTION_PADDING * 2.0F - PADDING_RIGHT + ACTION_PADDING_RIGHT;
        } else if (toast.hasProgress() && !toast.isBusy()) {
            width += GAP + metrics.width(percent(1.0F), SMALL);
        }
        return Math.clamp(width, MIN_WIDTH, MAX_WIDTH);
    }

    private static void render(UiGraphics graphics, MenuToast toast, Box frame, long now) {
        float visible = toast.visibility(now);
        if (visible <= 0.0F) {
            toast.layout(Box.EMPTY, Box.EMPTY);
            return;
        }
        int tone = toast.tone().color();
        TextMetrics metrics = graphics.metrics();
        graphics.push();
        graphics.fade(visible);
        float slide = toast.isShown() ? ENTER_SLIDE : EXIT_SLIDE;
        graphics.translate(0.0F, slide * (1.0F - visible));
        if (toast.isShown()) {
            graphics.scaleAround(ENTER_SCALE + (1.0F - ENTER_SCALE) * visible, frame.centerX(), frame.centerY());
        }
        graphics.shadow(frame, SHADOW_BLUR, SHADOW_OFFSET, SHADOW);
        graphics.fill(frame, FILL);
        graphics.border(frame, 1.0F, ArkColors.withAlpha(tone, BORDER_ALPHA));
        float rowHeight = toast.hasProgress() ? HEIGHT - PROGRESS_HEIGHT : HEIGHT;
        Box column = new Box(frame.x(), frame.y(), TONE_COLUMN, rowHeight);
        graphics.fill(column, ArkColors.withAlpha(tone, TONE_FILL));
        graphics.icon(toast.tone().icon(), column.centerX() - ICON_SIZE * 0.5F, column.centerY() - ICON_SIZE * 0.5F, ICON_SIZE, ICON_SIZE, tone);
        float centerY = frame.y() + rowHeight * 0.5F;
        float textX = column.right() + GAP;
        float right = frame.right() - PADDING_RIGHT;
        Box actionBox = Box.EMPTY;
        Component action = toast.action();
        if (action != null) {
            float actionWidth = metrics.width(action.getString(), SMALL) + ACTION_PADDING * 2.0F;
            actionBox = new Box(frame.right() - ACTION_PADDING_RIGHT - actionWidth, centerY - ACTION_HEIGHT * 0.5F, actionWidth, ACTION_HEIGHT);
            graphics.fill(actionBox, ArkColors.withAlpha(tone, ACTION_FILL));
            graphics.text(action.getString(), actionBox.x() + ACTION_PADDING, centerY - metrics.capHeight(SMALL) * 0.5F, SMALL, tone);
            right = actionBox.x() - GAP;
        } else if (toast.hasProgress()) {
            float barY = frame.bottom() - PROGRESS_HEIGHT;
            graphics.fill(frame.x(), barY, frame.width(), PROGRESS_HEIGHT, PROGRESS_TRACK);
            if (toast.isBusy()) {
                float phase = (now % BUSY_CYCLE_MS) / (float) BUSY_CYCLE_MS;
                float segment = frame.width() * BUSY_SEGMENT;
                float start = frame.x() - segment + (frame.width() + segment) * phase;
                float left = Math.max(frame.x(), start);
                graphics.fill(left, barY, Math.min(frame.right(), start + segment) - left, PROGRESS_HEIGHT, tone);
            } else {
                String value = percent(toast.progressValue());
                float valueWidth = metrics.width(value, SMALL);
                graphics.text(value, right - valueWidth, centerY - metrics.capHeight(SMALL) * 0.5F, SMALL, tone);
                right -= valueWidth + GAP;
                graphics.fill(frame.x(), barY, frame.width() * toast.progressValue(), PROGRESS_HEIGHT, tone);
            }
        }
        String message = metrics.ellipsize(toast.message().getString(), MESSAGE, right - textX);
        graphics.text(message, textX, centerY - metrics.capHeight(MESSAGE) * 0.5F, MESSAGE, ArkColors.TEXT_PRIMARY);
        graphics.pop();
        toast.layout(graphics.canvasBox(frame), graphics.canvasBox(actionBox));
    }

    private static String percent(float value) {
        return String.format(Locale.ROOT, "%d%%", Math.round(value * PERCENT));
    }
}
