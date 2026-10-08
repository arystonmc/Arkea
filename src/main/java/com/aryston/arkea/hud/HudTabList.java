package com.aryston.arkea.hud;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.jspecify.annotations.Nullable;

public final class HudTabList {
    private static final Comparator<PlayerInfo> ORDER = Comparator.<PlayerInfo>comparingInt(info -> -info.getTabListOrder())
        .thenComparingInt(info -> info.getGameMode() == GameType.SPECTATOR ? 1 : 0)
        .thenComparing(info -> Optionull.mapOrDefault(info.getTeam(), PlayerTeam::getName, ""))
        .thenComparing(info -> info.getProfile().name(), String::compareToIgnoreCase);
    private static final int MAX_PLAYERS = 80;
    private static final int MAX_ROWS = 20;
    private static final float TOP = 16.0F;
    private static final float SCREEN_INSET = 40.0F;
    private static final float PAD = 10.0F;
    private static final float ROW = 18.0F;
    private static final float ROW_GAP = 2.0F;
    private static final float COLUMN_GAP = 8.0F;
    private static final float SLOT_PAD = 5.0F;
    private static final float HEAD = 12.0F;
    private static final int FACE_PIXELS = 8;
    private static final float HEAD_GAP = 7.0F;
    private static final float SCORE_GAP = 12.0F;
    private static final float HEART = 9.0F;
    private static final int HEART_PIXELS = 9;
    private static final float HEART_GAP = 4.0F;
    private static final float HALF_HEARTS = 2.0F;
    private static final float LINE = 12.0F;
    private static final float SECTION_GAP = 8.0F;
    private static final int BARS = 4;
    private static final float BAR_WIDTH = 3.0F;
    private static final float BAR_GAP = 1.0F;
    private static final float BAR_STEP = 2.0F;
    private static final float BAR_BASE = 4.0F;
    private static final float PING_GAP = 10.0F;
    private static final int PING_GOOD = 150;
    private static final int PING_FAIR = 300;
    private static final int PING_SLOW = 600;
    private static final float MIN_OPACITY = 0.6F;
    private static final float SPECTATOR = 0.55F;
    private static final float HALF = 0.5F;
    private static final int SLOT_FILL = ArkColors.rgba(255, 255, 255, 0.03F);
    private static final int BAR_OFF = ArkColors.rgba(255, 255, 255, 0.12F);
    private static final int PING_FAST = ArkColors.rgba(123, 201, 95, 1.0F);
    private static final int PING_MEDIUM = ArkColors.rgba(230, 176, 74, 1.0F);
    private static final int PING_LAGGY = ArkColors.ERROR;
    private static final Identifier HEART_SPRITE = Identifier.withDefaultNamespace("hud/heart/full");
    private static final TextStyle TEXT = TextStyle.of(10.0F);

    private HudTabList() {
    }

