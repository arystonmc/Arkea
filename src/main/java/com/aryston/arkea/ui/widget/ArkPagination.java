package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ArkPagination extends ArkWidget {
    public static final float HEIGHT = 24.0F;
    private static final float ARROW = 24.0F;
    private static final float GAP = 4.0F;
    private static final float CHEVRON_WIDTH = 5.0F;
    private static final float CHEVRON_HEIGHT = 8.0F;
    private static final int ARROW_TIME = 120;
    private static final TextStyle LABEL = TextStyle.of(11.0F);

    private final IntSupplier page;
    private final IntSupplier pages;
    private final IntConsumer select;
    private final Transition leftHover = new Transition(0.0F, ARROW_TIME, Easing.EASE);
    private final Transition rightHover = new Transition(0.0F, ARROW_TIME, Easing.EASE);
    private int pendingDirection = 1;

    public ArkPagination(UiHost host, IntSupplier page, IntSupplier pages, IntConsumer select) {
        super(host);
        this.page = page;
        this.pages = pages;
        this.select = select;
    }

    public float preferredWidth() {
        String widest = this.label(this.pages.getAsInt() - 1);
        return ARROW * 2.0F + GAP * 2.0F + this.host.metrics().width(widest, LABEL);
    }

    private String label(int index) {
        return Component.translatable("arkea.pagination", index + 1, Math.max(1, this.pages.getAsInt())).getString();
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        long now = graphics.now();
        float mouse = this.isHovered() ? this.localMouseX() : Float.NaN;
        int current = this.page.getAsInt();
        this.leftHover.setTarget(mouse < box.x() + ARROW && current > 0 ? 1.0F : 0.0F, now);
        this.rightHover.setTarget(mouse >= box.right() - ARROW && current < this.pages.getAsInt() - 1 ? 1.0F : 0.0F, now);
        this.renderArrow(graphics, new Box(box.x(), box.centerY() - ARROW * 0.5F, ARROW, ARROW), this.leftHover.value(now), true, current > 0);
        Box right = new Box(box.right() - ARROW, box.centerY() - ARROW * 0.5F, ARROW, ARROW);
        this.renderArrow(graphics, right, this.rightHover.value(now), false, current < this.pages.getAsInt() - 1);
        TextMetrics metrics = graphics.metrics();
        String text = this.label(current);
        graphics.text(text, box.centerX() - metrics.width(text, LABEL) * 0.5F, box.centerY() - metrics.capHeight(LABEL) * 0.5F, LABEL, ArkColors.TEXT_SOFT);
    }

    private void renderArrow(UiGraphics graphics, Box area, float hover, boolean left, boolean enabled) {
        graphics.fill(area, ArkColors.multiplyAlpha(ArkColors.CONTROL_HOVER, hover));
        int color = enabled ? ArkColors.lerp(hover, ArkColors.TEXT_MUTED, ArkColors.TEXT_PRIMARY) : ArkColors.TEXT_DISABLED;
        graphics.icon(left ? Icons.CHEVRON_LEFT : Icons.CHEVRON_RIGHT, area.centerX() - CHEVRON_WIDTH * 0.5F, area.centerY() - CHEVRON_HEIGHT * 0.5F,
            CHEVRON_WIDTH, CHEVRON_HEIGHT, color);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!super.mouseClicked(event, doubleClick)) {
            return false;
        }
        float x = this.localX(this.host.uiScale().toDesign(event.x()));
        this.pendingDirection = x < this.bounds().centerX() ? -1 : 1;
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.isActive() && this.isFocused() && (event.isLeft() || event.isRight())) {
            this.pendingDirection = event.isLeft() ? -1 : 1;
            this.activate();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    protected void onPress() {
        int target = Math.clamp(this.page.getAsInt() + this.pendingDirection, 0, Math.max(0, this.pages.getAsInt() - 1));
        if (target != this.page.getAsInt()) {
            this.select.accept(target);
        }
    }

    @Override
    protected Component narrationMessage() {
        return Component.literal(this.label(this.page.getAsInt()));
    }
}
