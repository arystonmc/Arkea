package com.aryston.arkea.screen.options.keys;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

final class KeyConflicts {
    private final Map<KeyMapping, List<KeyMapping>> conflicts = new IdentityHashMap<>();
    private final Set<String> keys = new LinkedHashSet<>();

    KeyConflicts(KeyMapping[] mappings, @Nullable KeyMapping debugModifier) {
        for (KeyMapping mapping : mappings) {
            if (mapping.isUnbound() || mapping == debugModifier) {
                continue;
            }
            List<KeyMapping> others = new ArrayList<>();
            for (KeyMapping other : mappings) {
                if (other == mapping || other == debugModifier || other.isUnbound() || !canClash(mapping, other)) {
                    continue;
                }
                if (mapping.same(other) || other.hasKeyModifierConflict(mapping)) {
                    others.add(other);
                }
            }
            if (!others.isEmpty()) {
                this.conflicts.put(mapping, others);
                this.keys.add(mapping.getTranslatedKeyMessage().getString());
            }
        }
    }

    private static boolean canClash(KeyMapping first, KeyMapping second) {
        if (isIn(first, KeyMapping.Category.DEBUG) != isIn(second, KeyMapping.Category.DEBUG)
            || isIn(first, KeyMapping.Category.SPECTATOR) != isIn(second, KeyMapping.Category.SPECTATOR)) {
            return false;
        }
        return !isIn(first, KeyMapping.Category.DEBUG) || !first.isDefault() || !second.isDefault();
    }

    private static boolean isIn(KeyMapping mapping, KeyMapping.Category category) {
        return mapping.getCategory() == category;
    }

    boolean has(KeyMapping mapping) {
        return this.conflicts.containsKey(mapping);
    }

    List<KeyMapping> with(KeyMapping mapping) {
        return this.conflicts.getOrDefault(mapping, List.of());
    }

    int keyCount() {
        return this.keys.size();
    }

    Component keyList(int limit) {
        List<String> shown = this.keys.stream().limit(limit).toList();
        String joined = String.join(", ", shown);
        if (this.keys.size() > limit) {
            return Component.translatable("arkea.keybinds.conflict.more", joined, this.keys.size() - limit);
        }
        return Component.literal(joined);
    }
}
