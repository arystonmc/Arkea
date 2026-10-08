package com.aryston.arkea.hud.chat;

import com.aryston.arkea.hud.HudClock;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.network.chat.Component;

public final class ChatEntries {
    private static final int FRESH_TICKS = 2;
    private static final int RECENT_LIMIT = 8;
    private static final Map<GuiMessage, ChatEntry> ENTRIES = new WeakHashMap<>();
    private static final Map<Component, ChatSender> PENDING = new WeakHashMap<>();
    private static final Deque<ChatEntry> RECENT = new ArrayDeque<>();

    private ChatEntries() {
    }

    static void expect(Component message, ChatSender sender) {
        PENDING.put(message, sender);
    }

    public static ChatEntry entry(GuiMessage message) {
        return ENTRIES.computeIfAbsent(message, ChatEntries::create);
    }

    static float risingLines(long now) {
        float lines = 0.0F;
        for (ChatEntry entry : RECENT) {
            lines += (1.0F - entry.appear(now)) * entry.lineCount();
        }
        return lines;
    }

    private static ChatEntry create(GuiMessage message) {
        boolean fresh = Minecraft.getInstance().gui.hud.getGuiTicks() - message.addedTime() <= FRESH_TICKS;
        ChatEntry entry = new ChatEntry(PENDING.remove(message.content()), fresh ? HudClock.now() : ChatEntry.SETTLED);
        if (fresh) {
            RECENT.addFirst(entry);
            while (RECENT.size() > RECENT_LIMIT) {
                RECENT.removeLast();
            }
        }
        return entry;
    }
}
