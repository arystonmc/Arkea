package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.Theme;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.jspecify.annotations.Nullable;

public abstract class ArkWidget implements GuiEventListener, NarratableEntry {
    private static final float FOCUS_RING_GAP = 1.0F;
    private static final float FOCUS_RING_WIDTH = 1.0F;
    private static final float CLICK_VOLUME = 1.0F;
    protected static final float DISABLED_OPACITY = 0.4F;

    protected final UiHost host;
    private final Transition hover;
    private final Transition press = new Transition(0.0F, Motion.PRESS, Easing.EASE);
    private Box bounds = Box.EMPTY;
    private Box canvasBox = Box.EMPTY;
    private Box visibleBox = Box.EMPTY;
    private boolean active = true;
    private boolean focused;
    private boolean hovered;
    private boolean pressed;
    private float mouseX;
    private @Nullable Component tooltip;
    private @Nullable String key;

    protected ArkWidget(UiHost host) {
        this(host, Motion.HOVER, Easing.EASE);
    }

    protected ArkWidget(UiHost host, int hoverMs, Easing hoverEasing) {
        this.host = host;
        this.hover = new Transition(0.0F, hoverMs, hoverEasing);
    }

    public final void render(UiGraphics graphics, float mouseX, float mouseY) {
        long now = this.host.now();
        this.canvasBox = graphics.canvasBox(this.bounds);
        this.visibleBox = graphics.visible(this.canvasBox);
        this.mouseX = mouseX;
        this.hovered = this.containsPoint(mouseX, mouseY);
        this.hover.setTarget(this.isHighlighted() ? 1.0F : 0.0F, now);
        this.press.setTarget(this.pressed && this.active ? 1.0F : 0.0F, now);
        if (this.hovered) {
            graphics.cursor(this.active ? this.cursorType() : CursorTypes.NOT_ALLOWED);
        }
        graphics.push();
        graphics.translate(this.contentShiftX(), this.pressProgress());
        this.renderWidget(graphics);
        if (this.showsFocusRing()) {
            this.renderFocusRing(graphics);
        }
        graphics.pop();
    }

    protected CursorType cursorType() {
        return CursorTypes.POINTING_HAND;
    }

    protected float contentShiftX() {
        return 0.0F;
    }

    protected abstract void renderWidget(UiGraphics graphics);

    protected abstract void onPress();

    protected abstract Component narrationMessage();

    protected boolean isHighlighted() {
        return this.active && (this.hovered || this.focused && this.host.showsKeyboardFocus());
    }

    protected float hoverProgress() {
        return this.hover.value(this.host.now());
    }

    protected float pressProgress() {
        return this.press.value(this.host.now());
    }

    protected void renderFocusRing(UiGraphics graphics) {
        graphics.border(this.bounds.expand(FOCUS_RING_GAP + FOCUS_RING_WIDTH), FOCUS_RING_WIDTH, Theme.accent().light());
    }

    private boolean showsFocusRing() {
        return this.focused && this.active && this.host.showsKeyboardFocus();
    }

    public Box bounds() {
        return this.bounds;
    }

    public void setBounds(Box bounds) {
        this.bounds = bounds;
        this.canvasBox = bounds;
        this.visibleBox = bounds;
    }

    public void hide() {
        this.canvasBox = Box.EMPTY;
        this.visibleBox = Box.EMPTY;
        this.hovered = false;
    }

    public Box hitBox() {
        return this.visibleBox;
    }

    public Box canvasBox() {
        return this.canvasBox;
    }

    protected boolean containsPoint(float x, float y) {
        Box box = this.hitBox();
        return new Box(box.x(), box.y(), box.width() + this.contentReachX(), box.height()).contains(x, y);
    }

    protected float localX(float canvasX) {
        if (this.canvasBox.width() <= 0.0F) {
            return this.bounds.x();
        }
        return this.bounds.x() + (canvasX - this.canvasBox.x()) * this.bounds.width() / this.canvasBox.width();
    }

    protected float localMouseX() {
        return this.localX(this.mouseX);
    }

    protected float contentReachX() {
        return 0.0F;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isHovered() {
        return this.hovered;
    }

    public @Nullable String key() {
        return this.key;
    }

    public ArkWidget key(String value) {
        this.key = value;
        return this;
    }

    public @Nullable Component tooltip() {
        return this.tooltip;
    }

    public void setTooltip(@Nullable Component tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!this.isActive() || event.button() != InputConstants.MOUSE_BUTTON_LEFT || !this.isMouseOver(event.x(), event.y())) {
            return false;
        }
        this.pressed = true;
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (!this.pressed) {
            return false;
        }
        this.pressed = false;
        if (this.isActive() && this.isMouseOver(event.x(), event.y())) {
            this.activate();
        }
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!this.isActive() || !this.focused || !event.isSelection()) {
            return false;
        }
        this.activate();
        return true;
    }

    public void activate() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, CLICK_VOLUME));
        this.press.snap(1.0F);
        this.onPress();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        UiScale scale = this.host.uiScale();
        return this.containsPoint(scale.toDesign(mouseX), scale.toDesign(mouseY));
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
        if (!focused) {
            this.pressed = false;
        }
    }

    @Override
    public boolean isFocused() {
        return this.focused;
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent navigationEvent) {
        if (!this.isActive()) {
            return null;
        }
        return this.focused ? null : ComponentPath.leaf(this);
    }

    @Override
    public ScreenRectangle getRectangle() {
        UiScale scale = this.host.uiScale();
        Box box = this.canvasBox;
        int left = Math.round(scale.toGui(box.x()));
        int top = Math.round(scale.toGui(box.y()));
        int right = Math.round(scale.toGui(box.right()));
        int bottom = Math.round(scale.toGui(box.bottom()));
        return new ScreenRectangle(left, top, right - left, bottom - top);
    }

    @Override
    public NarrationPriority narrationPriority() {
        if (this.focused) {
            return NarrationPriority.FOCUSED;
        }
        return this.hovered ? NarrationPriority.HOVERED : NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.button", this.narrationMessage()));
        if (this.tooltip != null) {
            output.add(NarratedElementType.HINT, this.tooltip);
        }
        if (this.active) {
            String usage = this.focused ? "narration.button.usage.focused" : "narration.button.usage.hovered";
            output.add(NarratedElementType.USAGE, Component.translatable(usage));
        }
    }
}
