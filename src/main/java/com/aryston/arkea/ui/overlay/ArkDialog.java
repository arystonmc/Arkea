package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Presence;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkButton;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.UiHost;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class ArkDialog {
    public static final float WIDTH_SMALL = 360.0F;
    public static final float WIDTH_MEDIUM = 520.0F;
    public static final float WIDTH_LARGE = 736.0F;
    private static final float PADDING = 24.0F;
    private static final float HERO_HEIGHT = 110.0F;
    private static final float HERO_GAP = 4.0F;
    private static final float GAP = 16.0F;
    private static final float ICON_BOX = 28.0F;
    private static final float ICON_SIZE = 14.0F;
    private static final float THUMBNAIL_WIDTH = 44.0F;
    private static final float HEADER_GAP = 12.0F;
    private static final float BUTTON_GAP = 8.0F;
    private static final float BODY_LINE_HEIGHT = 16.0F;
    private static final float SHADOW_BLUR = 60.0F;
    private static final float SHADOW_OFFSET = 24.0F;
    private static final float ENTER_OFFSET = 14.0F;
    private static final float EXIT_OFFSET = 10.0F;
    private static final float ENTER_SCALE = 0.96F;
    private static final float EXIT_SCALE = 0.97F;
    private static final float ICON_TINT = 0.15F;
    private static final float HERO_FADE_START = 0.3F;
    private static final int HERO_SHADE = ArkColors.rgba(0, 0, 0, 0.3F);
    private static final TextStyle TITLE = TextStyle.of(16.0F);
    private static final TextStyle BODY = TextStyle.of(11.0F);

    private final UiHost host;
    private final Component title;
    private final Component body;
    private final Icon icon;
    private final int toneColor;
    private final float width;
    private final Runnable cancelAction;
    private final List<ArkButton> buttons = new ArrayList<>();
    private final Presence scrim = Presence.of(Motion.SCRIM_IN, Motion.SCRIM_OUT);
    private final Presence box = Presence.of(Motion.POPUP_IN, Motion.POPUP_OUT);
    private boolean dismissOnScrimClick = true;
    private @Nullable DialogImage hero;
    private @Nullable DialogImage thumbnail;
    private @Nullable DialogBody content;
    private Box frame = Box.EMPTY;
    private Box header = Box.EMPTY;
    private List<String> bodyLines = List.of();

    public ArkDialog(UiHost host, DialogContent content, float width, Runnable cancelAction) {
        this.host = host;
        this.title = content.title();
        this.body = content.body();
        this.icon = content.icon();
        this.toneColor = content.toneColor();
        this.width = width;
        this.cancelAction = cancelAction;
    }

    public ArkDialog button(ArkButton button) {
        this.buttons.add(button);
        return this;
    }

    public ArkDialog keepOpenOnScrimClick() {
        this.dismissOnScrimClick = false;
        return this;
    }

    public ArkDialog hero(DialogImage image) {
        this.hero = image;
        return this;
    }

    public ArkDialog thumbnail(DialogImage image) {
        this.thumbnail = image;
        return this;
    }

    public ArkDialog content(DialogBody newContent) {
        this.content = newContent;
        return this;
    }

    public void open(long now) {
        this.scrim.show(now);
        this.box.show(now);
    }

    public void close(long now) {
        this.scrim.hide(now);
        this.box.hide(now);
    }

    public void cancel() {
        this.cancelAction.run();
    }

    public boolean isOpen() {
        return this.box.isShown();
    }

    public boolean isGone(long now) {
        return this.scrim.isGone(now) && this.box.isGone(now);
    }

    public boolean dismissesOnScrimClick() {
        return this.dismissOnScrimClick;
    }

    public Component title() {
        return this.title;
    }

    public Component body() {
        return this.body;
    }

    public List<? extends ArkWidget> widgets() {
        List<ArkWidget> widgets = new ArrayList<>();
        if (this.content != null) {
            widgets.addAll(this.content.widgets());
        }
        widgets.addAll(this.buttons);
        return widgets;
    }

    public boolean contains(float x, float y) {
        return this.frame.contains(x, y);
    }

    public void layout(UiScale scale) {
        TextMetrics metrics = this.host.metrics();
        float contentWidth = this.width - PADDING * 2.0F;
        this.bodyLines = this.body.getString().isEmpty() ? List.of() : metrics.wrap(this.body.getString(), BODY, contentWidth);
        float top = this.hero != null ? HERO_HEIGHT + HERO_GAP : PADDING;
        float height = top + ICON_BOX;
        if (!this.bodyLines.isEmpty()) {
            height += GAP + this.bodyLines.size() * BODY_LINE_HEIGHT;
        }
        float contentHeight = this.content != null ? this.content.height(contentWidth) : 0.0F;
        if (this.content != null) {
            height += GAP + contentHeight;
        }
        height += GAP + ArkButton.HEIGHT + PADDING;
        float x = (scale.canvasWidth() - this.width) * 0.5F;
        float y = (scale.canvasHeight() - height) * 0.5F;
        this.frame = new Box(x, y, this.width, height);
        this.header = new Box(x + PADDING, y + top, contentWidth, ICON_BOX);
        if (this.content != null) {
            float contentY = this.header.bottom() + GAP + (this.bodyLines.isEmpty() ? 0.0F : this.bodyLines.size() * BODY_LINE_HEIGHT + GAP);
            this.content.layout(new Box(x + PADDING, contentY, contentWidth, contentHeight));
        }
        float buttonX = this.frame.right() - PADDING;
        float buttonY = this.frame.bottom() - PADDING - ArkButton.HEIGHT;
        for (int index = this.buttons.size() - 1; index >= 0; index--) {
            ArkButton button = this.buttons.get(index);
            float buttonWidth = button.preferredWidth();
            buttonX -= buttonWidth;
            button.setBounds(new Box(buttonX, buttonY, buttonWidth, ArkButton.HEIGHT));
            buttonX -= BUTTON_GAP;
        }
    }

    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        long now = graphics.now();
        UiScale scale = graphics.scale();
        graphics.push();
        graphics.fade(this.scrim.progress(now));
        graphics.fill(0.0F, 0.0F, scale.canvasWidth(), scale.canvasHeight(), ArkColors.SCRIM_MODAL);
        graphics.pop();
        float progress = this.box.progress(now);
        if (progress <= 0.0F) {
            return;
        }
        float offset = this.box.isShown() ? ENTER_OFFSET : EXIT_OFFSET;
        float startScale = this.box.isShown() ? ENTER_SCALE : EXIT_SCALE;
        graphics.push();
        graphics.fade(progress);
        graphics.translate(0.0F, offset * (1.0F - progress));
        graphics.scaleAround(startScale + (1.0F - startScale) * progress, this.frame.centerX(), this.frame.centerY());
        this.renderFrame(graphics);
        float hoverX = this.box.isShown() ? mouseX : Float.NEGATIVE_INFINITY;
        if (this.content != null) {
            this.content.render(graphics, hoverX, mouseY);
        }
        for (ArkButton button : this.buttons) {
            button.render(graphics, hoverX, mouseY);
        }
        graphics.pop();
    }

    private void renderFrame(UiGraphics graphics) {
        graphics.shadow(this.frame, SHADOW_BLUR, SHADOW_OFFSET, ArkColors.SHADOW_DIALOG);
        graphics.fill(this.frame, ArkColors.DIALOG);
        if (this.hero != null) {
            this.renderHero(graphics, this.hero);
        }
        graphics.border(this.frame, 1.0F, ArkColors.BORDER_OVERLAY);
        graphics.topHighlight(this.frame, ArkColors.INNER_HIGHLIGHT);
        float titleX = this.renderHeaderMark(graphics);
        TextMetrics metrics = graphics.metrics();
        String titleText = metrics.ellipsize(this.title.getString(), TITLE, this.header.right() - titleX);
        graphics.text(titleText, titleX, this.header.centerY() - metrics.capHeight(TITLE) * 0.5F, TITLE, ArkColors.TEXT_PRIMARY);
        float lineY = this.header.bottom() + GAP;
        for (String line : this.bodyLines) {
            graphics.text(line, this.header.x(), lineY + (BODY_LINE_HEIGHT - metrics.capHeight(BODY)) * 0.5F, BODY, ArkColors.TEXT_ICON_IDLE);
            lineY += BODY_LINE_HEIGHT;
        }
    }

    private void renderHero(UiGraphics graphics, DialogImage image) {
        Box area = new Box(this.frame.x(), this.frame.y(), this.frame.width(), HERO_HEIGHT);
        image.draw(graphics, area);
        graphics.fill(area, HERO_SHADE);
        float fadeY = area.y() + HERO_HEIGHT * HERO_FADE_START;
        graphics.gradientVertical(area.x(), fadeY, area.width(), area.bottom() - fadeY, ArkColors.withAlpha(ArkColors.DIALOG, 0.0F), ArkColors.DIALOG);
    }

    private float renderHeaderMark(UiGraphics graphics) {
        if (this.thumbnail != null) {
            Box image = new Box(this.header.x(), this.header.y(), THUMBNAIL_WIDTH, ICON_BOX);
            this.thumbnail.draw(graphics, image);
            graphics.border(image, 1.0F, ArkColors.BORDER_STRONG);
            return image.right() + HEADER_GAP;
        }
        Box iconBox = new Box(this.header.x(), this.header.y(), ICON_BOX, ICON_BOX);
        graphics.fill(iconBox, ArkColors.withAlpha(this.toneColor, ICON_TINT));
        graphics.icon(this.icon, iconBox.centerX() - ICON_SIZE * 0.5F, iconBox.centerY() - ICON_SIZE * 0.5F, ICON_SIZE, ICON_SIZE, this.toneColor);
        return iconBox.right() + HEADER_GAP;
    }
}
