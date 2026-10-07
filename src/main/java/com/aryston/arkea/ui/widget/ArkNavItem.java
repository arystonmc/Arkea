package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.Accent;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class ArkNavItem extends ArkWidget {
    public static final float HEIGHT = 32.0F;
    private static final int TRANSITION = 200;
    private static final float PADDING_X = 12.0F;
    private static final float ICON_SIZE = 14.0F;
    private static final float GAP = 10.0F;
    private static final float EXTERNAL_SIZE = 10.0F;
    private static final float GLOW_BLUR = 14.0F;
    private static final float GLOW_OFFSET = 4.0F;
    private static final float MARKER_WIDTH = 2.0F;
    private static final int HOVER_FILL = ArkColors.rgba(255, 255, 255, 0.05F);
    private static final TextStyle LABEL = TextStyle.of(13.0F);

    private final Icon icon;
    private final @Nullable Identifier logo;
    private final Component label;
    private final boolean selected;
    private final boolean external;
    private final Runnable action;

    public ArkNavItem(UiHost host, NavEntry entry, boolean selected, Runnable action) {
        super(host, TRANSITION, Easing.EASE);
        this.icon = entry.icon();
        this.logo = entry.logo();
        this.label = entry.label();
        this.external = entry.external();
        this.selected = selected;
        this.action = action;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        Accent accent = Theme.accent();
        int color;
        if (this.selected) {
            graphics.shadow(box, GLOW_BLUR, GLOW_OFFSET, accent.glow());
            graphics.fill(box, accent.base());
            graphics.fill(box.x(), box.y(), MARKER_WIDTH, box.height(), accent.light());
            color = ArkColors.TEXT_PRIMARY;
        } else {
            float hover = this.hoverProgress();
            graphics.fill(box, ArkColors.multiplyAlpha(HOVER_FILL, hover));
            color = ArkColors.lerp(hover, ArkColors.TEXT_MUTED, ArkColors.TEXT_PRIMARY);
        }
        float iconX = box.x() + PADDING_X;
        Box iconBox = new Box(iconX, box.centerY() - ICON_SIZE * 0.5F, ICON_SIZE, ICON_SIZE);
        if (this.logo != null) {
            graphics.image(this.logo, iconBox, 0.0F, 0.0F, 1.0F, 1.0F, ArkColors.TEXT_PRIMARY);
        } else {
            graphics.icon(this.icon, iconBox.x(), iconBox.y(), ICON_SIZE, ICON_SIZE, color);
        }
        float textX = iconX + ICON_SIZE + GAP;
        float textWidth = box.right() - PADDING_X - textX - (this.external ? EXTERNAL_SIZE + GAP : 0.0F);
        String text = graphics.metrics().ellipsize(this.label.getString(), LABEL, textWidth);
        graphics.text(text, textX, box.centerY() - graphics.metrics().capHeight(LABEL) * 0.5F, LABEL, color);
        if (this.external) {
            float externalX = box.right() - PADDING_X - EXTERNAL_SIZE;
            graphics.icon(Icons.EXTERNAL, externalX, box.centerY() - EXTERNAL_SIZE * 0.5F, EXTERNAL_SIZE, EXTERNAL_SIZE, color);
        }
    }

    @Override
    protected void onPress() {
        if (!this.selected) {
            this.action.run();
        }
    }

    @Override
    protected Component narrationMessage() {
        return this.label;
    }
}
