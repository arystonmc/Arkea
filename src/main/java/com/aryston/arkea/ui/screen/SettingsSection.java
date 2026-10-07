package com.aryston.arkea.ui.screen;

import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.PanelRow;
import com.aryston.arkea.ui.widget.SettingRow;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class SettingsSection {
    private final @Nullable Component title;
    private final boolean full;
    private final List<PanelRow> rows = new ArrayList<>();
    private int columns;
    private @Nullable Component hint;

    SettingsSection(@Nullable Component title, boolean full) {
        this.title = title;
        this.full = full;
        this.columns = full ? 2 : 1;
    }

    public SettingsSection add(PanelRow row) {
        this.rows.add(row);
        return this;
    }

    public SettingsSection add(SettingRow row, ArkWidget control, float controlWidth, float controlHeight) {
        return this.add(row.control(control, controlWidth, controlHeight));
    }

    public SettingsSection columns(int count) {
        this.columns = Math.max(1, count);
        return this;
    }

    public SettingsSection hint(@Nullable Component text) {
        this.hint = text;
        return this;
    }

    public @Nullable Component title() {
        return this.title;
    }

    public @Nullable Component hint() {
        return this.hint;
    }

    public boolean isFull() {
        return this.full;
    }

    public int columns() {
        return this.columns;
    }

    public List<PanelRow> rows() {
        return this.rows;
    }

    public boolean isEmpty() {
        return this.rows.isEmpty();
    }
}
