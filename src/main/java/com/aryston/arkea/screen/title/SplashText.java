package com.aryston.arkea.screen.title;

import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Oscillation;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.UiHost;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;

final class SplashText extends ArkWidget {
    private static final float ROTATION = (float) Math.toRadians(-14.0);
    private static final float PULSE = 0.09F;
    private static final float SHADOW_OFFSET = 2.0F;
    private static final int SHADOW = ArkColors.rgb(0x3F3A10);
    private static final TextStyle STYLE = TextStyle.of(14.0F).shadow(SHADOW, SHADOW_OFFSET);

    private final Supplier<Component> source;
    private Component text;
    private float anchorX;
    private float anchorY;

    SplashText(UiHost host, Component first, Supplier<Component> source) {
        super(host);
        this.source = source;
        this.text = first;
    }

    void place(float x, float y) {
        this.anchorX = x;
        this.anchorY = y;
        this.layout();
    }

    private void layout() {
        float width = this.host.metrics().width(this.text.getString(), STYLE);
        this.setBounds(new Box(this.anchorX, this.anchorY, width, STYLE.size()));
    }

    @Override
    protected boolean containsPoint(float x, float y) {
        Box box = this.hitBox();
        float deltaX = x - box.centerX();
        float deltaY = y - box.centerY();
        float cos = (float) Math.cos(-ROTATION);
        float sin = (float) Math.sin(-ROTATION);
        float localX = deltaX * cos - deltaY * sin + box.centerX();
        float localY = deltaX * sin + deltaY * cos + box.centerY();
        return box.contains(localX, localY);
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        float pulse = 1.0F + PULSE * Oscillation.wave(graphics.now(), Motion.SPLASH_PULSE);
        graphics.push();
        graphics.rotateAround(ROTATION, box.centerX(), box.centerY());
        graphics.scaleAround(pulse, box.centerX(), box.centerY());
        float textY = box.centerY() - graphics.metrics().capHeight(STYLE) * 0.5F;
        graphics.text(this.text.getString(), box.x(), textY, STYLE, ArkColors.MINECRAFT_YELLOW);
        graphics.pop();
    }

    @Override
    protected void renderFocusRing(UiGraphics graphics) {
        graphics.push();
        graphics.rotateAround(ROTATION, this.bounds().centerX(), this.bounds().centerY());
        super.renderFocusRing(graphics);
        graphics.pop();
    }

    @Override
    protected void onPress() {
        this.text = this.source.get();
        this.layout();
    }

    @Override
    protected Component narrationMessage() {
        return this.text;
    }
}
