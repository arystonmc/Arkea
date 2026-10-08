package com.aryston.arkea.screen.toasts;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.overlay.GameToast;
import com.aryston.arkea.ui.overlay.GameToastIcon;
import com.aryston.arkea.ui.overlay.ToastTone;
import com.aryston.arkea.ui.theme.ArkColors;
import java.util.List;
import java.util.Set;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

public final class GameToasts {
    private static final int CHALLENGE_COLOR = ArkColors.rgb(0xF0B848);
    private static final int TITLE_WIDTH = 220;
    private static final float TUTORIAL_ICON = 20.0F;
    private static final Set<SystemToast.SystemToastId> ERRORS = Set.of(SystemToast.SystemToastId.PACK_LOAD_FAILURE,
        SystemToast.SystemToastId.WORLD_ACCESS_FAILURE, SystemToast.SystemToastId.PACK_COPY_FAILURE, SystemToast.SystemToastId.FILE_DROP_FAILURE,
        SystemToast.SystemToastId.CHUNK_LOAD_FAILURE, SystemToast.SystemToastId.CHUNK_SAVE_FAILURE);
    private static final Set<SystemToast.SystemToastId> WARNINGS = Set.of(SystemToast.SystemToastId.LOW_DISK_SPACE,
        SystemToast.SystemToastId.UNSECURE_SERVER_WARNING);

    private GameToasts() {
    }

    public static boolean enabled() {
        return ArkeaConfig.SPEC.isLoaded() && ArkeaConfig.GAME_TOASTS.get();
    }

    public static GameToast system(SystemToast.SystemToastId id, List<FormattedCharSequence> title, List<FormattedCharSequence> message) {
        ToastTone tone = ERRORS.contains(id) ? ToastTone.ERROR : WARNINGS.contains(id) ? ToastTone.WARNING : ToastTone.INFO;
        return new GameToast(title, ArkColors.MINECRAFT_YELLOW, message, GameToastIcon.tone(tone), GameToast.NO_PROGRESS);
    }

    public static GameToast advancement(AdvancementHolder advancement, ItemStack icon) {
        DisplayInfo display = advancement.value().display().orElse(null);
        if (display == null) {
            return new GameToast(List.of(), ArkColors.MINECRAFT_YELLOW, List.of(), GameToastIcon.item(icon), GameToast.NO_PROGRESS);
        }
        int color = display.type() == AdvancementType.CHALLENGE ? CHALLENGE_COLOR : ArkColors.MINECRAFT_YELLOW;
        return new GameToast(List.of(display.type().getDisplayName().getVisualOrderText()), color, split(display.title()), GameToastIcon.item(icon),
            GameToast.NO_PROGRESS);
    }

    public static GameToast recipe(ItemStack unlocked) {
        return new GameToast(List.of(Component.translatable("recipe.toast.title").getVisualOrderText()), ArkColors.MINECRAFT_YELLOW,
            List.of(Component.translatable("recipe.toast.description").getVisualOrderText()), GameToastIcon.item(unlocked), GameToast.NO_PROGRESS);
    }

    public static GameToast tutorial(TutorialToast.Icons icon, List<FormattedCharSequence> lines, boolean progressable, float progress) {
        List<FormattedCharSequence> title = lines.isEmpty() ? List.of() : lines.subList(0, 1);
        List<FormattedCharSequence> message = lines.size() <= 1 ? List.of() : lines.subList(1, lines.size());
        GameToastIcon drawn = (graphics, tile) -> graphics.vanilla(new Box(tile.centerX() - TUTORIAL_ICON * 0.5F, tile.centerY() - TUTORIAL_ICON * 0.5F,
            TUTORIAL_ICON, TUTORIAL_ICON), TUTORIAL_ICON, raw -> icon.extractRenderState(raw, 0, 0));
        return new GameToast(title, ArkColors.MINECRAFT_YELLOW, message, drawn, progressable ? progress : GameToast.NO_PROGRESS);
    }

    private static List<FormattedCharSequence> split(Component text) {
        return Minecraft.getInstance().font.split(text, TITLE_WIDTH);
    }
}
