package com.aryston.arkea.ui.screen;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.overlay.TooltipHint;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.SettingRow;
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
    private static final float SECTION_GAP = 24.0F;
    private static final float DISABLED_OPACITY = 0.4F;
    private static final int MAX_STAGGER = 16;
    private static final TextStyle LABEL = TextStyle.of(10.0F).spacing(1.0F);

    private final List<SettingsSection> sections = new ArrayList<>();
    private final List<Label> labels = new ArrayList<>();

    public SettingsSection section(Component title, boolean full) {
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
                rowBottom = this.place(section, area.x(), y, area.width(), 2);
                y = rowBottom + SECTION_GAP;
                continue;
            }
            float x = secondColumn ? area.x() + columnWidth + COLUMN_GAP : area.x();
            float bottom = this.place(section, x, y, columnWidth, 1);
            rowBottom = secondColumn ? Math.max(rowBottom, bottom) : bottom;
            if (secondColumn) {
                y = rowBottom + SECTION_GAP;
            }
            secondColumn = !secondColumn;
        }
        return rowBottom;
    }

    private float place(SettingsSection section, float x, float y, float width, int columns) {
        this.labels.add(new Label(section.title(), x, y));
        float top = y + LABEL_HEIGHT + LABEL_GAP;
        float cellWidth = (width - COLUMN_GAP * (columns - 1)) / columns;
        List<SettingsSection.Entry> entries = section.entries();
        for (int index = 0; index < entries.size(); index++) {
            SettingsSection.Entry entry = entries.get(index);
            float cellX = x + (index % columns) * (cellWidth + COLUMN_GAP);
            float cellY = top + (index / columns) * (SettingRow.HEIGHT + ROW_GAP);
            entry.row().setBounds(new Box(cellX, cellY, cellWidth, SettingRow.HEIGHT));
            if (entry.control() != null) {
                entry.control().setBounds(entry.row().controlSlot(entry.controlWidth(), entry.controlHeight()));
            }
        }
        int lines = (entries.size() + columns - 1) / columns;
        return top + lines * (SettingRow.HEIGHT + ROW_GAP) - ROW_GAP;
    }

    public int render(UiGraphics graphics, float mouseX, float mouseY, RowRenderer rows, int firstIndex) {
        for (Label label : this.labels) {
            float y = label.y() + (LABEL_HEIGHT - graphics.metrics().capHeight(LABEL)) * 0.5F;
            graphics.text(label.text().getString(), label.x(), y, LABEL, ArkColors.TEXT_LABEL);
        }
        int index = firstIndex;
        for (SettingsSection section : this.sections) {
            for (SettingsSection.Entry entry : section.entries()) {
                ArkWidget control = entry.control();
                float opacity = control == null || control.isActive() ? 1.0F : DISABLED_OPACITY;
                rows.render(Math.min(index++, MAX_STAGGER), () -> {
                    entry.row().render(graphics, mouseX, mouseY, opacity);
                    if (control != null) {
                        graphics.push();
                        graphics.fade(opacity);
                        control.render(graphics, mouseX, mouseY);
                        graphics.pop();
                    }
                });
            }
        }
        return index;
    }

    public @Nullable TooltipHint hoveredHint() {
        for (SettingsSection section : this.sections) {
            for (SettingsSection.Entry entry : section.entries()) {
                Component tooltip = entry.row().tooltip();
                if (tooltip != null && entry.row().isHovered()) {
                    return new TooltipHint(entry.row(), tooltip);
                }
            }
        }
        return null;
    }

    public @Nullable Box rowOf(ArkWidget control) {
        for (SettingsSection section : this.sections) {
            for (SettingsSection.Entry entry : section.entries()) {
                if (entry.control() == control) {
                    return entry.row().bounds();
                }
            }
        }
        return null;
    }

    @FunctionalInterface
    public interface RowRenderer {
        void render(int index, Runnable draw);
    }

    private record Label(Component text, float x, float y) {
    }
}
