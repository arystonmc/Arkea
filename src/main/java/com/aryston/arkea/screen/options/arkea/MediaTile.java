package com.aryston.arkea.screen.options.arkea;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.UiHost;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

abstract class MediaTile extends ArkWidget {
    static final float CAPTION_HEIGHT = 52.0F;
    static final float THUMBNAIL_ASPECT = 9.0F / 16.0F;
    private static final float PADDING = 12.0F;
    private static final float LINE_GAP = 7.0F;
    private static final float SELECTED_FILL_ALPHA = 0.10F;
    private static final float GLOW_BLUR = 14.0F;
    private static final int BORDER = ArkColors.rgba(255, 255, 255, 0.07F);
    private static final TextStyle NAME = TextStyle.of(13.0F);
    private static final TextStyle SUB = TextStyle.of(10.0F);

    private @Nullable ArkWidget corner;

    MediaTile(UiHost host) {
        super(host);
    }

    void corner(@Nullable ArkWidget widget) {
        this.corner = widget;
    }

    @Override
    protected boolean containsPoint(float x, float y) {
        if (this.corner != null && this.corner.hitBox().contains(x, y)) {
            return false;
        }
        return super.containsPoint(x, y);
    }

    @Override
    protected boolean isHighlighted() {
        return super.isHighlighted() || this.isActive() && this.corner != null && this.corner.isHovered();
    }

    static float height(float width) {
        return width * THUMBNAIL_ASPECT + CAPTION_HEIGHT;
    }

    protected abstract void renderThumbnail(UiGraphics graphics, Box thumbnail, float hover);

    protected abstract Component name();

    protected abstract Component sub();

    protected boolean isSelected() {
        return false;
    }

    protected int subColor() {
        return ArkColors.TEXT_DESCRIPTION;
    }

    protected Box thumbnailBox() {
        Box box = this.bounds();
        return new Box(box.x(), box.y(), box.width(), box.height() - CAPTION_HEIGHT);
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        float hover = this.hoverProgress();
        boolean selected = this.isSelected();
        int accent = Theme.accent().base();
        if (selected) {
            graphics.shadow(box, GLOW_BLUR, 0.0F, Theme.accent().glow());
        }
        graphics.fill(box, selected ? ArkColors.withAlpha(accent, SELECTED_FILL_ALPHA) : ArkColors.lerp(hover, ArkColors.ROW, ArkColors.ROW_HOVER));
        Box thumbnail = this.thumbnailBox();
        graphics.clip(thumbnail);
        this.renderThumbnail(graphics, thumbnail, hover);
        graphics.endClip();
        TextMetrics metrics = graphics.metrics();
        float textWidth = box.width() - PADDING * 2.0F;
        float block = metrics.capHeight(NAME) + LINE_GAP + metrics.capHeight(SUB);
        float nameY = thumbnail.bottom() + (CAPTION_HEIGHT - block) * 0.5F;
        graphics.text(metrics.ellipsize(this.name().getString(), NAME, textWidth), box.x() + PADDING, nameY, NAME, ArkColors.TEXT_PRIMARY);
        float subY = nameY + metrics.capHeight(NAME) + LINE_GAP;
        graphics.text(metrics.ellipsize(this.sub().getString(), SUB, textWidth), box.x() + PADDING, subY, SUB, this.subColor());
        graphics.border(box, 1.0F, selected ? accent : ArkColors.lerp(hover, BORDER, ArkColors.BORDER_OVERLAY));
    }

    static void coverImage(UiGraphics graphics, net.minecraft.client.renderer.texture.AbstractTexture texture, Box area, float textureAspect,
        float zoom) {
        float areaAspect = area.width() / area.height();
        float spanU = textureAspect > areaAspect ? areaAspect / textureAspect : 1.0F;
        float spanV = textureAspect > areaAspect ? 1.0F : textureAspect / areaAspect;
        spanU /= zoom;
        spanV /= zoom;
        float u0 = 0.5F - spanU * 0.5F;
        float v0 = 0.5F - spanV * 0.5F;
        graphics.image(texture, area, u0, v0, u0 + spanU, v0 + spanV, ArkColors.TEXT_PRIMARY);
    }
}
