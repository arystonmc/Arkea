package com.aryston.arkea.debug;

import com.aryston.arkea.ui.overlay.ArkToasts;
import com.aryston.arkea.ui.overlay.ToastTone;
import net.minecraft.network.chat.Component;

final class UiCheckToasts {
    private static final float SAMPLE_PROGRESS = 0.4F;

    private UiCheckToasts() {
    }

    static void show() {
        ArkToasts.show(ToastTone.WARNING, Component.literal("Experimental mode can crash your game"));
        ArkToasts.show(ToastTone.SUCCESS, Component.literal("Rendering tab reset"), Component.literal("Undo"), () -> { });
        ArkToasts.progress(Component.literal("Exporting profile...")).progress(SAMPLE_PROGRESS);
    }
}
