package com.aryston.arkea.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.network.chat.Component;

final class UiCheckGameToasts {
    private static final float SAMPLE_PROGRESS = 0.6F;

    private UiCheckGameToasts() {
    }

    static void show(Minecraft minecraft) {
        SystemToast.add(minecraft.gui.toastManager(), SystemToast.SystemToastId.WORLD_BACKUP, Component.translatable("selectWorld.edit.backupCreated", "World"),
            Component.translatable("selectWorld.edit.backupSize", 12));
        SystemToast.add(minecraft.gui.toastManager(), SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.translatable("resourcePack.load_fail"), null);
        TutorialToast tutorial = new TutorialToast(minecraft.font, TutorialToast.Icons.MOVEMENT_KEYS, Component.translatable("tutorial.move.title", "WASD"),
            Component.translatable("tutorial.move.description", "Space"), true);
        tutorial.updateProgress(SAMPLE_PROGRESS);
        minecraft.gui.toastManager().addToast(tutorial);
    }
}
