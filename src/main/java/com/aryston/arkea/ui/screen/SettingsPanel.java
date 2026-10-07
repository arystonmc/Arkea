package com.aryston.arkea.ui.screen;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.overlay.TooltipHint;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.PanelRow;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class SettingsPanel {
    public static final float CONTROL_HEIGHT = 30.0F;
    private static final float LABEL_HEIGHT = 16.0F;
    private static final float LABEL_GAP = 8.0F;
    private static final float ROW_GAP = 6.0F;
    private static final float COLUMN_GAP = 20.0F;
    private static final float CELL_GAP = 6.0F;
    private static final float SECTION_GAP = 24.0F;
    private static final int MAX_STAGGER = 16;
    private static final TextStyle LABEL = TextStyle.of(10.0F).spacing(1.0F);

    private final List<SettingsSection> sections = new ArrayList<>();
    private final List<Label> labels = new ArrayList<>();

    public SettingsSection section(@Nullable Component title, boolean full) {
        SettingsSection section = new SettingsSection(title, full);
        this.sections.add(section);
        return section;
    }

    public float layout(Box area, float top) {
        this.labels.clear();
        float columnWidth = (area.width() - COLUMN_GAP) * 0.5F;
        float y = top;
        float rowBottom = top;
        boolean secondColumn = false;
        for (SettingsSection section : this.sections) {
            if (section.isEmpty()) {
                continue;
            }
            if (section.isFull()) {
                if (secondColumn) {
                    y = rowBottom + SECTION_GAP;
                    secondColumn = false;
                }
                rowBottom = this.place(section, area.x(), y, area.width());
                y = rowBottom + SECTION_GAP;
                continue;
            }
            float x = secondColumn ? area.x() + columnWidth + COLUMN_GAP : area.x();
            float bottom = this.place(section, x, y, columnWidth);
            rowBottom = secondColumn ? Math.max(rowBottom, bottom) : bottom;
            if (secondColumn) {
                y = rowBottom + SECTION_GAP;
            }
            secondColumn = !secondColumn;
        }
        return rowBottom;
    }

    private float place(SettingsSection section, float x, float y, float width) {
        float top = y;
        if (section.title() != null) {
            this.labels.add(new Label(section.title(), section.hint(), x, y, width));
            top += LABEL_HEIGHT + LABEL_GAP;
        }
        int columns = section.columns();
        float gap = columns > 2 ? CELL_GAP : COLUMN_GAP;
        float cellWidth = (width - gap * (columns - 1)) / columns;
        List<PanelRow> rows = section.rows();
        float lineY = top;
        for (int start = 0; start < rows.size(); start += columns) {
            float lineHeight = 0.0F;
            for (int index = start; index < Math.min(rows.size(), start + columns); index++) {
                lineHeight = Math.max(lineHeight, rows.get(index).height());
            }
            for (int index = start; index < Math.min(rows.size(), start + columns); index++) {
                PanelRow row = rows.get(index);
                row.place(new Box(x + (index - start) * (cellWidth + gap), lineY, cellWidth, row.height()));
            }
            lineY += lineHeight + ROW_GAP;
        }
        return lineY - ROW_GAP;
    }

    public int render(UiGraphics graphics, float mouseX, float mouseY, RowRenderer rows, int firstIndex) {
        for (Label label : this.labels) {
            float y = label.y() + (LABEL_HEIGHT - graphics.metrics().capHeight(LABEL)) * 0.5F;
            graphics.text(label.text().getString(), label.x(), y, LABEL, ArkColors.TEXT_LABEL);
            if (label.hint() != null) {
                String hint = label.hint().getString();
                graphics.text(hint, label.x() + label.width() - graphics.metrics().width(hint, LABEL), y, LABEL, ArkColors.TEXT_LABEL);
            }
        }
        int index = firstIndex;
        for (SettingsSection section : this.sections) {
            for (PanelRow row : section.rows()) {
                int stagger = Math.min(index++, MAX_STAGGER);
                if (graphics.visible(graphics.canvasBox(row.bounds())).isEmpty()) {
                    row.hide();
                    continue;
                }
                rows.render(stagger, () -> row.render(graphics, mouseX, mouseY));
            }
        }
        return index;
    }

    public @Nullable TooltipHint hoveredHint() {
        for (SettingsSection section : this.sections) {
            for (PanelRow row : section.rows()) {
                Component tooltip = row.isHovered() ? row.tooltip() : null;
                if (tooltip != null) {
                    return new TooltipHint(row, tooltip);
                }
            }
        }
        return null;
    }

    public @Nullable Box rowOf(ArkWidget widget) {
        for (SettingsSection section : this.sections) {
            for (PanelRow row : section.rows()) {
                if (row.widgets().contains(widget)) {
                    return row.bounds();
                }
            }
        }
        return null;
    }

    @FunctionalInterface
    public interface RowRenderer {
        void render(int index, Runnable draw);
    }

    private record Label(Component text, @Nullable Component hint, float x, float y, float width) {
    }
}
