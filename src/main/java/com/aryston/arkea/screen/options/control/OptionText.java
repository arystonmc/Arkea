package com.aryston.arkea.screen.options.control;

import com.aryston.arkea.mixin.OptionInstanceAccessor;
import com.aryston.arkea.mixin.TooltipAccessor;
import java.util.Set;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.jspecify.annotations.Nullable;

public final class OptionText {
    private static final String GENERIC = "options.generic_value";
    private static final Set<String> NAMED = Set.of(GENERIC, "options.percent_value", "options.percent_add_value", "options.pixel_value");
    private static final String VANILLA_PREFIX = "options.";
    private static final String DESCRIPTION_PREFIX = "arkea.option.";
    private static final String NAME_SEPARATOR = ": ";

    private OptionText() {
    }

    public static <T> Component value(OptionInstance<T> option, T value) {
        return value(option.caption, option.toString.apply(value));
    }

    public static Component value(Component caption, Component label) {
        if (label.getContents() instanceof TranslatableContents translatable && translatable.getArgs().length == 2) {
            String key = translatable.getKey();
            Object argument = translatable.getArgs()[1];
            if (key.equals(GENERIC)) {
                return argument instanceof Component component ? component : Component.literal(String.valueOf(argument));
            }
            if (NAMED.contains(key)) {
                return Component.translatable("arkea.value." + key.substring(VANILLA_PREFIX.length()), argument);
            }
            if (key.equals("options.on.composed")) {
                return CommonComponents.OPTION_ON;
            }
            if (key.equals("options.off.composed")) {
                return CommonComponents.OPTION_OFF;
            }
        }
        String text = label.getString();
        String prefix = caption.getString() + NAME_SEPARATOR;
        if (text.startsWith(prefix)) {
            return Component.literal(text.substring(prefix.length()));
        }
        int separator = text.indexOf(NAME_SEPARATOR);
        if (label.getContents() instanceof TranslatableContents translatable && translatable.getKey().startsWith(VANILLA_PREFIX) && separator > 0) {
            return Component.literal(text.substring(separator + NAME_SEPARATOR.length()));
        }
        return label;
    }

    public static boolean isOnOff(OptionInstance<Boolean> option) {
        return value(option, Boolean.TRUE).getString().equals(CommonComponents.OPTION_ON.getString())
            && value(option, Boolean.FALSE).getString().equals(CommonComponents.OPTION_OFF.getString());
    }

    public static Component name(Component caption) {
        String key = descriptionKey(caption);
        if (key != null && Language.getInstance().has(key + ".name")) {
            return Component.translatable(key + ".name");
        }
        return caption;
    }

    private static @Nullable String descriptionKey(Component caption) {
        if (caption.getContents() instanceof TranslatableContents translatable) {
            String key = translatable.getKey();
            return DESCRIPTION_PREFIX + (key.startsWith(VANILLA_PREFIX) ? key.substring(VANILLA_PREFIX.length()) : key);
        }
        return null;
    }

    public static <T> @Nullable Component tooltip(OptionInstance<T> option) {
        @SuppressWarnings("unchecked")
        OptionInstance.TooltipSupplier<T> supplier = (OptionInstance.TooltipSupplier<T>) ((OptionInstanceAccessor) (Object) option).arkea$tooltip();
        Tooltip tooltip = supplier.apply(option.get());
        return tooltip == null ? null : ((TooltipAccessor) (Object) tooltip).arkea$message();
    }

    public static Component description(Component caption) {
        String key = descriptionKey(caption);
        return key != null && Language.getInstance().has(key) ? Component.translatable(key) : Component.empty();
    }

    public static boolean isOff(Component value) {
        return value.getString().equals(CommonComponents.OPTION_OFF.getString());
    }
}
