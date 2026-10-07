package com.aryston.arkea.screen.worlds;

import com.aryston.arkea.Arkea;
import com.aryston.arkea.ui.overlay.ArkToasts;
import com.aryston.arkea.ui.overlay.MenuToast;
import com.aryston.arkea.ui.overlay.ToastTone;
import com.mojang.blaze3d.Blaze3D;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.NoticeWithLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.OptimizeWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FileUtil;
import net.minecraft.util.Util;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.validation.ContentValidationException;

final class WorldOperations {
    private static final double BYTES_PER_MEGABYTE = 1048576.0;

    private final Minecraft minecraft;
    private final Screen screen;
    private final Runnable reload;

    WorldOperations(Minecraft minecraft, Screen screen, Runnable reload) {
        this.minecraft = minecraft;
        this.screen = screen;
        this.reload = reload;
    }

    void play(LevelSummary summary) {
        if (summary instanceof LevelSummary.SymlinkLevelSummary) {
            this.minecraft.gui.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(this::returnToList));
            return;
        }
        this.minecraft.createWorldOpenFlows().openWorld(summary.getLevelId(), this::returnToList);
    }

    void returnToList() {
        this.minecraft.gui.setScreen(this.screen);
    }

    void createNew() {
        CreateWorldScreen.openFresh(this.minecraft, this::returnToList);
    }

    void openFolder(LevelSummary summary) {
        Blaze3D.openPath(this.minecraft.getLevelSource().getLevelPath(summary.getLevelId()));
    }

    void openBackups() {
        Path path = this.minecraft.getLevelSource().getBackupPath();
        try {
            FileUtil.createDirectoriesSafe(path);
            Blaze3D.openPath(path);
        } catch (IOException exception) {
            Arkea.LOGGER.warn("Could not open the backups folder {}", path, exception);
        }
    }

    void backup(LevelSummary summary) {
        MenuToast toast = ArkToasts.busy(Component.translatable("arkea.worlds.backup.running", summary.getLevelName()));
        String levelId = summary.getLevelId();
        this.inBackground(() -> {
            try (LevelStorageSource.LevelStorageAccess access = this.minecraft.getLevelSource().createAccess(levelId)) {
                return access.makeWorldBackup();
            }
        }).whenCompleteAsync((bytes, failure) -> {
            if (failure != null) {
                Arkea.LOGGER.warn("Could not back up world {}", levelId, failure);
                toast.finish(ToastTone.ERROR, Component.translatable("selectWorld.edit.backupFailed"));
                return;
            }
            toast.dismiss(Util.getMillis());
            ArkToasts.show(ToastTone.SUCCESS, Component.translatable("arkea.worlds.backup.done", Math.max(1L, Math.round(bytes / BYTES_PER_MEGABYTE))),
                Component.translatable("arkea.worlds.backup.show"), this::openBackups);
        }, this.minecraft);
    }

    void delete(LevelSummary summary, boolean backupFirst) {
        MenuToast toast = ArkToasts.busy(Component.translatable(backupFirst ? "arkea.worlds.delete.backing_up" : "arkea.worlds.delete.running",
            summary.getLevelName()));
        String levelId = summary.getLevelId();
        this.inBackground(() -> {
            try (LevelStorageSource.LevelStorageAccess access = this.minecraft.getLevelSource().createAccess(levelId)) {
                if (backupFirst) {
                    access.makeWorldBackup();
                }
                access.deleteLevel();
                return 0L;
            }
        }).whenCompleteAsync((ignored, failure) -> {
            if (failure != null) {
                Arkea.LOGGER.warn("Could not delete world {}", levelId, failure);
                toast.finish(ToastTone.ERROR, Component.translatable("arkea.worlds.delete.failed", summary.getLevelName()));
            } else {
                toast.finish(ToastTone.SUCCESS, Component.translatable("arkea.worlds.delete.done", summary.getLevelName()));
            }
            this.reload.run();
        }, this.minecraft);
    }

    void rename(LevelSummary summary, String name) {
        String levelId = summary.getLevelId();
        this.inBackground(() -> {
            try (LevelStorageSource.LevelStorageAccess access = this.minecraft.getLevelSource().validateAndCreateAccess(levelId)) {
                access.renameLevel(name);
                return 0L;
            }
        }).whenCompleteAsync((ignored, failure) -> {
            if (failure != null) {
                Arkea.LOGGER.warn("Could not rename world {}", levelId, failure);
                ArkToasts.show(ToastTone.ERROR, Component.translatable("arkea.worlds.rename.failed"));
            } else {
                ArkToasts.show(ToastTone.SUCCESS, Component.translatable("arkea.worlds.rename.done", name));
            }
            this.reload.run();
        }, this.minecraft);
    }

    void resetIcon(LevelSummary summary) {
        Path icon = this.minecraft.getLevelSource().getLevelPath(summary.getLevelId()).resolve(LevelResource.ICON_FILE.id());
        try {
            Files.deleteIfExists(icon);
            ArkToasts.show(ToastTone.SUCCESS, Component.translatable("arkea.worlds.icon.reset"));
        } catch (IOException exception) {
            Arkea.LOGGER.warn("Could not delete world icon {}", icon, exception);
            ArkToasts.show(ToastTone.ERROR, Component.translatable("arkea.worlds.icon.failed"));
        }
        this.reload.run();
    }

    void optimize(LevelSummary summary, boolean backupFirst, boolean eraseCache) {
        this.minecraft.setScreenAndShow(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));
        LevelStorageSource.LevelStorageAccess access;
        try {
            access = this.minecraft.getLevelSource().validateAndCreateAccess(summary.getLevelId());
            if (backupFirst) {
                access.makeWorldBackup();
            }
        } catch (IOException | ContentValidationException exception) {
            Arkea.LOGGER.warn("Could not open world {} for optimizing", summary.getLevelId(), exception);
            ArkToasts.show(ToastTone.ERROR, Component.translatable("arkea.worlds.optimize.failed"));
            this.returnToList();
            return;
        }
        OptimizeWorldScreen optimize = OptimizeWorldScreen.create(this.minecraft, done -> {
            access.safeClose();
            this.returnToList();
        }, this.minecraft.getFixerUpper(), access, eraseCache);
        if (optimize == null) {
            access.safeClose();
            ArkToasts.show(ToastTone.ERROR, Component.translatable("arkea.worlds.optimize.failed"));
            this.returnToList();
            return;
        }
        this.minecraft.gui.setScreen(optimize);
    }

    void recreate(LevelSummary summary) {
        this.minecraft.setScreenAndShow(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));
        try (LevelStorageSource.LevelStorageAccess access = this.minecraft.getLevelSource().validateAndCreateAccess(summary.getLevelId())) {
            Pair<LevelSettings, WorldCreationContext> settings = this.minecraft.createWorldOpenFlows().recreateWorldData(access);
            Path dataPacks = CreateWorldScreen.createTempDataPackDirFromExistingWorld(access.getLevelPath(LevelResource.DATAPACK_DIR), this.minecraft);
            settings.getSecond().validate();
            this.minecraft.gui.setScreen(CreateWorldScreen.createFromExisting(this.minecraft, this::returnToList, settings.getFirst(), settings.getSecond(),
                dataPacks));
        } catch (ContentValidationException exception) {
            Arkea.LOGGER.warn("{}", exception.getMessage());
            this.minecraft.gui.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(this::returnToList));
        } catch (Exception exception) {
            Arkea.LOGGER.error("Could not recreate world {}", summary.getLevelId(), exception);
            ArkToasts.show(ToastTone.ERROR, Component.translatable("selectWorld.recreate.error.text"));
            this.returnToList();
        }
    }

    private CompletableFuture<Long> inBackground(StorageTask task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.run();
            } catch (Exception exception) {
                throw new IllegalStateException(exception.getMessage(), exception);
            }
        }, Util.ioPool());
    }

    @FunctionalInterface
    private interface StorageTask {
        long run() throws Exception;
    }
}
