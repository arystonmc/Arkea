package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ArkBanner extends ArkWidget {
    public static final float HEIGHT = 56.0F;
    private static final float PREVIEW_WIDTH = 96.0F;
    private static final float LOGO_SIZE = 22.0F;
    private static final float GAP = 14.0F;
    private static final float PADDING_RIGHT = 14.0F;
    private static final float LINE_GAP = 6.0F;
    private static final float ACTION_HEIGHT = 30.0F;
    private static final float ACTION_PADDING = 12.0F;
    private static final float ACTION_GAP = 8.0F;
    private static final float ACTION_ICON = 10.0F;
    private static final float FILL_ALPHA = 0.10F;
    private static final float FILL_HOVER_ALPHA = 0.16F;
    private static final float BORDER_ALPHA = 0.45F;
    private static final float BORDER_HOVER_ALPHA = 0.70F;
    private static final float ACTION_HOVER_BRIGHTNESS = 0.18F;
    private static final int SUBTITLE = ArkColors.rgb(0xA8CC98);
    private static final TextStyle TITLE = TextStyle.of(13.0F);
    private static final TextStyle SUB = TextStyle.of(10.0F);
    private static final TextStyle ACTION = TextStyle.of(11.0F);

    private final Banner banner;
    private final Runnable action;

    public ArkBanner(UiHost host, Banner banner, Runnable action) {
        super(host);
        this.banner = banner;
        this.action = action;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        float hover = this.hoverProgress();
        int base = Theme.accent().base();
        graphics.fill(box, ArkColors.withAlpha(base, FILL_ALPHA + (FILL_HOVER_ALPHA - FILL_ALPHA) * hover));
        Box preview = new Box(box.x(), box.y(), PREVIEW_WIDTH, box.height());
        Banner.Image image = this.banner.preview();
        graphics.image(image.texture(), preview, image.u0(), image.v0(), image.u1(), image.v1(), ArkColors.TEXT_PRIMARY);
        graphics.border(box, 1.0F, ArkColors.withAlpha(base, BORDER_ALPHA + (BORDER_HOVER_ALPHA - BORDER_ALPHA) * hover));
        float logoX = preview.right() + GAP;
        graphics.image(this.banner.logo(), new Box(logoX, box.centerY() - LOGO_SIZE * 0.5F, LOGO_SIZE, LOGO_SIZE), 0.0F, 0.0F, 1.0F, 1.0F,
            ArkColors.TEXT_PRIMARY);
        TextMetrics metrics = graphics.metrics();
        String label = this.banner.action().getString();
        float actionWidth = ACTION_PADDING * 2.0F + metrics.width(label, ACTION) + ACTION_GAP + ACTION_ICON;
        Box action = new Box(box.right() - PADDING_RIGHT - actionWidth, box.centerY() - ACTION_HEIGHT * 0.5F, actionWidth, ACTION_HEIGHT);
        float textX = logoX + LOGO_SIZE + GAP;
        float textWidth = action.x() - GAP - textX;
        float blockHeight = metrics.capHeight(TITLE) + LINE_GAP + metrics.capHeight(SUB);
        float titleY = box.centerY() - blockHeight * 0.5F;
        graphics.text(metrics.ellipsize(this.banner.title().getString(), TITLE, textWidth), textX, titleY, TITLE, ArkColors.TEXT_PRIMARY);
        float subY = titleY + metrics.capHeight(TITLE) + LINE_GAP;
        graphics.text(metrics.ellipsize(this.banner.subtitle().getString(), SUB, textWidth), textX, subY, SUB, SUBTITLE);
        float brightness = 1.0F + ACTION_HOVER_BRIGHTNESS * hover;
        graphics.fill(action, ArkColors.brighten(base, brightness));
        graphics.border(action, 1.0F, ArkColors.brighten(Theme.accent().border(), brightness));
        float labelX = action.x() + ACTION_PADDING;
        graphics.text(label, labelX, action.centerY() - metrics.capHeight(ACTION) * 0.5F, ACTION, ArkColors.TEXT_PRIMARY);
        float iconX = action.right() - ACTION_PADDING - ACTION_ICON;
        graphics.icon(Icons.EXTERNAL, iconX, action.centerY() - ACTION_ICON * 0.5F, ACTION_ICON, ACTION_ICON, ArkColors.TEXT_PRIMARY);
    }

    @Override
    protected void onPress() {
        this.action.run();
    }

    @Override
    protected Component narrationMessage() {
        return Component.empty().append(this.banner.title()).append(". ").append(this.banner.subtitle()).append(". ").append(this.banner.action());
    }

    public record Banner(Image preview, Identifier logo, Component title, Component subtitle, Component action) {
        public record Image(Identifier texture, float u0, float v0, float u1, float v1) {
        }
    }
}
