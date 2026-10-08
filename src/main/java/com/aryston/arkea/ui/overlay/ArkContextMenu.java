package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Presence;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.UiHost;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

public final class ArkContextMenu implements Popup {
    private static final float WIDTH = 260.0F;
    private static final float PADDING = 6.0F;
    private static final float ROW_HEIGHT = 32.0F;
    private static final float ROW_GAP = 2.0F;
    private static final float ROW_PADDING = 12.0F;
    private static final float ICON = 14.0F;
    private static final float ICON_GAP = 12.0F;
    private static final float SEPARATOR_MARGIN = 4.0F;
    private static final float SEPARATOR_HEIGHT = 1.0F + SEPARATOR_MARGIN * 2.0F;
    private static final float EDGE = 8.0F;
    private static final float DROP_SLIDE = 6.0F;
    private static final float SHADOW_BLUR = 30.0F;
    private static final float SHADOW_OFFSET = 12.0F;
    private static final int OPEN_TIME = 140;
    private static final int CLOSE_TIME = 120;
    private static final int ROW_HOVER = ArkColors.rgba(255, 255, 255, 0.08F);
    private static final TextStyle LABEL = TextStyle.of(12.0F);
    private static final TextStyle SHORTCUT = TextStyle.of(9.0F);

    private final List<ContextMenuItem> items;
    private final Presence presence = new Presence(OPEN_TIME, CLOSE_TIME, Easing.STANDARD, Easing.EXIT);
    private final Box box;
    private int highlighted = -1;
    private final UiHost host;

    public ArkContextMenu(UiHost host, float cursorX, float cursorY, List<ContextMenuItem> items) {
        this.host = host;
        this.items = items;
        float height = PADDING * 2.0F;
        for (int index = 0; index < items.size(); index++) {
            height += this.itemHeight(index) + (index > 0 ? ROW_GAP : 0.0F);
        }
        float x = Math.clamp(cursorX, EDGE, host.uiScale().canvasWidth() - WIDTH - EDGE);
        float y = Math.clamp(cursorY, EDGE, host.uiScale().canvasHeight() - height - EDGE);
        this.box = new Box(x, y, WIDTH, height);
    }

    public void open(long now) {
        this.presence.show(now);
    }

    private float itemHeight(int index) {
        return this.items.get(index).isSeparator() ? SEPARATOR_HEIGHT : ROW_HEIGHT;
    }

    private Box rowBox(int index) {
        float y = this.box.y() + PADDING;
        for (int before = 0; before < index; before++) {
            y += this.itemHeight(before) + ROW_GAP;
        }
        return new Box(this.box.x() + PADDING, y, this.box.width() - PADDING * 2.0F, this.itemHeight(index));
    }

    private int rowAt(float x, float y) {
        for (int index = 0; index < this.items.size(); index++) {
            if (!this.items.get(index).isSeparator() && this.rowBox(index).contains(x, y)) {
                return index;
            }
        }
        return -1;
    }

    @Override
    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        float progress = this.presence.progress(graphics.now());
        if (progress <= 0.0F) {
            return;
        }
        if (this.box.contains(mouseX, mouseY)) {
            this.highlighted = this.rowAt(mouseX, mouseY);
        }
        graphics.push();
        graphics.fade(progress);
        graphics.translate(0.0F, -DROP_SLIDE * (1.0F - progress));
        graphics.shadow(this.box, SHADOW_BLUR, SHADOW_OFFSET, ArkColors.SHADOW_MENU);
        graphics.fill(this.box, ArkColors.MENU);
        graphics.border(this.box, 1.0F, ArkColors.BORDER_OVERLAY);
        TextMetrics metrics = graphics.metrics();
        for (int index = 0; index < this.items.size(); index++) {
            ContextMenuItem item = this.items.get(index);
            Box row = this.rowBox(index);
            if (item.isSeparator()) {
                graphics.fill(row.x() + PADDING, row.centerY(), row.width() - PADDING * 2.0F, 1.0F, ArkColors.BORDER_DEFAULT);
                continue;
            }
            if (index == this.highlighted && item.enabled()) {
                graphics.fill(row, ROW_HOVER);
            }
            int color = !item.enabled() ? ArkColors.TEXT_LABEL : item.danger() ? ArkColors.DANGER_TEXT
                : index == this.highlighted ? ArkColors.TEXT_PRIMARY : ArkColors.TEXT_SOFT;
            float x = row.x() + ROW_PADDING;
            if (item.icon() != null) {
                graphics.icon(item.icon(), x, row.centerY() - ICON * 0.5F, ICON, ICON, color);
            }
            x += ICON + ICON_GAP;
            float right = row.right() - ROW_PADDING;
            if (item.shortcut() != null) {
                String shortcut = item.shortcut().getString();
                right -= metrics.width(shortcut, SHORTCUT);
                graphics.text(shortcut, right, row.centerY() - metrics.capHeight(SHORTCUT) * 0.5F, SHORTCUT, ArkColors.TEXT_LABEL);
                right -= ROW_PADDING;
            }
            String label = metrics.ellipsize(item.label().getString(), LABEL, right - x);
            graphics.text(label, x, row.centerY() - metrics.capHeight(LABEL) * 0.5F, LABEL, color);
        }
        graphics.pop();
    }

    @Override
    public boolean contains(float x, float y) {
        return this.box.contains(x, y);
    }

    @Override
    public void mouseClicked(float x, float y) {
        int row = this.rowAt(x, y);
        if (row >= 0) {
            this.choose(row);
        }
    }

    @Override
    public void mouseScrolled(double amount) {
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isUp() || event.isDown()) {
            this.moveHighlight(event.isUp() ? -1 : 1);
            return true;
        }
        if ((event.isSelection() || event.isConfirmation()) && this.highlighted >= 0) {
            this.choose(this.highlighted);
            return true;
        }
        return false;
    }

    private void moveHighlight(int direction) {
        int size = this.items.size();
        int index = this.highlighted;
        for (int attempt = 0; attempt < size; attempt++) {
            index = Math.floorMod(index + direction, size);
            ContextMenuItem item = this.items.get(index);
            if (!item.isSeparator() && item.enabled()) {
                this.highlighted = index;
                return;
            }
        }
    }

    private void choose(int index) {
        ContextMenuItem item = this.items.get(index);
        if (!item.enabled() || item.action() == null) {
            return;
        }
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        this.close(this.host.now());
        item.action().run();
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
