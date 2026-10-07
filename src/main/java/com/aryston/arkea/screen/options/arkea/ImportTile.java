package com.aryston.arkea.screen.options.arkea;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.UiHost;
import net.minecraft.network.chat.Component;

final class ImportTile extends MediaTile {
    private static final float DASH = 4.0F;
    private static final float ICON_BOX = 36.0F;
    private static final float ICON = 16.0F;
    private static final float GAP = 12.0F;
    private static final int DASH_COLOR = ArkColors.rgba(255, 255, 255, 0.14F);
    private static final TextStyle HINT = TextStyle.of(11.0F);

    private final Runnable action;

    ImportTile(UiHost host, Runnable action) {
        super(host);
        this.action = action;
    }

    @Override
    protected void renderThumbnail(UiGraphics graphics, Box thumbnail, float hover) {
        Box area = thumbnail.inset(10.0F);
        graphics.dashedBorder(area, DASH, ArkColors.lerp(hover, DASH_COLOR, Theme.accent().border()));
        TextMetrics metrics = graphics.metrics();
        String hint = metrics.ellipsize(Component.translatable("arkea.background.import.hint").getString(), HINT, area.width() - GAP * 2.0F);
        float block = ICON_BOX + GAP + metrics.capHeight(HINT);
        float top = area.centerY() - block * 0.5F;
        Box iconBox = new Box(area.centerX() - ICON_BOX * 0.5F, top, ICON_BOX, ICON_BOX);
        int light = Theme.accent().light();
        graphics.fill(iconBox, ArkColors.withAlpha(light, 0.10F + 0.08F * hover));
        graphics.border(iconBox, 1.0F, ArkColors.withAlpha(light, 0.25F));
        graphics.icon(Icons.PLUS, iconBox.centerX() - ICON * 0.5F, iconBox.centerY() - ICON * 0.5F, ICON, ICON, light);
        graphics.text(hint, area.centerX() - metrics.width(hint, HINT) * 0.5F, iconBox.bottom() + GAP, HINT, ArkColors.TEXT_FAINT);
    }

    @Override
    protected Component name() {
        return Component.translatable("arkea.background.import");
    }

    @Override
    protected Component sub() {
        return Component.translatable("arkea.background.import.formats");
    }

    @Override
    protected void onPress() {
        this.action.run();
    }

    @Override
    protected Component narrationMessage() {
        return this.name();
    }
}
