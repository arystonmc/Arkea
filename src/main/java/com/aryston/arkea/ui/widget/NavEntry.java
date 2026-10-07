package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.render.Icon;
import net.minecraft.network.chat.Component;

public record NavEntry(String id, Icon icon, Component label, boolean external) {
}
