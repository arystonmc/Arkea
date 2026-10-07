package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import org.jspecify.annotations.Nullable;

public class ArkTextField extends ArkWidget {
    public static final float HEIGHT = 30.0F;
    private static final float PADDING = 10.0F;
    private static final float ICON_SIZE = 10.0F;
    private static final float ICON_GAP = 8.0F;
    private static final float CLEAR_SIZE = 8.0F;
    private static final float CLEAR_AREA = 22.0F;
    private static final float CARET_WIDTH = 1.0F;
    private static final float CARET_EXTRA = 2.0F;
    private static final int CARET_BLINK = 530;
    private static final int FOCUS_TIME = 200;
    private static final int FILL = ArkColors.rgba(0, 0, 0, 0.25F);
    private static final TextStyle TEXT = TextStyle.of(11.0F);

    private final Component hint;
    private final TextFieldState state;
    private final Consumer<String> onChange;
    private final Transition focus = new Transition(0.0F, FOCUS_TIME, Easing.EASE);
    private @Nullable Icon icon = Icons.SEARCH;
    private long lastEdit;
    private int firstVisible;

    public ArkTextField(UiHost host, Component hint, TextFieldState state, Consumer<String> onChange) {
        super(host);
        this.hint = hint;
        this.state = state;
        this.onChange = onChange;
    }

    public ArkTextField icon(@Nullable Icon value) {
        this.icon = value;
        return this;
    }

    public String value() {
        return this.state.text();
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        long now = graphics.now();
        this.focus.setTarget(this.isFocused() ? 1.0F : 0.0F, now);
        float focused = this.focus.value(now);
        graphics.fill(box, FILL);
        int border = ArkColors.lerp(this.hoverProgress(), ArkColors.BORDER_DEFAULT, ArkColors.BORDER_OVERLAY);
        graphics.border(box, 1.0F, ArkColors.lerp(focused, border, Theme.accent().base()));
        float x = box.x() + PADDING;
        if (this.icon != null) {
            int iconColor = ArkColors.lerp(focused, ArkColors.TEXT_FAINT, ArkColors.TEXT_SOFT);
            graphics.icon(this.icon, x, box.centerY() - ICON_SIZE * 0.5F, ICON_SIZE, ICON_SIZE, iconColor);
            x += ICON_SIZE + ICON_GAP;
        }
        Box textArea = this.textArea(x);
        TextMetrics metrics = graphics.metrics();
        float textY = box.centerY() - metrics.capHeight(TEXT) * 0.5F;
        if (this.state.isEmpty()) {
            graphics.text(metrics.ellipsize(this.hint.getString(), TEXT, textArea.width()), textArea.x(), textY, TEXT, ArkColors.TEXT_LABEL);
        } else {
            this.renderText(graphics, textArea, textY);
            this.renderClear(graphics, box);
        }
        if (this.isFocused() && (now - this.lastEdit) / CARET_BLINK % 2 == 0) {
            float caretX = textArea.x() + metrics.width(this.visibleText(this.state.cursor()), TEXT);
            float caretHeight = metrics.capHeight(TEXT) + CARET_EXTRA * 2.0F;
            graphics.fill(caretX, box.centerY() - caretHeight * 0.5F, CARET_WIDTH, caretHeight, ArkColors.TEXT_PRIMARY);
        }
        if (this.isFocused()) {
            this.updateInputArea(graphics, textArea.x() + metrics.width(this.visibleText(this.state.cursor()), TEXT));
        }
    }

    private void updateInputArea(UiGraphics graphics, float caretX) {
        Box caret = graphics.canvasBox(new Box(caretX, this.bounds().y(), 1.0F, this.bounds().height()));
        UiScale scale = graphics.scale();
        Minecraft.getInstance().textInputManager().setTextInputArea(Math.round(scale.toGui(caret.x())), Math.round(scale.toGui(caret.y())),
            Math.round(scale.toGui(caret.right())), Math.round(scale.toGui(caret.bottom())));
    }

    private Box textArea(float x) {
        Box box = this.bounds();
        float right = box.right() - (this.state.isEmpty() ? PADDING : CLEAR_AREA);
        return new Box(x, box.y(), Math.max(0.0F, right - x), box.height());
    }

    private void renderText(UiGraphics graphics, Box area, float textY) {
        TextMetrics metrics = graphics.metrics();
        String text = this.state.text();
        int cursor = this.state.cursor();
        this.firstVisible = Math.min(this.firstVisible, cursor);
        while (this.firstVisible < cursor && metrics.width(text.substring(this.firstVisible, cursor), TEXT) > area.width()) {
            this.firstVisible++;
        }
        String shown = text.substring(this.firstVisible);
        if (this.state.isAllSelected()) {
            float width = Math.min(area.width(), metrics.width(shown, TEXT));
            float height = metrics.capHeight(TEXT) + CARET_EXTRA * 2.0F;
            graphics.fill(area.x(), area.centerY() - height * 0.5F, width, height, Theme.accent().glow());
        }
        graphics.clip(area);
        graphics.text(shown, area.x(), textY, TEXT, ArkColors.TEXT_PRIMARY);
        graphics.endClip();
    }

