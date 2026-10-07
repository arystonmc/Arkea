package com.aryston.arkea.ui.screen;

import com.aryston.arkea.ui.widget.NavEntry;
import java.util.List;
import net.minecraft.network.chat.Component;

public record NavGroup(Component label, List<NavEntry> entries) {
}
