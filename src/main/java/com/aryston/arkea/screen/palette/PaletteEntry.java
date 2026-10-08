package com.aryston.arkea.screen.palette;

import com.aryston.arkea.ui.render.Icon;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

record PaletteEntry(Group group, Icon icon, Component label, Component detail, Consumer<Screen> action) {
    enum Group {
        ACTIONS,
        PAGES,
        SETTINGS,
        WORLDS,
        SERVERS;

        Component label() {
            return Component.translatable("arkea.palette.group." + this.name().toLowerCase(Locale.ROOT));
        }
    }
}
