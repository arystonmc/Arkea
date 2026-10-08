package com.aryston.arkea.screen.vanilla;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.ButtonPainter;
import com.aryston.arkea.ui.widget.ButtonVariant;
import com.mojang.blaze3d.platform.Window;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public abstract class VanillaSkin {
    private static final float FOCUS_GAP = 2.0F;
    private final Map<Screen, Long> shownAt = new WeakHashMap<>();

    public abstract boolean enabled();

    public abstract boolean handles(Screen screen);

    protected abstract void draw(Frame frame, Screen screen);

    protected boolean drawsBackground() {
        return true;
    }

    public final void render(Screen screen, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        UiScale scale = UiScale.compute(window.getWidth(), window.getHeight(), window.getGuiScale());
        long now = Util.getMillis();
        long since = now - this.shownAt.computeIfAbsent(screen, key -> now);
        if (this.drawsBackground()) {
            screen.extractBackground(graphics, mouseX, mouseY, partialTick);
        }
        graphics.nextStratum();
        UiGraphics ui = new UiGraphics(graphics, scale, minecraft.font, now);
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale.poseScale());
        this.draw(new Frame(ui, scale, mouseX, mouseY, minecraft.getLastInputType().isKeyboard(), since), screen);
        graphics.pose().popMatrix();
    }

    public record Frame(UiGraphics graphics, UiScale scale, int guiMouseX, int guiMouseY, boolean keyboard, long since) {
        public float mouseX() {
            return this.scale.toDesign(this.guiMouseX);
        }

        public float mouseY() {
            return this.scale.toDesign(this.guiMouseY);
        }

        public void place(AbstractWidget widget, Box box) {
            int left = Math.round(this.scale.toGui(box.x()));
            int top = Math.round(this.scale.toGui(box.y()));
            widget.setX(left);
            widget.setY(top);
            widget.setWidth(Math.round(this.scale.toGui(box.right())) - left);
            widget.setHeight(Math.round(this.scale.toGui(box.bottom())) - top);
        }

        public boolean highlighted(AbstractWidget widget) {
            return widget.active && (widget.isMouseOver(this.guiMouseX, this.guiMouseY) || this.keyboard && widget.isFocused());
        }

        public void button(AbstractWidget widget, Box box, ButtonVariant variant, @Nullable Icon leading, @Nullable Icon trailing) {
            this.place(widget, box);
            boolean highlighted = this.highlighted(widget);
            ButtonPainter.paint(this.graphics, box, widget.getMessage(), variant, highlighted ? 1.0F : 0.0F, widget.active, leading, trailing);
            this.focusRing(widget, box);
        }

        public void focusRing(AbstractWidget widget, Box box) {
            if (this.keyboard && widget.isFocused() && widget.active) {
                this.graphics.border(box.expand(FOCUS_GAP), 1.0F, Theme.accent().light());
            }
        }
    }
}
