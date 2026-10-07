package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.render.Icon;
import net.minecraft.network.chat.Component;

public record DialogContent(Component title, Component body, Icon icon, int toneColor) {
}
