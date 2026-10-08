package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class ChipsRow implements PanelRow {
    private static final float PADDING = 14.0F;
    private static final float HEADER_HEIGHT = 40.0F;
    private static final float LINE_GAP = 6.0F;
    private static final TextStyle NAME = TextStyle.of(13.0F);
    private static final TextStyle DESCRIPTION = TextStyle.of(10.0F);

    private final Component name;
    private final Component description;
    private final List<ArkChip> chips;
    private final Transition hover = new Transition(0.0F, Motion.HOVER, Easing.EASE);
    private @Nullable Component tooltip;
    private Box bounds = Box.EMPTY;
    private boolean hovered;

    public ChipsRow(Component name, Component description, List<ArkChip> chips) {
        this.name = name;
        this.description = description;
        this.chips = List.copyOf(chips);
    }

    public ChipsRow tooltip(@Nullable Component text) {
        this.tooltip = text;
        return this;
    }

    @Override
    public @Nullable Component tooltip() {
        return this.tooltip;
    }

    @Override
    public float height() {
        return HEADER_HEIGHT + ArkChip.HEIGHT + PADDING;
    }

    @Override
    public float height(float width) {
        return HEADER_HEIGHT + this.lines(width).size() * (ArkChip.HEIGHT + ArkChip.GAP) - ArkChip.GAP + PADDING;
    }

    private List<List<ArkChip>> lines(float width) {
        List<List<ArkChip>> lines = new ArrayList<>();
        List<ArkChip> line = new ArrayList<>();
        float available = width - PADDING * 2.0F;
        float used = 0.0F;
        for (ArkChip chip : this.chips) {
            float chipWidth = chip.preferredWidth();
            if (!line.isEmpty() && used + chipWidth > available) {
                lines.add(line);
                line = new ArrayList<>();
                used = 0.0F;
            }
            line.add(chip);
            used += chipWidth + ArkChip.GAP;
        }
        if (!line.isEmpty()) {
            lines.add(line);
        }
        return lines;
    }

    @Override
    public void place(Box newBounds) {
        this.bounds = newBounds;
        float y = newBounds.y() + HEADER_HEIGHT;
        for (List<ArkChip> line : this.lines(newBounds.width())) {
            float x = newBounds.x() + PADDING;
            for (ArkChip chip : line) {
                float width = chip.preferredWidth();
                chip.setBounds(new Box(x, y, width, ArkChip.HEIGHT));
                x += width + ArkChip.GAP;
            }
            y += ArkChip.HEIGHT + ArkChip.GAP;
        }
    }

    @Override
    public Box bounds() {
        return this.bounds;
    }

    @Override
    public List<ArkWidget> widgets() {
        return List.copyOf(this.chips);
    }

    @Override
    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        Box box = this.bounds;
        long now = graphics.now();
        this.hovered = graphics.visible(graphics.canvasBox(box)).contains(mouseX, mouseY);
        this.hover.setTarget(this.hovered ? 1.0F : 0.0F, now);
        float hoverAmount = this.hover.value(now);
        graphics.fill(box, ArkColors.lerp(hoverAmount, ArkColors.ROW, ArkColors.ROW_HOVER));
        graphics.border(box, 1.0F, ArkColors.lerp(hoverAmount, ArkColors.BORDER_SUBTLE, ArkColors.BORDER_OVERLAY));
        TextMetrics metrics = graphics.metrics();
        float textWidth = box.width() - PADDING * 2.0F;
        float nameY = box.y() + PADDING;
        graphics.text(metrics.ellipsize(this.name.getString(), NAME, textWidth), box.x() + PADDING, nameY, NAME, ArkColors.TEXT_PRIMARY);
        float descriptionX = box.x() + PADDING + metrics.width(this.name.getString(), NAME) + LINE_GAP * 2.0F;
        String description = metrics.ellipsize(this.description.getString(), DESCRIPTION, box.right() - PADDING - descriptionX);
        graphics.text(description, descriptionX, nameY + metrics.capHeight(NAME) - metrics.capHeight(DESCRIPTION), DESCRIPTION, ArkColors.TEXT_DESCRIPTION);
        for (ArkChip chip : this.chips) {
            chip.render(graphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean isHovered() {
        return this.hovered;
    }
}
