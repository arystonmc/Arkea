package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Presence;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.CycleModel;
import com.aryston.arkea.ui.widget.UiHost;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

public final class DropdownMenu implements Popup {
    private static final float MIN_WIDTH = 160.0F;
    private static final float GAP = 4.0F;
    private static final float PADDING = 4.0F;
    private static final float ROW_HEIGHT = 28.0F;
    private static final float ROW_PADDING = 10.0F;
    private static final float CHECK_WIDTH = 8.0F;
    private static final float CHECK_HEIGHT = 6.0F;
    private static final float EDGE = 8.0F;
    private static final float START_SCALE = 0.9F;
    private static final float SHADOW_BLUR = 30.0F;
    private static final float SHADOW_OFFSET = 12.0F;
    private static final int MAX_ROWS = 8;
    private static final int OPEN_TIME = 180;
    private static final int CLOSE_TIME = 140;
    private static final int SELECTED_FILL = ArkColors.rgba(255, 255, 255, 0.06F);
    private static final TextStyle LABEL = TextStyle.of(12.0F);

    private final UiHost host;
    private final CycleModel model;
    private final Presence presence = new Presence(OPEN_TIME, CLOSE_TIME, Easing.STANDARD, Easing.EXIT);
    private final Box box;
    private final boolean above;
    private int highlighted;
    private int firstRow;
    private float lastMouseY = Float.NaN;

    public DropdownMenu(UiHost host, Box anchor, float minWidth, CycleModel model) {
        this.host = host;
        this.model = model;
        int rows = Math.min(MAX_ROWS, Math.max(1, model.size()));
        float width = Math.max(Math.max(MIN_WIDTH, minWidth), anchor.width());
        float height = rows * ROW_HEIGHT + PADDING * 2.0F;
        float x = Math.max(EDGE, anchor.right() - width);
        float canvasHeight = host.uiScale().canvasHeight();
        this.above = anchor.bottom() + GAP + height > canvasHeight - EDGE && anchor.y() - GAP - height >= EDGE;
        float y = this.above ? anchor.y() - GAP - height : anchor.bottom() + GAP;
        this.box = new Box(x, y, width, height);
        this.highlighted = Math.max(0, model.index());
        this.firstRow = Math.clamp(this.highlighted - rows / 2, 0, Math.max(0, model.size() - rows));
    }

    public void open(long now) {
        this.presence.show(now);
    }

    private int visibleRows() {
        return Math.min(MAX_ROWS, this.model.size());
    }

    @Override
    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        float progress = this.presence.progress(graphics.now());
        if (progress <= 0.0F) {
            return;
        }
        if (this.box.contains(mouseX, mouseY) && mouseY != this.lastMouseY) {
            int row = this.rowAt(mouseY);
            if (row >= 0) {
                this.highlighted = row;
            }
        }
        this.lastMouseY = mouseY;
        graphics.push();
        graphics.fade(progress);
        float scaleY = START_SCALE + (1.0F - START_SCALE) * progress;
        Box shown = new Box(this.box.x(), this.above ? this.box.bottom() - this.box.height() * scaleY : this.box.y(), this.box.width(),
            this.box.height() * scaleY);
        graphics.shadow(shown, SHADOW_BLUR, SHADOW_OFFSET, ArkColors.SHADOW_MENU);
        graphics.fill(shown, ArkColors.MENU);
        graphics.border(shown, 1.0F, ArkColors.BORDER_OVERLAY);
        graphics.clip(shown);
        TextMetrics metrics = graphics.metrics();
        int selected = this.model.index();
        for (int offset = 0; offset < this.visibleRows(); offset++) {
            int index = this.firstRow + offset;
            Box row = this.rowBox(offset);
            if (index == selected) {
                graphics.fill(row, SELECTED_FILL);
            }
            if (index == this.highlighted) {
                graphics.fill(row, ArkColors.CONTROL_HOVER);
            }
            boolean bright = index == selected || index == this.highlighted;
            float labelWidth = row.width() - ROW_PADDING * 3.0F - CHECK_WIDTH;
            String label = metrics.ellipsize(this.model.label(index).getString(), LABEL, labelWidth);
            graphics.text(label, row.x() + ROW_PADDING, row.centerY() - metrics.capHeight(LABEL) * 0.5F, LABEL,
                bright ? ArkColors.TEXT_PRIMARY : ArkColors.TEXT_MUTED);
            if (index == selected) {
                graphics.icon(Icons.CHECK, row.right() - ROW_PADDING - CHECK_WIDTH, row.centerY() - CHECK_HEIGHT * 0.5F, CHECK_WIDTH, CHECK_HEIGHT,
                    Theme.accent().light());
            }
        }
        this.renderScrollHint(graphics);
        graphics.endClip();
        graphics.pop();
    }

    private void renderScrollHint(UiGraphics graphics) {
        int size = this.model.size();
        if (size <= MAX_ROWS) {
            return;
        }
        float track = this.box.height() - PADDING * 2.0F;
        float thumb = Math.max(ROW_HEIGHT, track * MAX_ROWS / size);
        float y = this.box.y() + PADDING + (track - thumb) * this.firstRow / (size - MAX_ROWS);
        graphics.fill(this.box.right() - PADDING, y, 2.0F, thumb, ArkColors.SCROLL_THUMB_HOVER);
    }

    private Box rowBox(int offset) {
        return new Box(this.box.x() + PADDING, this.box.y() + PADDING + offset * ROW_HEIGHT, this.box.width() - PADDING * 2.0F, ROW_HEIGHT);
    }

    private int rowAt(float y) {
        int offset = (int) Math.floor((y - this.box.y() - PADDING) / ROW_HEIGHT);
        if (offset < 0 || offset >= this.visibleRows()) {
            return -1;
        }
        return this.firstRow + offset;
    }

    @Override
    public boolean contains(float x, float y) {
        return this.box.contains(x, y);
    }

    @Override
    public void mouseClicked(float x, float y) {
        int row = this.rowAt(y);
        if (row >= 0) {
            this.choose(row);
        }
    }

    @Override
    public void mouseScrolled(double amount) {
        this.firstRow = Math.clamp(this.firstRow - (int) Math.signum(amount), 0, Math.max(0, this.model.size() - MAX_ROWS));
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isUp() || event.isDown()) {
            this.highlighted = Math.clamp(this.highlighted + (event.isUp() ? -1 : 1), 0, this.model.size() - 1);
            if (this.highlighted < this.firstRow) {
                this.firstRow = this.highlighted;
            } else if (this.highlighted >= this.firstRow + MAX_ROWS) {
                this.firstRow = this.highlighted - MAX_ROWS + 1;
            }
            return true;
        }
        if (event.isSelection() || event.isConfirmation()) {
            this.choose(this.highlighted);
            return true;
        }
        return false;
    }

    private void choose(int index) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        if (index != this.model.index()) {
            this.model.select(index);
        }
        this.close(this.host.now());
    }

    @Override
    public void close(long now) {
        this.presence.hide(now);
    }

    @Override
    public boolean isOpen() {
        return this.presence.isShown();
    }

    @Override
    public boolean isGone(long now) {
        return this.presence.isGone(now);
    }
}
