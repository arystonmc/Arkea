package com.aryston.arkea.screen.config;

import com.aryston.arkea.api.config.ConfigOption;
import com.aryston.arkea.api.config.OptionKind;
import com.aryston.arkea.ui.overlay.ColorPickerPopup;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;

final class ConfigClipboard {
    private static final String HEX_PREFIX = "#";
    private static final int HEX_DIGITS = 6;
    private static final int HEX_RADIX = 16;
    private static final String SEPARATOR = ", ";

    private ConfigClipboard() {
    }

    static <T> void copy(ConfigSession session, ConfigOption<T> option) {
        Minecraft.getInstance().keyboardHandler.setClipboard(text(option, session.value(option)));
    }

    static <T> boolean canPaste(ConfigOption<T> option) {
        return parse(option, clipboard()).isPresent();
    }

    static <T> void paste(ConfigSession session, ConfigOption<T> option) {
        parse(option, clipboard()).ifPresent(value -> session.stage(option, value));
    }

    private static String clipboard() {
        return Minecraft.getInstance().keyboardHandler.getClipboard().trim();
    }

    @SuppressWarnings("unchecked")
    private static <T> String text(ConfigOption<T> option, T value) {
        return switch (option.kind()) {
            case OptionKind.Color _ -> HEX_PREFIX + ColorPickerPopup.hex((Integer) value);
            case OptionKind.Choice<?> choice -> ((OptionKind.Choice<T>) choice).label().apply(value).getString();
            case OptionKind.Multi<?> multi -> ((List<Object>) value).stream()
                .map(element -> ((OptionKind.Multi<Object>) multi).label().apply(element).getString())
                .collect(Collectors.joining(SEPARATOR));
            default -> String.valueOf(value);
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> Optional<T> parse(ConfigOption<T> option, String text) {
        if (text.isEmpty()) {
            return Optional.empty();
        }
        Object parsed = switch (option.kind()) {
            case OptionKind.Toggle _ -> parseBoolean(text);
            case OptionKind.Choice<?> choice -> parseChoice((OptionKind.Choice<Object>) choice, text);
            case OptionKind.IntRange range -> parseInt(text, range);
            case OptionKind.DoubleRange range -> parseDouble(text, range);
            case OptionKind.Text field -> text.length() <= field.maxLength() && field.validator().test(text) ? text : null;
            case OptionKind.Color _ -> parseColor(text);
            default -> null;
        };
        return Optional.ofNullable((T) parsed);
    }

    private static Boolean parseBoolean(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        if (lower.equals(Boolean.TRUE.toString())) {
            return true;
        }
        return lower.equals(Boolean.FALSE.toString()) ? false : null;
    }

    private static Object parseChoice(OptionKind.Choice<Object> choice, String text) {
        for (Object value : choice.values()) {
            if (choice.label().apply(value).getString().equalsIgnoreCase(text) || String.valueOf(value).equalsIgnoreCase(text)) {
                return value;
            }
        }
        return null;
    }

    private static Integer parseInt(String text, OptionKind.IntRange range) {
        try {
            int value = Integer.parseInt(text);
            return value >= range.min() && value <= range.max() ? value : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static Double parseDouble(String text, OptionKind.DoubleRange range) {
        try {
            double value = Double.parseDouble(text);
            return value >= range.min() && value <= range.max() ? value : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static Integer parseColor(String text) {
        String digits = text.startsWith(HEX_PREFIX) ? text.substring(HEX_PREFIX.length()) : text;
        if (digits.length() != HEX_DIGITS || !digits.chars().allMatch(character -> Character.digit(character, HEX_RADIX) >= 0)) {
            return null;
        }
        return HexFormat.fromHexDigits(digits);
    }
}
