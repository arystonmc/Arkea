package com.aryston.arkea.hud;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public final class HudPreview {
    public static final float ASPECT = 16.0F / 9.0F;
    private static final Identifier SCENE = Identifier.fromNamespaceAndPath("arkea", "textures/gui/hud_scene.png");
    private static final Identifier HOTBAR = Identifier.withDefaultNamespace("hud/hotbar");
    private static final Identifier SELECTION = Identifier.withDefaultNamespace("hud/hotbar_selection");
    private static final Identifier OFFHAND = Identifier.withDefaultNamespace("hud/hotbar_offhand_left");
    private static final Identifier XP_BACKGROUND = Identifier.withDefaultNamespace("hud/experience_bar_background");
    private static final Identifier XP_PROGRESS = Identifier.withDefaultNamespace("hud/experience_bar_progress");
    private static final Identifier FOOD_FULL = Identifier.withDefaultNamespace("hud/food_full");
    private static final Identifier FOOD_HALF = Identifier.withDefaultNamespace("hud/food_half");
    private static final Identifier FOOD_EMPTY = Identifier.withDefaultNamespace("hud/food_empty");
    private static final Identifier ARMOR_FULL = Identifier.withDefaultNamespace("hud/armor_full");
    private static final Identifier ARMOR_HALF = Identifier.withDefaultNamespace("hud/armor_half");
    private static final Identifier ARMOR_EMPTY = Identifier.withDefaultNamespace("hud/armor_empty");
    private static final int HOTBAR_WIDTH = 182;
    private static final int HOTBAR_HALF = 91;
    private static final int HOTBAR_HEIGHT = 22;
    private static final int SELECTION_WIDTH = 24;
    private static final int SELECTION_HEIGHT = 23;
    private static final int SLOT_STEP = 20;
    private static final int ITEM_INSET = 3;
    private static final int ITEM_TOP = 19;
    private static final int OFFHAND_WIDTH = 29;
    private static final int OFFHAND_HEIGHT = 24;
    private static final int OFFHAND_ITEM = 26;
    private static final int XP_TOP = 29;
    private static final int XP_HEIGHT = 5;
    private static final int LEVEL_TOP = 35;
    private static final int STATUS_TOP = 39;
    private static final int ARMOR_TOP = 49;
    private static final int ICON = 9;
    private static final int ICON_STEP = 8;
    private static final int ICONS = 10;
    private static final int LEVEL_COLOR = 0xFF80FF20;
    private static final int OUTLINE = 0xFF000000;

    private HudPreview() {
    }

    public static void draw(UiGraphics graphics, Box box, HudSettings settings, HudPainter painter, HudSnapshot hud) {
        graphics.clip(box);
        graphics.imageCover(SCENE, box, ASPECT, ArkColors.TEXT_PRIMARY);
        Minecraft minecraft = Minecraft.getInstance();
        if (settings.style().arkea()) {
            UiScale scale = settings.scale(minecraft.getWindow());
            float width = scale.canvasWidth();
            Box screen = new Box(0.0F, 0.0F, width, width * box.height() / box.width());
            graphics.push();
            graphics.translate(box.x(), box.y());
            graphics.scaleAround(box.width() / width, 0.0F, 0.0F);
            painter.drawStatus(graphics, screen, hud, settings);
            painter.drawExperience(graphics, screen, hud);
            painter.drawHotbar(graphics, screen, hud, settings);
            painter.drawItemName(graphics, screen, hud, settings);
            graphics.pop();
        } else {
            int width = minecraft.getWindow().getGuiScaledWidth();
            int height = (int) (width * box.height() / box.width());
            graphics.vanilla(box, width, vanilla -> vanillaHud(vanilla, minecraft.font, width, height, hud));
        }
        graphics.endClip();
    }

    private static void vanillaHud(GuiGraphicsExtractor graphics, Font font, int width, int height, HudSnapshot hud) {
        int left = width / 2 - HOTBAR_HALF;
        int right = width / 2 + HOTBAR_HALF;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR, left, height - HOTBAR_HEIGHT, HOTBAR_WIDTH, HOTBAR_HEIGHT);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SELECTION, left - 1 + hud.selected() * SLOT_STEP, height - SELECTION_HEIGHT, SELECTION_WIDTH,
            SELECTION_HEIGHT);
        for (int slot = 0; slot < hud.hotbar().size(); slot++) {
            item(graphics, font, hud.hotbar().get(slot), left + ITEM_INSET + slot * SLOT_STEP, height - ITEM_TOP);
        }
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, OFFHAND, left - OFFHAND_WIDTH, height - OFFHAND_HEIGHT, OFFHAND_WIDTH, OFFHAND_HEIGHT);
        item(graphics, font, hud.offhand(), left - OFFHAND_ITEM, height - ITEM_TOP);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, XP_BACKGROUND, left, height - XP_TOP, HOTBAR_WIDTH, XP_HEIGHT);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, XP_PROGRESS, HOTBAR_WIDTH, XP_HEIGHT, 0, 0, left, height - XP_TOP, (int) (hud.progress() * HOTBAR_WIDTH),
            XP_HEIGHT);
        Component level = Component.literal(String.valueOf(hud.level()));
        int levelX = (width - font.width(level)) / 2;
        int levelY = height - LEVEL_TOP;
        graphics.text(font, level, levelX + 1, levelY, OUTLINE, false);
        graphics.text(font, level, levelX - 1, levelY, OUTLINE, false);
        graphics.text(font, level, levelX, levelY + 1, OUTLINE, false);
        graphics.text(font, level, levelX, levelY - 1, OUTLINE, false);
        graphics.text(font, level, levelX, levelY, LEVEL_COLOR, false);
        int health = (int) Math.ceil(hud.health());
        for (int index = 0; index < ICONS; index++) {
            int halves = index * 2 + 1;
            int heartX = left + index * ICON_STEP;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Hud.HeartType.CONTAINER.getSprite(false, false, false), heartX, height - STATUS_TOP, ICON, ICON);
            if (halves <= health) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Hud.HeartType.NORMAL.getSprite(false, halves == health, false), heartX, height - STATUS_TOP,
                    ICON, ICON);
            }
            Identifier armor = halves < hud.armor() ? ARMOR_FULL : halves == hud.armor() ? ARMOR_HALF : ARMOR_EMPTY;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, armor, heartX, height - ARMOR_TOP, ICON, ICON);
            int foodX = right - index * ICON_STEP - ICON;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, FOOD_EMPTY, foodX, height - STATUS_TOP, ICON, ICON);
            if (halves <= hud.food()) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, halves < hud.food() ? FOOD_FULL : FOOD_HALF, foodX, height - STATUS_TOP, ICON, ICON);
            }
        }
    }

    private static void item(GuiGraphicsExtractor graphics, Font font, ItemStack stack, int x, int y) {
        if (stack.isEmpty()) {
            return;
        }
        graphics.fakeItem(stack, x, y);
        graphics.itemDecorations(font, stack, x, y);
    }
}
