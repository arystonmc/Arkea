package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.Tag;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class DialogList implements DialogBody {
    private static final float ROW_HEIGHT = 44.0F;
    private static final float ROW_GAP = 4.0F;
    private static final float PADDING = 12.0F;
    private static final float ICON_BOX = 24.0F;
    private static final float ICON = 12.0F;
    private static final float GAP = 12.0F;
    private static final float LINE_GAP = 5.0F;
    private static final float ICON_TINT = 0.15F;
    private static final TextStyle TITLE = TextStyle.of(12.0F);
    private static final TextStyle DETAIL = TextStyle.of(10.0F);

    private final List<Entry> entries = new ArrayList<>();
    private Box area = Box.EMPTY;

    public DialogList entry(Icon icon, int tone, Component title, @Nullable Component detail, @Nullable Tag tag) {
        this.entries.add(new Entry(icon, tone, title, detail, tag));
        return this;
    }

    @Override
    public float height(float width) {
        return this.entries.isEmpty() ? 0.0F : this.entries.size() * (ROW_HEIGHT + ROW_GAP) - ROW_GAP;
    }

    @Override
    public void layout(Box newArea) {
        this.area = newArea;
    }

    @Override
    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        TextMetrics metrics = graphics.metrics();
        float y = this.area.y();
        for (Entry entry : this.entries) {
            Box row = new Box(this.area.x(), y, this.area.width(), ROW_HEIGHT);
            graphics.fill(row, ArkColors.ROW);
            graphics.border(row, 1.0F, ArkColors.BORDER_SUBTLE);
            Box iconBox = new Box(row.x() + PADDING, row.centerY() - ICON_BOX * 0.5F, ICON_BOX, ICON_BOX);
            graphics.fill(iconBox, ArkColors.withAlpha(entry.tone(), ICON_TINT));
            graphics.icon(entry.icon(), iconBox.centerX() - ICON * 0.5F, iconBox.centerY() - ICON * 0.5F, ICON, ICON, entry.tone());
            float textX = iconBox.right() + GAP;
            float right = row.right() - PADDING;
            if (entry.tag() != null) {
                right -= entry.tag().width(metrics);
                entry.tag().draw(graphics, right, row.centerY());
                right -= GAP;
            }
            float width = right - textX;
            if (entry.detail() == null) {
                graphics.text(metrics.ellipsize(entry.title().getString(), TITLE, width), textX, row.centerY() - metrics.capHeight(TITLE) * 0.5F, TITLE,
                    ArkColors.TEXT_PRIMARY);
            } else {
                float blockHeight = metrics.capHeight(TITLE) + LINE_GAP + metrics.capHeight(DETAIL);
                float titleY = row.centerY() - blockHeight * 0.5F;
                graphics.text(metrics.ellipsize(entry.title().getString(), TITLE, width), textX, titleY, TITLE, ArkColors.TEXT_PRIMARY);
                graphics.text(metrics.ellipsize(entry.detail().getString(), DETAIL, width), textX, titleY + metrics.capHeight(TITLE) + LINE_GAP, DETAIL,
                    ArkColors.TEXT_DESCRIPTION);
            }
            y += ROW_HEIGHT + ROW_GAP;
        }
    }

    @Override
    public List<? extends ArkWidget> widgets() {
        return List.of();
    }

    private record Entry(Icon icon, int tone, Component title, @Nullable Component detail, @Nullable Tag tag) {
    }
}
