package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ArkSegmented extends ArkWidget {
    public static final float HEIGHT = 32.0F;
    private static final float PADDING = 2.0F;
    private static final float SEGMENT_PADDING = 12.0F;
    private static final float GLOW_BLUR = 8.0F;
    private static final float GLOW_OFFSET = 2.0F;
    private static final TextStyle LABEL = TextStyle.of(11.0F);

    private final Component name;
    private final List<Component> labels;
    private final IntSupplier selected;
    private final IntConsumer select;
    private int pending;

    public ArkSegmented(UiHost host, Component name, List<Component> labels, IntSupplier selected, IntConsumer select) {
        super(host);
        this.name = name;
        this.labels = labels;
        this.selected = selected;
        this.select = select;
    }

    public float preferredWidth() {
        float width = PADDING * 2.0F;
        for (Component label : this.labels) {
            width += this.segmentWidth(this.host.metrics(), label);
        }
        return width;
    }

    private float segmentWidth(TextMetrics metrics, Component label) {
        return metrics.width(label.getString(), LABEL) + SEGMENT_PADDING * 2.0F;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        graphics.fill(box, ArkColors.CONTROL_FILL);
        graphics.border(box, 1.0F, ArkColors.BORDER_DEFAULT);
        TextMetrics metrics = graphics.metrics();
        int current = this.selected.getAsInt();
        float mouse = this.isHovered() ? this.localMouseX() : Float.NaN;
        float x = box.x() + PADDING;
        for (int index = 0; index < this.labels.size(); index++) {
            Component label = this.labels.get(index);
            float width = this.segmentWidth(metrics, label);
            Box segment = new Box(x, box.y() + PADDING, width, box.height() - PADDING * 2.0F);
            boolean hot = mouse >= segment.x() && mouse < segment.right();
            if (index == current) {
                graphics.shadow(segment, GLOW_BLUR, GLOW_OFFSET, Theme.accent().glow());
                graphics.fill(segment, Theme.accent().base());
            } else if (hot) {
                graphics.fill(segment, ArkColors.CONTROL_HOVER);
            }
            int color = index == current || hot ? ArkColors.TEXT_PRIMARY : ArkColors.TEXT_MUTED;
            graphics.text(label.getString(), segment.x() + SEGMENT_PADDING, segment.centerY() - metrics.capHeight(LABEL) * 0.5F, LABEL, color);
            x += width;
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!super.mouseClicked(event, doubleClick)) {
            return false;
        }
        float local = this.localX(this.host.uiScale().toDesign(event.x()));
        float x = this.bounds().x() + PADDING;
        this.pending = this.selected.getAsInt();
        for (int index = 0; index < this.labels.size(); index++) {
            float width = this.segmentWidth(this.host.metrics(), this.labels.get(index));
            if (local >= x && local < x + width) {
                this.pending = index;
            }
            x += width;
        }
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.isActive() && this.isFocused() && (event.isLeft() || event.isRight())) {
            int next = Math.floorMod(this.selected.getAsInt() + (event.isLeft() ? -1 : 1), this.labels.size());
            this.pending = next;
            this.activate();
            return true;
        }
        if (event.isSelection()) {
            this.pending = (this.selected.getAsInt() + 1) % this.labels.size();
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
