package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ArkSwatch extends ArkWidget {
    public static final float SIZE = 22.0F;
    public static final float GAP = 6.0F;
    private static final float OUTLINE = 2.0F;
    private static final float OUTLINE_GAP = 2.0F;
    private static final float CHECK_WIDTH = 10.0F;
    private static final float CHECK_HEIGHT = 8.0F;
    private static final int POP_TIME = 280;

    private final Component name;
    private final List<Component> labels;
    private final int[] colors;
    private final IntSupplier selected;
    private final IntConsumer select;
    private final Transition pop = new Transition(1.0F, POP_TIME, Easing.SPRING);
    private int shown = -1;
    private int pending;

    public ArkSwatch(UiHost host, Component name, List<Component> labels, int[] colors, IntSupplier selected, IntConsumer select) {
        super(host);
        this.name = name;
        this.labels = labels;
        this.colors = colors;
        this.selected = selected;
        this.select = select;
    }

    public float preferredWidth() {
        return this.colors.length * SIZE + (this.colors.length - 1) * GAP;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        int current = this.selected.getAsInt();
        if (current != this.shown) {
            this.shown = current;
            this.pop.snap(0.0F);
            this.pop.setTarget(1.0F, graphics.now());
        }
        float pop = this.pop.value(graphics.now());
        float mouse = this.isHovered() ? this.localMouseX() : Float.NaN;
        for (int index = 0; index < this.colors.length; index++) {
            Box swatch = new Box(box.x() + index * (SIZE + GAP), box.centerY() - SIZE * 0.5F, SIZE, SIZE);
            boolean hot = mouse >= swatch.x() && mouse < swatch.right();
            graphics.fill(swatch, ArkColors.brighten(this.colors[index], hot ? 1.15F : 1.0F));
            if (index == current) {
                graphics.border(swatch.expand(OUTLINE_GAP + OUTLINE), OUTLINE, ArkColors.TEXT_PRIMARY);
                float width = CHECK_WIDTH * pop;
                float height = CHECK_HEIGHT * pop;
                graphics.icon(Icons.CHECK, swatch.centerX() - width * 0.5F, swatch.centerY() - height * 0.5F, width, height, ArkColors.TEXT_PRIMARY);
            }
        }
    }

    @Override
    protected void renderFocusRing(UiGraphics graphics) {
        Box box = this.bounds();
        int current = this.selected.getAsInt();
        Box swatch = new Box(box.x() + current * (SIZE + GAP), box.centerY() - SIZE * 0.5F, SIZE, SIZE);
        graphics.border(swatch.expand(OUTLINE_GAP + OUTLINE + 2.0F), 1.0F, ArkColors.TEXT_SOFT);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!super.mouseClicked(event, doubleClick)) {
            return false;
        }
        float local = this.localX(this.host.uiScale().toDesign(event.x())) - this.bounds().x();
        this.pending = Math.clamp((int) (local / (SIZE + GAP)), 0, this.colors.length - 1);
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.isActive() && this.isFocused() && (event.isLeft() || event.isRight())) {
            this.pending = Math.floorMod(this.selected.getAsInt() + (event.isLeft() ? -1 : 1), this.colors.length);
            this.activate();
            return true;
        }
        if (event.isSelection()) {
            this.pending = (this.selected.getAsInt() + 1) % this.colors.length;
        }
        return super.keyPressed(event);
    }

    @Override
    protected void onPress() {
        if (this.pending != this.selected.getAsInt()) {
            this.select.accept(this.pending);
        }
    }

    @Override
    protected Component narrationMessage() {
        return CommonComponents.optionNameValue(this.name, this.labels.get(this.selected.getAsInt()));
    }
}
