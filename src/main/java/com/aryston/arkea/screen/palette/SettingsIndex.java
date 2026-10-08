package com.aryston.arkea.screen.palette;

import com.aryston.arkea.Arkea;
import com.aryston.arkea.screen.options.OptionsPage;
import com.aryston.arkea.screen.options.OptionsPageScreen;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.widget.SettingRow;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

final class SettingsIndex {
    private static @Nullable String language;
    private static List<Setting> settings = List.of();

    private SettingsIndex() {
    }

    static List<Setting> get(Minecraft minecraft) {
        String current = minecraft.getLanguageManager().getSelected();
        if (!current.equals(language)) {
            language = current;
            settings = build(minecraft);
        }
        return settings;
    }

    private static List<Setting> build(Minecraft minecraft) {
        List<Setting> found = new ArrayList<>();
        Screen parent = new TitleScreen();
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        for (OptionsPage page : OptionsPage.values()) {
            if (!page.isWindow() || page == OptionsPage.OVERVIEW) {
                continue;
            }
            try {
                if (page.open(parent, minecraft) instanceof OptionsPageScreen screen) {
                    screen.init(width, height);
                    for (SettingRow row : screen.settingRows()) {
                        found.add(new Setting(page, row.icon(), row.name(), row.description()));
                    }
                }
            } catch (RuntimeException exception) {
                Arkea.LOGGER.warn("Could not index the settings of {}", page, exception);
            }
        }
        return List.copyOf(found);
    }

    record Setting(OptionsPage page, Icon icon, Component name, Component description) {
    }
}
