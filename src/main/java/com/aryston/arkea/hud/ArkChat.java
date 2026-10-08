package com.aryston.arkea.hud;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix3x2f;

public final class ArkChat {
    private static final float SCALE = 0.85F;
    private static final float WIDTH_SHARE = 0.42F;
    private static final int MIN_WIDTH = 180;
    private static final int ARKEA_HUD_BOTTOM = 48;
    private static final float INSET = 4.0F;
    private static final int PLATE_RGB = 0x0C0C0E;
    private static final float PLATE_SHARE = 0.85F;
    private static final float ACCENT_SHARE = 0.7F;
    private static final int ACCENT_WIDTH = 1;
    private static final int RGB_MASK = 0xFFFFFF;

    private ArkChat() {
    }

    public static boolean active() {
        return ArkeaConfig.on(ArkeaConfig.CHAT);
    }

    public static double scale(double vanilla) {
        return active() ? vanilla * SCALE : vanilla;
    }

    public static int width(int vanilla) {
        if (!active()) {
            return vanilla;
        }
        int screen = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        return Math.min(vanilla, Math.max(MIN_WIDTH, Math.round(screen * WIDTH_SHARE)));
    }

    public static int bottom(int vanilla) {
        return active() && HudSettings.current().style().arkea() ? Math.max(vanilla, ARKEA_HUD_BOTTOM) : vanilla;
    }

    public static ChatComponent.ChatGraphicsAccess wrap(ChatComponent.ChatGraphicsAccess graphics) {
        return active() ? new Styled(graphics) : graphics;
    }

    private record Styled(ChatComponent.ChatGraphicsAccess graphics) implements ChatComponent.ChatGraphicsAccess {
        @Override
        public void updatePose(Consumer<Matrix3x2f> updater) {
            this.graphics.updatePose(pose -> {
                updater.accept(pose);
                pose.translate(INSET, 0.0F);
            });
        }

        @Override
        public void fill(int x0, int y0, int x1, int y1, int color) {
            if ((color & RGB_MASK) != 0) {
                this.graphics.fill(x0, y0, x1, y1, ArkColors.withAlpha(Theme.accent().light(), ArkColors.alpha(color)));
                return;
            }
            float alpha = ArkColors.alpha(color);
            this.graphics.fill(x0, y0, x1, y1, ArkColors.withAlpha(ArkColors.rgb(PLATE_RGB), alpha * PLATE_SHARE));
            this.graphics.fill(x0, y0, x0 + ACCENT_WIDTH, y1, ArkColors.withAlpha(Theme.accent().light(), alpha * ACCENT_SHARE));
        }

        @Override
        public boolean handleMessage(int textTop, float opacity, FormattedCharSequence message) {
            return this.graphics.handleMessage(textTop, opacity, message);
        }

        @Override
        public void handleTag(int x0, int y0, int x1, int y1, float opacity, GuiMessageTag tag) {
            this.graphics.handleTag(x0, y0, x1, y1, opacity, tag);
        }

        @Override
        public void handleTagIcon(int left, int bottom, boolean forceVisible, GuiMessageTag tag, GuiMessageTag.Icon icon) {
            this.graphics.handleTagIcon(left, bottom, forceVisible, tag, icon);
        }
    }
}
