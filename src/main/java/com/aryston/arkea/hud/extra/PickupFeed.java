package com.aryston.arkea.hud.extra;

import com.aryston.arkea.hud.HudClock;
import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.hud.HudPainter;
import com.aryston.arkea.hud.HudSettings;
import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class PickupFeed {
    private static final int MAX_ROWS = 6;
    private static final long MERGE_WINDOW = 3000L;
    private static final long LIFE = 4000L;
    private static final int ENTER = 220;
    private static final int LEAVE = 450;
    private static final int BUMP = 260;
    private static final float ROW = 22.0F;
    private static final float ROW_GAP = 3.0F;
    private static final float PAD = 6.0F;
    private static final float ICON = 16.0F;
    private static final int ITEM_PIXELS = 16;
    private static final float GAP = 6.0F;
    private static final float MARGIN = 20.0F;
    private static final float SLIDE = 24.0F;
    private static final float BUMP_SCALE = 0.25F;
    private static final float BUMP_FLASH = 0.3F;
    private static final float MIN_OPACITY = 0.45F;
    private static final float HALF = 0.5F;
    private static final int GAIN = ArkColors.rgba(170, 235, 130, 1.0F);
    private static final TextStyle COUNT = TextStyle.of(10.0F);
    private static final TextStyle NAME = TextStyle.of(10.0F);
    private static final List<Entry> ENTRIES = new ArrayList<>();

    private PickupFeed() {
    }

    public static void add(ItemStack stack, int amount) {
        if (stack.isEmpty() || amount <= 0 || !ArkeaConfig.on(ArkeaConfig.PICKUP_FEED)) {
            return;
        }
        long now = HudClock.now();
        for (Entry entry : ENTRIES) {
            if (!entry.experience && now - entry.updatedAt < MERGE_WINDOW && ItemStack.isSameItemSameComponents(entry.stack, stack)) {
                entry.count += amount;
                entry.updatedAt = now;
                return;
            }
        }
        push(new Entry(stack.copyWithCount(1), amount, false, now));
    }

    public static void experience(int amount) {
        if (amount <= 0 || !ArkeaConfig.on(ArkeaConfig.PICKUP_FEED)) {
            return;
        }
        long now = HudClock.now();
        for (Entry entry : ENTRIES) {
            if (entry.experience && now - entry.updatedAt < MERGE_WINDOW) {
                entry.count += amount;
                entry.updatedAt = now;
                return;
            }
        }
        push(new Entry(new ItemStack(Items.EXPERIENCE_BOTTLE), amount, true, now));
    }

    private static void push(Entry entry) {
        ENTRIES.addFirst(entry);
        while (ENTRIES.size() > MAX_ROWS) {
            ENTRIES.removeLast();
        }
    }

    public static void draw(UiGraphics graphics, Box screen, HudSettings settings, float bottom) {
        long now = graphics.now();
        ENTRIES.removeIf(entry -> now - entry.updatedAt > LIFE);
        TextMetrics metrics = graphics.metrics();
        float y = bottom;
        for (Entry entry : ENTRIES) {
            float enter = Easing.STANDARD.apply(Math.clamp((now - entry.addedAt) / (float) ENTER, 0.0F, 1.0F));
            long left = LIFE - (now - entry.updatedAt);
            float leave = Math.clamp(left / (float) LEAVE, 0.0F, 1.0F);
            float alpha = Math.min(enter, leave);
            String count = "+" + entry.count;
            Component name = entry.experience ? Component.translatable("arkea.pickup.experience") : entry.stack.getHoverName();
            float width = PAD * 2.0F + metrics.width(count, COUNT) + GAP + ICON + GAP + metrics.width(name.getString(), NAME);
            float x = screen.right() - MARGIN - width + (1.0F - enter) * SLIDE;
            Box row = new Box(x, y - ROW, width, ROW);
            graphics.push();
            graphics.fade(alpha);
            graphics.fill(row, ArkColors.withAlpha(HudPainter.plate(settings), Math.max(settings.opacity(), MIN_OPACITY)));
            float textY = row.centerY() - metrics.capHeight(COUNT) * HALF;
            float bump = bump(now - entry.updatedAt) * (entry.updatedAt > entry.addedAt ? 1.0F : 0.0F);
            float countX = row.x() + PAD;
            float countWidth = metrics.width(count, COUNT);
            graphics.push();
            graphics.scaleAround(1.0F + BUMP_SCALE * bump, countX + countWidth * HALF, row.centerY());
            graphics.text(count, countX, textY, COUNT, ArkColors.brighten(GAIN, 1.0F + BUMP_FLASH * bump));
            graphics.pop();
            float iconX = countX + countWidth + GAP;
            graphics.vanilla(new Box(iconX, row.centerY() - ICON * HALF, ICON, ICON), ITEM_PIXELS, vanilla -> vanilla.fakeItem(entry.stack, 0, 0));
            graphics.richText(name, iconX + ICON + GAP, textY, row.right() - PAD - iconX - ICON - GAP, NAME, ArkColors.TEXT_SOFT);
            graphics.pop();
            y -= (ROW + ROW_GAP) * Math.min(enter, leave);
        }
    }

    private static float bump(long elapsed) {
        if (elapsed >= BUMP) {
            return 0.0F;
        }
        return Mth.sin(Easing.EASE_OUT.apply(elapsed / (float) BUMP) * Mth.PI);
    }

    private static final class Entry {
        private final ItemStack stack;
        private final boolean experience;
        private final long addedAt;
        private int count;
        private long updatedAt;

        private Entry(ItemStack stack, int count, boolean experience, long now) {
            this.stack = stack;
            this.count = count;
            this.experience = experience;
            this.addedAt = now;
            this.updatedAt = now;
        }
    }
}
