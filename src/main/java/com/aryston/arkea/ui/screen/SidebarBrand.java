package com.aryston.arkea.ui.screen;

import com.aryston.arkea.ui.render.Icon;
import net.minecraft.network.chat.Component;

public record SidebarBrand(Icon icon, Component title, Component subtitle) {
}
