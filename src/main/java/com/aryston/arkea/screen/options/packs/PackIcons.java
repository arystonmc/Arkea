package com.aryston.arkea.screen.options.packs;

import com.aryston.arkea.Arkea;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackMetadataResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.Util;

final class PackIcons {
    private static final Identifier UNKNOWN = Identifier.withDefaultNamespace("textures/misc/unknown_pack.png");
    private static final String ICON_FILE = "pack.png";

    private final Map<String, Identifier> icons = new HashMap<>();

    Identifier get(Pack pack) {
        return this.icons.computeIfAbsent(pack.getId(), id -> load(pack));
    }

    void clear() {
        this.icons.clear();
    }

    private static Identifier load(Pack pack) {
        try (PackMetadataResources resources = pack.openMetadata()) {
            IoSupplier<InputStream> resource = resources.getRootResource(ICON_FILE);
            if (resource == null) {
                return UNKNOWN;
            }
            String id = pack.getId();
            Identifier location = Identifier.withDefaultNamespace(
                "pack/" + Util.sanitizeName(id, Identifier::validPathChar) + "/" + Hashing.sha1().hashUnencodedChars(id) + "/icon");
            try (InputStream stream = resource.get()) {
                NativeImage image = NativeImage.read(stream);
                Minecraft.getInstance().getTextureManager().register(location, new DynamicTexture(location::toString, image));
                return location;
            }
        } catch (Exception exception) {
            Arkea.LOGGER.warn("Failed to load icon from pack {}", pack.getId(), exception);
            return UNKNOWN;
        }
    }
}
