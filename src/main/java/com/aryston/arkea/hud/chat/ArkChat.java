package com.aryston.arkea.hud.chat;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.hud.HudSettings;
import com.mojang.blaze3d.platform.Window;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;

public final class ArkChat {
    private static final float SCALE = 0.85F;
    private static final float WIDTH_SHARE = 0.42F;
    private static final int MIN_WIDTH = 180;
    private static final float HUD_CLEARANCE = 108.0F;
    private static @Nullable GuiGraphicsExtractor drawing;
    private static GuiMessage.@Nullable Line line;

    private ArkChat() {
    }

    public static boolean active() {
        return ArkeaConfig.on(ArkeaConfig.CHAT);
    }

    public static double scale(double vanilla) {
        if (!active()) {
            return vanilla;
        }
        int guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        long pixels = Math.max(1L, Math.round(vanilla * SCALE * guiScale));
        return (double) pixels / guiScale;
    }

    public static int width(int vanilla) {
        if (!active()) {
            return vanilla;
        }
        int screen = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        return Math.min(vanilla, Math.max(MIN_WIDTH, Math.round(screen * WIDTH_SHARE)));
    }

    public static int bottom(int vanilla) {
        HudSettings settings = HudSettings.current();
        if (!active() || !settings.style().arkea()) {
            return vanilla;
        }
        Window window = Minecraft.getInstance().getWindow();
        return Math.max(vanilla, (int) Math.ceil(settings.scale(window).toGui(HUD_CLEARANCE)));
    }

    public static List<FormattedCharSequence> split(GuiMessage message, Font font, int maxWidth) {
        ChatEntry entry = ChatEntries.entry(message);
        boolean indented = active() && entry.sender() != null;
        List<FormattedCharSequence> lines = message.splitLines(font, indented ? maxWidth - ChatStyle.INDENT : maxWidth);
        entry.lines(lines);
        return lines;
    }

    public static void begin(GuiGraphicsExtractor graphics) {
        drawing = graphics;
    }

    public static void end() {
        drawing = null;
    }

    public static GuiMessage.Line enter(GuiMessage.Line current) {
        line = current;
        return current;
    }

    public static void leave() {
        line = null;
    }

    static GuiMessage.@Nullable Line line() {
        return line;
    }

    public static ChatComponent.ChatGraphicsAccess wrap(ChatComponent.ChatGraphicsAccess graphics) {
        return active() ? new ChatStyle(graphics, drawing) : graphics;
    }
}
