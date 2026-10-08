package com.aryston.arkea.api.config;

import com.aryston.arkea.ui.render.Icons;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ModConfigScreens {
    private static final String GENERAL_PAGE = "general";
    private static final String PAGE_SEPARATOR = ".";
    private static final List<ModConfig.Type> TYPE_ORDER = List.of(ModConfig.Type.CLIENT, ModConfig.Type.LOCAL, ModConfig.Type.STARTUP,
        ModConfig.Type.SYNCED);

    private ModConfigScreens() {
    }

    public static IConfigScreenFactory factory() {
        return ModConfigScreens::forMod;
    }

    public static boolean hasEditableConfig(ModContainer container) {
        return ModConfigs.getModConfigs(container.getModId()).stream().anyMatch(config -> config.getSpec() instanceof ModConfigSpec);
    }

    public static Screen forMod(ModContainer container, Screen parent) {
        Component name = Component.literal(container.getModInfo().getDisplayName());
        ArkeaConfigScreen.Builder builder = ArkeaConfigScreen.builder(name)
            .brand(Icons.SLIDERS, name, Component.translatable("arkea.config.subtitle"));
        boolean empty = true;
        List<ModConfig> configs = new ArrayList<>(ModConfigs.getModConfigs(container.getModId()));
        configs.sort(Comparator.comparingInt(config -> TYPE_ORDER.indexOf(config.getType())));
        for (ModConfig config : configs) {
            if (!(config.getSpec() instanceof ModConfigSpec spec)) {
                continue;
            }
            builder.group(Component.translatable("arkea.config.type." + config.getType().name().toLowerCase(Locale.ROOT)));
            for (ConfigPage page : pages(container.getModId(), config.getType(), spec)) {
                builder.page(page);
                empty = false;
            }
        }
        if (empty) {
            builder.page(ArkeaConfigScreen.page(GENERAL_PAGE, Component.translatable("arkea.config.general"), Icons.SLIDERS)
                .notice(Component.translatable("arkea.config.empty")));
        }
        return builder.build(parent);
    }

    public static List<ConfigPage> pages(String modId, ModConfig.Type type, ModConfigSpec spec) {
        String prefix = type.name().toLowerCase(Locale.ROOT) + PAGE_SEPARATOR;
        if (!spec.isLoaded()) {
            ConfigPage page = ArkeaConfigScreen.page(prefix + GENERAL_PAGE, Component.translatable("arkea.config.general"), Icons.SLIDERS);
            return List.of(page.notice(Component.translatable("arkea.config.notLoaded")));
        }
        boolean editable = isEditable(type);
        List<ConfigPage> pages = new ArrayList<>();
        ConfigPage general = ArkeaConfigScreen.page(prefix + GENERAL_PAGE, Component.translatable("arkea.config.general"), Icons.SLIDERS);
        List<ConfigOption<?>> topLevel = new ArrayList<>();
        for (Map.Entry<String, Object> entry : spec.getValues().valueMap().entrySet()) {
            if (entry.getValue() instanceof ModConfigSpec.ConfigValue<?> value) {
                addOption(topLevel, modId, value);
            } else if (entry.getValue() instanceof UnmodifiableConfig section) {
                List<String> path = List.of(entry.getKey());
                ConfigPage page = ArkeaConfigScreen.page(prefix + entry.getKey(), sectionTitle(modId, spec, path), Icons.LAYERS);
                fillPage(page, modId, spec, path, section);
                pages.add(page);
            }
        }
        if (!topLevel.isEmpty()) {
            general.section(null, section -> topLevel.forEach(section::add));
            pages.addFirst(general);
        }
        if (!editable) {
            pages.replaceAll(page -> ArkeaConfigScreen.page(page.id(), page.title(), page.icon()).notice(Component.translatable("arkea.config.locked")));
        }
        return pages;
    }

    private static void fillPage(ConfigPage page, String modId, ModConfigSpec spec, List<String> path, UnmodifiableConfig values) {
        List<ConfigOption<?>> direct = new ArrayList<>();
        List<Nested> nested = new ArrayList<>();
        collect(modId, spec, path, values, direct, nested);
        if (!direct.isEmpty()) {
            page.section(null, section -> direct.forEach(section::add));
        }
        for (Nested group : nested) {
            page.section(group.title(), section -> group.options().forEach(section::add));
        }
    }

    private static void collect(String modId, ModConfigSpec spec, List<String> path, UnmodifiableConfig values, List<ConfigOption<?>> direct,
        List<Nested> nested) {
        for (Map.Entry<String, Object> entry : values.valueMap().entrySet()) {
            if (entry.getValue() instanceof ModConfigSpec.ConfigValue<?> value) {
                addOption(direct, modId, value);
            } else if (entry.getValue() instanceof UnmodifiableConfig section) {
                List<String> childPath = new ArrayList<>(path);
                childPath.add(entry.getKey());
                List<ConfigOption<?>> options = new ArrayList<>();
                List<Nested> deeper = new ArrayList<>();
                collect(modId, spec, childPath, section, options, deeper);
                nested.add(new Nested(sectionTitle(modId, spec, childPath), options));
                nested.addAll(deeper);
            }
        }
    }

    private static void addOption(List<ConfigOption<?>> target, String modId, ModConfigSpec.ConfigValue<?> value) {
        ConfigOption<?> option = SpecOptions.of(modId, value);
        if (option != null) {
            target.add(option);
        }
    }

    private static Component sectionTitle(String modId, ModConfigSpec spec, List<String> path) {
        String key = spec.getLevelTranslationKey(path);
        String fallback = SpecOptions.pretty(path.getLast());
        return Component.translatableWithFallback(key != null ? key : modId + ".configuration." + String.join(".", path), fallback);
    }

    private static boolean isEditable(ModConfig.Type type) {
        if (type != ModConfig.Type.SYNCED) {
            return true;
        }
        Minecraft minecraft = Minecraft.getInstance();
        boolean remote = minecraft.getCurrentServer() != null && (!minecraft.hasSingleplayerServer() || !minecraft.getSingleplayerServer().isPublished());
        boolean openToLan = minecraft.hasSingleplayerServer() && minecraft.getSingleplayerServer().isPublished();
        return !remote && !openToLan;
    }

    private record Nested(Component title, List<ConfigOption<?>> options) {
    }
}