    private String visibleText(int end) {
        int start = Math.min(this.firstVisible, end);
        return this.state.text().substring(start, end);
    }

    private void renderClear(UiGraphics graphics, Box box) {
        boolean hot = this.isHovered() && this.localMouseX() >= box.right() - CLEAR_AREA;
        float x = box.right() - CLEAR_AREA + (CLEAR_AREA - CLEAR_SIZE) * 0.5F;
        graphics.icon(Icons.CLOSE, x, box.centerY() - CLEAR_SIZE * 0.5F, CLEAR_SIZE, CLEAR_SIZE, hot ? ArkColors.TEXT_PRIMARY : ArkColors.TEXT_FAINT);
    }

    @Override
    protected void renderFocusRing(UiGraphics graphics) {
    }

    @Override
    protected CursorType cursorType() {
        return CursorTypes.IBEAM;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!super.mouseClicked(event, doubleClick)) {
            return false;
        }
        Box box = this.bounds();
        float x = this.localX(this.host.uiScale().toDesign(event.x()));
        if (!this.state.isEmpty() && x >= box.right() - CLEAR_AREA) {
            this.change("");
            return true;
        }
        if (doubleClick) {
            this.state.selectAll();
        } else {
            this.state.setCursor(this.indexAt(x - this.textStart()));
        }
        this.lastEdit = this.host.now();
        return true;
    }

    private float textStart() {
        return this.bounds().x() + PADDING + (this.icon != null ? ICON_SIZE + ICON_GAP : 0.0F);
    }

    private int indexAt(float offset) {
        TextMetrics metrics = this.host.metrics();
        String text = this.state.text();
        for (int index = this.firstVisible; index < text.length(); index++) {
            float middle = metrics.width(text.substring(this.firstVisible, index), TEXT)
                + metrics.width(text.substring(index, index + 1), TEXT) * 0.5F;
            if (offset < middle) {
                return index;
            }
        }
        return text.length();
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (!this.isFocused() || !this.isActive() || !event.isAllowedChatCharacter()) {
            return false;
        }
        this.edit(() -> this.state.insert(event.codepointAsString()));
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!this.isFocused() || !this.isActive()) {
            return false;
        }
        boolean word = event.hasControlDownWithQuirk();
        if (event.isSelectAll()) {
            this.state.selectAll();
        } else if (event.isCopy()) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.state.text());
        } else if (event.isCut()) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.state.text());
            this.change("");
        } else if (event.isPaste()) {
            String pasted = StringUtil.filterText(Minecraft.getInstance().keyboardHandler.getClipboard());
            this.edit(() -> this.state.insert(pasted));
        } else if (event.key() == InputConstants.KEY_BACKSPACE) {
            this.edit(() -> this.state.deleteBackward(word));
        } else if (event.key() == InputConstants.KEY_DELETE) {
            this.edit(() -> this.state.deleteForward(word));
        } else if (event.isLeft() || event.isRight()) {
            this.state.moveCursor(event.isLeft() ? -1 : 1, word);
        } else if (event.key() == InputConstants.KEY_HOME) {
            this.state.setCursor(0);
        } else if (event.key() == InputConstants.KEY_END) {
            this.state.setCursor(this.state.text().length());
        } else if (event.isEscape() && !this.state.isEmpty()) {
            this.change("");
        } else {
            return false;
        }
        this.lastEdit = this.host.now();
        return true;
    }

    private void edit(Runnable action) {
        String before = this.state.text();
        action.run();
        this.lastEdit = this.host.now();
        if (!before.equals(this.state.text())) {
            this.onChange.accept(this.state.text());
        }
    }

    private void change(String value) {
        this.edit(() -> this.state.setText(value));
    }

    @Override
    public void setFocused(boolean focused) {
        boolean changed = focused != this.isFocused();
        super.setFocused(focused);
        if (!focused) {
            this.state.deselect();
        }
        this.lastEdit = this.host.now();
        if (changed) {
            Minecraft.getInstance().onTextInputFocusChange(this, focused);
        }
    }

    @Override
    public void activate() {
    }

    @Override
    protected void onPress() {
    }

    @Override
    protected Component narrationMessage() {
        return this.hint;
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.editBox", this.hint, this.state.text()));
    }
}
