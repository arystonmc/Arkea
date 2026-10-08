package com.aryston.arkea.screen.vanilla;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.ButtonVariant;
import com.mojang.blaze3d.platform.Window;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public final class VanillaTheme {
    private static final float HOVER_BRIGHTNESS = 0.18F;
    private static final float DISABLED_OPACITY = 0.4F;
    private static final float GLOW_BLUR = 12.0F;
    private static final float GLOW_OFFSET = 3.0F;
    private static final float HANDLE_WIDTH = 6.0F;
    private static final float CHECK_WIDTH = 10.0F;
    private static final float CHECK_HEIGHT = 8.0F;
    private static final float CHECK_SHARE = 0.6F;
    private static final float UNDERLINE = 2.0F;
    private static final float SCROLLBAR_WIDTH = 4.0F;
    private static final float CHAT_GLOW = 6.0F;
    private static final float CHAT_BORDER_ALPHA = 0.5F;
    private static final int CHAT_FILL = ArkColors.rgba(8, 8, 9, 0.78F);
    private static final int MENU_DIM = ArkColors.rgba(8, 8, 9, 0.55F);
    private static final int LIST_FILL = ArkColors.rgba(0, 0, 0, 0.22F);
    private static final int SLIDER_FILL = ArkColors.rgba(255, 255, 255, 0.04F);
    private static final int CHECKBOX_IDLE = ArkColors.rgba(255, 255, 255, 0.25F);
    private static final int TAB_HOVER = ArkColors.rgba(255, 255, 255, 0.05F);
    private static final List<Component> PRIMARY_LABELS = List.of(CommonComponents.GUI_DONE, CommonComponents.GUI_PROCEED, CommonComponents.GUI_YES,
        CommonComponents.GUI_OK, CommonComponents.GUI_CONTINUE);
    private static final Map<Object, Transition> HOVERS = new WeakHashMap<>();

    private VanillaTheme() {
    }

    public static boolean active() {
        if (!ArkeaConfig.SPEC.isLoaded() || !ArkeaConfig.VANILLA_THEME.get()) {
            return false;
        }
        Screen screen = Minecraft.getInstance().gui.screen();
        return screen != null && !(screen instanceof AbstractContainerScreen<?>);
    }

    private static void paint(GuiGraphicsExtractor graphics, int x, int y, int width, int height, BiConsumer<UiGraphics, Box> painter) {
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        UiScale scale = UiScale.compute(window.getWidth(), window.getHeight(), window.getGuiScale());
        UiGraphics ui = new UiGraphics(graphics, scale, minecraft.font, Util.getMillis());
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale.poseScale());
        painter.accept(ui, new Box(scale.toDesign(x), scale.toDesign(y), scale.toDesign(width), scale.toDesign(height)));
        graphics.pose().popMatrix();
    }

    private static float hover(Object owner, boolean highlighted) {
        Transition transition = HOVERS.computeIfAbsent(owner, key -> new Transition(highlighted ? 1.0F : 0.0F, Motion.HOVER, Easing.EASE));
        long now = Util.getMillis();
        transition.setTarget(highlighted ? 1.0F : 0.0F, now);
        return transition.value(now);
    }

    public static void button(GuiGraphicsExtractor graphics, Object owner, Component message, int x, int y, int width, int height, boolean active,
        boolean highlighted) {
        ButtonVariant variant = PRIMARY_LABELS.contains(message) ? ButtonVariant.PRIMARY : ButtonVariant.SECONDARY;
        float amount = hover(owner, active && highlighted);
        paint(graphics, x, y, width, height, (ui, box) -> {
            ui.push();
            if (!active) {
                ui.fade(DISABLED_OPACITY);
            }
            ui.shadow(box, GLOW_BLUR, GLOW_OFFSET, variant.glow());
            float brightness = 1.0F + HOVER_BRIGHTNESS * amount;
            ui.fill(box, ArkColors.brighten(ArkColors.lerp(amount, variant.fill(), variant == ButtonVariant.PRIMARY ? variant.fill() : ArkColors.CONTROL_HOVER),
                brightness));
            int border = variant == ButtonVariant.PRIMARY ? variant.border() : ArkColors.lerp(amount, ArkColors.BORDER_STRONG, ArkColors.BORDER_TOOLTIP);
            ui.border(box, 1.0F, ArkColors.brighten(border, brightness));
            ui.topHighlight(box, ArkColors.INNER_HIGHLIGHT);
            ui.pop();
        });
    }

    public static void sliderTrack(GuiGraphicsExtractor graphics, Object owner, int x, int y, int width, int height, double value, boolean active,
        boolean highlighted) {
        float amount = hover(owner, active && highlighted);
        paint(graphics, x, y, width, height, (ui, box) -> {
            ui.push();
            if (!active) {
                ui.fade(DISABLED_OPACITY);
            }
            ui.fill(box, ArkColors.lerp(amount, ArkColors.CONTROL_FILL, ArkColors.CONTROL_HOVER));
            ui.fill(box.x(), box.y(), box.width() * (float) Math.clamp(value, 0.0, 1.0), box.height(), ArkColors.lerp(amount, SLIDER_FILL, Theme.accent().tint()));
            ui.border(box, 1.0F, ArkColors.lerp(amount, ArkColors.BORDER_DEFAULT, ArkColors.BORDER_OVERLAY));
            ui.pop();
        });
    }

    public static void sliderHandle(GuiGraphicsExtractor graphics, int x, int y, int width, int height, boolean active) {
        paint(graphics, x, y, width, height, (ui, box) -> {
            Box handle = new Box(box.centerX() - HANDLE_WIDTH * 0.5F, box.y() + 1.0F, HANDLE_WIDTH, box.height() - 2.0F);
            ui.fill(handle, active ? ArkColors.TEXT_PRIMARY : ArkColors.CONTROL_IDLE);
        });
    }

    public static void textField(GuiGraphicsExtractor graphics, int x, int y, int width, int height, boolean active, boolean focused) {
        paint(graphics, x, y, width, height, (ui, box) -> {
            ui.fill(box, ArkColors.CONTROL_FILL);
            int border = focused ? Theme.accent().border() : ArkColors.BORDER_DEFAULT;
            ui.border(box, 1.0F, active ? border : ArkColors.BORDER_SUBTLE);
        });
    }

    public static void checkbox(GuiGraphicsExtractor graphics, Object owner, int x, int y, int size, boolean selected, boolean highlighted) {
        float amount = hover(owner, highlighted);
        paint(graphics, x, y, size, size, (ui, box) -> {
            if (selected) {
                ui.fill(box, Theme.accent().base());
                float checkWidth = box.width() * CHECK_SHARE;
                float checkHeight = checkWidth * CHECK_HEIGHT / CHECK_WIDTH;
                ui.icon(Icons.CHECK, box.centerX() - checkWidth * 0.5F, box.centerY() - checkHeight * 0.5F, checkWidth, checkHeight, ArkColors.TEXT_PRIMARY);
            } else {
                ui.fill(box, ArkColors.lerp(amount, ArkColors.CONTROL_FILL, ArkColors.CONTROL_HOVER));
            }
            ui.border(box, 1.0F, ArkColors.lerp(amount, selected ? Theme.accent().border() : CHECKBOX_IDLE, Theme.accent().light()));
        });
    }

    public static void menuBackground(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, MENU_DIM);
    }

    public static void listBackground(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, LIST_FILL);
    }

    public static void separator(GuiGraphicsExtractor graphics, int x, int y, int width, int height, boolean header) {
        paint(graphics, x, y, width, height, (ui, box) -> {
            float line = ui.scale().snapThickness(1.0F);
            ui.fill(box.x(), header ? box.bottom() - line : box.y(), box.width(), line, ArkColors.BORDER_DEFAULT);
        });
    }

    public static void scrollbar(GuiGraphicsExtractor graphics, Object owner, int x, int y, int width, int height, int thumbY, int thumbHeight,
        boolean highlighted) {
        float amount = hover(owner, highlighted);
        paint(graphics, x, y, width, height, (ui, box) -> {
            UiScale scale = ui.scale();
            float barX = box.centerX() - SCROLLBAR_WIDTH * 0.5F;
            ui.fill(barX, box.y(), SCROLLBAR_WIDTH, box.height(), ArkColors.ROW);
            Box thumb = new Box(barX, scale.toDesign(thumbY), SCROLLBAR_WIDTH, scale.toDesign(thumbHeight));
            ui.fill(thumb, ArkColors.lerp(amount, ArkColors.SCROLL_THUMB, ArkColors.SCROLL_THUMB_HOVER));
        });
    }

    public static void tab(GuiGraphicsExtractor graphics, Object owner, int x, int y, int width, int height, boolean highlighted) {
        float amount = hover(owner, highlighted);
        paint(graphics, x, y, width, height, (ui, box) -> ui.fill(box, ArkColors.multiplyAlpha(TAB_HOVER, amount)));
    }

    public static void tabUnderline(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        paint(graphics, x, y, width, height, (ui, box) -> ui.fill(box.x(), box.bottom() - UNDERLINE, box.width(), UNDERLINE, Theme.accent().light()));
    }

    public static void chatBar(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        paint(graphics, x, y, width, height, (ui, box) -> {
            ui.shadow(box, CHAT_GLOW, 0.0F, Theme.accent().glow());
            ui.fill(box, CHAT_FILL);
            ui.border(box, 1.0F, ArkColors.withAlpha(Theme.accent().light(), CHAT_BORDER_ALPHA));
        });
    }
}
