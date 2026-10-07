package com.aryston.arkea.screen.options.arkea;

import com.aryston.arkea.background.BackgroundEntry;
import com.aryston.arkea.background.BackgroundThumbnails;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.UiHost;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

final class BackgroundTile extends MediaTile {
    private static final Identifier PANORAMA = Identifier.withDefaultNamespace("textures/gui/title/background/panorama_0.png");
    private static final float HOVER_ZOOM = 0.04F;
    private static final float BADGE = 20.0F;
    private static final float BADGE_INSET = 8.0F;
    private static final float CHECK_WIDTH = 10.0F;
    private static final float CHECK_HEIGHT = 8.0F;
    private static final float KIND_SIZE = 12.0F;
    private static final float BYTES_PER_MEGABYTE = 1024.0F * 1024.0F;
    private static final int KIND_FILL = ArkColors.rgba(0, 0, 0, 0.55F);

    private final @Nullable BackgroundEntry entry;
    private final BooleanSupplier selected;
    private final Runnable action;

    BackgroundTile(UiHost host, @Nullable BackgroundEntry entry, BooleanSupplier selected, Runnable action) {
        super(host);
        this.entry = entry;
        this.selected = selected;
        this.action = action;
    }

    @Override
    protected void renderThumbnail(UiGraphics graphics, Box thumbnail, float hover) {
        float zoom = 1.0F + HOVER_ZOOM * hover;
        if (this.entry == null) {
            AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(PANORAMA);
            coverImage(graphics, texture, thumbnail, 1.0F, zoom);
        } else {
            AbstractTexture texture = BackgroundThumbnails.get(this.entry);
            if (texture != null) {
                coverImage(graphics, texture, thumbnail, 16.0F / 9.0F, zoom);
            } else {
                graphics.fill(thumbnail, ArkColors.CONTROL_FILL);
            }
            if (this.entry.kind() == BackgroundEntry.Kind.VIDEO) {
                Box badge = new Box(thumbnail.right() - BADGE_INSET - BADGE, thumbnail.bottom() - BADGE_INSET - BADGE, BADGE, BADGE);
                graphics.fill(badge, KIND_FILL);
                float offset = (BADGE - KIND_SIZE) * 0.5F;
                graphics.icon(Icons.PLAY, badge.x() + offset, badge.y() + offset, KIND_SIZE, KIND_SIZE, ArkColors.TEXT_PRIMARY);
            }
        }
        if (this.isSelected()) {
            Box badge = new Box(thumbnail.x() + BADGE_INSET, thumbnail.y() + BADGE_INSET, BADGE, BADGE);
            graphics.fill(badge, Theme.accent().base());
            graphics.icon(Icons.CHECK, badge.centerX() - CHECK_WIDTH * 0.5F, badge.centerY() - CHECK_HEIGHT * 0.5F, CHECK_WIDTH, CHECK_HEIGHT,
                ArkColors.TEXT_PRIMARY);
        }
    }

    @Override
    protected Component name() {
        return this.entry == null ? Component.translatable("arkea.background.vanilla") : Component.literal(this.entry.name());
    }

    @Override
    protected Component sub() {
        if (this.entry == null) {
            return Component.translatable("arkea.background.vanilla.description");
        }
        String size = String.format(Locale.ROOT, "%.1f MB", this.entry.bytes() / BYTES_PER_MEGABYTE);
        if (this.entry.kind() == BackgroundEntry.Kind.VIDEO) {
            return Component.translatable("arkea.background.video", Math.round(this.entry.seconds()), this.entry.fps(), size);
        }
        return Component.translatable("arkea.background.image", size);
    }

    @Override
    protected boolean isSelected() {
        return this.selected.getAsBoolean();
    }

    @Override
    protected void onPress() {
        this.action.run();
    }

    @Override
    protected Component narrationMessage() {
        return Component.empty().append(this.name()).append(", ").append(this.sub());
    }
}
