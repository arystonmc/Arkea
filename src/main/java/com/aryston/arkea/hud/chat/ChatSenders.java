package com.aryston.arkea.hud.chat;

import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Hsv;
import com.aryston.arkea.ui.theme.Theme;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.Nullable;

public final class ChatSenders {
    private static final String CHAT_KEY = "chat.type.text";
    private static final int CHAT_ARGUMENTS = 2;
    private static final int HUE_STEPS = 360;
    private static final float NAME_SATURATION = 0.5F;
    private static final float NAME_VALUE = 1.0F;
    private static final int RGB_MASK = 0xFFFFFF;
    private static final int SEPARATOR_COLOR = ArkColors.TEXT_LABEL & RGB_MASK;
    private static final String SEPARATOR = " » ";
    private static final int NAME_SEARCH_LIMIT = 48;
    private static final String NAME_ENDS = ":>»";

    private ChatSenders() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, ChatSenders::onChat);
    }

    private static void onChat(ClientChatReceivedEvent event) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) {
            return;
        }
        if (event instanceof ClientChatReceivedEvent.System system) {
            PlayerInfo guessed = system.isOverlay() ? null : guess(event.getMessage().getString(), connection);
            if (guessed != null) {
                ChatEntries.expect(event.getMessage(), sender(guessed.getProfile().id()));
            }
            return;
        }
        UUID id = senderId(event, connection);
        if (id == null) {
            return;
        }
        ChatSender sender = sender(id);
        Component message = ArkChat.active() ? restyle(event.getMessage(), event.getBoundChatType(), sender) : event.getMessage();
        event.setMessage(message);
        ChatEntries.expect(message, sender);
    }

    private static @Nullable UUID senderId(ClientChatReceivedEvent event, ClientPacketListener connection) {
        if (!event.isSystem()) {
            return event.getSender();
        }
        ChatType.Bound bound = event.getBoundChatType();
        PlayerInfo info = bound != null ? connection.getPlayerInfo(bound.name().getString()) : null;
        return info != null ? info.getProfile().id() : null;
    }

    private static ChatSender sender(UUID id) {
        Minecraft minecraft = Minecraft.getInstance();
        boolean self = minecraft.player != null && minecraft.player.getUUID().equals(id);
        int color = self ? Theme.accent().light() : new Hsv(Math.floorMod(id.hashCode(), HUE_STEPS) / (float) HUE_STEPS, NAME_SATURATION, NAME_VALUE).toRgb();
        return new ChatSender(id, color & RGB_MASK);
    }

    private static Component restyle(Component message, ChatType.@Nullable Bound bound, ChatSender sender) {
        if (bound == null || !bound.chatType().is(ChatType.CHAT) || !message.getSiblings().isEmpty()
            || !(message.getContents() instanceof TranslatableContents translatable) || !CHAT_KEY.equals(translatable.getKey())
            || translatable.getArgs().length != CHAT_ARGUMENTS) {
            return message;
        }
        Object[] arguments = translatable.getArgs();
        TextColor color = TextColor.fromRgb(sender.color());
        MutableComponent name = component(arguments[0]).copy().withStyle(style -> style.getColor() == null ? style.withColor(color) : style);
        return Component.empty().setStyle(message.getStyle()).append(name)
            .append(Component.literal(SEPARATOR).withColor(SEPARATOR_COLOR)).append(component(arguments[1]));
    }

    private static Component component(Object argument) {
        return argument instanceof Component component ? component : Component.literal(String.valueOf(argument));
    }

    private static @Nullable PlayerInfo guess(String text, ClientPacketListener connection) {
        int end = nameEnd(text);
        if (end < 0) {
            return null;
        }
        String prefix = text.substring(0, end);
        PlayerInfo best = null;
        for (PlayerInfo info : connection.getOnlinePlayers()) {
            String name = info.getProfile().name();
            if (containsWord(prefix, name) && (best == null || name.length() > best.getProfile().name().length())) {
                best = info;
            }
        }
        return best;
    }

    private static int nameEnd(String text) {
        int limit = Math.min(text.length(), NAME_SEARCH_LIMIT);
        for (int index = 0; index < limit; index++) {
            if (NAME_ENDS.indexOf(text.charAt(index)) >= 0) {
                return index;
            }
        }
        return -1;
    }

    private static boolean containsWord(String text, String word) {
        int from = text.indexOf(word);
        while (from >= 0) {
            int after = from + word.length();
            if ((from == 0 || !nameCharacter(text.charAt(from - 1))) && (after >= text.length() || !nameCharacter(text.charAt(after)))) {
                return true;
            }
            from = text.indexOf(word, from + 1);
        }
        return false;
    }

    private static boolean nameCharacter(char character) {
        return Character.isLetterOrDigit(character) || character == '_';
    }
}
