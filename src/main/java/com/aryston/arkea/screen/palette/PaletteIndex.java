package com.aryston.arkea.screen.palette;

import com.aryston.arkea.Arkea;
import com.aryston.arkea.background.BackgroundLibrary;
import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.screen.loading.JoinTarget;
import com.aryston.arkea.screen.options.OptionsPage;
import com.aryston.arkea.screen.options.OptionsPageScreen;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.theme.Accent;
import com.mojang.blaze3d.Blaze3D;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.modlist.ModListScreen;

final class PaletteIndex {
    private static final int MAX_WORLDS = 12;

    private PaletteIndex() {
    }

    static List<PaletteEntry> entries(Minecraft minecraft) {
        List<PaletteEntry> entries = new ArrayList<>();
        boolean inMenus = minecraft.level == null;
        if (inMenus) {
            entries.add(action(Icons.USER, "menu.singleplayer", origin -> minecraft.gui.setScreen(new SelectWorldScreen(origin))));
            entries.add(action(Icons.PLUS, "selectWorld.create", origin -> CreateWorldScreen.openFresh(minecraft, () -> minecraft.gui.setScreen(origin))));
            entries.add(action(Icons.GLOBE, "menu.multiplayer", origin -> minecraft.gui.setScreen(new JoinMultiplayerScreen(origin))));
        }
        entries.add(action(Icons.PACK, "arkea.mods.title", origin -> minecraft.gui.setScreen(ModListScreen.create(origin))));
        entries.add(action(Icons.SLIDERS, "options.title", origin -> minecraft.gui.setScreen(new OptionsScreen(origin, minecraft.options))));
        entries.add(folder(Icons.FOLDER, "arkea.palette.folder.packs", minecraft.getResourcePackDirectory()));
        entries.add(folder(Icons.FOLDER, "arkea.palette.folder.mods", FMLPaths.MODSDIR.get()));
        entries.add(folder(Icons.FOLDER, "arkea.palette.folder.saves", minecraft.getLevelSource().getBaseDir()));
        entries.add(folder(Icons.FOLDER, "arkea.palette.folder.screenshots", minecraft.gameDirectory.toPath().resolve(Screenshot.SCREENSHOT_DIR)));
        entries.add(folder(Icons.FOLDER, "arkea.palette.folder.backgrounds", BackgroundLibrary.get().root()));
        for (Accent accent : Accent.values()) {
            Component name = Component.translatable("arkea.accent." + accent.name().toLowerCase(Locale.ROOT));
            entries.add(new PaletteEntry(PaletteEntry.Group.ACTIONS, Icons.SPARKLE, Component.translatable("arkea.palette.accent", name),
                Component.translatable("arkea.palette.accent.detail"), origin -> ArkeaConfig.set(ArkeaConfig.ACCENT, accent)));
        }
        for (OptionsPage page : OptionsPage.values()) {
            if (page.isAvailable() && page != OptionsPage.OVERVIEW) {
                entries.add(new PaletteEntry(PaletteEntry.Group.PAGES, page.icon(), page.title(), page.description(),
                    origin -> minecraft.gui.setScreen(page.open(origin, minecraft))));
            }
        }
        for (SettingsIndex.Setting setting : SettingsIndex.get(minecraft)) {
            Component detail = Component.translatable("arkea.palette.setting.detail", setting.page().title(), setting.description());
            entries.add(new PaletteEntry(PaletteEntry.Group.SETTINGS, setting.icon(), setting.name(), detail, origin -> {
                Screen screen = setting.page().open(origin, minecraft);
                if (screen instanceof OptionsPageScreen page) {
                    page.reveal(setting.name());
                }
                minecraft.gui.setScreen(screen);
            }));
        }
        if (inMenus) {
            entries.addAll(servers(minecraft));
        }
        return entries;
    }

    static CompletableFuture<List<PaletteEntry>> worlds(Minecraft minecraft) {
        if (minecraft.level != null) {
            return CompletableFuture.completedFuture(List.of());
        }
        try {
            return minecraft.getLevelSource().loadLevelSummaries(minecraft.getLevelSource().findLevelCandidates())
                .thenApply(summaries -> summaries.stream()
                    .filter(LevelSummary::primaryActionActive)
                    .sorted(Comparator.comparingLong(LevelSummary::getLastPlayed).reversed())
                    .limit(MAX_WORLDS)
                    .map(summary -> new PaletteEntry(PaletteEntry.Group.WORLDS, Icons.WORLD, Component.literal(summary.getLevelName()),
                        Component.translatable("arkea.palette.world.detail", summary.getLevelId()), origin -> {
                            JoinTarget.set(summary.getLevelName());
                            minecraft.createWorldOpenFlows().openWorld(summary.getLevelId(), () -> minecraft.gui.setScreen(origin));
                        }))
                    .toList());
        } catch (LevelStorageException exception) {
            Arkea.LOGGER.warn("Could not list worlds for the command palette", exception);
            return CompletableFuture.completedFuture(List.of());
        }
    }

    private static List<PaletteEntry> servers(Minecraft minecraft) {
        ServerList list = new ServerList(minecraft);
        list.load();
        List<PaletteEntry> entries = new ArrayList<>();
        for (int index = 0; index < list.size(); index++) {
            ServerData data = list.get(index);
            entries.add(new PaletteEntry(PaletteEntry.Group.SERVERS, Icons.SERVER, Component.literal(data.name),
                Component.literal(minecraft.options.hideServerAddress ? "" : data.ip), origin -> {
                    JoinTarget.server(data);
                    ConnectScreen.startConnecting(origin, minecraft, ServerAddress.parseString(data.ip), data, false, null);
                }));
        }
        return entries;
    }

    private static PaletteEntry action(Icon icon, String key, Consumer<Screen> run) {
        return new PaletteEntry(PaletteEntry.Group.ACTIONS, icon, Component.translatable(key), Component.translatable("arkea.palette.open"), run);
    }

    private static PaletteEntry folder(Icon icon, String key, Path path) {
        Component detail = Component.literal(path.toAbsolutePath().normalize().toString());
        return new PaletteEntry(PaletteEntry.Group.ACTIONS, icon, Component.translatable(key), detail, origin -> {
            if (Files.isDirectory(path)) {
                Blaze3D.openPath(path);
            }
        });
    }
}
