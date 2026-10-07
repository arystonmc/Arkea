package com.aryston.arkea.screen.mods;

import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.VersionChecker;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.modlist.DefaultModDisplayInfo;
import net.neoforged.neoforge.client.gui.modlist.ImageResource;
import net.neoforged.neoforge.client.gui.modlist.ModDisplayInfo;
import org.jspecify.annotations.Nullable;

record ModEntry(ModContainer container, ModDisplayInfo info) {
    private static final String MOJANG = "Mojang Studios";
    private static final String VANILLA_PACK = "vanilla";
    private static final String PACK_ICON = "pack.png";

    static ModEntry of(ModContainer container) {
        ModDisplayInfo info = container.getCustomExtension(ModDisplayInfo.class).orElseGet(() -> new DefaultModDisplayInfo(container));
        return new ModEntry(container, info);
    }

    static List<ModEntry> all() {
        return ModList.get().getSortedMods().stream().map(ModEntry::of).toList();
    }

    String id() {
        return this.info.id();
    }

    boolean isGame() {
        return Identifier.DEFAULT_NAMESPACE.equals(this.id());
    }

    Component authors() {
        Component authors = this.info.authors();
        return authors.getString().isEmpty() && this.isGame() ? Component.literal(MOJANG) : authors;
    }

    @Nullable ImageResource icon() {
        if (this.isGame()) {
            return ImageResource.packRoot(VANILLA_PACK, PACK_ICON);
        }
        ImageResource icon = this.info.icon();
        return icon != null ? icon : this.info.banner();
    }

    Optional<IConfigScreenFactory> configFactory() {
        return this.container.getCustomExtension(IConfigScreenFactory.class);
    }

    @Nullable Screen configScreen(Screen parent) {
        return this.configFactory().map(factory -> factory.createScreen(this.container, parent)).orElse(null);
    }

    boolean hasUpdate() {
        VersionChecker.Status status = VersionChecker.getResult(this.container.getModInfo()).status();
        return status == VersionChecker.Status.OUTDATED || status == VersionChecker.Status.BETA_OUTDATED;
    }

    @Nullable String latestVersion() {
        VersionChecker.CheckResult result = VersionChecker.getResult(this.container.getModInfo());
        return result.target() != null ? result.target().toString() : null;
    }
}
