package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkWidget;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class DialogForm implements DialogBody {
    private static final float ROW_GAP = 12.0F;
    private static final float LABEL_GAP = 6.0F;
    private static final float COLUMN_GAP = 6.0F;
    private static final float LABEL_HEIGHT = 10.0F;
    private static final float MESSAGE_HEIGHT = 10.0F;
    private static final TextStyle LABEL = TextStyle.of(10.0F).spacing(1.0F);
    private static final TextStyle MESSAGE = TextStyle.of(10.0F);
    private static final TextStyle SETTING = TextStyle.of(11.0F);

    private final List<Row> rows = new ArrayList<>();
    private final List<ArkWidget> widgets = new ArrayList<>();

    public DialogForm label(Component text) {
        this.rows.add(new Row(List.of(), LABEL_HEIGHT, LABEL_GAP, text, null, ArkColors.TEXT_LABEL, 0.0F));
        return this;
    }

    public DialogForm field(ArkWidget widget, float height) {
        return this.columns(height, widget);
    }

    public DialogForm columns(float height, ArkWidget... columns) {
        List<ArkWidget> row = List.of(columns);
        this.rows.add(new Row(row, height, ROW_GAP, null, null, 0, 0.0F));
        this.widgets.addAll(row);
        return this;
    }

    public DialogForm labeled(Component text, ArkWidget widget, float widgetWidth, float height) {
        this.rows.add(new Row(List.of(widget), height, ROW_GAP, text, null, ArkColors.TEXT_SOFT, widgetWidth));
        this.widgets.add(widget);
        return this;
    }

    public DialogForm message(Supplier<@Nullable Component> text, int color) {
        this.rows.add(new Row(List.of(), MESSAGE_HEIGHT, ROW_GAP, null, text, color, 0.0F));
        return this;
    }

    @Override
    public float height(float width) {
        float height = 0.0F;
        for (int index = 0; index < this.rows.size(); index++) {
            Row row = this.rows.get(index);
            height += row.height() + (index < this.rows.size() - 1 ? row.gapAfter() : 0.0F);
        }
        return height;
    }

    @Override
    public void layout(Box area) {
        float y = area.y();
        for (Row row : this.rows) {
            int count = row.widgets().size();
            if (row.widgetWidth() > 0.0F) {
                row.widgets().getFirst().setBounds(new Box(area.right() - row.widgetWidth(), y, row.widgetWidth(), row.height()));
                row.place(area.x(), y);
                y += row.height() + row.gapAfter();
                continue;
            }
            float columnWidth = count == 0 ? 0.0F : (area.width() - COLUMN_GAP * (count - 1)) / count;
            for (int index = 0; index < count; index++) {
                row.widgets().get(index).setBounds(new Box(area.x() + index * (columnWidth + COLUMN_GAP), y, columnWidth, row.height()));
            }
            row.place(area.x(), y);
            y += row.height() + row.gapAfter();
        }
    }

    @Override
    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        TextMetrics metrics = graphics.metrics();
        for (Row row : this.rows) {
            Component text = row.text();
            if (text != null) {
                TextStyle style = row.widgetWidth() > 0.0F ? SETTING : LABEL;
                graphics.text(text.getString(), row.x(), row.y() + (row.height() - metrics.capHeight(style)) * 0.5F, style, row.color());
            }
            Component message = row.message() != null ? row.message().get() : null;
            if (message != null) {
                graphics.text(message.getString(), row.x(), row.y() + (row.height() - metrics.capHeight(MESSAGE)) * 0.5F, MESSAGE, row.color());
            }
            for (ArkWidget widget : row.widgets()) {
                widget.render(graphics, mouseX, mouseY);
            }
        }
    }

    @Override
    public List<? extends ArkWidget> widgets() {
        return this.widgets;
    }

    private static final class Row {
        private final List<ArkWidget> widgets;
        private final float height;
        private final float gapAfter;
        private final @Nullable Component text;
        private final @Nullable Supplier<@Nullable Component> message;
        private final int color;
        private final float widgetWidth;
        private float x;
        private float y;

        Row(List<ArkWidget> widgets, float height, float gapAfter, @Nullable Component text, @Nullable Supplier<@Nullable Component> message,
            int color, float widgetWidth) {
            this.widgets = widgets;
            this.height = height;
            this.gapAfter = gapAfter;
            this.text = text;
            this.message = message;
            this.color = color;
            this.widgetWidth = widgetWidth;
        }

        float widgetWidth() {
            return this.widgetWidth;
        }

        List<ArkWidget> widgets() {
            return this.widgets;
        }

        float height() {
            return this.height;
        }

        float gapAfter() {
            return this.gapAfter;
        }

        @Nullable Component text() {
            return this.text;
        }

        @Nullable Supplier<@Nullable Component> message() {
            return this.message;
        }

        int color() {
            return this.color;
        }

        float x() {
            return this.x;
        }

        float y() {
            return this.y;
        }

        void place(float newX, float newY) {
            this.x = newX;
            this.y = newY;
        }
    }
}