    public static void draw(UiGraphics graphics, Box screen, Minecraft minecraft, PlayerTabOverlay overlay, Scoreboard scoreboard,
        @Nullable Objective objective, @Nullable Component header, @Nullable Component footer, HudSettings settings) {
        if (minecraft.player == null || minecraft.level == null || minecraft.getConnection() == null) {
            return;
        }
        TextMetrics metrics = graphics.metrics();
        Font font = minecraft.font;
        float fontScale = metrics.fontScale(TEXT);
        List<PlayerInfo> infos = minecraft.player.connection.getListedOnlinePlayers().stream().sorted(ORDER).limit(MAX_PLAYERS).toList();
        boolean hearts = objective != null && objective.getRenderType() == ObjectiveCriteria.RenderType.HEARTS;
        List<Entry> entries = new ArrayList<>(infos.size());
        float nameWidth = 0.0F;
        float scoreWidth = 0.0F;
        for (PlayerInfo info : infos) {
            Component name = overlay.getNameForDisplay(info);
            Component score = score(scoreboard, objective, info, hearts);
            entries.add(new Entry(info, name, score));
            nameWidth = Math.max(nameWidth, font.width(name) * fontScale);
            if (score != null) {
                scoreWidth = Math.max(scoreWidth, font.width(score) * fontScale + (hearts ? HEART + HEART_GAP : 0.0F));
            }
        }
        int rows = entries.size();
        int columns = 1;
        while (rows > MAX_ROWS) {
            columns++;
            rows = (entries.size() + columns - 1) / columns;
        }
        boolean heads = minecraft.getConnection().onlineMode();
        float available = screen.width() - SCREEN_INSET * 2.0F;
        float slotWidth = SLOT_PAD * 2.0F + (heads ? HEAD + HEAD_GAP : 0.0F) + nameWidth + (scoreWidth > 0.0F ? SCORE_GAP + scoreWidth : 0.0F) + PING_GAP
            + pingWidth();
        slotWidth = Math.min(slotWidth, (available - PAD * 2.0F - (columns - 1) * COLUMN_GAP) / columns);
        float gridWidth = columns * slotWidth + (columns - 1) * COLUMN_GAP;
        int wrap = (int) ((available - PAD * 2.0F) / fontScale);
        List<FormattedCharSequence> headerLines = header != null ? font.split(header, wrap) : List.of();
        List<FormattedCharSequence> footerLines = footer != null ? font.split(footer, wrap) : List.of();
        float contentWidth = Math.max(gridWidth, Math.max(linesWidth(font, headerLines, fontScale), linesWidth(font, footerLines, fontScale)));
        float height = PAD * 2.0F + rows * ROW + Math.max(0, rows - 1) * ROW_GAP + section(headerLines) + section(footerLines);
        Box card = new Box(screen.centerX() - contentWidth * HALF - PAD, screen.y() + TOP, contentWidth + PAD * 2.0F, height);
        graphics.fill(card, ArkColors.withAlpha(HudPainter.plate(settings), Math.max(settings.opacity(), MIN_OPACITY)));
        graphics.border(card, 1.0F, ArkColors.BORDER_OVERLAY);
        float y = card.y() + PAD;
        y = lines(graphics, headerLines, card, y, font, fontScale);
        if (!headerLines.isEmpty()) {
            graphics.fill(card.x() + PAD, y + SECTION_GAP * HALF - HALF, contentWidth, 1.0F, ArkColors.BORDER_DEFAULT);
            y += SECTION_GAP;
        }
        float gridX = card.centerX() - gridWidth * HALF;
        for (int index = 0; index < entries.size(); index++) {
            Box slot = new Box(gridX + index / rows * (slotWidth + COLUMN_GAP), y + index % rows * (ROW + ROW_GAP), slotWidth, ROW);
            slot(graphics, slot, entries.get(index), minecraft, heads, hearts, fontScale);
        }
        y += rows * ROW + Math.max(0, rows - 1) * ROW_GAP;
        if (!footerLines.isEmpty()) {
            graphics.fill(card.x() + PAD, y + SECTION_GAP * HALF - HALF, contentWidth, 1.0F, ArkColors.BORDER_DEFAULT);
            lines(graphics, footerLines, card, y + SECTION_GAP, font, fontScale);
        }
    }

    private static @Nullable Component score(Scoreboard scoreboard, @Nullable Objective objective, PlayerInfo info, boolean hearts) {
        if (objective == null || info.getGameMode() == GameType.SPECTATOR) {
            return null;
        }
        ReadOnlyScoreInfo score = scoreboard.getPlayerScoreInfo(ScoreHolder.fromGameProfile(info.getProfile()), objective);
        if (score == null) {
            return null;
        }
        if (hearts) {
            return Component.literal(String.valueOf(score.value() / HALF_HEARTS));
        }
        return score.formatValue(objective.numberFormatOrDefault(StyledFormat.PLAYER_LIST_DEFAULT));
    }

