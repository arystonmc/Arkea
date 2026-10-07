package com.aryston.arkea.screen.title;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.UiHost;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

final class JumpBackInCard extends ArkWidget {
    static final float WIDTH = 360.0F;
    static final float HEIGHT = 202.0F;
    private static final float IMAGE_HEIGHT = 150.0F;
    private static final float PADDING_X = 14.0F;
    private static final float PADDING_TOP = 4.0F;
    private static final float PADDING_BOTTOM = 14.0F;
    private static final float LINE_GAP = 6.0F;
    private static final float BADGE_INSET = 12.0F;
    private static final float BADGE_HEIGHT = 16.0F;
    private static final float BADGE_PADDING = 6.0F;
    private static final float PLAY_HEIGHT = 32.0F;
    private static final float PLAY_PADDING = 14.0F;
    private static final float PLAY_ICON = 10.0F;
    private static final float PLAY_GAP = 8.0F;
    private static final float ROW_GAP = 12.0F;
    private static final float SHADOW_BLUR = 50.0F;
    private static final float SHADOW_OFFSET = 20.0F;
    private static final float PLAY_GLOW_BLUR = 14.0F;
    private static final float PLAY_GLOW_OFFSET = 4.0F;
    private static final float HOVER_BRIGHTNESS = 0.18F;
    private static final float IMAGE_ZOOM = 0.06F;
    private static final float GRADIENT_START = 0.45F;
    private static final float PLACEHOLDER_ICON = 40.0F;
    private static final int FILL = ArkColors.rgba(16, 16, 18, 0.78F);
    private static final int IMAGE_FADE = ArkColors.rgba(16, 16, 18, 0.95F);
    private static final int BADGE_FILL = ArkColors.rgba(0, 0, 0, 0.60F);
    private static final int SHADOW = ArkColors.rgba(0, 0, 0, 0.50F);
    private static final int PLACEHOLDER_TOP = ArkColors.rgb(0x2A3A2A);
    private static final int PLACEHOLDER_BOTTOM = ArkColors.rgb(0x141416);
    private static final TextStyle NAME = TextStyle.of(14.0F);
    private static final TextStyle DETAIL = TextStyle.of(10.0F);
    private static final TextStyle BADGE = TextStyle.of(8.0F).spacing(1.0F);
    private static final TextStyle PLAY = TextStyle.of(12.0F);

    private final Transition zoom = new Transition(0.0F, Motion.IMAGE_ZOOM, Easing.STANDARD);
    private final Component worldName;
    private final Component lastPlayed;
    private final Component mode;
    private final Component play;
    private final Runnable action;
    private @Nullable Identifier icon;

    JumpBackInCard(UiHost host, CardText text, Runnable action) {
        super(host);
        this.worldName = text.worldName();
        this.lastPlayed = text.lastPlayed();
        this.mode = text.mode();
        this.play = text.play();
        this.action = action;
    }

    void setIcon(@Nullable Identifier newIcon) {
        this.icon = newIcon;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        float hover = this.hoverProgress();
        this.zoom.setTarget(this.isHovered() && this.isActive() ? 1.0F : 0.0F, graphics.now());
        float brightness = 1.0F + HOVER_BRIGHTNESS * hover;
        Box box = this.bounds();
        graphics.push();
        graphics.shadow(box, SHADOW_BLUR, SHADOW_OFFSET, SHADOW);
        graphics.fill(box, ArkColors.brighten(FILL, brightness));
        Box image = new Box(box.x() + 1.0F, box.y() + 1.0F, box.width() - 2.0F, IMAGE_HEIGHT);
        this.renderImage(graphics, image);
        this.renderBadge(graphics, image);
        this.renderFooter(graphics, new Box(box.x(), image.bottom(), box.width(), box.bottom() - image.bottom()), brightness);
        graphics.border(box, 1.0F, ArkColors.brighten(ArkColors.BORDER_OVERLAY, brightness));
        graphics.pop();
    }

