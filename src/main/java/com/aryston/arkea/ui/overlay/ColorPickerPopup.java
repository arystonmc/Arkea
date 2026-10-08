package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Presence;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Hsv;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.UiHost;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;

public final class ColorPickerPopup implements Popup {
    public static final List<Integer> PRESETS = List.of(0xFFE066, 0xF0A050, 0xE0705F, 0x9DB4D6, 0x7BC95F, 0xFFFFFF);
    private static final float WIDTH = 240.0F;
    private static final float PADDING = 12.0F;
    private static final float GAP = 10.0F;
    private static final float FIELD_HEIGHT = 120.0F;
    private static final float HUE_HEIGHT = 10.0F;
    private static final float HUE_HANDLE_WIDTH = 6.0F;
    private static final float HUE_HANDLE_HEIGHT = 16.0F;
    private static final float MARKER = 10.0F;
    private static final float MARKER_LINE = 2.0F;
    private static final float ROW_HEIGHT = 30.0F;
    private static final float HEX_PADDING = 10.0F;
    private static final float PRESET_HEIGHT = 16.0F;
    private static final float PRESET_GAP = 4.0F;
    private static final float ANCHOR_GAP = 4.0F;
    private static final float EDGE = 8.0F;
    private static final float SHADOW_BLUR = 30.0F;
    private static final float SHADOW_OFFSET = 12.0F;
    private static final float CARET_WIDTH = 1.0F;
    private static final float CARET_HEIGHT = 14.0F;
    private static final float DROP_SLIDE = 6.0F;
    private static final int OPEN_TIME = 180;
    private static final int CLOSE_TIME = 140;
    private static final int CARET_BLINK = 500;
    private static final int HEX_DIGITS = 6;
    private static final int HEX_RADIX = 16;
    private static final int RGB_MASK = 0xFFFFFF;
    private static final int HUE_SEGMENTS = 6;
    private static final int OPAQUE = 0xFF000000;
    private static final int MARKER_OUTLINE = ArkColors.rgba(0, 0, 0, 0.5F);
    private static final int SWATCH_BORDER = ArkColors.rgba(255, 255, 255, 0.20F);
    private static final TextStyle HEX = TextStyle.of(12.0F);

    private final IntConsumer setter;
    private final Presence presence = new Presence(OPEN_TIME, CLOSE_TIME, Easing.STANDARD, Easing.EXIT);
    private final Box box;
    private final Box field;
    private final Box hueBar;
    private final Box swatch;
    private final Box hexBox;
    private final Box presets;
    private Hsv color;
    private Drag drag = Drag.NONE;
    private boolean editingHex;
    private String hexText = "";

    public ColorPickerPopup(UiHost host, Box anchor, IntSupplier getter, IntConsumer setter) {
        this.setter = setter;
        this.color = Hsv.fromRgb(getter.getAsInt());
        float inner = WIDTH - PADDING * 2.0F;
        float height = PADDING * 2.0F + FIELD_HEIGHT + GAP + HUE_HEIGHT + GAP + ROW_HEIGHT + GAP + PRESET_HEIGHT;
        float x = Math.clamp(anchor.right() - WIDTH, EDGE, host.uiScale().canvasWidth() - WIDTH - EDGE);
        boolean below = anchor.bottom() + ANCHOR_GAP + height <= host.uiScale().canvasHeight() - EDGE;
        float y = below ? anchor.bottom() + ANCHOR_GAP : Math.max(EDGE, anchor.y() - ANCHOR_GAP - height);
        this.box = new Box(x, y, WIDTH, height);
        float left = x + PADDING;
        float top = y + PADDING;
        this.field = new Box(left, top, inner, FIELD_HEIGHT);
        this.hueBar = new Box(left, this.field.bottom() + GAP, inner, HUE_HEIGHT);
        this.swatch = new Box(left, this.hueBar.bottom() + GAP, ROW_HEIGHT, ROW_HEIGHT);
        this.hexBox = new Box(this.swatch.right() + PRESET_GAP * 1.5F, this.swatch.y(), inner - ROW_HEIGHT - PRESET_GAP * 1.5F, ROW_HEIGHT);
        this.presets = new Box(left, this.swatch.bottom() + GAP, inner, PRESET_HEIGHT);
    }

