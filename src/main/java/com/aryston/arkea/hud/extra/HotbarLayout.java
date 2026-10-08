package com.aryston.arkea.hud.extra;

import com.aryston.arkea.ui.layout.Box;
import org.jspecify.annotations.Nullable;

public record HotbarLayout(Box hotbar, Box selected, Box experience, @Nullable Box level) {
}
