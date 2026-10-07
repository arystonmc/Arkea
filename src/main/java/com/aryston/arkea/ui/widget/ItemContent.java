package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.render.Icon;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public record ItemContent(Icon icon, Component label, @Nullable Component sub) {
}
