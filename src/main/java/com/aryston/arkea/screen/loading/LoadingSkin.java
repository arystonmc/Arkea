package com.aryston.arkea.screen.loading;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.PixelSpinner;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.ButtonPainter;
import com.aryston.arkea.ui.widget.ButtonVariant;
import com.aryston.arkea.ui.widget.Tag;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public final class LoadingSkin {
    private static final int TIP_COUNT = 13;
    private static final int ENTER_MS = 300;
    private static final long PULSE_MS = 900L;
    private static final float ENTER_RISE = 12.0F;
    private static final float ICON_AREA = 40.0F;
    private static final float SPINNER_SCALE = 32.0F / PixelSpinner.SIZE;
    private static final float SECTION_GAP = 28.0F;
    private static final float TITLE_GAP = 12.0F;
    private static final float DETAIL_GAP = 14.0F;
    private static final float DETAIL_WIDTH = 520.0F;
    private static final float DETAIL_LINE = 16.0F;
    private static final float COLUMN_WIDTH = 440.0F;
    private static final float BLOCK_GAP = 14.0F;
    private static final float BAR_HEIGHT = 8.0F;
    private static final float STEP_ICON = 12.0F;
    private static final float STEP_GAP = 8.0F;
    private static final float STEP_TEXT_GAP = 10.0F;
    private static final float PULSE_DOT = 6.0F;
    private static final float BUTTON_HEIGHT = 34.0F;
    private static final float BUTTON_GAP = 8.0F;
    private static final float BUTTON_MIN_WIDTH = 120.0F;
    private static final float TIP_BOTTOM = 40.0F;
    private static final float TIP_WIDTH = 520.0F;
    private static final float TIP_PADDING_X = 16.0F;
    private static final float TIP_PADDING_Y = 14.0F;
    private static final float TIP_GAP = 14.0F;
    private static final float TIP_LINE = 16.0F;
    private static final float TITLE_SHADOW = 3.0F;
    private static final float ERROR_ICON = 18.0F;
    private static final float ERROR_FILL = 0.15F;
    private static final float ERROR_BORDER = 0.5F;
    private static final float LABEL_SHARE = 0.8F;
    private static final float BAR_GLOW = 12.0F;
    private static final float MIN_PULSE = 0.35F;
    private static final double HALF = 0.5;
    private static final int PERCENT = 100;
    private static final int DIM = ArkColors.rgba(8, 8, 9, 0.55F);
    private static final int TRACK = ArkColors.rgba(255, 255, 255, 0.10F);
    private static final int WAITING_BORDER = ArkColors.rgba(255, 255, 255, 0.20F);
    private static final int TIP_FILL = ArkColors.rgba(16, 16, 18, 0.7F);
    private static final int TITLE_SHADOW_COLOR = ArkColors.rgba(0, 0, 0, 0.45F);
    private static final TextStyle KICKER = TextStyle.of(10.0F).spacing(2.0F);
    private static final TextStyle TITLE = TextStyle.of(26.0F).shadow(TITLE_SHADOW_COLOR, TITLE_SHADOW);
    private static final TextStyle SMALL = TextStyle.of(11.0F);

    private @Nullable Screen current;
    private long shownAt;
    private int tip;

    public static boolean handles(Screen screen) {
        return LoadingViews.supports(screen);
    }

    public void render(Screen screen, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        Optional<LoadingView> found = LoadingViews.of(screen);
        if (found.isEmpty()) {
            return;
        }
        if (screen != this.current) {
            this.current = screen;
            this.shownAt = Util.getMillis();
            this.tip = ThreadLocalRandom.current().nextInt(TIP_COUNT);
        }
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        UiScale scale = UiScale.compute(window.getWidth(), window.getHeight(), window.getGuiScale());
        screen.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.nextStratum();
        UiGraphics ui = new UiGraphics(graphics, scale, minecraft.font, Util.getMillis());
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale.poseScale());
        ui.fill(0.0F, 0.0F, scale.canvasWidth(), scale.canvasHeight(), DIM);
        float enter = Easing.STANDARD.apply(Math.clamp((ui.now() - this.shownAt) / (float) ENTER_MS, 0.0F, 1.0F));
        ui.push();
        ui.fade(enter);
        ui.translate(0.0F, ENTER_RISE * (1.0F - enter));
        this.draw(ui, found.get(), buttons(screen), minecraft.getLastInputType().isKeyboard());
        ui.pop();
        graphics.pose().popMatrix();
    }

    private static List<Button> buttons(Screen screen) {
        List<Button> buttons = new ArrayList<>();
        for (var child : screen.children()) {
            if (child instanceof Button button && button.visible) {
                buttons.add(button);
            }
        }
        return buttons;
    }

    private void draw(UiGraphics graphics, LoadingView view, List<Button> buttons, boolean keyboard) {
        TextMetrics metrics = graphics.metrics();
        UiScale scale = graphics.scale();
        List<String> detail = view.detail() == null ? List.of() : metrics.wrap(view.detail().getString(), SMALL, DETAIL_WIDTH);
        boolean block = view.hasProgressBar() || !view.steps().isEmpty() || view.stepLabel() != null;
        float height = ICON_AREA + SECTION_GAP + metrics.capHeight(KICKER) + TITLE_GAP + metrics.capHeight(TITLE);
        if (!detail.isEmpty()) {
            height += DETAIL_GAP + detail.size() * DETAIL_LINE;
        }
        if (block) {
            height += SECTION_GAP + this.blockHeight(metrics, view);
        }
        if (!buttons.isEmpty()) {
            height += SECTION_GAP + BUTTON_HEIGHT;
        }
        float centerX = scale.canvasWidth() * 0.5F;
        float y = (scale.canvasHeight() - height) * 0.5F;
        this.drawIcon(graphics, view.mood(), centerX, y);
        y += ICON_AREA + SECTION_GAP;
        String kicker = view.kicker().getString().toUpperCase(Minecraft.getInstance().getLanguageManager().getJavaLocale());
        graphics.text(kicker, centerX - metrics.width(kicker, KICKER) * 0.5F, y, KICKER, ArkColors.TEXT_MUTED);
        y += metrics.capHeight(KICKER) + TITLE_GAP;
        String title = metrics.ellipsize(view.title().getString(), TITLE, DETAIL_WIDTH);
        graphics.text(title, centerX - metrics.width(title, TITLE) * 0.5F, y, TITLE, ArkColors.TEXT_PRIMARY);
        y += metrics.capHeight(TITLE);
        if (!detail.isEmpty()) {
            y += DETAIL_GAP;
            for (String line : detail) {
                graphics.text(line, centerX - metrics.width(line, SMALL) * 0.5F, y + (DETAIL_LINE - metrics.capHeight(SMALL)) * 0.5F, SMALL, ArkColors.TEXT_SOFT);
                y += DETAIL_LINE;
            }
        }
        if (block) {
            y += SECTION_GAP;
            y = this.drawBlock(graphics, view, centerX - COLUMN_WIDTH * 0.5F, y);
        }
        if (!buttons.isEmpty()) {
            y += SECTION_GAP;
            this.drawButtons(graphics, view.mood(), buttons, centerX, y, keyboard);
        }
        this.drawTip(graphics, centerX);
    }

    private float blockHeight(TextMetrics metrics, LoadingView view) {
        float height = metrics.capHeight(SMALL);
        if (view.hasProgressBar()) {
            height += BLOCK_GAP + BAR_HEIGHT;
        }
        if (!view.steps().isEmpty()) {
            height += BLOCK_GAP + view.steps().size() * STEP_ICON + (view.steps().size() - 1) * STEP_GAP;
        }
        return height;
    }

    private void drawIcon(UiGraphics graphics, LoadingView.Mood mood, float centerX, float y) {
        Box tile = new Box(centerX - ICON_AREA * 0.5F, y, ICON_AREA, ICON_AREA);
        switch (mood) {
            case BUSY -> {
                graphics.push();
                graphics.scaleAround(SPINNER_SCALE, tile.centerX(), tile.centerY());
                PixelSpinner.render(graphics, tile.centerX() - PixelSpinner.SIZE * 0.5F, tile.centerY() - PixelSpinner.SIZE * 0.5F, Theme.accent().light());
                graphics.pop();
            }
            case ERROR -> {
                graphics.fill(tile, ArkColors.withAlpha(ArkColors.ERROR, ERROR_FILL));
                graphics.border(tile, 1.0F, ArkColors.withAlpha(ArkColors.ERROR, ERROR_BORDER));
                graphics.icon(Icons.WARNING, tile.centerX() - ERROR_ICON * 0.5F, tile.centerY() - ERROR_ICON * 0.5F, ERROR_ICON, ERROR_ICON, ArkColors.ERROR);
            }
        }
    }

    private float drawBlock(UiGraphics graphics, LoadingView view, float x, float y) {
        TextMetrics metrics = graphics.metrics();
        Component label = view.stepLabel();
        if (label != null) {
            graphics.text(metrics.ellipsize(label.getString(), SMALL, COLUMN_WIDTH * LABEL_SHARE), x, y, SMALL, ArkColors.TEXT_SOFT);
        }
        if (view.hasProgressBar()) {
            String percent = Math.round(Math.clamp(view.progress(), 0.0F, 1.0F) * PERCENT) + "%";
            graphics.text(percent, x + COLUMN_WIDTH - metrics.width(percent, SMALL), y, SMALL, Theme.accent().light());
        }
        y += metrics.capHeight(SMALL);
        if (view.hasProgressBar()) {
            y += BLOCK_GAP;
            graphics.fill(x, y, COLUMN_WIDTH, BAR_HEIGHT, TRACK);
            float width = COLUMN_WIDTH * Math.clamp(view.progress(), 0.0F, 1.0F);
            if (width > 0.0F) {
                Box bar = new Box(x, y, width, BAR_HEIGHT);
                graphics.shadow(bar, BAR_GLOW, 0.0F, Theme.accent().glow());
                graphics.fill(bar, Theme.accent().base());
            }
            y += BAR_HEIGHT;
        }
        if (!view.steps().isEmpty()) {
            y += BLOCK_GAP;
            for (LoadingView.Step step : view.steps()) {
                this.drawStep(graphics, step, x, y);
                y += STEP_ICON + STEP_GAP;
            }
            y -= STEP_GAP;
        }
        return y;
    }

    private void drawStep(UiGraphics graphics, LoadingView.Step step, float x, float y) {
        Box icon = new Box(x, y, STEP_ICON, STEP_ICON);
        int color = switch (step.state()) {
            case DONE -> {
                graphics.icon(Icons.TICK, icon.x(), icon.y(), STEP_ICON, STEP_ICON, Theme.accent().light());
                yield ArkColors.TEXT_MUTED;
            }
            case ACTIVE -> {
                float pulse = (float) (HALF + HALF * Math.cos(graphics.now() % PULSE_MS / (double) PULSE_MS * Math.TAU));
                graphics.fill(icon.centerX() - PULSE_DOT * 0.5F, icon.centerY() - PULSE_DOT * 0.5F, PULSE_DOT, PULSE_DOT,
                    ArkColors.multiplyAlpha(ArkColors.WARNING, MIN_PULSE + (1.0F - MIN_PULSE) * pulse));
                yield ArkColors.TEXT_PRIMARY;
            }
            case WAITING -> {
                graphics.border(icon, 1.0F, WAITING_BORDER);
                yield ArkColors.TEXT_LABEL;
            }
        };
        TextMetrics metrics = graphics.metrics();
        graphics.text(step.label().getString(), icon.right() + STEP_TEXT_GAP, icon.centerY() - metrics.capHeight(SMALL) * 0.5F, SMALL, color);
    }

    private void drawButtons(UiGraphics graphics, LoadingView.Mood mood, List<Button> buttons, float centerX, float y, boolean keyboard) {
        TextMetrics metrics = graphics.metrics();
        UiScale scale = graphics.scale();
        float[] widths = new float[buttons.size()];
        float total = BUTTON_GAP * (buttons.size() - 1);
        for (int index = 0; index < buttons.size(); index++) {
            widths[index] = Math.max(BUTTON_MIN_WIDTH, ButtonPainter.width(metrics, buttons.get(index).getMessage(), null, null));
            total += widths[index];
        }
        float x = centerX - total * 0.5F;
        for (int index = 0; index < buttons.size(); index++) {
            Button button = buttons.get(index);
            Box box = new Box(x, y, widths[index], BUTTON_HEIGHT);
            button.setX(Math.round(scale.toGui(box.x())));
            button.setY(Math.round(scale.toGui(box.y())));
            button.setWidth(Math.round(scale.toGui(box.width())));
            button.setHeight(Math.round(scale.toGui(box.height())));
            boolean highlighted = button.isHovered() || keyboard && button.isFocused();
            ButtonVariant variant = mood == LoadingView.Mood.BUSY ? ButtonVariant.SUBTLE : index == 0 ? ButtonVariant.PRIMARY : ButtonVariant.SECONDARY;
            ButtonPainter.paint(graphics, box, button.getMessage(), variant, highlighted ? 1.0F : 0.0F, button.active, null, null);
            x += widths[index] + BUTTON_GAP;
        }
    }

    private void drawTip(UiGraphics graphics, float centerX) {
        TextMetrics metrics = graphics.metrics();
        Tag tag = Tag.tone(Component.translatable("arkea.loading.tip"), Theme.accent().light());
        float textWidth = TIP_WIDTH - TIP_PADDING_X * 2.0F - tag.width(metrics) - TIP_GAP;
        List<String> lines = metrics.wrap(Component.translatable("arkea.loading.tip." + this.tip).getString(), SMALL, textWidth);
        float height = TIP_PADDING_Y * 2.0F + Math.max(Tag.HEIGHT, lines.size() * TIP_LINE);
        Box card = new Box(centerX - TIP_WIDTH * 0.5F, graphics.scale().canvasHeight() - TIP_BOTTOM - height, TIP_WIDTH, height);
        graphics.fill(card, TIP_FILL);
        graphics.border(card, 1.0F, ArkColors.BORDER_STRONG);
        float textX = tag.draw(graphics, card.x() + TIP_PADDING_X, card.centerY()) + TIP_GAP;
        float lineY = card.centerY() - lines.size() * TIP_LINE * 0.5F;
        for (String line : lines) {
            graphics.text(line, textX, lineY + (TIP_LINE - metrics.capHeight(SMALL)) * 0.5F, SMALL, ArkColors.TEXT_SOFT);
            lineY += TIP_LINE;
        }
    }
}
