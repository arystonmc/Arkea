package com.aryston.arkea.hud;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.TeamColor;
import org.jspecify.annotations.Nullable;

public final class HudSidebar {
    private static final Comparator<PlayerScoreEntry> ORDER = Comparator.comparing(PlayerScoreEntry::value).reversed()
        .thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER);
    private static final int MAX_ROWS = 15;
    private static final float MARGIN = 16.0F;
    private static final float PAD_X = 10.0F;
    private static final float PAD_Y = 7.0F;
    private static final float ROW = 13.0F;
    private static final float TITLE_GAP = 7.0F;
    private static final float SCORE_GAP = 12.0F;
    private static final float CENTER_SHARE = 1.0F / 3.0F;
    private static final float HALF = 0.5F;
    private static final TextStyle TEXT = TextStyle.of(10.0F);

    private HudSidebar() {
    }

    public static @Nullable Objective objective(Scoreboard scoreboard, Player player) {
        PlayerTeam team = scoreboard.getPlayersTeam(player.getScoreboardName());
        if (team != null) {
            Optional<TeamColor> color = team.getColor();
            if (color.isPresent()) {
                Objective objective = scoreboard.getDisplayObjective(color.get().displaySlot());
                if (objective != null) {
                    return objective;
                }
            }
        }
        return scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
    }

    public static void draw(UiGraphics graphics, Box screen, Objective objective, HudSettings settings) {
        Scoreboard scoreboard = objective.getScoreboard();
        NumberFormat format = objective.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT);
        List<Row> rows = scoreboard.listPlayerScores(objective).stream()
            .filter(entry -> !entry.isHidden())
            .sorted(ORDER)
            .limit(MAX_ROWS)
            .map(entry -> new Row(PlayerTeam.formatNameForTeam(scoreboard.getPlayersTeam(entry.owner()), entry.ownerName()), entry.formatValue(format)))
            .toList();
        TextMetrics metrics = graphics.metrics();
        Component title = objective.getDisplayName();
        float content = metrics.width(title.getString(), TEXT);
        for (Row row : rows) {
            float score = metrics.width(row.score().getString(), TEXT);
            content = Math.max(content, metrics.width(row.name().getString(), TEXT) + (score > 0.0F ? SCORE_GAP + score : 0.0F));
        }
        float width = content + PAD_X * 2.0F;
        float height = PAD_Y * 2.0F + ROW + TITLE_GAP + rows.size() * ROW;
        float bottom = screen.centerY() + height * CENTER_SHARE;
        Box box = new Box(screen.right() - MARGIN - width, bottom - height, width, height);
        graphics.fill(box, HudPainter.plate(settings));
        float titleWidth = metrics.width(title.getString(), TEXT);
        float textInset = (ROW - metrics.capHeight(TEXT)) * HALF;
        graphics.richText(title, box.centerX() - titleWidth * HALF, box.y() + PAD_Y + textInset, titleWidth + 1.0F, TEXT, Theme.accent().light());
        float lineY = box.y() + PAD_Y + ROW + TITLE_GAP * HALF;
        graphics.fill(box.x() + PAD_X, lineY, content, 1.0F, ArkColors.BORDER_DEFAULT);
        float y = box.y() + PAD_Y + ROW + TITLE_GAP;
        for (Row row : rows) {
            float scoreWidth = metrics.width(row.score().getString(), TEXT);
            graphics.richText(row.name(), box.x() + PAD_X, y + textInset, content - scoreWidth, TEXT, ArkColors.TEXT_SOFT);
            graphics.richText(row.score(), box.right() - PAD_X - scoreWidth, y + textInset, scoreWidth + 1.0F, TEXT, ArkColors.TEXT_PRIMARY);
            y += ROW;
        }
    }

    private record Row(Component name, Component score) {
    }
}
