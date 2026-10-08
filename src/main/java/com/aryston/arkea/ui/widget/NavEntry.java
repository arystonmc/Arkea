package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.render.Icon;
import java.util.function.BooleanSupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public record NavEntry(String id, Icon icon, @Nullable Identifier logo, Component label, boolean external, @Nullable BooleanSupplier marker) {
    public NavEntry(String id, Icon icon, @Nullable Identifier logo, Component label, boolean external) {
        this(id, icon, logo, label, external, null);
    }
}
