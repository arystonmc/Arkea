package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import net.minecraft.network.chat.Component;

public class ArkTile extends ArkWidget {
    public static final float HEIGHT = 72.0F;
    private static final float PADDING_LEFT = 14.0F;
    private static final float PADDING_RIGHT = 16.0F;
    private static final float ICON_BOX = 36.0F;
    private static final float ICON_SIZE = 16.0F;
    private static final float GAP = 14.0F;
    private static final float LINE_GAP = 6.0F;
    private static final float CHEVRON_WIDTH = 5.0F;
    private static final float CHEVRON_HEIGHT = 8.0F;
    private static final float ICON_FILL_ALPHA = 0.10F;
    private static final float ICON_BORDER_ALPHA = 0.25F;
    private static final int FILL = ArkColors.ROW;
    private static final int BORDER = ArkColors.rgba(255, 255, 255, 0.07F);
    private static final TextStyle NAME = TextStyle.of(13.0F);
    private static final TextStyle DESCRIPTION = TextStyle.of(10.0F);

    private final Icon icon;
    private final Component name;
    private final Component description;
    private final Runnable action;

    public ArkTile(UiHost host, ItemContent content, Runnable action) {
        super(host);
        this.icon = content.icon();
        this.name = content.label();
        this.description = content.sub() != null ? content.sub() : Component.empty();
        this.action = action;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        float hover = this.hoverProgress();
        graphics.push();
        if (!this.isActive()) {
            graphics.fade(DISABLED_OPACITY);
        }
        graphics.fill(box, ArkColors.lerp(hover, FILL, ArkColors.ROW_HOVER));
        graphics.border(box, 1.0F, ArkColors.lerp(hover, BORDER, ArkColors.BORDER_OVERLAY));
        int light = Theme.accent().light();
        Box iconBox = new Box(box.x() + PADDING_LEFT, box.centerY() - ICON_BOX * 0.5F, ICON_BOX, ICON_BOX);
        graphics.fill(iconBox, ArkColors.withAlpha(light, ICON_FILL_ALPHA));
        graphics.border(iconBox, 1.0F, ArkColors.withAlpha(light, ICON_BORDER_ALPHA));
        float iconOffset = (ICON_BOX - ICON_SIZE) * 0.5F;
        graphics.icon(this.icon, iconBox.x() + iconOffset, iconBox.y() + iconOffset, ICON_SIZE, ICON_SIZE, light);
        TextMetrics metrics = graphics.metrics();
        float textX = iconBox.right() + GAP;
        float textWidth = box.right() - PADDING_RIGHT - CHEVRON_WIDTH - GAP - textX;
        float blockHeight = metrics.capHeight(NAME) + LINE_GAP + metrics.capHeight(DESCRIPTION);
        float nameY = box.centerY() - blockHeight * 0.5F;
        graphics.text(metrics.ellipsize(this.name.getString(), NAME, textWidth), textX, nameY, NAME, ArkColors.TEXT_PRIMARY);
        float descriptionY = nameY + metrics.capHeight(NAME) + LINE_GAP;
        String description = metrics.ellipsize(this.description.getString(), DESCRIPTION, textWidth);
        graphics.text(description, textX, descriptionY, DESCRIPTION, ArkColors.TEXT_DESCRIPTION);
        float chevronX = box.right() - PADDING_RIGHT - CHEVRON_WIDTH;
        int chevron = ArkColors.lerp(hover, ArkColors.TEXT_LABEL, ArkColors.TEXT_PRIMARY);
        graphics.icon(Icons.CHEVRON_RIGHT, chevronX, box.centerY() - CHEVRON_HEIGHT * 0.5F, CHEVRON_WIDTH, CHEVRON_HEIGHT, chevron);
        graphics.pop();
    }

    @Override
    protected void onPress() {
        this.action.run();
    }

    @Override
    protected Component narrationMessage() {
        return Component.empty().append(this.name).append(", ").append(this.description);
    }
}
