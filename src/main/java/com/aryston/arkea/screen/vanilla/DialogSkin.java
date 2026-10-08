package com.aryston.arkea.screen.vanilla;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.mixin.AlertScreenAccessor;
import com.aryston.arkea.mixin.BackupConfirmScreenAccessor;
import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.ButtonPainter;
import com.aryston.arkea.ui.widget.ButtonVariant;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractStringWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.FittingMultiLineTextWidget;
import net.minecraft.client.gui.components.PopupScreen;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class DialogSkin extends VanillaSkin {
    private static final float MIN_WIDTH = 520.0F;
    private static final float MAX_WIDTH = 736.0F;
    private static final float PADDING = 24.0F;
    private static final float ICON_BOX = 28.0F;
    private static final float ICON = 14.0F;
    private static final float HEADER_GAP = 12.0F;
    private static final float GAP = 16.0F;
    private static final float PARAGRAPH_GAP = 10.0F;
    private static final float LINE = 16.0F;
    private static final float TITLE_LINE = 22.0F;
    private static final float CHECK_ROW = 24.0F;
    private static final float CHECK_BOX = 16.0F;
    private static final float CHECK_GAP = 10.0F;
    private static final float CHECK_WIDTH = 10.0F;
    private static final float CHECK_HEIGHT = 8.0F;
    private static final float BUTTON_HEIGHT = 32.0F;
    private static final float BUTTON_GAP = 8.0F;
    private static final float BUTTON_MIN = 96.0F;
    private static final float ICON_TINT = 0.15F;
    private static final float ENTER_RISE = 14.0F;
    private static final float ENTER_SCALE = 0.96F;
    private static final float SHADOW_BLUR = 60.0F;
    private static final float SHADOW_OFFSET = 24.0F;
    private static final int CHECK_IDLE = ArkColors.rgba(255, 255, 255, 0.25F);
    private static final TextStyle TITLE = TextStyle.of(16.0F);
    private static final TextStyle BODY = TextStyle.of(11.0F);
    private static final List<Component> PRIMARY = List.of(CommonComponents.GUI_YES, CommonComponents.GUI_PROCEED, CommonComponents.GUI_OK,
        CommonComponents.GUI_CONTINUE, CommonComponents.GUI_DONE, CommonComponents.GUI_OPEN_IN_BROWSER, BackupConfirmScreen.BACKUP_AND_JOIN);

    @Override
    public boolean enabled() {
        return ArkeaConfig.VANILLA_THEME.get();
    }

    @Override
    public boolean handles(Screen screen) {
        boolean family = screen instanceof ConfirmScreen || screen instanceof AlertScreen || screen instanceof PopupScreen || screen instanceof WarningScreen
            || screen instanceof BackupConfirmScreen;
        if (!family) {
            return false;
        }
        for (GuiEventListener child : screen.children()) {
            if (!(child instanceof AbstractButton || child instanceof AbstractStringWidget || child instanceof FittingMultiLineTextWidget)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void draw(Frame frame, Screen screen) {
        UiGraphics graphics = frame.graphics();
        TextMetrics metrics = graphics.metrics();
        List<AbstractButton> buttons = new ArrayList<>();
        List<Checkbox> checks = new ArrayList<>();
        for (GuiEventListener child : screen.children()) {
            if (child instanceof Checkbox checkbox) {
                checks.add(checkbox);
            } else if (child instanceof AbstractButton button && button.visible) {
                buttons.add(button);
            }
        }
        float width = this.width(metrics, buttons);
        float textWidth = width - PADDING * 2.0F;
        List<List<String>> paragraphs = paragraphs(screen).stream().map(text -> metrics.wrap(text, BODY, textWidth)).toList();
        List<String> titleLines = metrics.wrap(screen.getTitle().getString(), TITLE, textWidth - ICON_BOX - HEADER_GAP);
        float headerHeight = Math.max(ICON_BOX, titleLines.size() * TITLE_LINE);
        float height = PADDING + headerHeight;
        for (List<String> lines : paragraphs) {
            height += PARAGRAPH_GAP + lines.size() * LINE;
        }
        height += checks.isEmpty() ? 0.0F : GAP + checks.size() * CHECK_ROW;
        height += GAP + BUTTON_HEIGHT + PADDING;
        Box frameBox = new Box((frame.scale().canvasWidth() - width) * 0.5F, (frame.scale().canvasHeight() - height) * 0.5F, width, height);
        float enter = Easing.STANDARD.apply(Math.clamp(frame.since() / (float) Motion.POPUP_IN, 0.0F, 1.0F));
        float scrim = Math.clamp(frame.since() / (float) Motion.SCRIM_IN, 0.0F, 1.0F);
        graphics.fill(0.0F, 0.0F, frame.scale().canvasWidth(), frame.scale().canvasHeight(), ArkColors.multiplyAlpha(ArkColors.SCRIM_MODAL, scrim));
        graphics.push();
        graphics.fade(enter);
        graphics.translate(0.0F, ENTER_RISE * (1.0F - enter));
        graphics.scaleAround(ENTER_SCALE + (1.0F - ENTER_SCALE) * enter, frameBox.centerX(), frameBox.centerY());
        graphics.shadow(frameBox, SHADOW_BLUR, SHADOW_OFFSET, ArkColors.SHADOW_DIALOG);
        graphics.fill(frameBox, ArkColors.DIALOG);
        graphics.border(frameBox, 1.0F, ArkColors.BORDER_OVERLAY);
        graphics.topHighlight(frameBox, ArkColors.INNER_HIGHLIGHT);
        float y = this.drawHeader(graphics, screen, frameBox, titleLines, headerHeight);
        for (List<String> lines : paragraphs) {
            y += PARAGRAPH_GAP;
            for (String line : lines) {
                graphics.text(line, frameBox.x() + PADDING, y + (LINE - metrics.capHeight(BODY)) * 0.5F, BODY, ArkColors.TEXT_ICON_IDLE);
                y += LINE;
            }
        }
        if (!checks.isEmpty()) {
            y += GAP;
            for (Checkbox checkbox : checks) {
                this.drawCheck(frame, checkbox, new Box(frameBox.x() + PADDING, y, textWidth, CHECK_ROW));
                y += CHECK_ROW;
            }
        }
        this.drawButtons(frame, buttons, frameBox);
        graphics.pop();
    }

    private float width(TextMetrics metrics, List<AbstractButton> buttons) {
        float needed = PADDING * 2.0F - BUTTON_GAP;
        for (AbstractButton button : buttons) {
            needed += buttonWidth(metrics, button) + BUTTON_GAP;
        }
        return Math.clamp(needed, MIN_WIDTH, MAX_WIDTH);
    }

    private static float buttonWidth(TextMetrics metrics, AbstractWidget button) {
        return Math.max(BUTTON_MIN, ButtonPainter.width(metrics, button.getMessage(), null, null));
    }

    private float drawHeader(UiGraphics graphics, Screen screen, Box frameBox, List<String> titleLines, float headerHeight) {
        Box iconBox = new Box(frameBox.x() + PADDING, frameBox.y() + PADDING, ICON_BOX, ICON_BOX);
        int tone = tone(screen);
        graphics.fill(iconBox, ArkColors.withAlpha(tone, ICON_TINT));
        graphics.icon(icon(screen), iconBox.centerX() - ICON * 0.5F, iconBox.centerY() - ICON * 0.5F, ICON, ICON, tone);
        TextMetrics metrics = graphics.metrics();
        float titleX = iconBox.right() + HEADER_GAP;
        float lineY = iconBox.y() + (Math.min(ICON_BOX, TITLE_LINE) - metrics.capHeight(TITLE)) * 0.5F;
        if (titleLines.size() == 1) {
            lineY = iconBox.centerY() - metrics.capHeight(TITLE) * 0.5F;
        }
        for (String line : titleLines) {
            graphics.text(line, titleX, lineY, TITLE, ArkColors.TEXT_PRIMARY);
            lineY += TITLE_LINE;
        }
        return iconBox.y() + headerHeight;
    }

    private static Icon icon(Screen screen) {
        if (screen instanceof ConfirmLinkScreen) {
            return Icons.LINK;
        }
        return screen instanceof WarningScreen || screen instanceof AlertScreen || screen instanceof BackupConfirmScreen ? Icons.WARNING : Icons.INFO;
    }

    private static int tone(Screen screen) {
        return screen instanceof WarningScreen || screen instanceof AlertScreen || screen instanceof BackupConfirmScreen ? ArkColors.WARNING
            : Theme.accent().light();
    }

    private static List<String> paragraphs(Screen screen) {
        String title = screen.getTitle().getString();
        List<String> texts = new ArrayList<>();
        if (screen instanceof AlertScreen alert) {
            texts.add(((AlertScreenAccessor) alert).arkea$message().getString());
        }
        if (screen instanceof BackupConfirmScreen backup) {
            texts.add(((BackupConfirmScreenAccessor) backup).arkea$description().getString());
        }
        for (GuiEventListener child : screen.children()) {
            if (child instanceof AbstractWidget widget && !(child instanceof AbstractButton) && widget.visible) {
                String text = widget.getMessage().getString();
                if (!text.isBlank() && !text.equals(title)) {
                    texts.add(text);
                }
            }
        }
        return texts;
    }

    private void drawCheck(Frame frame, Checkbox checkbox, Box row) {
        UiGraphics graphics = frame.graphics();
        frame.place(checkbox, row);
        boolean highlighted = frame.highlighted(checkbox);
        Box box = new Box(row.x(), row.centerY() - CHECK_BOX * 0.5F, CHECK_BOX, CHECK_BOX);
        if (checkbox.selected()) {
            graphics.fill(box, Theme.accent().base());
            graphics.icon(Icons.CHECK, box.centerX() - CHECK_WIDTH * 0.5F, box.centerY() - CHECK_HEIGHT * 0.5F, CHECK_WIDTH, CHECK_HEIGHT,
                ArkColors.TEXT_PRIMARY);
        } else {
            graphics.fill(box, highlighted ? ArkColors.CONTROL_HOVER : ArkColors.CONTROL_FILL);
            graphics.border(box, 1.0F, highlighted ? Theme.accent().light() : CHECK_IDLE);
        }
        TextMetrics metrics = graphics.metrics();
        graphics.text(checkbox.getMessage().getString(), box.right() + CHECK_GAP, row.centerY() - metrics.capHeight(BODY) * 0.5F, BODY,
            highlighted ? ArkColors.TEXT_PRIMARY : ArkColors.TEXT_SOFT);
        frame.focusRing(checkbox, row);
    }

    private void drawButtons(Frame frame, List<AbstractButton> buttons, Box frameBox) {
        TextMetrics metrics = frame.graphics().metrics();
        float x = frameBox.right() - PADDING;
        float y = frameBox.bottom() - PADDING - BUTTON_HEIGHT;
        for (int index = buttons.size() - 1; index >= 0; index--) {
            AbstractButton button = buttons.get(index);
            float width = buttonWidth(metrics, button);
            x -= width;
            ButtonVariant variant = PRIMARY.contains(button.getMessage()) || index == 0 && buttons.size() > 1 ? ButtonVariant.PRIMARY : ButtonVariant.SUBTLE;
            frame.button(button, new Box(x, y, width, BUTTON_HEIGHT), variant, null, null);
            x -= BUTTON_GAP;
        }
    }
}
