package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class ArkButton extends ArkWidget {
    public static final float HEIGHT = 32.0F;
    private static final float PADDING_X = 14.0F;
    private static final float ICON_SIZE = 14.0F;
    private static final float PIXEL_ICON_HEIGHT = 8.0F;
    private static final float ICON_GAP = 8.0F;
    private static final float GLOW_BLUR = 16.0F;
    private static final float GLOW_OFFSET = 4.0F;
    private static final float HOVER_BRIGHTNESS = 0.18F;
    private static final TextStyle LABEL = TextStyle.of(12.0F);

    private final Component label;
    private final ButtonVariant variant;
    private final Runnable action;
    private @Nullable Icon leadingIcon;
    private @Nullable Icon trailingIcon;

    public ArkButton(UiHost host, Component label, ButtonVariant variant, Runnable action) {
        super(host);
        this.label = label;
        this.variant = variant;
        this.action = action;
    }

    public ArkButton icon(Icon newIcon) {
        this.leadingIcon = newIcon;
        return this;
    }

    public ArkButton trailingIcon(Icon newIcon) {
        this.trailingIcon = newIcon;
        return this;
    }

    public float preferredWidth() {
        return this.contentWidth(this.host.metrics().width(this.label.getString(), LABEL)) + PADDING_X * 2.0F;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        float hover = this.hoverProgress();
        graphics.push();
        if (!this.isActive()) {
            graphics.fade(DISABLED_OPACITY);
        }
        graphics.shadow(box, GLOW_BLUR, GLOW_OFFSET, this.variant.glow());
        float brightness = 1.0F + HOVER_BRIGHTNESS * hover;
        int fill = ArkColors.lerp(hover, this.variant.fill(), this.variant.hoverFill());
        graphics.fill(box, ArkColors.brighten(fill, brightness));
        graphics.border(box, 1.0F, ArkColors.brighten(this.variant.border(), brightness));
        this.renderContent(graphics, box, ArkColors.brighten(this.variant.text(), brightness));
        graphics.pop();
    }

    private void renderContent(UiGraphics graphics, Box box, int color) {
        String text = this.label.getString();
        float textWidth = graphics.metrics().width(text, LABEL);
        float x = box.centerX() - this.contentWidth(textWidth) * 0.5F;
        if (this.leadingIcon != null) {
            x = this.drawIcon(graphics, this.leadingIcon, x, box, color) + ICON_GAP;
        }
        graphics.text(text, x, box.centerY() - graphics.metrics().capHeight(LABEL) * 0.5F, LABEL, color);
        if (this.trailingIcon != null) {
            this.drawIcon(graphics, this.trailingIcon, x + textWidth + ICON_GAP, box, color);
        }
    }

    private float drawIcon(UiGraphics graphics, Icon icon, float x, Box box, int color) {
        float height = iconHeight(icon);
        float width = iconWidth(icon);
        graphics.icon(icon, x, box.centerY() - height * 0.5F, width, height, color);
        return x + width;
    }

    private float contentWidth(float textWidth) {
        float width = textWidth;
        if (this.leadingIcon != null) {
            width += iconWidth(this.leadingIcon) + ICON_GAP;
        }
        if (this.trailingIcon != null) {
            width += iconWidth(this.trailingIcon) + ICON_GAP;
        }
        return width;
    }

    private static float iconHeight(Icon icon) {
        return icon.viewWidth() == icon.viewHeight() ? ICON_SIZE : PIXEL_ICON_HEIGHT;
    }

    private static float iconWidth(Icon icon) {
        return iconHeight(icon) * icon.viewWidth() / icon.viewHeight();
    }

    @Override
    protected void onPress() {
        this.action.run();
    }

    @Override
    protected Component narrationMessage() {
        return this.label;
    }
}
