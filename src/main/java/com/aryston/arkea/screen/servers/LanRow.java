package com.aryston.arkea.screen.servers;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.UiHost;
import java.util.function.BooleanSupplier;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.server.LanServer;
import net.minecraft.network.chat.Component;

final class LanRow extends ArkWidget {
    private static final float PADDING_LEFT = 12.0F;
    private static final float PADDING_RIGHT = 18.0F;
    private static final float GAP = 14.0F;
    private static final float TILE = 40.0F;
    private static final float ICON = 18.0F;
    private static final float LINE_GAP = 8.0F;
    private static final float SELECTED_FILL = 0.12F;
    private static final float TILE_FILL = 0.18F;
    private static final int BORDER = ArkColors.rgba(255, 255, 255, 0.07F);
    private static final TextStyle NAME = TextStyle.of(14.0F);
    private static final TextStyle DETAILS = TextStyle.of(11.0F);

    private final LanServer server;
    private final boolean hideAddress;
    private final BooleanSupplier selected;
    private final Runnable select;
    private final Runnable join;

    LanRow(UiHost host, LanServer server, boolean hideAddress, BooleanSupplier selected, Runnable select, Runnable join) {
        super(host);
        this.server = server;
        this.hideAddress = hideAddress;
        this.selected = selected;
        this.select = select;
        this.join = join;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        boolean isSelected = this.selected.getAsBoolean();
        int accent = Theme.accent().base();
        graphics.fill(box, isSelected ? ArkColors.withAlpha(accent, SELECTED_FILL) : ArkColors.lerp(this.hoverProgress(), ArkColors.ROW, ArkColors.ROW_HOVER));
        graphics.border(box, 1.0F, isSelected ? accent : BORDER);
        Box tile = new Box(box.x() + PADDING_LEFT, box.centerY() - TILE * 0.5F, TILE, TILE);
        graphics.fill(tile, ArkColors.withAlpha(Theme.accent().light(), TILE_FILL));
        graphics.icon(Icons.WORLD, tile.centerX() - ICON * 0.5F, tile.centerY() - ICON * 0.5F, ICON, ICON, Theme.accent().light());
        TextMetrics metrics = graphics.metrics();
        float textX = tile.right() + GAP;
        float width = box.right() - PADDING_RIGHT - textX;
        float block = metrics.capHeight(NAME) + LINE_GAP + metrics.capHeight(DETAILS);
        float y = box.centerY() - block * 0.5F;
        graphics.text(metrics.ellipsize(this.server.getMotd(), NAME, width), textX, y, NAME, ArkColors.TEXT_PRIMARY);
        Component address = this.hideAddress ? Component.translatable("selectServer.hiddenAddress") : Component.literal(this.server.getAddress());
        String details = Component.translatable("arkea.servers.lan.details", address).getString();
        graphics.text(metrics.ellipsize(details, DETAILS, width), textX, y + metrics.capHeight(NAME) + LINE_GAP, DETAILS, ArkColors.TEXT_MUTED);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean handled = super.mouseClicked(event, doubleClick);
        if (handled && doubleClick) {
            this.join.run();
        }
        return handled;
    }

    @Override
    protected void onPress() {
        if (this.selected.getAsBoolean() && this.host.showsKeyboardFocus()) {
            this.join.run();
            return;
        }
        this.select.run();
    }

    @Override
    protected Component narrationMessage() {
        return Component.translatable("narrator.select", this.server.getMotd());
    }
}
