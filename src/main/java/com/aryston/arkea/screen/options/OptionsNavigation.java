package com.aryston.arkea.screen.options;

import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.screen.NavGroup;
import com.aryston.arkea.ui.screen.SidebarBrand;
import java.util.Arrays;
import java.util.List;
import net.minecraft.network.chat.Component;

final class OptionsNavigation {
    private OptionsNavigation() {
    }

    static SidebarBrand brand() {
        return new SidebarBrand(Icons.SLIDERS, Component.translatable("arkea.options.brand"), Component.translatable("arkea.options.brand.subtitle"));
    }

    static List<NavGroup> groups() {
        return Arrays.stream(OptionsPage.Group.values())
            .map(group -> new NavGroup(group.label(), Arrays.stream(OptionsPage.values())
                .filter(page -> page.group() == group && page.isAvailable())
                .map(OptionsPage::navEntry)
                .toList()))
            .toList();
    }
}
