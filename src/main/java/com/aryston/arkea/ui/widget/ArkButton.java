package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.UiGraphics;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class ArkButton extends ArkWidget {
    public static final float HEIGHT = 32.0F;

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
        return ButtonPainter.width(this.host.metrics(), this.label, this.leadingIcon, this.trailingIcon);
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        ButtonPainter.paint(graphics, this.bounds(), this.label, this.variant, this.hoverProgress(), this.isActive(), this.leadingIcon, this.trailingIcon);
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
