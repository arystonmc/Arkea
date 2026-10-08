package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ArkCompareView extends ArkWidget {
    public static final int MODE_BEFORE = 0;
    public static final int MODE_SPLIT = 1;
    public static final int MODE_AFTER = 2;
    public static final List<Component> MODE_LABELS = List.of(Component.translatable("arkea.compare.before"),
        Component.translatable("arkea.compare.split"), Component.translatable("arkea.compare.after"));
    private static final float HALF = 0.5F;
    private static final float HANDLE_LINE = 2.0F;
    private static final float GRIP = 16.0F;
    private static final float LABEL_INSET = 10.0F;
    private static final float LABEL_PADDING = 6.0F;
    private static final float LABEL_HEIGHT = 16.0F;
    private static final float KEY_STEP = 0.05F;
    private static final int SPLIT_TIME = 450;
    private static final int GRIP_BORDER = ArkColors.rgba(0, 0, 0, 0.5F);
    private static final int LABEL_FILL = ArkColors.rgba(0, 0, 0, 0.55F);
    private static final TextStyle LABEL = TextStyle.of(9.0F).spacing(1.0F);

    private final Component name;
    private final Identifier before;
    private final Identifier after;
    private final float aspect;
    private final Transition split = new Transition(HALF, SPLIT_TIME, Easing.EASE_IN_OUT);
    private float dragSplit = HALF;
    private int mode = MODE_SPLIT;
    private boolean dragging;

    public ArkCompareView(UiHost host, Component name, Identifier before, Identifier after, float aspect) {
        super(host);
        this.name = name;
        this.before = before;
        this.after = after;
        this.aspect = aspect;
    }

    public int mode() {
        return this.mode;
    }

    public void setMode(int newMode) {
        this.mode = newMode;
        float target = switch (newMode) {
            case MODE_BEFORE -> 1.0F;
            case MODE_AFTER -> 0.0F;
            default -> this.dragSplit;
        };
        this.split.setTarget(target, this.host.now());
    }

    @Override
    protected CursorType cursorType() {
        return this.mode == MODE_SPLIT ? CursorTypes.RESIZE_EW : CursorTypes.ARROW;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        float fraction = this.dragging ? this.dragSplit : this.split.value(graphics.now());
        float splitX = box.x() + box.width() * fraction;
        graphics.imageCover(this.after, box, this.aspect, ArkColors.TEXT_PRIMARY);
        if (fraction > 0.0F) {
            graphics.clip(new Box(box.x(), box.y(), splitX - box.x(), box.height()));
            graphics.imageCover(this.before, box, this.aspect, ArkColors.TEXT_PRIMARY);
            graphics.endClip();
        }
        graphics.border(box, 1.0F, ArkColors.BORDER_STRONG);
        if (fraction > 0.0F && fraction < 1.0F) {
            graphics.fill(splitX - HANDLE_LINE * 0.5F, box.y(), HANDLE_LINE, box.height(), ArkColors.TEXT_PRIMARY);
            Box grip = new Box(splitX - GRIP * 0.5F, box.centerY() - GRIP * 0.5F, GRIP, GRIP);
            graphics.fill(grip, ArkColors.TEXT_PRIMARY);
            graphics.border(grip, 1.0F, GRIP_BORDER);
        }
        this.renderLabel(graphics, MODE_LABELS.get(MODE_BEFORE), box.x() + LABEL_INSET, box.y() + LABEL_INSET, fraction > 0.0F);
        TextMetrics metrics = graphics.metrics();
        String afterText = MODE_LABELS.get(MODE_AFTER).getString();
        float afterWidth = metrics.width(afterText, LABEL) + LABEL_PADDING * 2.0F;
        this.renderLabel(graphics, MODE_LABELS.get(MODE_AFTER), box.right() - LABEL_INSET - afterWidth, box.y() + LABEL_INSET, fraction < 1.0F);
    }

    private void renderLabel(UiGraphics graphics, Component text, float x, float y, boolean shown) {
        if (!shown) {
            return;
        }
        TextMetrics metrics = graphics.metrics();
        String label = text.getString().toUpperCase(Locale.ROOT);
        Box box = new Box(x, y, metrics.width(label, LABEL) + LABEL_PADDING * 2.0F, LABEL_HEIGHT);
        graphics.fill(box, LABEL_FILL);
        graphics.text(label, box.x() + LABEL_PADDING, box.centerY() - metrics.capHeight(LABEL) * 0.5F, LABEL, ArkColors.TEXT_PRIMARY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.mode != MODE_SPLIT || !super.mouseClicked(event, doubleClick)) {
            return false;
        }
        this.dragging = true;
        this.dragTo(event.x());
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (!this.dragging || event.button() != InputConstants.MOUSE_BUTTON_LEFT) {
            return false;
        }
        this.dragTo(event.x());
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (!this.dragging) {
            return false;
        }
        this.dragging = false;
        this.split.snap(this.dragSplit);
        return super.mouseReleased(event);
    }

    private void dragTo(double guiX) {
        Box box = this.bounds();
        float local = this.localX(this.host.uiScale().toDesign(guiX));
        this.dragSplit = Math.clamp((local - box.x()) / box.width(), 0.0F, 1.0F);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!this.isActive() || !this.isFocused() || this.mode != MODE_SPLIT || !(event.isLeft() || event.isRight())) {
            return false;
        }
        this.dragSplit = Math.clamp(this.dragSplit + (event.isLeft() ? -KEY_STEP : KEY_STEP), 0.0F, 1.0F);
        this.split.snap(this.dragSplit);
        return true;
    }

    @Override
    public void activate() {
    }

    @Override
    protected void onPress() {
    }

    @Override
    protected Component narrationMessage() {
        return this.name;
    }
}
