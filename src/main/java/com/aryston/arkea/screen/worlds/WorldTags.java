package com.aryston.arkea.screen.worlds;

import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.Tag;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelSummary;

final class WorldTags {
    private WorldTags() {
    }

    static List<Tag> of(LevelSummary summary) {
        List<Tag> tags = new ArrayList<>();
        if (summary.isHardcore()) {
            tags.add(tag("hardcore", ArkColors.ERROR));
        } else if (summary.hasCommands()) {
            tags.add(tag("cheats", ArkColors.INFO));
        }
        if (summary.isLocked()) {
            tags.add(tag("locked", ArkColors.TEXT_SOFT));
        } else if (!summary.isCompatible()) {
            tags.add(tag("incompatible", ArkColors.ERROR));
        } else if (summary.isDowngrade()) {
            tags.add(tag("newer", ArkColors.ERROR));
        } else if (summary.requiresManualConversion() || summary.backupStatus().shouldBackup()) {
            tags.add(tag("older", ArkColors.WARNING));
        }
        if (summary.isExperimental()) {
            tags.add(tag("experimental", ArkColors.WARNING));
        }
        return tags;
    }

    private static Tag tag(String key, int color) {
        return Tag.tone(Component.translatable("arkea.worlds.tag." + key), color);
    }
}