    private static void slot(UiGraphics graphics, Box slot, Entry entry, Minecraft minecraft, boolean heads, boolean hearts, float fontScale) {
        PlayerInfo info = entry.info();
        boolean self = info.getProfile().id().equals(minecraft.player.getUUID());
        graphics.fill(slot, self ? Theme.accent().tint() : SLOT_FILL);
        if (self) {
            graphics.border(slot, 1.0F, Theme.accent().border());
        }
        graphics.push();
        if (info.getGameMode() == GameType.SPECTATOR) {
            graphics.fade(SPECTATOR);
        }
        float x = slot.x() + SLOT_PAD;
        if (heads) {
            Player player = minecraft.level.getPlayerByUUID(info.getProfile().id());
            boolean flip = player != null && AvatarRenderer.isPlayerUpsideDown(player);
            Identifier skin = info.getSkin().body().texturePath();
            graphics.vanilla(new Box(x, slot.centerY() - HEAD * HALF, HEAD, HEAD), FACE_PIXELS,
                vanilla -> PlayerFaceExtractor.extractRenderState(vanilla, skin, 0, 0, FACE_PIXELS, info.showHat(), flip, -1));
            x += HEAD + HEAD_GAP;
        }
        float textY = slot.centerY() - graphics.metrics().capHeight(TEXT) * HALF;
        float pingX = slot.right() - SLOT_PAD - pingWidth();
        Component score = entry.score();
        float scoreWidth = score != null ? minecraft.font.width(score) * fontScale : 0.0F;
        float nameRoom = pingX - PING_GAP - x - (score != null ? SCORE_GAP + scoreWidth + (hearts ? HEART + HEART_GAP : 0.0F) : 0.0F);
        graphics.richText(entry.name(), x, textY, Math.max(0.0F, nameRoom), TEXT, ArkColors.TEXT_PRIMARY);
        if (score != null) {
            float scoreX = pingX - PING_GAP - scoreWidth;
            graphics.richText(score, scoreX, textY, scoreWidth + 1.0F, TEXT, hearts ? ArkColors.DANGER_TEXT : ArkColors.TEXT_SOFT);
            if (hearts) {
                float heartX = scoreX - HEART_GAP - HEART;
                graphics.vanilla(new Box(heartX, slot.centerY() - HEART * HALF, HEART, HEART), HEART_PIXELS,
                    vanilla -> vanilla.blitSprite(RenderPipelines.GUI_TEXTURED, HEART_SPRITE, 0, 0, HEART_PIXELS, HEART_PIXELS));
            }
        }
        ping(graphics, pingX, slot.centerY(), info.getLatency());
        graphics.pop();
    }

    private static void ping(UiGraphics graphics, float x, float centerY, int latency) {
        int bars = latency < 0 ? 0 : latency < PING_GOOD ? BARS : latency < PING_FAIR ? BARS - 1 : latency < PING_SLOW ? BARS - 2 : 1;
        int color = bars >= BARS - 1 ? PING_FAST : bars == BARS - 2 ? PING_MEDIUM : PING_LAGGY;
        float bottom = centerY + (BAR_BASE + (BARS - 1) * BAR_STEP) * HALF;
        for (int index = 0; index < BARS; index++) {
            float height = BAR_BASE + index * BAR_STEP;
            graphics.fill(x + index * (BAR_WIDTH + BAR_GAP), bottom - height, BAR_WIDTH, height, index < bars ? color : BAR_OFF);
        }
    }

    private static float pingWidth() {
        return BARS * BAR_WIDTH + (BARS - 1) * BAR_GAP;
    }

    private static float linesWidth(Font font, List<FormattedCharSequence> lines, float fontScale) {
        float width = 0.0F;
        for (FormattedCharSequence line : lines) {
            width = Math.max(width, font.width(line) * fontScale);
        }
        return width;
    }

    private static float section(List<FormattedCharSequence> lines) {
        return lines.isEmpty() ? 0.0F : lines.size() * LINE + SECTION_GAP;
    }

    private static float lines(UiGraphics graphics, List<FormattedCharSequence> lines, Box card, float y, Font font, float fontScale) {
        float top = y;
        for (FormattedCharSequence line : lines) {
            float width = font.width(line) * fontScale;
            graphics.textLine(line, card.centerX() - width * HALF, top + (LINE - graphics.metrics().capHeight(TEXT)) * HALF, TEXT, ArkColors.TEXT_PRIMARY);
            top += LINE;
        }
        return top;
    }

    private record Entry(PlayerInfo info, Component name, @Nullable Component score) {
    }
}
