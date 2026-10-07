package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.render.Icon;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public record NavEntry(String id, Icon icon, @Nullable Identifier logo, Component label, boolean external) {
}
