package com.aryston.arkea.screen.options;

import com.aryston.arkea.screen.options.arkea.ArkArkeaScreen;
import com.aryston.arkea.screen.options.hud.ArkHudScreen;
import com.aryston.arkea.screen.options.keys.ArkKeyBindsScreen;
import com.aryston.arkea.screen.options.packs.ArkPacksScreen;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.widget.NavEntry;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jspecify.annotations.Nullable;

public enum OptionsPage {
    OVERVIEW(Group.GAME, Icons.SLIDERS, "options.title", (parent, minecraft) -> parent),
    VIDEO(Group.GAME, Icons.MONITOR, "options.video", (parent, minecraft) -> new ArkVideoScreen(parent)),
    CONTROLS(Group.GAME, Icons.MOUSE, "options.controls", (parent, minecraft) -> new ArkControlsScreen(parent)),
    KEY_BINDS(Group.GAME, Icons.KEYBOARD, "controls.keybinds", (parent, minecraft) -> new ArkKeyBindsScreen(parent)),
    SOUND(Group.GAME, Icons.SPEAKER, "options.sounds", (parent, minecraft) -> new ArkSoundScreen(parent)),
    SKIN(Group.PLAYER, Icons.SHIRT, "options.skinCustomisation", (parent, minecraft) -> new ArkSkinScreen(parent)),
    CHAT(Group.PLAYER, Icons.CHAT, "options.chat", (parent, minecraft) -> new ArkChatScreen(parent)),
    LANGUAGE(Group.PLAYER, Icons.GLOBE, "options.language", (parent, minecraft) -> new ArkLanguageScreen(parent)),
    ACCESSIBILITY(Group.PLAYER, Icons.ACCESS, "options.accessibility", (parent, minecraft) -> new ArkAccessibilityScreen(parent)),
    RESOURCE_PACKS(Group.CONTENT, Icons.PACK, "options.resourcepack", (parent, minecraft) -> new ArkPacksScreen(parent)),
    ARKEA(Group.CONTENT, Icons.ARKEA, "arkea.options.arkea", (parent, minecraft) -> new ArkArkeaScreen(parent)),
    HUD(Group.CONTENT, Icons.MONITOR, "arkea.options.hud", (parent, minecraft) -> new ArkHudScreen(parent)),
    HELION(Group.CONTENT, Icons.SPARKLE, "arkea.options.helion", OptionsPage::helionScreen);

    public static final Identifier ARKEA_LOGO = Identifier.fromNamespaceAndPath("arkea", "textures/gui/arkea_logo.png");
    public static final Identifier HELION_LOGO = Identifier.fromNamespaceAndPath("arkea", "textures/gui/helion_logo.png");
    private static final String HELION_ID = "helion";
    private static final String TRAILING_DOTS = "...";
    private static final String TRAILING_ELLIPSIS = "…";

    private final Group group;
    private final Icon icon;
    private final String titleKey;
    private final BiFunction<Screen, Minecraft, Screen> factory;

    OptionsPage(Group group, Icon icon, String titleKey, BiFunction<Screen, Minecraft, Screen> factory) {
        this.group = group;
        this.icon = icon;
        this.titleKey = titleKey;
        this.factory = factory;
    }

    public Group group() {
        return this.group;
    }

    public Icon icon() {
        return this.icon;
    }

    public String id() {
        return this.name();
    }

    public boolean isAvailable() {
        return this != HELION || helionConfig().isPresent();
    }

    public boolean isExternal() {
        return this == HELION;
    }

    public boolean isWindow() {
        return switch (this) {
            case OVERVIEW, VIDEO, CONTROLS, KEY_BINDS, SOUND, SKIN, CHAT, LANGUAGE, ACCESSIBILITY, RESOURCE_PACKS, ARKEA, HUD -> true;
            case HELION -> false;
        };
    }

    public Component title() {
        return withoutEllipsis(Component.translatable(this.titleKey));
    }

    public Component navLabel() {
        return Component.translatable("arkea.options.nav." + this.key());
    }

    public Component description() {
        return Component.translatable("arkea.options.description." + this.key());
    }

    public NavEntry navEntry() {
        return new NavEntry(this.id(), this.icon, this.logo(), this.navLabel(), this.isExternal());
    }

    public @Nullable Identifier logo() {
        return switch (this) {
            case ARKEA -> ARKEA_LOGO;
            case HELION -> HELION_LOGO;
            default -> null;
        };
    }

    public Screen open(Screen parent, Minecraft minecraft) {
        return this.factory.apply(parent, minecraft);
    }

    String key() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    static Component withoutEllipsis(Component text) {
        String value = text.getString();
        if (value.endsWith(TRAILING_DOTS)) {
            return Component.literal(value.substring(0, value.length() - TRAILING_DOTS.length()));
        }
        if (value.endsWith(TRAILING_ELLIPSIS)) {
            return Component.literal(value.substring(0, value.length() - TRAILING_ELLIPSIS.length()));
        }
        return text;
    }


    private static Screen helionScreen(Screen parent, Minecraft minecraft) {
        return helionConfig()
            .map(config -> config.factory().createScreen(config.container(), parent))
            .orElse(parent);
    }

    private static Optional<HelionConfig> helionConfig() {
        return ModList.get().getModContainerById(HELION_ID)
            .flatMap(container -> container.getCustomExtension(IConfigScreenFactory.class).map(factory -> new HelionConfig(container, factory)));
    }

    public enum Group {
        GAME,
        PLAYER,
        CONTENT;

        public Component label() {
            return Component.translatable("arkea.options.group." + this.name().toLowerCase(Locale.ROOT));
        }
    }

    private record HelionConfig(ModContainer container, IConfigScreenFactory factory) {
    }
}
