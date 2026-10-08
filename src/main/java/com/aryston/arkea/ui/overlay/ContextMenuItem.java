package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.render.Icon;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public record ContextMenuItem(@Nullable Icon icon, Component label, @Nullable Component shortcut, boolean enabled, boolean danger,
    @Nullable Runnable action) {

    public static ContextMenuItem action(Icon icon, Component label, Runnable action) {
        return new ContextMenuItem(icon, label, null, true, false, action);
    }

    public static ContextMenuItem separator() {
        return new ContextMenuItem(null, Component.empty(), null, false, false, null);
    }

    public ContextMenuItem shortcut(Component hint) {
        return new ContextMenuItem(this.icon, this.label, hint, this.enabled, this.danger, this.action);
    }

    public ContextMenuItem enabled(boolean value) {
        return new ContextMenuItem(this.icon, this.label, this.shortcut, value, this.danger, this.action);
    }

    public ContextMenuItem dangerous() {
        return new ContextMenuItem(this.icon, this.label, this.shortcut, this.enabled, true, this.action);
    }

    public boolean isSeparator() {
        return this.action == null;
    }
}
