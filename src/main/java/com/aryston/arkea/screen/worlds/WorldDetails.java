package com.aryston.arkea.screen.worlds;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkButton;
import com.aryston.arkea.ui.widget.ArkIconButton;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelSummary;

final class WorldDetails {
    static final float WIDTH = 380.0F;
    private static final float IMAGE_HEIGHT = 190.0F;
    private static final float PADDING = 18.0F;
    private static final float GAP = 16.0F;
    private static final float NAME_GAP = 8.0F;
    private static final float NAME_BLOCK = 36.0F;
    private static final float INFO_ROW = 28.0F;
    private static final float PLAY_HEIGHT = 40.0F;
    private static final float ACTION_HEIGHT = 32.0F;
    private static final float ACTION_GAP = 6.0F;
    private static final float DELETE_WIDTH = 40.0F;
    private static final int FILL = ArkColors.rgba(255, 255, 255, 0.03F);
    private static final int INFO_LINE = ArkColors.rgba(255, 255, 255, 0.05F);
    private static final TextStyle NAME = TextStyle.of(18.0F);
    private static final TextStyle FOLDER = TextStyle.of(10.0F);
    private static final TextStyle INFO = TextStyle.of(11.0F);

    private final LevelSummary summary;
    private final WorldImage image;
    private final Supplier<WorldFacts> facts;
    private final ArkButton play;
    private final ArkButton edit;
    private final ArkButton recreate;
    private final ArkIconButton delete;
    private Box frame = Box.EMPTY;

    WorldDetails(LevelSummary summary, WorldImage image, Supplier<WorldFacts> facts, ArkButton play, ArkButton edit, ArkButton recreate, ArkIconButton delete) {
        this.summary = summary;
        this.image = image;
        this.facts = facts;
        this.play = play;
        this.edit = edit;
        this.recreate = recreate;
        this.delete = delete;
    }

    private List<Info> info() {
        return List.of(
            new Info(Component.translatable("selectWorld.gameMode"), WorldText.gameMode(this.summary)),
            new Info(Component.translatable("options.difficulty"), WorldText.difficulty(this.summary)),
            new Info(Component.translatable("arkea.worlds.info.version"), this.summary.getWorldVersionName()),
            new Info(Component.translatable("arkea.worlds.info.last_played"), WorldText.ago(this.summary.getLastPlayed())),
            new Info(Component.translatable("arkea.worlds.info.play_time"), WorldText.playTime(this.facts.get().playTicks())),
            new Info(Component.translatable("arkea.worlds.info.size"), WorldText.size(this.facts.get().bytes()))
        );
    }

    float layout(float x, float y) {
        float height = 1.0F + IMAGE_HEIGHT + PADDING + NAME_BLOCK + GAP + this.info().size() * INFO_ROW + GAP + PLAY_HEIGHT + GAP + ACTION_HEIGHT
            + PADDING + 1.0F;
        this.frame = new Box(x, y, WIDTH, height);
        float inner = WIDTH - PADDING * 2.0F;
        float left = x + PADDING;
        float playY = this.frame.bottom() - 1.0F - PADDING - ACTION_HEIGHT - GAP - PLAY_HEIGHT;
        this.play.setBounds(new Box(left, playY, inner, PLAY_HEIGHT));
        float actionY = playY + PLAY_HEIGHT + GAP;
        float half = (inner - DELETE_WIDTH - ACTION_GAP * 2.0F) * 0.5F;
        this.edit.setBounds(new Box(left, actionY, half, ACTION_HEIGHT));
        this.recreate.setBounds(new Box(left + half + ACTION_GAP, actionY, half, ACTION_HEIGHT));
        this.delete.setBounds(new Box(left + inner - DELETE_WIDTH, actionY, DELETE_WIDTH, ACTION_HEIGHT));
        return this.frame.bottom();
    }

    void render(UiGraphics graphics, float mouseX, float mouseY) {
        graphics.fill(this.frame, FILL);
        Box picture = new Box(this.frame.x() + 1.0F, this.frame.y() + 1.0F, WIDTH - 2.0F, IMAGE_HEIGHT);
        this.image.draw(graphics, picture);
        graphics.border(this.frame, 1.0F, ArkColors.BORDER_DEFAULT);
        TextMetrics metrics = graphics.metrics();
        float x = this.frame.x() + PADDING;
        float inner = WIDTH - PADDING * 2.0F;
        float y = picture.bottom() + PADDING;
        graphics.text(metrics.ellipsize(this.summary.getLevelName(), NAME, inner), x, y, NAME, ArkColors.TEXT_PRIMARY);
        String folder = Component.translatable("arkea.worlds.folder", this.summary.getLevelId()).getString();
        graphics.text(metrics.ellipsize(folder, FOLDER, inner), x, y + metrics.capHeight(NAME) + NAME_GAP, FOLDER, ArkColors.TEXT_FAINT);
        y += NAME_BLOCK + GAP;
        float line = graphics.scale().snapThickness(1.0F);
        for (Info info : this.info()) {
            float textY = y + (INFO_ROW - metrics.capHeight(INFO)) * 0.5F;
            graphics.text(info.key().getString(), x, textY, INFO, ArkColors.TEXT_DESCRIPTION);
            String value = info.value().getString();
            graphics.text(value, x + inner - metrics.width(value, INFO), textY, INFO, ArkColors.TEXT_PRIMARY);
            graphics.fill(x, y + INFO_ROW - line, inner, line, INFO_LINE);
            y += INFO_ROW;
        }
        this.play.render(graphics, mouseX, mouseY);
        this.edit.render(graphics, mouseX, mouseY);
        this.recreate.render(graphics, mouseX, mouseY);
        this.delete.render(graphics, mouseX, mouseY);
    }

    private record Info(Component key, Component value) {
    }
}
