package com.aryston.arkea.ui.screen;

import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.SettingRow;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class SettingsSection {
    private final Component title;
    private final boolean full;
    private final List<Entry> entries = new ArrayList<>();

    SettingsSection(Component title, boolean full) {
        this.title = title;
        this.full = full;
    }

    public SettingsSection add(SettingRow row, @Nullable ArkWidget control, float controlWidth, float controlHeight) {
        this.entries.add(new Entry(row, control, controlWidth, controlHeight));
        return this;
    }

    public Component title() {
        return this.title;
    }

    public boolean isFull() {
        return this.full;
    }

    public List<Entry> entries() {
        return this.entries;
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public record Entry(SettingRow row, @Nullable ArkWidget control, float controlWidth, float controlHeight) {
    }
}
