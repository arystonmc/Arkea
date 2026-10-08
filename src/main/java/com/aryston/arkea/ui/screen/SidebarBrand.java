package com.aryston.arkea.ui.screen;

import com.aryston.arkea.ui.render.Icon;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public record SidebarBrand(Icon icon, @Nullable Identifier logo, Component title, Component subtitle) {
    public SidebarBrand(Icon icon, Component title, Component subtitle) {
        this(icon, null, title, subtitle);
    }
}
