package com.aryston.arkea.hud;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.hud.extra.Durability;
import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public final class HudPainter {
    static final float LOW_SHARE = 0.3F;
    private static final float MARGIN = 20.0F;
    private static final float STATUS_WIDTH = 236.0F;
    private static final float PAD_X = 8.0F;
    private static final float PLATE_HEIGHT = 20.0F;
    private static final float BAR = 6.0F;
    private static final int SEGMENTS = 10;
    private static final float SEGMENT_GAP = 2.0F;
    private static final float ICON = 14.0F;
    private static final float ICON_GAP = 8.0F;
    private static final float VALUE_MIN = 22.0F;
    private static final float EXTRA_GAP = 2.0F;
    private static final float ROW_GAP = 5.0F;
    private static final float THIN_ROW = 10.0F;
    private static final float THIN_BAR = 2.0F;
    private static final float THIN_ICON = 10.0F;
    private static final float OVERLAY_LINE = 2.0F;
    private static final float PULSE_MIN = 0.25F;
    private static final float PULSE_RANGE = 0.55F;
    private static final int SLOTS = 9;
    private static final float SLOT = 40.0F;
    private static final float SLOT_GAP = 3.0F;
    private static final float HOTBAR_PAD = 4.0F;
    private static final float HOTBAR_BOTTOM = 16.0F;
    private static final float SIDE_GAP = 8.0F;
    private static final float ATTACK_WIDTH = 8.0F;
    private static final float ITEM = 28.0F;
    private static final float ITEM_NATIVE = 16.0F;
    private static final float GLOW_BLUR = 10.0F;
    private static final float COUNT_INSET = 3.0F;
    private static final float ROLL_DISTANCE = 9.0F;
    private static final float NAME_RISE = 6.0F;
    private static final float LEVEL_FLASH = 0.6F;
    private static final float ARMOR_ICON = 16.0F;
    private static final int ITEM_PIXELS = 16;
    private static final float ARMOR_ICON_GAP = 4.0F;
    private static final float ARMOR_BAR = 2.0F;
    private static final float ARMOR_BAR_GAP = 2.0F;
    private static final float PREVIEW_MIN = 0.3F;
    private static final float PREVIEW_RANGE = 0.5F;
    private static final int PREVIEW_PERIOD = 1100;
    private static final float XP_GAP = 6.0F;
    private static final float XP_HEIGHT = 2.0F;
    private static final float LEVEL_GAP = 3.0F;
    private static final float NAME_GAP = 8.0F;
    private static final float NAME_PAD_X = 8.0F;
    private static final float NAME_PAD_Y = 4.0F;
    private static final float NAME_BACKDROP_MIN = 0.3F;
    private static final int SPRITE_PIXELS = 9;
    private static final float SPRITE = 18.0F;
    private static final float SPRITE_STEP = 16.0F;
    private static final float CLASSIC_PAD = 4.0F;
    private static final float CLASSIC_GAP = 3.0F;
    private static final float HEART_ROW = 20.0F;
    private static final float HEART_ROW_SHRINK = 2.0F;
    private static final float HEART_ROW_MIN = 6.0F;
    private static final int VANILLA_ROW_BASE = 2;
    private static final int HEARTS_PER_ROW = 10;
    private static final float HALF = 0.5F;
    private static final float HALVES = 2.0F;
    private static final int SHAKE_HEALTH = 4;
    private static final long SHAKE_MILLIS = 100L;
    private static final int PLATE_RGB = 0x0A0A0C;
    private static final int TRACK = ArkColors.rgba(255, 255, 255, 0.12F);
    private static final int THIN_TRACK = ArkColors.rgba(255, 255, 255, 0.10F);
    private static final int SLOT_BORDER = ArkColors.rgba(255, 255, 255, 0.06F);
    private static final int HEALTH = ArkColors.rgba(229, 83, 75, 0.9F);
    private static final int HEALTH_LOW = ArkColors.rgba(240, 88, 74, 1.0F);
    private static final int POISON = ArkColors.rgba(141, 163, 59, 0.9F);
    private static final int WITHER = ArkColors.rgba(96, 96, 102, 0.95F);
    private static final int FROZEN = ArkColors.rgba(140, 199, 232, 0.9F);
    private static final int ABSORPTION = ArkColors.rgba(230, 176, 74, 1.0F);
    private static final int FOOD = ArkColors.rgba(217, 151, 58, 0.9F);
    private static final int FOOD_HUNGER = ArkColors.rgba(126, 154, 58, 0.9F);
    private static final int SATURATION = ArkColors.rgba(255, 224, 102, 0.9F);
    private static final int ARMOR = ArkColors.rgba(200, 211, 222, 0.85F);
    private static final int AIR = ArkColors.rgba(157, 180, 214, 0.9F);
    private static final int VEHICLE = ArkColors.rgba(224, 122, 154, 0.9F);
    private static final int XP_TRACK = ArkColors.rgba(0, 0, 0, 0.35F);
    private static final int XP_FILL = ArkColors.rgba(143, 209, 79, 0.9F);
    private static final int GHOST = ArkColors.rgba(255, 255, 255, 0.55F);
    private static final int COUNT_GAIN = ArkColors.rgba(170, 235, 130, 1.0F);
    private static final int COUNT_LOSS = ArkColors.rgba(255, 170, 140, 1.0F);
    private static final int TEXT_SHADOW = ArkColors.rgba(0, 0, 0, 0.55F);
    private static final TextStyle VALUE = TextStyle.of(10.0F).shadow(TEXT_SHADOW, 1.0F);
    private static final TextStyle NAME = TextStyle.of(10.0F);
    private static final TextStyle COUNT = TextStyle.of(11.0F).shadow(TEXT_SHADOW, 1.0F);
    private static final Identifier HEART_ICON = Identifier.withDefaultNamespace("hud/heart/full");
    private static final Identifier ARMOR_FULL = Identifier.withDefaultNamespace("hud/armor_full");
    private static final Identifier ARMOR_HALF = Identifier.withDefaultNamespace("hud/armor_half");
    private static final Identifier ARMOR_EMPTY = Identifier.withDefaultNamespace("hud/armor_empty");
    private static final Identifier FOOD_FULL = Identifier.withDefaultNamespace("hud/food_full");
    private static final Identifier FOOD_HALF = Identifier.withDefaultNamespace("hud/food_half");
    private static final Identifier FOOD_EMPTY = Identifier.withDefaultNamespace("hud/food_empty");
    private static final Identifier FOOD_FULL_HUNGER = Identifier.withDefaultNamespace("hud/food_full_hunger");
    private static final Identifier FOOD_HALF_HUNGER = Identifier.withDefaultNamespace("hud/food_half_hunger");
    private static final Identifier FOOD_EMPTY_HUNGER = Identifier.withDefaultNamespace("hud/food_empty_hunger");
    private static final Identifier AIR_FULL = Identifier.withDefaultNamespace("hud/air");
    private static final Identifier AIR_EMPTY = Identifier.withDefaultNamespace("hud/air_empty");
    private static final Identifier VEHICLE_CONTAINER = Identifier.withDefaultNamespace("hud/heart/vehicle_container");
    private static final Identifier VEHICLE_FULL = Identifier.withDefaultNamespace("hud/heart/vehicle_full");
    private static final Identifier VEHICLE_HALF = Identifier.withDefaultNamespace("hud/heart/vehicle_half");

    private final Transition status = new Transition(1.0F, Motion.HUD_FADE, Easing.EASE);
    private final HudMotion motion = new HudMotion();

    public static int plate(HudSettings settings) {
        return ArkColors.withAlpha(ArkColors.rgb(PLATE_RGB), settings.opacity());
    }

    public void drawStatus(UiGraphics graphics, Box screen, HudSnapshot hud, HudSettings settings) {
        float shown = this.visibility(graphics, hud, settings);
        if (shown <= 0.0F) {
            return;
        }
        graphics.push();
        graphics.fade(shown);
        if (hud.survival()) {
            this.drawLeft(graphics, screen, hud, settings);
        }
        this.drawRight(graphics, screen, hud, settings);
        graphics.pop();
    }

    public void drawHotbar(UiGraphics graphics, Box screen, HudSnapshot hud, HudSettings settings) {
        Box bar = hotbarBox(screen);
        int plate = plate(settings);
        long now = graphics.now();
        graphics.fill(bar, plate);
        for (int slot = 0; slot < SLOTS; slot++) {
            graphics.border(slotIn(bar, slot), 1.0F, SLOT_BORDER);
        }
        float selection = this.motion.selection(hud.selected(), now);
        float pop = this.motion.selectionScale(now);
        Box highlight = scaled(slotIn(bar, selection), pop);
        graphics.shadow(highlight, GLOW_BLUR, 0.0F, Theme.accent().glow());
        graphics.fill(highlight, Theme.accent().tint());
        graphics.border(highlight, 1.0F, Theme.accent().light());
        boolean warn = ArkeaConfig.on(ArkeaConfig.DURABILITY_WARNING);
        for (int slot = 0; slot < SLOTS; slot++) {
            ItemStack stack = hud.hotbar().get(slot);
            if (warn && Durability.low(stack)) {
                graphics.border(slotIn(bar, slot), 1.0F, ArkColors.withAlpha(ArkColors.ERROR, pulse(now)));
            }
            this.item(graphics, slotIn(bar, slot), stack, hud.owner(), slot, slot == hud.selected() ? pop : 1.0F);
        }
        float sideY = bar.y();
        if (!hud.offhand().isEmpty()) {
            float width = SLOT + HOTBAR_PAD * 2.0F;
            Box side = new Box(hud.offhandLeft() ? bar.x() - SIDE_GAP - width : bar.right() + SIDE_GAP, sideY, width, bar.height());
            graphics.fill(side, plate);
            Box box = new Box(side.x() + HOTBAR_PAD, side.y() + HOTBAR_PAD, SLOT, SLOT);
            graphics.border(box, 1.0F, SLOT_BORDER);
            this.item(graphics, box, hud.offhand(), hud.owner(), SLOTS, 1.0F);
        } else {
            this.motion.slot(SLOTS).update(ItemStack.EMPTY, now);
        }
        if (hud.attack() >= 0.0F) {
            Box gauge = new Box(hud.offhandLeft() ? bar.right() + SIDE_GAP : bar.x() - SIDE_GAP - ATTACK_WIDTH, sideY, ATTACK_WIDTH, bar.height());
            graphics.fill(gauge, plate);
            float filled = gauge.height() * hud.attack();
            graphics.fill(gauge.x(), gauge.bottom() - filled, gauge.width(), filled, Theme.accent().light());
        }
    }

    public void drawExperience(UiGraphics graphics, Box screen, HudSnapshot hud) {
        if (!hud.experience()) {
            return;
        }
        Box bar = hotbarBox(screen);
        long now = graphics.now();
        float y = bar.y() - XP_GAP - XP_HEIGHT;
        float progress = this.motion.progress(Math.clamp(hud.progress(), 0.0F, 1.0F), now);
        graphics.fill(bar.x(), y, bar.width(), XP_HEIGHT, XP_TRACK);
        graphics.fill(bar.x(), y, bar.width() * progress, XP_HEIGHT, XP_FILL);
        float pop = this.motion.levelScale(hud.level(), now);
        if (hud.level() > 0) {
            TextMetrics metrics = graphics.metrics();
            String level = String.valueOf(hud.level());
            float textY = y - LEVEL_GAP - metrics.capHeight(VALUE);
            graphics.push();
            graphics.scaleAround(pop, bar.centerX(), textY + metrics.capHeight(VALUE) * HALF);
            int color = ArkColors.brighten(ArkColors.XP, 1.0F + (pop - 1.0F) * LEVEL_FLASH);
            graphics.text(level, bar.centerX() - metrics.width(level, VALUE) * HALF, textY, VALUE, color);
            graphics.pop();
        }
    }

    public void drawItemName(UiGraphics graphics, Box screen, HudSnapshot hud, HudSettings settings) {
        Component name = hud.itemName();
        if (name == null || hud.itemNameAlpha() <= 0.0F) {
            return;
        }
        TextMetrics metrics = graphics.metrics();
        float width = metrics.width(name.getString(), NAME);
        Box bar = hotbarBox(screen);
        float textY = bar.y() - XP_GAP - XP_HEIGHT - LEVEL_GAP - metrics.capHeight(VALUE) - NAME_GAP - metrics.capHeight(NAME);
        float x = bar.centerX() - width * HALF;
        float entrance = this.motion.nameEntrance(name.getString(), graphics.now());
        textY += (1.0F - entrance) * NAME_RISE;
        graphics.push();
        graphics.fade(hud.itemNameAlpha() * entrance);
        Box backdrop = new Box(x - NAME_PAD_X, textY - NAME_PAD_Y, width + NAME_PAD_X * 2.0F, metrics.capHeight(NAME) + NAME_PAD_Y * 2.0F);
        graphics.fill(backdrop, ArkColors.withAlpha(ArkColors.rgb(PLATE_RGB), Math.max(settings.opacity(), NAME_BACKDROP_MIN)));
        graphics.richText(name, x, textY, width + 1.0F, NAME, ArkColors.TEXT_PRIMARY);
        graphics.pop();
    }

    public static Box hotbarBox(Box screen) {
        float width = SLOTS * SLOT + (SLOTS - 1) * SLOT_GAP + HOTBAR_PAD * 2.0F;
        float height = SLOT + HOTBAR_PAD * 2.0F;
        return new Box(screen.centerX() - width * HALF, screen.bottom() - HOTBAR_BOTTOM - height, width, height);
    }

    private float visibility(UiGraphics graphics, HudSnapshot hud, HudSettings settings) {
        if (!settings.style().hidesWhenFull()) {
            this.status.snap(1.0F);
            return 1.0F;
        }
        this.status.setTarget(hud.full() ? 0.0F : 1.0F, graphics.now());
        return this.status.value(graphics.now());
    }

    private void drawLeft(UiGraphics graphics, Box screen, HudSnapshot hud, HudSettings settings) {
        if (settings.style().icons()) {
            this.classicLeft(graphics, screen, hud, settings);
            return;
        }
        float x = screen.x() + MARGIN;
        Box plate = new Box(x, screen.bottom() - MARGIN - PLATE_HEIGHT, STATUS_WIDTH, PLATE_HEIGHT);
        String extra = hud.absorption() > 0.0F ? "+" + Mth.ceil(hud.absorption()) : null;
        boolean low = hud.low();
        long now = graphics.now();
        this.motion.health().update(hud.health(), now);
        Meter health = new Meter(HEART_ICON, this.motion.health().value(now), this.motion.health().ghost(now), 0.0F, hud.maxHealth(), healthColor(hud),
            String.valueOf(Mth.ceil(hud.health())), low ? ArkColors.DANGER_TEXT : ArkColors.TEXT_PRIMARY, extra, ABSORPTION, hud.absorption() / hud.maxHealth(),
            0.0F, ABSORPTION, low);
        meter(graphics, plate, settings, health, false);
        float top = plate.y();
        if (hud.armor() > 0) {
            Box row = new Box(x, plate.y() - ROW_GAP - THIN_ROW, STATUS_WIDTH, THIN_ROW);
            thin(graphics, row, ARMOR_FULL, hud.armor() / HudSnapshot.MAX_ARMOR, ARMOR, String.valueOf(hud.armor()), ArkColors.TEXT_SOFT, false);
            top = row.y();
        }
        armorIcons(graphics, x + PAD_X, top - ROW_GAP, hud);
    }

    private static void armorIcons(UiGraphics graphics, float x, float bottom, HudSnapshot hud) {
        if (!ArkeaConfig.on(ArkeaConfig.ARMOR_ICONS) || hud.armorItems().isEmpty()) {
            return;
        }
        float y = bottom - ARMOR_BAR - ARMOR_BAR_GAP - ARMOR_ICON;
        boolean warn = ArkeaConfig.on(ArkeaConfig.DURABILITY_WARNING);
        for (ItemStack stack : hud.armorItems()) {
            Box icon = new Box(x, y, ARMOR_ICON, ARMOR_ICON);
            graphics.vanilla(icon, ITEM_PIXELS, vanilla -> vanilla.fakeItem(stack, 0, 0));
            if (stack.isDamageableItem()) {
                float share = Durability.remaining(stack) / (float) stack.getMaxDamage();
                float barY = icon.bottom() + ARMOR_BAR_GAP;
                graphics.fill(x, barY, ARMOR_ICON, ARMOR_BAR, THIN_TRACK);
                graphics.fill(x, barY, ARMOR_ICON * share, ARMOR_BAR, ArkColors.withAlpha(stack.getBarColor(), 1.0F));
                if (warn && Durability.low(stack)) {
                    graphics.border(new Box(x - 1.0F, y - 1.0F, ARMOR_ICON + 2.0F, ARMOR_ICON + ARMOR_BAR_GAP + ARMOR_BAR + 2.0F), 1.0F,
                        ArkColors.withAlpha(ArkColors.ERROR, pulse(graphics.now())));
                }
            }
            x += ARMOR_ICON + ARMOR_ICON_GAP;
        }
    }

    private static float pulse(long now) {
        return PREVIEW_MIN + PREVIEW_RANGE * (Mth.sin((float) (now % PREVIEW_PERIOD) / PREVIEW_PERIOD * Mth.TWO_PI) + 1.0F) * HALF;
    }

    private void drawRight(UiGraphics graphics, Box screen, HudSnapshot hud, HudSettings settings) {
        if (!hud.riding() && !hud.survival()) {
            return;
        }
        if (settings.style().icons()) {
            this.classicRight(graphics, screen, hud, settings);
            return;
        }
        float x = screen.right() - MARGIN - STATUS_WIDTH;
        Box plate = new Box(x, screen.bottom() - MARGIN - PLATE_HEIGHT, STATUS_WIDTH, PLATE_HEIGHT);
        if (hud.riding()) {
            Meter vehicle = new Meter(VEHICLE_FULL, hud.vehicleHealth(), hud.vehicleHealth(), 0.0F, hud.vehicleMaxHealth(), VEHICLE,
                String.valueOf(Mth.ceil(hud.vehicleHealth())), ArkColors.TEXT_PRIMARY, null, VEHICLE, 0.0F, 0.0F, VEHICLE, false);
            meter(graphics, plate, settings, vehicle, true);
        } else {
            int color = hud.hungerEffect() ? FOOD_HUNGER : FOOD;
            Identifier icon = hud.hungerEffect() ? FOOD_FULL_HUNGER : FOOD_FULL;
            boolean hungry = hud.food() <= HudSnapshot.MAX_FOOD * LOW_SHARE;
            long now = graphics.now();
            this.motion.food().update(hud.food(), now);
            boolean preview = ArkeaConfig.on(ArkeaConfig.FOOD_PREVIEW) && hud.previewFood() > 0;
            float after = preview ? Math.min(HudSnapshot.MAX_FOOD, hud.food() + hud.previewFood()) : 0.0F;
            float saturationAfter = preview ? Math.min(after, hud.saturation() + hud.previewSaturation()) : 0.0F;
            Meter food = new Meter(icon, this.motion.food().value(now), this.motion.food().ghost(now), after, HudSnapshot.MAX_FOOD, color,
                String.valueOf(hud.food()), hungry ? ArkColors.WARNING_TEXT : ArkColors.TEXT_PRIMARY, null, SATURATION, hud.saturation() / HudSnapshot.MAX_FOOD,
                saturationAfter / HudSnapshot.MAX_FOOD, SATURATION, false);
            meter(graphics, plate, settings, food, true);
        }
        if (hud.survival() && hud.airVisible()) {
            Box row = new Box(x, plate.y() - ROW_GAP - THIN_ROW, STATUS_WIDTH, THIN_ROW);
            thin(graphics, row, AIR_FULL, hud.air(), AIR, String.valueOf(Mth.ceil(hud.air() * SEGMENTS)), ArkColors.INFO_TEXT, true);
        }
    }

    private void classicLeft(UiGraphics graphics, Box screen, HudSnapshot hud, HudSettings settings) {
        int containers = Mth.ceil(hud.maxHealth() / HALVES);
        int absorbing = Mth.ceil(hud.absorption() / HALVES);
        int total = containers + absorbing;
        int rows = Mth.ceil(total / (float) HEARTS_PER_ROW);
        float rowStep = Math.max(HEART_ROW - (rows - VANILLA_ROW_BASE) * HEART_ROW_SHRINK, HEART_ROW_MIN);
        Box plate = classicPlate(screen, false, SPRITE + (rows - 1) * rowStep);
        graphics.fill(plate, plate(settings));
        int health = Mth.ceil(hud.health());
        int absorption = Mth.ceil(hud.absorption());
        boolean shake = health + absorption <= SHAKE_HEALTH;
        long tick = graphics.now() / SHAKE_MILLIS;
        for (int index = total - 1; index >= 0; index--) {
            float x = plate.x() + CLASSIC_PAD + (index % HEARTS_PER_ROW) * SPRITE_STEP;
            float y = plate.bottom() - CLASSIC_PAD - SPRITE - (index / HEARTS_PER_ROW) * rowStep + (shake && (tick + index) % 2 == 0 ? 1.0F : 0.0F);
            sprite(graphics, Hud.HeartType.CONTAINER.getSprite(hud.hardcore(), false, false), x, y);
            int halves = index * 2;
            if (index >= containers) {
                int absorbingHalves = halves - containers * 2;
                if (absorbingHalves < absorption) {
                    Hud.HeartType type = hud.heart() == Hud.HeartType.WITHERED ? hud.heart() : Hud.HeartType.ABSORBING;
                    sprite(graphics, type.getSprite(hud.hardcore(), absorbingHalves + 1 == absorption, false), x, y);
                }
            } else if (halves < health) {
                sprite(graphics, hud.heart().getSprite(hud.hardcore(), halves + 1 == health, false), x, y);
            }
        }
        if (hud.armor() > 0) {
            Box armor = new Box(plate.x(), plate.y() - CLASSIC_GAP - SPRITE - CLASSIC_PAD * 2.0F, plate.width(), SPRITE + CLASSIC_PAD * 2.0F);
            graphics.fill(armor, plate(settings));
            for (int index = 0; index < HEARTS_PER_ROW; index++) {
                int halves = index * 2 + 1;
                Identifier icon = halves < hud.armor() ? ARMOR_FULL : halves == hud.armor() ? ARMOR_HALF : ARMOR_EMPTY;
                sprite(graphics, icon, armor.x() + CLASSIC_PAD + index * SPRITE_STEP, armor.y() + CLASSIC_PAD);
            }
            armorIcons(graphics, plate.x() + CLASSIC_PAD, armor.y() - CLASSIC_GAP, hud);
        } else {
            armorIcons(graphics, plate.x() + CLASSIC_PAD, plate.y() - CLASSIC_GAP, hud);
        }
    }

    private void classicRight(UiGraphics graphics, Box screen, HudSnapshot hud, HudSettings settings) {
        Box plate;
        if (hud.riding()) {
            int hearts = Math.min(Mth.ceil(hud.vehicleMaxHealth() / HALVES), HEARTS_PER_ROW * 3);
            int rows = Mth.ceil(hearts / (float) HEARTS_PER_ROW);
            plate = classicPlate(screen, true, SPRITE + (rows - 1) * HEART_ROW);
            graphics.fill(plate, plate(settings));
            int health = Mth.ceil(hud.vehicleHealth());
            for (int index = 0; index < hearts; index++) {
                float x = plate.right() - CLASSIC_PAD - SPRITE - (index % HEARTS_PER_ROW) * SPRITE_STEP;
                float y = plate.bottom() - CLASSIC_PAD - SPRITE - (index / HEARTS_PER_ROW) * HEART_ROW;
                sprite(graphics, VEHICLE_CONTAINER, x, y);
                int halves = index * 2 + 1;
                if (halves <= health) {
                    sprite(graphics, halves < health ? VEHICLE_FULL : VEHICLE_HALF, x, y);
                }
            }
        } else {
            plate = classicPlate(screen, true, SPRITE);
            graphics.fill(plate, plate(settings));
            Identifier full = hud.hungerEffect() ? FOOD_FULL_HUNGER : FOOD_FULL;
            Identifier half = hud.hungerEffect() ? FOOD_HALF_HUNGER : FOOD_HALF;
            Identifier empty = hud.hungerEffect() ? FOOD_EMPTY_HUNGER : FOOD_EMPTY;
            int after = ArkeaConfig.on(ArkeaConfig.FOOD_PREVIEW) ? Math.min((int) HudSnapshot.MAX_FOOD, hud.food() + hud.previewFood()) : hud.food();
            int pulse = ARGB.white(pulse(graphics.now()));
            for (int index = 0; index < HEARTS_PER_ROW; index++) {
                float x = plate.right() - CLASSIC_PAD - SPRITE - index * SPRITE_STEP;
                float y = plate.y() + CLASSIC_PAD;
                sprite(graphics, empty, x, y);
                int halves = index * 2 + 1;
                if (halves <= hud.food()) {
                    sprite(graphics, halves < hud.food() ? full : half, x, y);
                } else if (halves <= after) {
                    sprite(graphics, halves < after ? full : half, x, y, SPRITE, pulse);
                }
            }
        }
        if (hud.survival() && hud.airVisible()) {
            Box air = new Box(plate.x(), plate.y() - CLASSIC_GAP - SPRITE - CLASSIC_PAD * 2.0F, plate.width(), SPRITE + CLASSIC_PAD * 2.0F);
            graphics.fill(air, plate(settings));
            int bubbles = Mth.ceil(hud.air() * HEARTS_PER_ROW);
            for (int index = 0; index < HEARTS_PER_ROW; index++) {
                sprite(graphics, index < bubbles ? AIR_FULL : AIR_EMPTY, air.right() - CLASSIC_PAD - SPRITE - index * SPRITE_STEP, air.y() + CLASSIC_PAD);
            }
        }
    }

    private static Box classicPlate(Box screen, boolean right, float contentHeight) {
        float width = CLASSIC_PAD * 2.0F + SPRITE_STEP * (HEARTS_PER_ROW - 1) + SPRITE;
        float height = CLASSIC_PAD * 2.0F + contentHeight;
        float x = right ? screen.right() - MARGIN - width : screen.x() + MARGIN;
        return new Box(x, screen.bottom() - MARGIN - height, width, height);
    }

    private static int healthColor(HudSnapshot hud) {
        if (hud.heart() == Hud.HeartType.POISIONED) {
            return POISON;
        }
        if (hud.heart() == Hud.HeartType.WITHERED) {
            return WITHER;
        }
        if (hud.heart() == Hud.HeartType.FROZEN) {
            return FROZEN;
        }
        return hud.low() ? HEALTH_LOW : HEALTH;
    }

    private static void meter(UiGraphics graphics, Box plate, HudSettings settings, Meter meter, boolean mirrored) {
        graphics.fill(plate, plate(settings));
        if (meter.pulse()) {
            float wave = (Mth.sin((float) (graphics.now() % Motion.HUD_PULSE) / Motion.HUD_PULSE * Mth.TWO_PI) + 1.0F) * HALF;
            graphics.border(plate, 1.0F, ArkColors.withAlpha(ArkColors.ERROR, PULSE_MIN + PULSE_RANGE * wave));
        }
        TextMetrics metrics = graphics.metrics();
        float textWidth = metrics.width(meter.text(), VALUE);
        float extraWidth = meter.extra() != null ? metrics.width(meter.extra(), VALUE) + EXTRA_GAP : 0.0F;
        float valueWidth = Math.max(VALUE_MIN, textWidth + extraWidth);
        float centerY = plate.centerY();
        float textY = centerY - metrics.capHeight(VALUE) * HALF;
        float iconX = mirrored ? plate.right() - PAD_X - ICON : plate.x() + PAD_X;
        sprite(graphics, meter.icon(), iconX, centerY - ICON * HALF, ICON);
        float valueX = mirrored ? plate.x() + PAD_X : plate.right() - PAD_X - valueWidth;
        float textX = mirrored ? valueX : valueX + valueWidth - textWidth - extraWidth;
        if (meter.extra() != null) {
            graphics.text(meter.extra(), textX, textY, VALUE, meter.extraColor());
            textX += extraWidth;
        }
        graphics.text(meter.text(), textX, textY, VALUE, meter.textColor());
        float barX = mirrored ? valueX + valueWidth + ICON_GAP : iconX + ICON + ICON_GAP;
        float barWidth = plate.width() - PAD_X * 2.0F - ICON - valueWidth - ICON_GAP * 2.0F;
        Box bar = new Box(barX, centerY - BAR * HALF, barWidth, BAR);
        segments(graphics, bar, 0.0F, BAR, meter.color(), TRACK, mirrored);
        if (meter.ghost() > meter.value()) {
            segments(graphics, bar, meter.ghost() / meter.max(), BAR, GHOST, ArkColors.TRANSPARENT, mirrored);
        }
        float pulse = pulse(graphics.now());
        if (meter.preview() > meter.value()) {
            segments(graphics, bar, meter.preview() / meter.max(), BAR, ArkColors.multiplyAlpha(meter.color(), pulse), ArkColors.TRANSPARENT, mirrored);
        }
        segments(graphics, bar, meter.value() / meter.max(), BAR, meter.color(), ArkColors.TRANSPARENT, mirrored);
        float previewOverlay = Math.clamp(meter.previewOverlay(), 0.0F, 1.0F);
        if (previewOverlay > meter.overlay()) {
            float width = bar.width() * previewOverlay;
            graphics.fill(mirrored ? bar.right() - width : bar.x(), bar.y() - OVERLAY_LINE, width, OVERLAY_LINE,
                ArkColors.multiplyAlpha(meter.overlayColor(), pulse));
        }
        float overlay = Math.clamp(meter.overlay(), 0.0F, 1.0F);
        if (overlay > 0.0F) {
            float width = bar.width() * overlay;
            graphics.fill(mirrored ? bar.right() - width : bar.x(), bar.y() - OVERLAY_LINE, width, OVERLAY_LINE, meter.overlayColor());
        }
    }

    private static void thin(UiGraphics graphics, Box row, Identifier icon, float fraction, int color, String text, int textColor, boolean mirrored) {
        TextMetrics metrics = graphics.metrics();
        float iconX = mirrored ? row.right() - PAD_X - THIN_ICON : row.x() + PAD_X;
        sprite(graphics, icon, iconX, row.centerY() - THIN_ICON * HALF, THIN_ICON);
        float textWidth = metrics.width(text, VALUE);
        float valueX = mirrored ? row.x() + PAD_X : row.right() - PAD_X - VALUE_MIN;
        float textX = mirrored ? valueX : valueX + VALUE_MIN - textWidth;
        graphics.text(text, textX, row.centerY() - metrics.capHeight(VALUE) * HALF, VALUE, textColor);
        float barX = mirrored ? valueX + VALUE_MIN + ICON_GAP : iconX + THIN_ICON + ICON_GAP;
        float barWidth = row.width() - PAD_X * 2.0F - THIN_ICON - VALUE_MIN - ICON_GAP * 2.0F;
        segments(graphics, new Box(barX, row.centerY() - THIN_BAR * HALF, barWidth, THIN_BAR), fraction, THIN_BAR, color, THIN_TRACK, mirrored);
    }

    private static void segments(UiGraphics graphics, Box bar, float fraction, float height, int color, int track, boolean mirrored) {
        float width = (bar.width() - SEGMENT_GAP * (SEGMENTS - 1)) / SEGMENTS;
        float filled = Math.clamp(fraction, 0.0F, 1.0F) * SEGMENTS;
        for (int index = 0; index < SEGMENTS; index++) {
            float x = mirrored ? bar.right() - width - index * (width + SEGMENT_GAP) : bar.x() + index * (width + SEGMENT_GAP);
            if (track != ArkColors.TRANSPARENT) {
                graphics.fill(x, bar.y(), width, height, track);
            }
            float share = Math.clamp(filled - index, 0.0F, 1.0F);
            if (share > 0.0F) {
                graphics.fill(mirrored ? x + width * (1.0F - share) : x, bar.y(), width * share, height, color);
            }
        }
    }

    private static void sprite(UiGraphics graphics, Identifier sprite, float x, float y) {
        sprite(graphics, sprite, x, y, SPRITE);
    }

    private static void sprite(UiGraphics graphics, Identifier sprite, float x, float y, float size) {
        sprite(graphics, sprite, x, y, size, ARGB.white(1.0F));
    }

    private static void sprite(UiGraphics graphics, Identifier sprite, float x, float y, float size, int color) {
        graphics.vanilla(new Box(x, y, size, size), SPRITE_PIXELS,
            vanilla -> vanilla.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, 0, 0, SPRITE_PIXELS, SPRITE_PIXELS, color));
    }

    private void item(UiGraphics graphics, Box slot, ItemStack stack, @Nullable Player owner, int index, float extraScale) {
        long now = graphics.now();
        HudMotion.SlotMotion motion = this.motion.slot(index);
        motion.update(stack, now);
        if (stack.isEmpty()) {
            return;
        }
        Font font = Minecraft.getInstance().font;
        Box base = new Box(slot.centerX() - ITEM * HALF, slot.centerY() - ITEM * HALF, ITEM, ITEM);
        int seed = index + 1;
        graphics.vanilla(scaled(base, motion.iconScale(now) * extraScale), ITEM_NATIVE, vanilla -> {
            if (owner != null) {
                vanilla.item(owner, stack, 0, 0, seed);
            } else {
                vanilla.fakeItem(stack, 0, 0, seed);
            }
        });
        graphics.vanilla(base, ITEM_NATIVE, vanilla -> vanilla.itemDecorations(font, stack, 0, 0, ""));
        count(graphics, slot, stack.getCount(), motion, now);
    }

    private static void count(UiGraphics graphics, Box slot, int count, HudMotion.SlotMotion motion, long now) {
        float roll = motion.roll(now);
        if (count <= 1 && (roll >= 1.0F || motion.previous() <= 1)) {
            return;
        }
        TextMetrics metrics = graphics.metrics();
        float baseY = slot.bottom() - COUNT_INSET - metrics.capHeight(COUNT);
        graphics.clip(slot);
        if (roll < 1.0F && motion.previous() > 1) {
            String old = String.valueOf(motion.previous());
            graphics.push();
            graphics.fade(1.0F - roll);
            graphics.text(old, slot.right() - COUNT_INSET - metrics.width(old, COUNT), baseY - motion.direction() * roll * ROLL_DISTANCE, COUNT,
                ArkColors.TEXT_PRIMARY);
            graphics.pop();
        }
        if (count > 1) {
            String text = String.valueOf(count);
            int flash = motion.direction() > 0 ? COUNT_GAIN : COUNT_LOSS;
            graphics.push();
            graphics.fade(roll);
            graphics.text(text, slot.right() - COUNT_INSET - metrics.width(text, COUNT), baseY + motion.direction() * (1.0F - roll) * ROLL_DISTANCE, COUNT,
                ArkColors.lerp(roll, flash, ArkColors.TEXT_PRIMARY));
            graphics.pop();
        }
        graphics.endClip();
    }

    public static Box slot(Box screen, int slot) {
        return slotIn(hotbarBox(screen), slot);
    }

    private static Box slotIn(Box bar, float slot) {
        return new Box(bar.x() + HOTBAR_PAD + slot * (SLOT + SLOT_GAP), bar.y() + HOTBAR_PAD, SLOT, SLOT);
    }

    private static Box scaled(Box box, float scale) {
        float width = box.width() * scale;
        float height = box.height() * scale;
        return new Box(box.centerX() - width * HALF, box.centerY() - height * HALF, width, height);
    }

    private record Meter(Identifier icon, float value, float ghost, float preview, float max, int color, String text, int textColor, @Nullable String extra,
        int extraColor, float overlay, float previewOverlay, int overlayColor, boolean pulse) {
    }
}