    public void open(long now) {
        this.presence.show(now);
    }

    @Override
    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        float progress = this.presence.progress(graphics.now());
        if (progress <= 0.0F) {
            return;
        }
        graphics.push();
        graphics.fade(progress);
        graphics.translate(0.0F, -DROP_SLIDE * (1.0F - progress));
        graphics.shadow(this.box, SHADOW_BLUR, SHADOW_OFFSET, ArkColors.SHADOW_MENU);
        graphics.fill(this.box, ArkColors.MENU);
        graphics.border(this.box, 1.0F, ArkColors.BORDER_OVERLAY);
        this.renderField(graphics);
        this.renderHue(graphics);
        this.renderValueRow(graphics);
        this.renderPresets(graphics);
        graphics.pop();
    }

    private void renderField(UiGraphics graphics) {
        Box area = this.field;
        graphics.gradientHorizontal(area.x(), area.y(), area.width(), area.height(), ArkColors.rgb(0xFFFFFF), OPAQUE | Hsv.hueColor(this.color.hue()));
        graphics.gradientVertical(area.x(), area.y(), area.width(), area.height(), ArkColors.TRANSPARENT, OPAQUE);
        float markerX = area.x() + area.width() * this.color.saturation();
        float markerY = area.y() + area.height() * (1.0F - this.color.value());
        Box marker = new Box(markerX - MARKER * 0.5F, markerY - MARKER * 0.5F, MARKER, MARKER);
        graphics.border(marker.expand(1.0F), 1.0F, MARKER_OUTLINE);
        graphics.border(marker, MARKER_LINE, ArkColors.TEXT_PRIMARY);
    }

    private void renderHue(UiGraphics graphics) {
        Box bar = this.hueBar;
        float segment = bar.width() / HUE_SEGMENTS;
        for (int index = 0; index < HUE_SEGMENTS; index++) {
            int from = OPAQUE | Hsv.hueColor(index / (float) HUE_SEGMENTS);
            int to = OPAQUE | Hsv.hueColor((index + 1) / (float) HUE_SEGMENTS);
            graphics.gradientHorizontal(bar.x() + segment * index, bar.y(), segment, bar.height(), from, to);
        }
        float handleX = bar.x() + bar.width() * this.color.hue() - HUE_HANDLE_WIDTH * 0.5F;
        Box handle = new Box(handleX, bar.centerY() - HUE_HANDLE_HEIGHT * 0.5F, HUE_HANDLE_WIDTH, HUE_HANDLE_HEIGHT);
        graphics.border(handle.expand(1.0F), 1.0F, MARKER_OUTLINE);
        graphics.fill(handle, ArkColors.TEXT_PRIMARY);
    }

    private void renderValueRow(UiGraphics graphics) {
        graphics.fill(this.swatch, OPAQUE | this.color.toRgb());
        graphics.border(this.swatch, 1.0F, SWATCH_BORDER);
        graphics.fill(this.hexBox, ArkColors.CONTROL_FILL);
        graphics.border(this.hexBox, 1.0F, this.editingHex ? Theme.accent().base() : ArkColors.BORDER_DEFAULT);
        TextMetrics metrics = graphics.metrics();
        String text = "#" + (this.editingHex ? this.hexText : hex(this.color.toRgb()));
        float textX = this.hexBox.x() + HEX_PADDING;
        float textY = this.hexBox.centerY() - metrics.capHeight(HEX) * 0.5F;
        graphics.text(text, textX, textY, HEX, ArkColors.TEXT_PRIMARY);
        if (this.editingHex && graphics.now() / CARET_BLINK % 2 == 0) {
            float caretX = textX + metrics.width(text, HEX) + 1.0F;
            graphics.fill(caretX, this.hexBox.centerY() - CARET_HEIGHT * 0.5F, CARET_WIDTH, CARET_HEIGHT, ArkColors.TEXT_PRIMARY);
        }
    }

    private void renderPresets(UiGraphics graphics) {
        for (int index = 0; index < PRESETS.size(); index++) {
            graphics.fill(this.presetBox(index), OPAQUE | PRESETS.get(index));
        }
    }

    private Box presetBox(int index) {
        float width = (this.presets.width() - PRESET_GAP * (PRESETS.size() - 1)) / PRESETS.size();
        return new Box(this.presets.x() + index * (width + PRESET_GAP), this.presets.y(), width, this.presets.height());
    }

    @Override
    public boolean contains(float x, float y) {
        return this.box.contains(x, y);
    }

    @Override
    public void mouseClicked(float x, float y) {
        this.editingHex = false;
        if (this.field.expand(MARKER * 0.5F).contains(x, y)) {
            this.drag = Drag.FIELD;
            this.mouseDragged(x, y);
        } else if (this.hueBar.expand(HUE_HANDLE_HEIGHT * 0.5F).contains(x, y)) {
            this.drag = Drag.HUE;
            this.mouseDragged(x, y);
        } else if (this.hexBox.contains(x, y)) {
            this.editingHex = true;
            this.hexText = hex(this.color.toRgb());
        } else {
            this.clickPreset(x, y);
        }
    }

    private void clickPreset(float x, float y) {
        for (int index = 0; index < PRESETS.size(); index++) {
            if (this.presetBox(index).contains(x, y)) {
                this.color = Hsv.fromRgb(PRESETS.get(index));
                this.setter.accept(PRESETS.get(index));
                return;
            }
        }
    }

    @Override
    public void mouseDragged(float x, float y) {
        if (this.drag == Drag.FIELD) {
            float saturation = Math.clamp((x - this.field.x()) / this.field.width(), 0.0F, 1.0F);
            float value = 1.0F - Math.clamp((y - this.field.y()) / this.field.height(), 0.0F, 1.0F);
            this.color = this.color.withSaturationValue(saturation, value);
            this.setter.accept(this.color.toRgb());
        } else if (this.drag == Drag.HUE) {
            this.color = this.color.withHue(Math.clamp((x - this.hueBar.x()) / this.hueBar.width(), 0.0F, 1.0F));
            this.setter.accept(this.color.toRgb());
        }
    }

    @Override
    public void mouseReleased() {
        this.drag = Drag.NONE;
    }

    @Override
    public void mouseScrolled(double amount) {
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!this.editingHex) {
            return false;
        }
        if (event.key() == InputConstants.KEY_BACKSPACE) {
            this.hexText = this.hexText.isEmpty() ? "" : this.hexText.substring(0, this.hexText.length() - 1);
            return true;
        }
        if (event.isConfirmation()) {
            this.applyHex();
            return true;
        }
        if (event.isEscape()) {
            this.editingHex = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (!this.editingHex || this.hexText.length() >= HEX_DIGITS || Character.digit(event.codepoint(), HEX_RADIX) < 0) {
            return this.editingHex;
        }
        this.hexText += Character.toString(event.codepoint()).toUpperCase(Locale.ROOT);
        if (this.hexText.length() == HEX_DIGITS) {
            this.applyHex();
        }
        return true;
    }

    private void applyHex() {
        if (this.hexText.length() == HEX_DIGITS) {
            int rgb = HexFormat.fromHexDigits(this.hexText);
            this.color = Hsv.fromRgb(rgb);
            this.setter.accept(rgb);
        }
        this.editingHex = false;
    }

    public static String hex(int rgb) {
        return HexFormat.of().withUpperCase().toHexDigits(rgb & RGB_MASK).substring(2);
    }

    @Override
    public void close(long now) {
        this.presence.hide(now);
        this.drag = Drag.NONE;
    }

    @Override
    public boolean isOpen() {
        return this.presence.isShown();
    }

    @Override
    public boolean isGone(long now) {
        return this.presence.isGone(now);
    }

    private enum Drag {
        NONE,
        FIELD,
        HUE
    }
}
