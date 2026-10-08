package com.aryston.arkea.hud;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Transition;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

final class HudMotion {
    static final int SLOT_COUNT = 10;
    private static final int SELECT_SLIDE = 170;
    private static final int SELECT_POP = 240;
    private static final int ICON_POP = 300;
    private static final int COUNT_ROLL = 260;
    private static final int BAR_FILL = 320;
    private static final int GHOST_HOLD = 420;
    private static final int GHOST_DRAIN = 520;
    private static final int LEVEL_POP = 420;
    private static final int NAME_IN = 220;
    private static final float SELECT_SCALE = 0.12F;
    private static final float GROW = 0.2F;
    private static final float SHRINK = 0.14F;
    private static final float SWAP_FROM = 0.72F;
    private static final float LEVEL_SCALE = 0.45F;

    private final SlotMotion[] slots = new SlotMotion[SLOT_COUNT];
    private final Transition selectedX = new Transition(0.0F, SELECT_SLIDE, Easing.STANDARD);
    private final Bar health = new Bar();
    private final Bar food = new Bar();
    private final Transition progress = new Transition(0.0F, BAR_FILL, Easing.EASE_OUT);
    private int selected = -1;
    private long selectedAt;
    private int level = -1;
    private long levelAt;
    private String name = "";
    private long nameAt;
    private boolean xpSeen;

    HudMotion() {
        for (int index = 0; index < SLOT_COUNT; index++) {
            this.slots[index] = new SlotMotion();
        }
    }

    float selection(int slot, long now) {
        if (this.selected < 0) {
            this.selectedX.snap(slot);
        } else if (slot != this.selected) {
            this.selectedAt = now;
            this.selectedX.setTarget(slot, now);
        }
        this.selected = slot;
        return this.selectedX.value(now);
    }

    float selectionScale(long now) {
        return 1.0F + SELECT_SCALE * bump(now - this.selectedAt, SELECT_POP);
    }

    SlotMotion slot(int index) {
        return this.slots[index];
    }

    Bar health() {
        return this.health;
    }

    Bar food() {
        return this.food;
    }

    float progress(float target, long now) {
        if (!this.xpSeen) {
            this.xpSeen = true;
            this.progress.snap(target);
        }
        this.progress.setTarget(target, now);
        return this.progress.value(now);
    }

    float levelScale(int value, long now) {
        if (this.level >= 0 && value > this.level) {
            this.levelAt = now;
        }
        this.level = value;
        return 1.0F + LEVEL_SCALE * bump(now - this.levelAt, LEVEL_POP);
    }

    float nameEntrance(String value, long now) {
        if (!value.equals(this.name)) {
            this.name = value;
            this.nameAt = now;
        }
        return Easing.EASE_OUT.apply(Math.clamp((now - this.nameAt) / (float) NAME_IN, 0.0F, 1.0F));
    }

    private static float bump(long elapsed, int duration) {
        if (elapsed < 0L || elapsed >= duration) {
            return 0.0F;
        }
        float progress = Easing.EASE_OUT.apply(elapsed / (float) duration);
        return Mth.sin(progress * Mth.PI);
    }

    static final class SlotMotion {
        private ItemStack item = ItemStack.EMPTY;
        private int count;
        private int previous;
        private int direction;
        private boolean swapped;
        private boolean seen;
        private long changedAt;

        void update(ItemStack stack, long now) {
            int newCount = stack.isEmpty() ? 0 : stack.getCount();
            if (this.seen && !ItemStack.isSameItem(stack, this.item)) {
                this.swapped = !stack.isEmpty();
                this.direction = 0;
                this.changedAt = now;
            } else if (this.seen && newCount != this.count) {
                this.swapped = false;
                this.previous = this.count;
                this.direction = Integer.signum(newCount - this.count);
                this.changedAt = now;
            }
            this.seen = true;
            this.item = stack.copy();
            this.count = newCount;
        }

        float iconScale(long now) {
            long elapsed = now - this.changedAt;
            if (this.swapped) {
                float progress = Math.clamp(elapsed / (float) ICON_POP, 0.0F, 1.0F);
                return SWAP_FROM + (1.0F - SWAP_FROM) * Easing.SPRING.apply(progress);
            }
            float amount = bump(elapsed, ICON_POP);
            return this.direction > 0 ? 1.0F + GROW * amount : this.direction < 0 ? 1.0F - SHRINK * amount : 1.0F;
        }

        float roll(long now) {
            if (this.swapped || this.direction == 0) {
                return 1.0F;
            }
            return Easing.EASE_OUT.apply(Math.clamp((now - this.changedAt) / (float) COUNT_ROLL, 0.0F, 1.0F));
        }

        int previous() {
            return this.previous;
        }

        int direction() {
            return this.direction;
        }
    }

    static final class Bar {
        private final Transition value = new Transition(0.0F, BAR_FILL, Easing.EASE_OUT);
        private float ghost;
        private float ghostFrom;
        private long ghostAt;
        private boolean seen;

        void update(float target, long now) {
            if (!this.seen) {
                this.seen = true;
                this.value.snap(target);
                this.ghost = target;
                this.ghostFrom = target;
                return;
            }
            if (target < this.value.target()) {
                this.ghostFrom = Math.max(this.ghost(now), this.value.value(now));
                this.ghost = target;
                this.ghostAt = now + GHOST_HOLD;
            } else if (target > this.value.target()) {
                this.ghost = target;
                this.ghostFrom = target;
            }
            this.value.setTarget(target, now);
        }

        float value(long now) {
            return this.value.value(now);
        }

        float ghost(long now) {
            if (now <= this.ghostAt) {
                return this.ghostFrom;
            }
            float progress = Math.clamp((now - this.ghostAt) / (float) GHOST_DRAIN, 0.0F, 1.0F);
            return this.ghostFrom + (this.ghost - this.ghostFrom) * Easing.EASE_IN_OUT.apply(progress);
        }
    }
}
