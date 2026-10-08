package com.aryston.arkea.hud.target;

import com.aryston.arkea.hud.HudPainter;
import com.aryston.arkea.hud.HudSettings;
import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public final class TargetCard {
    private static final int FADE = 150;
    private static final float TOP = 8.0F;
    private static final float BOSS_GAP = 6.0F;
    private static final float PAD = 8.0F;
    private static final float ICON_BOX = 24.0F;
    private static final float ICON_GAP = 9.0F;
    private static final float LINE_GAP = 4.0F;
    private static final float LINE_ICON = 10.0F;
    private static final float LINE_ICON_GAP = 4.0F;
    private static final float MARK = 7.0F;
    private static final float MARK_GAP = 5.0F;
    private static final float CHECK_HEIGHT = 5.0F;
    private static final float HEART = 9.0F;
    private static final float HEART_STEP = 8.0F;
    private static final int HEART_PIXELS = 9;
    private static final int ITEM_PIXELS = 16;
    private static final String SEPARATOR = " / ";
    private static final int HEARTS_PER_ROW = 10;
    private static final int MAX_HEART_ROWS = 2;
    private static final float HEALTH_BAR_WIDTH = 90.0F;
    private static final float HEALTH_BAR = 4.0F;
    private static final float VALUE_GAP = 6.0F;
    private static final float MIN_WIDTH = 120.0F;
    private static final float BREAK_BAR = 2.0F;
    private static final float SLIDE = 10.0F;
    private static final float MIN_OPACITY = 0.7F;
    private static final float HALVES = 2.0F;
    private static final float HALF = 0.5F;
    private static final int HEALTH_COLOR = ArkColors.rgba(229, 83, 75, 0.95F);
    private static final int HEALTH_TRACK = ArkColors.rgba(255, 255, 255, 0.12F);
    private static final TextStyle NAME = TextStyle.of(11.0F);
    private static final TextStyle TEXT = TextStyle.of(10.0F);
    private static final TextStyle MOD = TextStyle.of(9.0F);
    private static final Identifier ARMOR = Identifier.withDefaultNamespace("hud/armor_full");

    private final Transition presence = new Transition(0.0F, FADE, Easing.EASE_OUT);
    private @Nullable TargetInfo shown;

    public void draw(UiGraphics graphics, Box screen, Minecraft minecraft, HudSettings settings, float bossBarsBottom) {
        long now = graphics.now();
        TargetInfo info = TargetReader.read(minecraft);
        if (info != null) {
            if (this.shown == null) {
                this.presence.snap(0.0F);
            }
            this.shown = info;
            this.presence.setTarget(1.0F, now);
        } else if (this.shown != null) {
            this.presence.setTarget(0.0F, now);
        }
        TargetInfo card = this.shown;
        if (card == null) {
            return;
        }
        float alpha = this.presence.value(now);
        if (info == null && alpha <= 0.0F) {
            this.shown = null;
            return;
        }
        TextMetrics metrics = graphics.metrics();
        float textWidth = Math.max(metrics.width(card.name().getString(), NAME), metrics.width(card.mod().getString(), MOD));
        for (TargetInfo.Line line : card.lines()) {
            textWidth = Math.max(textWidth, (line.icon().isEmpty() ? 0.0F : LINE_ICON + LINE_ICON_GAP) + metrics.width(line.text().getString(), TEXT)
                + (line.mark() == TargetInfo.Mark.NONE ? 0.0F : MARK_GAP + MARK));
        }
        if (card.living()) {
            textWidth = Math.max(textWidth, this.healthWidth(metrics, card));
        }
        boolean icon = !card.icon().isEmpty();
        float cardWidth = Math.max(MIN_WIDTH, PAD * 2.0F + (icon ? ICON_BOX + ICON_GAP : 0.0F) + textWidth + 1.0F);
        float textHeight = metrics.capHeight(NAME) + LINE_GAP + metrics.capHeight(MOD)
            + card.lines().size() * (metrics.capHeight(TEXT) + LINE_GAP) + (card.living() ? this.healthHeight(card) + LINE_GAP : 0.0F);
        float height = PAD * 2.0F + Math.max(icon ? ICON_BOX : 0.0F, textHeight);
        float top = Math.max(screen.y() + TOP, bossBarsBottom + BOSS_GAP);
        Box box = new Box(screen.centerX() - cardWidth * HALF, top - (1.0F - alpha) * SLIDE, cardWidth, height);
        graphics.push();
        graphics.fade(alpha);
        graphics.fill(box, ArkColors.withAlpha(HudPainter.plate(settings), Math.max(settings.opacity(), MIN_OPACITY)));
        graphics.border(box, 1.0F, ArkColors.BORDER_OVERLAY);
        graphics.clip(box);
        float x = box.x() + PAD;
        if (icon) {
            Box iconBox = new Box(x, box.y() + PAD, ICON_BOX, ICON_BOX);
            graphics.vanilla(iconBox, ITEM_PIXELS, vanilla -> vanilla.fakeItem(card.icon(), 0, 0));
            x += ICON_BOX + ICON_GAP;
        }
        float y = box.y() + PAD;
        float room = box.right() - PAD - x;
        graphics.richText(card.name(), x, y, room, NAME, ArkColors.TEXT_PRIMARY);
        y += metrics.capHeight(NAME) + LINE_GAP;
        if (card.living()) {
            this.health(graphics, metrics, card, x, y);
            y += this.healthHeight(card) + LINE_GAP;
        }
        for (TargetInfo.Line line : card.lines()) {
            float lineX = x;
            if (!line.icon().isEmpty()) {
                graphics.vanilla(new Box(lineX, y + (metrics.capHeight(TEXT) - LINE_ICON) * HALF, LINE_ICON, LINE_ICON), ITEM_PIXELS,
                    vanilla -> vanilla.fakeItem(line.icon(), 0, 0));
                lineX += LINE_ICON + LINE_ICON_GAP;
            }
            graphics.richText(line.text(), lineX, y, box.right() - PAD - lineX, TEXT, line.color());
            mark(graphics, line.mark(), lineX + metrics.width(line.text().getString(), TEXT) + MARK_GAP, y + metrics.capHeight(TEXT) * HALF);
            y += metrics.capHeight(TEXT) + LINE_GAP;
        }
        graphics.richText(card.mod(), x, y, room, MOD, TargetReader.modColor());
        if (card.breaking() >= 0.0F) {
            graphics.fill(box.x(), box.bottom() - BREAK_BAR, box.width() * card.breaking(), BREAK_BAR, Theme.accent().light());
        }
        graphics.endClip();
        graphics.pop();
    }

    private static void mark(UiGraphics graphics, TargetInfo.Mark mark, float x, float centerY) {
        if (mark == TargetInfo.Mark.YES) {
            graphics.icon(Icons.CHECK, x, centerY - CHECK_HEIGHT * HALF, MARK, CHECK_HEIGHT, TargetReader.goodColor());
        } else if (mark == TargetInfo.Mark.NO) {
            graphics.icon(Icons.CLOSE, x, centerY - MARK * HALF, MARK, MARK, ArkColors.ERROR);
        }
    }

    private boolean hearts(TargetInfo card) {
        return card.maxHealth() <= HEARTS_PER_ROW * MAX_HEART_ROWS * HALVES;
    }

    private float healthWidth(TextMetrics metrics, TargetInfo card) {
        String value = this.healthText(card);
        float graphic = this.hearts(card) ? Math.min(HEARTS_PER_ROW, Mth.ceil(card.maxHealth() / HALVES)) * HEART_STEP + 1.0F : HEALTH_BAR_WIDTH;
        float armor = card.armor() > 0 ? VALUE_GAP + LINE_ICON + LINE_ICON_GAP + metrics.width(String.valueOf(card.armor()), TEXT) : 0.0F;
        return graphic + VALUE_GAP + metrics.width(value, TEXT) + armor;
    }

    private float healthHeight(TargetInfo card) {
        if (!this.hearts(card)) {
            return HEART;
        }
        int rows = Mth.ceil(Mth.ceil(card.maxHealth() / HALVES) / (float) HEARTS_PER_ROW);
        return HEART + (rows - 1) * HEART_STEP;
    }

    private String healthText(TargetInfo card) {
        return Mth.ceil(card.health()) + SEPARATOR + Mth.ceil(card.maxHealth());
    }

    private void health(UiGraphics graphics, TextMetrics metrics, TargetInfo card, float x, float y) {
        float right;
        if (this.hearts(card)) {
            int containers = Mth.ceil(card.maxHealth() / HALVES);
            int health = Mth.ceil(card.health());
            for (int index = 0; index < containers; index++) {
                float heartX = x + index % HEARTS_PER_ROW * HEART_STEP;
                float heartY = y + index / HEARTS_PER_ROW * HEART_STEP;
                sprite(graphics, Hud.HeartType.CONTAINER.getSprite(false, false, false), heartX, heartY, HEART);
                int halves = index * 2 + 1;
                if (halves <= health) {
                    sprite(graphics, Hud.HeartType.NORMAL.getSprite(false, halves == health, false), heartX, heartY, HEART);
                }
            }
            right = x + Math.min(HEARTS_PER_ROW, containers) * HEART_STEP + 1.0F;
        } else {
            float barY = y + (HEART - HEALTH_BAR) * HALF;
            graphics.fill(x, barY, HEALTH_BAR_WIDTH, HEALTH_BAR, HEALTH_TRACK);
            graphics.fill(x, barY, HEALTH_BAR_WIDTH * Math.clamp(card.health() / card.maxHealth(), 0.0F, 1.0F), HEALTH_BAR, HEALTH_COLOR);
            right = x + HEALTH_BAR_WIDTH;
        }
        float textY = y + (HEART - metrics.capHeight(TEXT)) * HALF;
        String value = this.healthText(card);
        graphics.text(value, right + VALUE_GAP, textY, TEXT, ArkColors.TEXT_SOFT);
        if (card.armor() > 0) {
            float armorX = right + VALUE_GAP + metrics.width(value, TEXT) + VALUE_GAP;
            sprite(graphics, ARMOR, armorX, y + (HEART - LINE_ICON) * HALF, LINE_ICON);
            graphics.text(String.valueOf(card.armor()), armorX + LINE_ICON + LINE_ICON_GAP, textY, TEXT, ArkColors.TEXT_SOFT);
        }
    }

    private static void sprite(UiGraphics graphics, Identifier sprite, float x, float y, float size) {
        graphics.vanilla(new Box(x, y, size, size), HEART_PIXELS,
            vanilla -> vanilla.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, 0, 0, HEART_PIXELS, HEART_PIXELS));
    }
}
