package com.aryston.arkea.integration;

import com.aryston.arkea.Arkea;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.sdl.SDLDialog;
import org.lwjgl.sdl.SDLError;
import org.lwjgl.sdl.SDL_DialogFileCallback;
import org.lwjgl.sdl.SDL_DialogFileFilter;
import org.lwjgl.system.MemoryUtil;

public final class FileDialogs {
    private static @Nullable OpenDialog open;

    private FileDialogs() {
    }

    public static boolean isOpen() {
        return open != null;
    }

    public static void openFiles(String filterName, String extensions, Consumer<List<Path>> chosen, Consumer<String> failed) {
        if (open != null) {
            return;
        }
        OpenDialog dialog = new OpenDialog(filterName, extensions, chosen, failed);
        open = dialog;
        try {
            SDLDialog.SDL_ShowOpenFileDialog(dialog.callback, 0L, Minecraft.getInstance().getWindow().handle(), dialog.filters, (CharSequence) null, true);
        } catch (RuntimeException | LinkageError exception) {
            Arkea.LOGGER.warn("Could not open the file dialog", exception);
            dialog.finish(null, exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage());
        }
    }

    private static final class OpenDialog {
        private final Consumer<List<Path>> chosen;
        private final Consumer<String> failed;
        private final ByteBuffer name;
        private final ByteBuffer pattern;
        private final SDL_DialogFileFilter.Buffer filters;
        private final SDL_DialogFileCallback callback;
        private boolean finished;

        private OpenDialog(String filterName, String extensions, Consumer<List<Path>> chosen, Consumer<String> failed) {
            this.chosen = chosen;
            this.failed = failed;
            this.name = MemoryUtil.memUTF8(filterName);
            this.pattern = MemoryUtil.memUTF8(extensions);
            this.filters = SDL_DialogFileFilter.calloc(1);
            this.filters.get(0).name(this.name).pattern(this.pattern);
            this.callback = SDL_DialogFileCallback.create((userdata, fileList, filter) -> {
                List<Path> files = new ArrayList<>();
                String error = null;
                if (fileList == 0L) {
                    error = SDLError.SDL_GetError();
                } else {
                    PointerBuffer pointers = MemoryUtil.memPointerBuffer(fileList, Integer.MAX_VALUE);
                    for (int index = 0; pointers.get(index) != 0L; index++) {
                        files.add(Path.of(MemoryUtil.memUTF8(pointers.get(index))));
                    }
                }
                String message = error;
                Minecraft.getInstance().execute(() -> this.finish(files, message));
            });
        }

        private void finish(@Nullable List<Path> files, @Nullable String error) {
            if (this.finished) {
                return;
            }
            this.finished = true;
            open = null;
            if (error != null) {
                this.failed.accept(error);
            } else if (files != null && !files.isEmpty()) {
                this.chosen.accept(files);
            }
            this.callback.free();
            this.filters.free();
            MemoryUtil.memFree(this.name);
            MemoryUtil.memFree(this.pattern);
        }
    }
}
