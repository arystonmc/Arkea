package com.aryston.arkea.screen.mods;

import com.aryston.arkea.Arkea;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.neoforge.client.gui.modlist.ImageResource;
import org.jspecify.annotations.Nullable;

final class ModIcons implements AutoCloseable {
    private final Map<String, Optional<DynamicTexture>> textures = new HashMap<>();

    @Nullable DynamicTexture get(ModEntry entry) {
        return this.textures.computeIfAbsent(entry.id(), id -> load(entry)).orElse(null);
    }

    private static Optional<DynamicTexture> load(ModEntry entry) {
        ImageResource resource = entry.icon();
        if (resource == null) {
            return Optional.empty();
        }
        IoSupplier<InputStream> supplier = resource.get(Minecraft.getInstance().getResourceManager());
        if (supplier == null) {
            return Optional.empty();
        }
        try (InputStream stream = supplier.get()) {
            return Optional.of(new DynamicTexture(() -> "Arkea mod icon " + entry.id(), NativeImage.read(stream)));
        } catch (Exception exception) {
            Arkea.LOGGER.warn("Could not load the icon of mod {}", entry.id(), exception);
            return Optional.empty();
        }
    }

    @Override
    public void close() {
        this.textures.values().forEach(texture -> texture.ifPresent(DynamicTexture::close));
        this.textures.clear();
    }
}
