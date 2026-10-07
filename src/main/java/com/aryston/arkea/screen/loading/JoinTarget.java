package com.aryston.arkea.screen.loading;

import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class JoinTarget {
    private static @Nullable Component name;

    private JoinTarget() {
    }

    public static void set(String targetName) {
        name = Component.literal(targetName);
    }

    static @Nullable Component name() {
        return name;
    }
}
