package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class NoticeRow implements PanelRow {
    public static final float HEIGHT = 44.0F;
    private static final float PADDING = 14.0F;
    private static final float ICON_SIZE = 14.0F;
    private static final float GAP = 12.0F;
    private static final float FILL_ALPHA = 0.08F;
    private static final float BORDER_ALPHA = 0.35F;
    private static final TextStyle TEXT = TextStyle.of(11.0F);

    private final Icon icon;
    private final int tone;
    private final int textColor;
    private final Component text;
    private final @Nullable ArkButton action;
    private Box bounds = Box.EMPTY;

    public NoticeRow(Icon icon, int tone, int textColor, Component text, @Nullable ArkButton action) {
        this.icon = icon;
        this.tone = tone;
        this.textColor = textColor;
        this.text = text;
        this.action = action;
    }

    @Override
    public float height() {
        return HEIGHT;
    }

    @Override
    public void place(Box newBounds) {
        this.bounds = newBounds;
        if (this.action != null) {
            float width = this.action.preferredWidth();
            float height = ArkButton.HEIGHT - 4.0F;
            this.action.setBounds(new Box(newBounds.right() - PADDING + 6.0F - width, newBounds.centerY() - height * 0.5F, width, height));
        }
    }

    @Override
    public Box bounds() {
        return this.bounds;
    }

    @Override
    public List<ArkWidget> widgets() {
        return this.action == null ? List.of() : List.of(this.action);
    }

    @Override
    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        Box box = this.bounds;
        graphics.fill(box, ArkColors.withAlpha(this.tone, FILL_ALPHA));
        graphics.border(box, 1.0F, ArkColors.withAlpha(this.tone, BORDER_ALPHA));
        graphics.icon(this.icon, box.x() + PADDING, box.centerY() - ICON_SIZE * 0.5F, ICON_SIZE, ICON_SIZE, this.tone);
        TextMetrics metrics = graphics.metrics();
        float textX = box.x() + PADDING + ICON_SIZE + GAP;
        float right = this.action != null ? this.action.bounds().x() - GAP : box.right() - PADDING;
        String text = metrics.ellipsize(this.text.getString(), TEXT, right - textX);
        graphics.text(text, textX, box.centerY() - metrics.capHeight(TEXT) * 0.5F, TEXT, this.textColor);
        if (this.action != null) {
            this.action.render(graphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean isHovered() {
        return false;
    }
}