    private void renderImage(UiGraphics graphics, Box image) {
        if (this.icon != null) {
            float zoomScale = 1.0F + IMAGE_ZOOM * this.zoom.value(graphics.now());
            float visibleHeight = image.height() / image.width() / zoomScale;
            float visibleWidth = 1.0F / zoomScale;
            float u0 = (1.0F - visibleWidth) * 0.5F;
            float v0 = (1.0F - visibleHeight) * 0.5F;
            graphics.image(this.icon, image, u0, v0, u0 + visibleWidth, v0 + visibleHeight, ArkColors.TEXT_PRIMARY);
        } else {
            graphics.gradientVertical(image.x(), image.y(), image.width(), image.height(), PLACEHOLDER_TOP, PLACEHOLDER_BOTTOM);
            float iconX = image.centerX() - PLACEHOLDER_ICON * 0.5F;
            float iconY = image.centerY() - PLACEHOLDER_ICON * 0.5F;
            graphics.icon(Icons.CUBE, iconX, iconY, PLACEHOLDER_ICON, PLACEHOLDER_ICON, ArkColors.BORDER_TOOLTIP);
        }
        float fadeTop = image.y() + image.height() * GRADIENT_START;
        graphics.gradientVertical(image.x(), fadeTop, image.width(), image.bottom() - fadeTop, ArkColors.TRANSPARENT, IMAGE_FADE);
    }

    private void renderBadge(UiGraphics graphics, Box image) {
        TextMetrics metrics = graphics.metrics();
        String badge = this.mode.getString();
        Box badgeBox = new Box(image.x() + BADGE_INSET, image.y() + BADGE_INSET, metrics.width(badge, BADGE) + BADGE_PADDING * 2.0F, BADGE_HEIGHT);
        graphics.fill(badgeBox, BADGE_FILL);
        graphics.text(badge, badgeBox.x() + BADGE_PADDING, badgeBox.centerY() - metrics.capHeight(BADGE) * 0.5F, BADGE, ArkColors.TEXT_PRIMARY);
    }

    private void renderFooter(UiGraphics graphics, Box footer, float brightness) {
        TextMetrics metrics = graphics.metrics();
        String playText = this.play.getString();
        float playWidth = PLAY_PADDING * 2.0F + PLAY_ICON + PLAY_GAP + metrics.width(playText, PLAY);
        float contentTop = footer.y() + PADDING_TOP;
        float contentHeight = footer.height() - PADDING_TOP - PADDING_BOTTOM;
        Box playBox = new Box(footer.right() - PADDING_X - playWidth, contentTop + (contentHeight - PLAY_HEIGHT) * 0.5F, playWidth, PLAY_HEIGHT);
        graphics.shadow(playBox, PLAY_GLOW_BLUR, PLAY_GLOW_OFFSET, Theme.accent().glow());
        graphics.fill(playBox, ArkColors.brighten(Theme.accent().base(), brightness));
        graphics.border(playBox, 1.0F, ArkColors.brighten(Theme.accent().border(), brightness));
        float playIconY = playBox.centerY() - PLAY_ICON * 0.5F;
        graphics.icon(Icons.PLAY, playBox.x() + PLAY_PADDING, playIconY, PLAY_ICON, PLAY_ICON, ArkColors.TEXT_PRIMARY);
        float playTextX = playBox.x() + PLAY_PADDING + PLAY_ICON + PLAY_GAP;
        graphics.text(playText, playTextX, playBox.centerY() - metrics.capHeight(PLAY) * 0.5F, PLAY, ArkColors.TEXT_PRIMARY);
        float textX = footer.x() + PADDING_X;
        float textWidth = playBox.x() - ROW_GAP - textX;
        float blockHeight = metrics.capHeight(NAME) + LINE_GAP + metrics.capHeight(DETAIL);
        float nameY = contentTop + (contentHeight - blockHeight) * 0.5F;
        graphics.text(metrics.ellipsize(this.worldName.getString(), NAME, textWidth), textX, nameY, NAME, ArkColors.TEXT_PRIMARY);
        float detailY = nameY + metrics.capHeight(NAME) + LINE_GAP;
        int detailColor = ArkColors.brighten(ArkColors.TEXT_DESCRIPTION, brightness);
        graphics.text(metrics.ellipsize(this.lastPlayed.getString(), DETAIL, textWidth), textX, detailY, DETAIL, detailColor);
    }

    @Override
    protected void onPress() {
        this.action.run();
    }

    @Override
    protected Component narrationMessage() {
        return Component.empty().append(this.play).append(" ").append(this.worldName).append(", ").append(this.lastPlayed);
    }

    record CardText(Component worldName, Component lastPlayed, Component mode, Component play) {
    }
}
