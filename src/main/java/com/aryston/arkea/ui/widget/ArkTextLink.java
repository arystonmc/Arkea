package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import net.minecraft.network.chat.Component;

public class ArkTextLink extends ArkWidget {
    private static final float UNDERLINE_GAP = 2.0F;

    private final Component text;
    private final TextStyle style;
    private final int color;
    private final Runnable action;

    public ArkTextLink(UiHost host, Component text, TextStyle style, int color, Runnable action) {
        super(host);
        this.text = text;
        this.style = style;
        this.color = color;
        this.action = action;
    }

    public float preferredWidth() {
        return this.host.metrics().width(this.text.getString(), this.style);
    }

    public float preferredHeight() {
        return this.host.metrics().capHeight(this.style);
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        float hover = this.hoverProgress();
        Box box = this.bounds();
        int tinted = ArkColors.lerp(hover, this.color, ArkColors.TEXT_PRIMARY);
        graphics.text(this.text.getString(), box.x(), box.y(), this.style, tinted);
        if (hover > 0.0F) {
            graphics.fill(box.x(), box.bottom() + UNDERLINE_GAP, box.width(), graphics.scale().snapThickness(1.0F), ArkColors.multiplyAlpha(tinted, hover));
        }
    }

    @Override
    protected void onPress() {
        this.action.run();
    }

    @Override
    protected Component narrationMessage() {
        return this.text;
    }
}
