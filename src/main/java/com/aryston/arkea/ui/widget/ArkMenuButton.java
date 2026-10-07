package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class ArkMenuButton extends ArkWidget {
    private static final float HOVER_SLIDE = 6.0F;
    private static final float CHEVRON_SLIDE = 3.0F;
    private static final float HOVER_BRIGHTNESS = 0.15F;
    private static final int HOVER_BORDER = ArkColors.rgba(255, 255, 255, 0.28F);
    private static final float GLOW_BLUR = 24.0F;
    private static final float GLOW_OFFSET = 8.0F;
    private static final float TILE_PADDING_LEFT = 8.0F;
    private static final float TILE_PADDING_RIGHT = 16.0F;
    private static final float TILE_SIZE = 36.0F;
    private static final float TILE_ICON = 16.0F;
    private static final float TILE_GAP = 14.0F;
    private static final float INLINE_PADDING = 14.0F;
    private static final float INLINE_ICON = 14.0F;
    private static final float INLINE_GAP = 12.0F;
    private static final float LINE_GAP = 6.0F;
    private static final float CHEVRON_WIDTH = 5.0F;
    private static final float CHEVRON_HEIGHT = 8.0F;
    private static final TextStyle SUB = TextStyle.of(10.0F);

    private final Icon icon;
    private final Component label;
    private final @Nullable Component sub;
    private final MenuButtonStyle style;
    private final Runnable action;

    public ArkMenuButton(UiHost host, ItemContent content, MenuButtonStyle style, Runnable action) {
        super(host, Motion.MENU_HOVER, Easing.STANDARD);
        this.icon = content.icon();
        this.label = content.label();
        this.sub = content.sub();
        this.style = style;
        this.action = action;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        float hover = this.hoverProgress();
        float brightness = 1.0F + HOVER_BRIGHTNESS * hover;
        Box box = this.bounds();
        graphics.push();
        if (!this.isActive()) {
            graphics.fade(DISABLED_OPACITY);
        }
        graphics.shadow(box, GLOW_BLUR, GLOW_OFFSET, this.style.glow());
        graphics.fill(box, ArkColors.brighten(this.style.fill(), brightness));
        graphics.border(box, 1.0F, ArkColors.lerp(hover, this.style.border(), HOVER_BORDER));
        if (ArkColors.alpha(this.style.topHighlight()) > 0.0F) {
            graphics.topHighlight(box, this.style.topHighlight());
        }
        if (this.style.layout() == MenuButtonLayout.TILE) {
            this.renderTile(graphics, box, brightness, hover);
        } else {
            this.renderInline(graphics, box, brightness);
        }
        graphics.pop();
    }

    private void renderTile(UiGraphics graphics, Box box, float brightness, float hover) {
        Box tile = new Box(box.x() + TILE_PADDING_LEFT, box.centerY() - TILE_SIZE * 0.5F, TILE_SIZE, TILE_SIZE);
        graphics.fill(tile, ArkColors.brighten(this.style.iconFill(), brightness));
        float iconOffset = (TILE_SIZE - TILE_ICON) * 0.5F;
        graphics.icon(this.icon, tile.x() + iconOffset, tile.y() + iconOffset, TILE_ICON, TILE_ICON, ArkColors.brighten(this.style.iconColor(), brightness));
        TextMetrics metrics = graphics.metrics();
        TextStyle labelStyle = TextStyle.of(this.style.labelSize());
        float labelHeight = metrics.capHeight(labelStyle);
        float subHeight = this.sub != null ? LINE_GAP + metrics.capHeight(SUB) : 0.0F;
        float textX = tile.right() + TILE_GAP;
        float textWidth = box.right() - TILE_PADDING_RIGHT - CHEVRON_WIDTH - TILE_GAP - textX;
        float textY = box.centerY() - (labelHeight + subHeight) * 0.5F;
        int labelColor = ArkColors.brighten(this.style.labelColor(), brightness);
        graphics.text(metrics.ellipsize(this.label.getString(), labelStyle, textWidth), textX, textY, labelStyle, labelColor);
        if (this.sub != null) {
            int subColor = ArkColors.brighten(this.style.subColor(), brightness);
            graphics.text(metrics.ellipsize(this.sub.getString(), SUB, textWidth), textX, textY + labelHeight + LINE_GAP, SUB, subColor);
        }
        float chevronX = box.right() - TILE_PADDING_RIGHT - CHEVRON_WIDTH + CHEVRON_SLIDE * hover;
        graphics.icon(Icons.CHEVRON_RIGHT, chevronX, box.centerY() - CHEVRON_HEIGHT * 0.5F, CHEVRON_WIDTH, CHEVRON_HEIGHT, labelColor);
    }

    private void renderInline(UiGraphics graphics, Box box, float brightness) {
        int color = ArkColors.brighten(this.style.labelColor(), brightness);
        float iconX = box.x() + INLINE_PADDING;
        graphics.icon(this.icon, iconX, box.centerY() - INLINE_ICON * 0.5F, INLINE_ICON, INLINE_ICON, color);
        TextStyle labelStyle = TextStyle.of(this.style.labelSize());
        TextMetrics metrics = graphics.metrics();
        float textX = iconX + INLINE_ICON + INLINE_GAP;
        String text = metrics.ellipsize(this.label.getString(), labelStyle, box.right() - INLINE_PADDING - textX);
        graphics.text(text, textX, box.centerY() - metrics.capHeight(labelStyle) * 0.5F, labelStyle, color);
    }

    @Override
    protected float contentShiftX() {
        return HOVER_SLIDE * this.hoverProgress();
    }

    @Override
    protected float contentReachX() {
        return HOVER_SLIDE;
    }

    @Override
    protected void onPress() {
        this.action.run();
    }

    @Override
    protected Component narrationMessage() {
        return this.sub != null ? Component.empty().append(this.label).append(", ").append(this.sub) : this.label;
    }
}
