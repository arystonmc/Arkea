package com.aryston.arkea.screen.options.keys;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkIconButton;
import com.aryston.arkea.ui.widget.ArkKeycap;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.PanelRow;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

final class KeyBindRow implements PanelRow {
    static final float HEIGHT = 44.0F;
    private static final float PADDING_LEFT = 16.0F;
    private static final float PADDING_RIGHT = 8.0F;
    private static final float GAP = 10.0F;
    private static final float RESET_WIDTH = 28.0F;
    private static final float BADGE_HEIGHT = 12.0F;
    private static final float BADGE_PADDING = 4.0F;
    private static final int CONFLICT = ArkColors.rgb(0xE0705F);
    private static final int CONFLICT_FILL = ArkColors.rgba(224, 112, 95, 0.14F);
    private static final int CONFLICT_BORDER = ArkColors.rgba(224, 112, 95, 0.35F);
    private static final TextStyle NAME = TextStyle.of(12.0F);
    private static final TextStyle BADGE = TextStyle.of(8.0F).spacing(1.0F);

    private final Component name;
    private final ArkKeycap keycap;
    private final ArkIconButton reset;
    private final boolean conflict;
    private final @Nullable Component tooltip;
    private final Transition hover = new Transition(0.0F, Motion.HOVER, Easing.EASE);
    private Box bounds = Box.EMPTY;
    private boolean hovered;

    KeyBindRow(Component name, ArkKeycap keycap, ArkIconButton reset, boolean conflict, @Nullable Component tooltip) {
        this.name = name;
        this.keycap = keycap;
        this.reset = reset;
        this.conflict = conflict;
        this.tooltip = tooltip;
    }

    @Override
    public float height() {
        return HEIGHT;
    }

    @Override
    public void place(Box newBounds) {
        this.bounds = newBounds;
        float resetX = newBounds.right() - PADDING_RIGHT - RESET_WIDTH;
        this.reset.setBounds(new Box(resetX, newBounds.centerY() - ArkKeycap.HEIGHT * 0.5F, RESET_WIDTH, ArkKeycap.HEIGHT));
        float keyX = resetX - GAP - ArkKeycap.WIDTH;
        this.keycap.setBounds(new Box(keyX, newBounds.centerY() - ArkKeycap.HEIGHT * 0.5F, ArkKeycap.WIDTH, ArkKeycap.HEIGHT));
    }

    @Override
    public Box bounds() {
        return this.bounds;
    }

    @Override
    public List<ArkWidget> widgets() {
        return List.of(this.keycap, this.reset);
    }

    @Override
    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        Box box = this.bounds;
        this.hovered = graphics.visible(graphics.canvasBox(box)).contains(mouseX, mouseY);
        this.hover.setTarget(this.hovered ? 1.0F : 0.0F, graphics.now());
        float hover = this.hover.value(graphics.now());
        graphics.fill(box, ArkColors.lerp(hover, ArkColors.ROW, ArkColors.ROW_HOVER));
        int border = this.conflict ? CONFLICT_BORDER : ArkColors.lerp(hover, ArkColors.BORDER_SUBTLE, ArkColors.BORDER_OVERLAY);
        graphics.border(box, 1.0F, border);
        TextMetrics metrics = graphics.metrics();
        float textX = box.x() + PADDING_LEFT;
        float right = this.keycap.bounds().x() - GAP;
        String badge = Component.translatable("arkea.keybinds.conflict.badge").getString();
        float badgeWidth = this.conflict ? metrics.width(badge, BADGE) + BADGE_PADDING * 2.0F : 0.0F;
        float nameWidth = right - textX - (this.conflict ? badgeWidth + GAP : 0.0F);
        String name = metrics.ellipsize(this.name.getString(), NAME, nameWidth);
        graphics.text(name, textX, box.centerY() - metrics.capHeight(NAME) * 0.5F, NAME, ArkColors.TEXT_PRIMARY);
        if (this.conflict) {
            Box badgeBox = new Box(textX + metrics.width(name, NAME) + GAP, box.centerY() - BADGE_HEIGHT * 0.5F, badgeWidth, BADGE_HEIGHT);
            graphics.fill(badgeBox, CONFLICT_FILL);
            graphics.text(badge, badgeBox.x() + BADGE_PADDING, badgeBox.centerY() - metrics.capHeight(BADGE) * 0.5F, BADGE, CONFLICT);
        }
        this.keycap.render(graphics, mouseX, mouseY);
        this.reset.render(graphics, mouseX, mouseY);
    }

    @Override
    public boolean isHovered() {
        return this.hovered;
    }

    @Override
    public @Nullable Component tooltip() {
        return this.tooltip;
    }

    @Override
    public void hide() {
        PanelRow.super.hide();
        this.hovered = false;
    }
}
