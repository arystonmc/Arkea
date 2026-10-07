package com.aryston.arkea.screen.worlds;

import com.aryston.arkea.Arkea;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import org.jspecify.annotations.Nullable;

final class WorldLibrary implements AutoCloseable {
    private final Minecraft minecraft;
    private final Map<String, Optional<FaviconTexture>> icons = new HashMap<>();
    private final Map<String, CompletableFuture<WorldFacts>> facts = new HashMap<>();
    private @Nullable List<LevelSummary> worlds;
    private @Nullable Component error;
    private int version;

    WorldLibrary(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    void load() {
        this.worlds = null;
        this.error = null;
        this.facts.clear();
        this.version++;
        try {
            this.minecraft.getLevelSource().loadLevelSummaries(this.minecraft.getLevelSource().findLevelCandidates())
                .whenCompleteAsync((summaries, failure) -> {
                    if (failure != null) {
                        Arkea.LOGGER.warn("Could not read the world list", failure);
                        this.error = Component.translatable("arkea.worlds.error");
                        this.worlds = List.of();
                    } else {
                        this.worlds = List.copyOf(summaries);
                    }
                    this.version++;
                }, this.minecraft);
        } catch (LevelStorageException exception) {
            Arkea.LOGGER.warn("Could not list the worlds", exception);
            this.error = exception.getMessageComponent();
            this.worlds = List.of();
        }
    }

    boolean isLoading() {
        return this.worlds == null;
    }

    List<LevelSummary> worlds() {
        return this.worlds == null ? List.of() : this.worlds;
    }

    @Nullable Component error() {
        return this.error;
    }

    int version() {
        return this.version;
    }

    @Nullable Identifier icon(LevelSummary summary) {
        return this.icons.computeIfAbsent(summary.getLevelId(), id -> this.loadIcon(summary)).map(FaviconTexture::textureLocation).orElse(null);
    }

    private Optional<FaviconTexture> loadIcon(LevelSummary summary) {
        Path file = summary.getIcon();
        if (!Files.isRegularFile(file)) {
            return Optional.empty();
        }
        FaviconTexture texture = FaviconTexture.forWorld(this.minecraft.getTextureManager(), summary.getLevelId());
        try (InputStream stream = Files.newInputStream(file)) {
            texture.upload(NativeImage.read(stream));
            return Optional.of(texture);
        } catch (IOException | RuntimeException exception) {
            Arkea.LOGGER.warn("Could not load the icon of world {}", summary.getLevelId(), exception);
            texture.close();
            return Optional.empty();
        }
    }

    WorldFacts facts(LevelSummary summary) {
        CompletableFuture<WorldFacts> future = this.facts.computeIfAbsent(summary.getLevelId(), id -> {
            Path folder = this.minecraft.getLevelSource().getLevelPath(id);
            return CompletableFuture.supplyAsync(() -> WorldFacts.read(folder, this.minecraft.getUser().getProfileId()), Util.ioPool());
        });
        return future.getNow(WorldFacts.UNKNOWN);
    }

    void forget(String levelId) {
        this.facts.remove(levelId);
        Optional<FaviconTexture> icon = this.icons.remove(levelId);
        if (icon != null) {
            icon.ifPresent(FaviconTexture::close);
        }
    }

    @Override
    public void close() {
        this.icons.values().forEach(icon -> icon.ifPresent(FaviconTexture::close));
        this.icons.clear();
    }
}
