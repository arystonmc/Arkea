package com.aryston.arkea.ui.screen;

import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.overlay.ArkDialog;
import com.aryston.arkea.ui.overlay.ArkTooltip;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.UiHost;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public abstract class ArkScreen extends Screen implements UiHost {
    private static final float BLUR_THRESHOLD = 1.0F;
    private static final float OUTSIDE = Float.NEGATIVE_INFINITY;

    private final List<ArkWidget> arkWidgets = new ArrayList<>();
    private final ArkTooltip tooltip = new ArkTooltip();
    private UiScale scale = UiScale.compute((int) UiScale.DESIGN_WIDTH, (int) UiScale.DESIGN_HEIGHT, 1);
    private TextMetrics metrics;
    private long openedAt;
    private long leftAt;
    private @Nullable Runnable destination;
    private boolean navigating;
    private @Nullable ArkDialog dialog;

    protected ArkScreen(Component title) {
        super(title);
        this.metrics = new TextMetrics(this.font, this.scale);
    }

    protected abstract void buildUi();

    protected abstract void renderUi(UiGraphics graphics, float mouseX, float mouseY);

    protected abstract int exitDuration();

    protected boolean blursBackground() {
        return false;
    }

    @Override
    public void added() {
        super.added();
        this.openedAt = Util.getMillis();
        this.destination = null;
        this.navigating = false;
    }

    @Override
    protected final void init() {
        Window window = this.minecraft.getWindow();
        this.scale = UiScale.compute(window.getWidth(), window.getHeight(), window.getGuiScale());
        this.metrics = new TextMetrics(this.font, this.scale);
        this.arkWidgets.clear();
        this.buildUi();
        if (this.dialog != null) {
            this.dialog.layout(this.scale);
        }
    }

    protected <T extends ArkWidget> T add(T widget) {
        this.arkWidgets.add(widget);
        this.addWidget(widget);
        return widget;
    }

    @Override
    public UiScale uiScale() {
        return this.scale;
    }

    @Override
    public TextMetrics metrics() {
        return this.metrics;
    }

    @Override
    public long now() {
        return Util.getMillis();
    }

    @Override
    public boolean showsKeyboardFocus() {
        return this.minecraft.getLastInputType().isKeyboard();
    }

    protected long sinceOpened() {
        return this.now() - this.openedAt;
    }

    protected boolean isLeaving() {
        return this.destination != null;
    }

    protected long sinceLeft() {
        return this.now() - this.leftAt;
    }

    protected void navigate(Supplier<Screen> next) {
        this.leave(() -> this.minecraft.gui.setScreen(next.get()));
    }

    protected void leave(Runnable action) {
        if (this.isLeaving()) {
            return;
        }
        this.destination = action;
        this.leftAt = this.now();
        this.clearFocus();
    }

    protected void openDialog(ArkDialog newDialog) {
        this.dialog = newDialog;
        newDialog.layout(this.scale);
        newDialog.open(this.now());
        this.clearFocus();
        List<? extends ArkWidget> widgets = newDialog.widgets();
        if (!widgets.isEmpty() && this.showsKeyboardFocus()) {
            this.setFocused(widgets.getFirst());
        }
    }

    protected void closeDialog() {
        if (this.dialog == null) {
            return;
        }
        this.dialog.close(this.now());
        this.clearFocus();
    }

    @Override
    public List<? extends GuiEventListener> children() {
        if (this.dialog != null) {
            return this.dialog.isOpen() ? this.dialog.widgets() : List.of();
        }
        return super.children();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (this.isLeaving() && this.sinceLeft() >= this.exitDuration() && !this.navigating) {
            this.navigating = true;
            this.destination.run();
            return;
        }
        long now = this.now();
        UiGraphics ui = new UiGraphics(graphics, this.scale, this.font, now);
        float designX = this.scale.toDesign(mouseX);
        float designY = this.scale.toDesign(mouseY);
        boolean contentInteractive = this.dialog == null && !this.isLeaving();
        float contentX = contentInteractive ? designX : OUTSIDE;
        graphics.pose().pushMatrix();
        graphics.pose().scale(this.scale.poseScale());
        this.renderUi(ui, contentX, designY);
        this.tooltip.track(contentInteractive ? this.hoveredWidget() : null, now, designX, designY);
        this.tooltip.render(ui);
        graphics.pose().popMatrix();
        this.renderDialog(graphics, ui, designX, designY);
    }

    private void renderDialog(GuiGraphicsExtractor graphics, UiGraphics ui, float mouseX, float mouseY) {
        if (this.dialog == null) {
            return;
        }
        long now = ui.now();
        if (this.dialog.isGone(now)) {
            this.dialog = null;
            return;
        }
        graphics.nextStratum();
        if (this.dialog.isOpen() && !this.blursBackground() && this.menuBlurEnabled()) {
            graphics.blurBeforeThisStratum();
        }
        graphics.pose().pushMatrix();
        graphics.pose().scale(this.scale.poseScale());
        this.dialog.render(ui, mouseX, mouseY);
        graphics.pose().popMatrix();
    }

    protected boolean menuBlurEnabled() {
        return this.minecraft.options.getMenuBackgroundBlurriness() >= BLUR_THRESHOLD;
    }

    private @Nullable ArkWidget hoveredWidget() {
        for (ArkWidget widget : this.arkWidgets) {
            if (widget.isHovered()) {
                return widget;
            }
        }
        return null;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.isLeaving()) {
            return true;
        }
        if (this.dialog == null) {
            return super.mouseClicked(event, doubleClick);
        }
        if (!this.dialog.isOpen()) {
            return true;
        }
        boolean handled = super.mouseClicked(event, doubleClick);
        boolean outside = !this.dialog.contains(this.scale.toDesign(event.x()), this.scale.toDesign(event.y()));
        if (!handled && outside && this.dialog.dismissesOnScrimClick()) {
            this.dialog.cancel();
        }
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.isLeaving()) {
            return true;
        }
        if (this.dialog != null && event.isEscape()) {
            if (this.dialog.isOpen()) {
                this.dialog.cancel();
            }
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    protected void updateNarrationState(NarrationElementOutput output) {
        if (this.dialog == null || !this.dialog.isOpen()) {
            super.updateNarrationState(output);
            return;
        }
        output.add(NarratedElementType.TITLE, this.dialog.title());
        output.add(NarratedElementType.HINT, this.dialog.body());
        for (ArkWidget widget : this.dialog.widgets()) {
            if (widget.isFocused()) {
                widget.updateNarration(output.nest());
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
