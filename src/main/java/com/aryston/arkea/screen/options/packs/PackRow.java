package com.aryston.arkea.screen.options.packs;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.PanelRow;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

final class PackRow implements PanelRow {
    static final float HEIGHT = 64.0F;
    static final float ACTION_SIZE = 30.0F;
    private static final float PADDING = 8.0F;
    private static final float ICON = 48.0F;
    private static final float GAP = 12.0F;
    private static final float ACTION_GAP = 6.0F;
    private static final float LINE_GAP = 7.0F;
    private static final float TAG_HEIGHT = 12.0F;
    private static final float TAG_PADDING = 4.0F;
    private static final float TAG_GAP = 8.0F;
    private static final float SELECTED_FILL_ALPHA = 0.08F;
    private static final float SELECTED_BORDER_ALPHA = 0.35F;
    private static final int BORDER = ArkColors.rgba(255, 255, 255, 0.07F);
    private static final TextStyle NAME = TextStyle.of(13.0F);
    private static final TextStyle DESCRIPTION = TextStyle.of(10.0F);
    private static final TextStyle TAG = TextStyle.of(8.0F).spacing(1.0F);

    private final Identifier icon;
    private final Component name;
    private final Component description;
    private final @Nullable Tag tag;
    private final boolean selected;
    private final List<ArkWidget> actions;
    private final @Nullable Component tooltip;
    private final Transition hover = new Transition(0.0F, Motion.HOVER, Easing.EASE);
    private Box bounds = Box.EMPTY;
    private boolean hovered;

    PackRow(Identifier icon, Component name, Component description, @Nullable Tag tag, boolean selected, List<ArkWidget> actions,
        @Nullable Component tooltip) {
        this.icon = icon;
        this.name = name;
        this.description = description;
        this.tag = tag;
        this.selected = selected;
        this.actions = actions;
        this.tooltip = tooltip;
    }

    @Override
    public float height() {
        return HEIGHT;
    }

    @Override
    public void place(Box newBounds) {
        this.bounds = newBounds;
        float x = newBounds.right() - PADDING;
        for (int index = this.actions.size() - 1; index >= 0; index--) {
            x -= ACTION_SIZE;
            this.actions.get(index).setBounds(new Box(x, newBounds.centerY() - ACTION_SIZE * 0.5F, ACTION_SIZE, ACTION_SIZE));
            x -= ACTION_GAP;
        }
    }

    @Override
    public Box bounds() {
        return this.bounds;
    }

    @Override
    public List<ArkWidget> widgets() {
        return this.actions;
    }

    @Override
    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        Box box = this.bounds;
        this.hovered = graphics.visible(graphics.canvasBox(box)).contains(mouseX, mouseY);
        this.hover.setTarget(this.hovered ? 1.0F : 0.0F, graphics.now());
        float hover = this.hover.value(graphics.now());
        int base = Theme.accent().base();
        int fill = this.selected ? ArkColors.withAlpha(base, SELECTED_FILL_ALPHA) : ArkColors.ROW;
        graphics.fill(box, ArkColors.lerp(hover * 0.5F, fill, ArkColors.ROW_HOVER));
        graphics.border(box, 1.0F, this.selected ? ArkColors.withAlpha(base, SELECTED_BORDER_ALPHA) : ArkColors.lerp(hover, BORDER, ArkColors.BORDER_OVERLAY));
        Box iconBox = new Box(box.x() + PADDING, box.centerY() - ICON * 0.5F, ICON, ICON);
        graphics.image(this.icon, iconBox, 0.0F, 0.0F, 1.0F, 1.0F, ArkColors.TEXT_PRIMARY);
        TextMetrics metrics = graphics.metrics();
        float textX = iconBox.right() + GAP;
        float right = (this.actions.isEmpty() ? box.right() - PADDING : this.actions.getFirst().bounds().x()) - GAP;
        float block = metrics.capHeight(NAME) + LINE_GAP + metrics.capHeight(DESCRIPTION);
        float nameY = box.centerY() - block * 0.5F;
        float tagWidth = this.tag != null ? metrics.width(this.tag.text().getString(), TAG) + TAG_PADDING * 2.0F + TAG_GAP : 0.0F;
        String name = metrics.ellipsize(this.name.getString(), NAME, right - textX - tagWidth);
        graphics.text(name, textX, nameY, NAME, ArkColors.TEXT_PRIMARY);
        if (this.tag != null) {
            Box tagBox = new Box(textX + metrics.width(name, NAME) + TAG_GAP, nameY + metrics.capHeight(NAME) * 0.5F - TAG_HEIGHT * 0.5F,
                tagWidth - TAG_GAP, TAG_HEIGHT);
            graphics.fill(tagBox, this.tag.fill());
            graphics.text(this.tag.text().getString(), tagBox.x() + TAG_PADDING, tagBox.centerY() - metrics.capHeight(TAG) * 0.5F, TAG, this.tag.color());
        }
        String description = metrics.ellipsize(this.description.getString(), DESCRIPTION, right - textX);
        graphics.text(description, textX, nameY + metrics.capHeight(NAME) + LINE_GAP, DESCRIPTION, ArkColors.TEXT_DESCRIPTION);
        for (ArkWidget action : this.actions) {
            action.render(graphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean isHovered() {
        return this.hovered;
    }

    @Override
    public @Nullable Component tooltip() {
        return this.tooltip;
    }

    @Override
    public void hide() {
        PanelRow.super.hide();
        this.hovered = false;
    }

    record Tag(Component text, int color, int fill) {
    }
}
