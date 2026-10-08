package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Presence;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkButton;
import com.aryston.arkea.ui.widget.ArkIconButton;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.IconButtonStyle;
import com.aryston.arkea.ui.widget.UiHost;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class ArkSideSheet implements Overlay {
    public static final float WIDTH = 420.0F;
    private static final float PADDING = 24.0F;
    private static final float HEADER_HEIGHT = 72.0F;
    private static final float FOOTER_HEIGHT = 64.0F;
    private static final float CLOSE_SIZE = 30.0F;
    private static final float SUBTITLE_GAP = 6.0F;
    private static final float BUTTON_GAP = 8.0F;
    private static final float SLIDE = 24.0F;
    private static final float SHADOW_BLUR = 60.0F;
    private static final int IN_TIME = 320;
    private static final int OUT_TIME = 200;
    private static final TextStyle TITLE = TextStyle.of(16.0F);
    private static final TextStyle SUBTITLE = TextStyle.of(10.0F);

    private final Component title;
    private final Component subtitle;
    private final Runnable cancelAction;
    private final ArkIconButton close;
    private final List<ArkButton> buttons = new ArrayList<>();
    private final Presence scrim = Presence.of(Motion.SCRIM_IN, Motion.SCRIM_OUT);
    private final Presence panel = Presence.of(IN_TIME, OUT_TIME);
    private @Nullable DialogBody content;
    private Box frame = Box.EMPTY;

    public ArkSideSheet(UiHost host, Component title, Component subtitle, Runnable cancelAction) {
        this.title = title;
        this.subtitle = subtitle;
        this.cancelAction = cancelAction;
        Component closeLabel = Component.translatable("arkea.window.close");
        this.close = new ArkIconButton(host, Icons.CLOSE, closeLabel, IconButtonStyle.CLOSE, cancelAction);
    }

    public ArkSideSheet content(DialogBody body) {
        this.content = body;
        return this;
    }

    public ArkSideSheet button(ArkButton button) {
        this.buttons.add(button);
        return this;
    }

    @Override
    public void layout(UiScale scale) {
        this.frame = new Box(scale.canvasWidth() - WIDTH, 0.0F, WIDTH, scale.canvasHeight());
        this.close.setBounds(new Box(this.frame.right() - PADDING - CLOSE_SIZE, HEADER_HEIGHT * 0.5F - CLOSE_SIZE * 0.5F, CLOSE_SIZE, CLOSE_SIZE));
        if (this.content != null) {
            float bottom = this.buttons.isEmpty() ? this.frame.bottom() - PADDING : this.frame.bottom() - FOOTER_HEIGHT - PADDING;
            Box area = new Box(this.frame.x() + PADDING, HEADER_HEIGHT + PADDING, WIDTH - PADDING * 2.0F, bottom - HEADER_HEIGHT - PADDING);
            this.content.layout(new Box(area.x(), area.y(), area.width(), Math.min(area.height(), this.content.height(area.width()))));
        }
        float x = this.frame.right() - PADDING;
        float y = this.frame.bottom() - FOOTER_HEIGHT * 0.5F - ArkButton.HEIGHT * 0.5F;
        for (int index = this.buttons.size() - 1; index >= 0; index--) {
            ArkButton button = this.buttons.get(index);
            float width = button.preferredWidth();
            x -= width;
            button.setBounds(new Box(x, y, width, ArkButton.HEIGHT));
            x -= BUTTON_GAP;
        }
    }

    @Override
    public void open(long now) {
        this.scrim.show(now);
        this.panel.show(now);
    }

    @Override
    public void close(long now) {
        this.scrim.hide(now);
        this.panel.hide(now);
    }

    @Override
    public void cancel() {
        this.cancelAction.run();
    }

    @Override
    public boolean isOpen() {
        return this.panel.isShown();
    }

    @Override
    public boolean isGone(long now) {
        return this.scrim.isGone(now) && this.panel.isGone(now);
    }

    @Override
    public boolean dismissesOnScrimClick() {
        return true;
    }

    @Override
    public Component title() {
        return this.title;
    }

    @Override
    public Component body() {
        return this.subtitle;
    }

    @Override
    public List<? extends ArkWidget> widgets() {
        List<ArkWidget> widgets = new ArrayList<>();
        widgets.add(this.close);
        if (this.content != null) {
            widgets.addAll(this.content.widgets());
        }
        widgets.addAll(this.buttons);
        return widgets;
    }

    @Override
    public boolean contains(float x, float y) {
        return this.frame.contains(x, y);
    }

    @Override
    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        long now = graphics.now();
        UiScale scale = graphics.scale();
        graphics.push();
        graphics.fade(this.scrim.progress(now));
        graphics.fill(0.0F, 0.0F, scale.canvasWidth(), scale.canvasHeight(), ArkColors.SCRIM_IN_GAME);
        graphics.pop();
        float progress = this.panel.progress(now);
        if (progress <= 0.0F) {
            return;
        }
        float hoverX = this.panel.isShown() ? mouseX : Float.NEGATIVE_INFINITY;
        graphics.push();
        graphics.fade(progress);
        graphics.translate(SLIDE * (1.0F - progress), 0.0F);
        graphics.shadow(this.frame, SHADOW_BLUR, 0.0F, ArkColors.SHADOW_DIALOG);
        graphics.fill(this.frame, ArkColors.DIALOG);
        float line = scale.snapThickness(1.0F);
        graphics.fill(this.frame.x(), this.frame.y(), line, this.frame.height(), ArkColors.BORDER_OVERLAY);
        graphics.fill(this.frame.x(), HEADER_HEIGHT - line, WIDTH, line, ArkColors.BORDER_SUBTLE);
        TextMetrics metrics = graphics.metrics();
        float blockHeight = metrics.capHeight(TITLE) + SUBTITLE_GAP + metrics.capHeight(SUBTITLE);
        float titleY = HEADER_HEIGHT * 0.5F - blockHeight * 0.5F;
        float textWidth = WIDTH - PADDING * 3.0F - CLOSE_SIZE;
        graphics.text(metrics.ellipsize(this.title.getString(), TITLE, textWidth), this.frame.x() + PADDING, titleY, TITLE, ArkColors.TEXT_PRIMARY);
        graphics.text(metrics.ellipsize(this.subtitle.getString(), SUBTITLE, textWidth), this.frame.x() + PADDING,
            titleY + metrics.capHeight(TITLE) + SUBTITLE_GAP, SUBTITLE, ArkColors.TEXT_FAINT);
        this.close.render(graphics, hoverX, mouseY);
        if (this.content != null) {
            this.content.render(graphics, hoverX, mouseY);
        }
        if (!this.buttons.isEmpty()) {
            float footerY = this.frame.bottom() - FOOTER_HEIGHT;
            graphics.fill(this.frame.x(), footerY, WIDTH, line, ArkColors.BORDER_SUBTLE);
            for (ArkButton button : this.buttons) {
                button.render(graphics, hoverX, mouseY);
            }
        }
        graphics.pop();
    }
}
