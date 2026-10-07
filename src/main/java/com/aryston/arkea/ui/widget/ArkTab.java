package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class ArkTab extends ArkWidget {
    public static final float HEIGHT = 34.0F;
    private static final float GAP = 8.0F;
    private static final float ICON_SIZE = 12.0F;
    private static final float ICON_GAP = 8.0F;
    private static final float BADGE_MIN = 16.0F;
    private static final float BADGE_HEIGHT = 14.0F;
    private static final float BADGE_PADDING = 4.0F;
    private static final float LINE = 2.0F;
    private static final int LINE_TIME = 200;
    private static final int BADGE_FILL = ArkColors.rgba(255, 255, 255, 0.08F);
    private static final TextStyle LABEL = TextStyle.of(12.0F);
    private static final TextStyle COUNT = TextStyle.of(9.0F);

    private final Component label;
    private final @Nullable IntSupplier count;
    private final BooleanSupplier selected;
    private final Runnable select;
    private final Transition line;
    private @Nullable Icon icon;

    public ArkTab(UiHost host, Component label, @Nullable IntSupplier count, BooleanSupplier selected, Runnable select) {
        super(host);
        this.label = label;
        this.count = count;
        this.selected = selected;
        this.select = select;
        this.line = new Transition(selected.getAsBoolean() ? 1.0F : 0.0F, LINE_TIME, Easing.EASE);
    }

    public ArkTab icon(Icon newIcon) {
        this.icon = newIcon;
        return this;
    }

    public float preferredWidth() {
        TextMetrics metrics = this.host.metrics();
        float width = metrics.width(this.label.getString(), LABEL);
        if (this.icon != null) {
            width += ICON_SIZE + ICON_GAP;
        }
        if (this.count != null) {
            width += GAP + this.badgeWidth(metrics, Integer.toString(this.count.getAsInt()));
        }
        return width;
    }

    private float badgeWidth(TextMetrics metrics, String text) {
        return Math.max(BADGE_MIN, metrics.width(text, COUNT) + BADGE_PADDING * 2.0F);
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        boolean isSelected = this.selected.getAsBoolean();
        this.line.setTarget(isSelected ? 1.0F : 0.0F, graphics.now());
        float amount = this.line.value(graphics.now());
        TextMetrics metrics = graphics.metrics();
        int color = ArkColors.lerp(Math.max(amount, this.hoverProgress()), ArkColors.TEXT_MUTED, ArkColors.TEXT_PRIMARY);
        String text = this.label.getString();
        float textX = box.x();
        if (this.icon != null) {
            graphics.icon(this.icon, textX, box.centerY() - ICON_SIZE * 0.5F, ICON_SIZE, ICON_SIZE, color);
            textX += ICON_SIZE + ICON_GAP;
        }
        graphics.text(text, textX, box.centerY() - metrics.capHeight(LABEL) * 0.5F, LABEL, color);
        if (this.count != null) {
            String value = Integer.toString(this.count.getAsInt());
            float badgeWidth = this.badgeWidth(metrics, value);
            Box badge = new Box(textX + metrics.width(text, LABEL) + GAP, box.centerY() - BADGE_HEIGHT * 0.5F, badgeWidth, BADGE_HEIGHT);
            graphics.fill(badge, BADGE_FILL);
            graphics.text(value, badge.centerX() - metrics.width(value, COUNT) * 0.5F, badge.centerY() - metrics.capHeight(COUNT) * 0.5F, COUNT, color);
        }
        if (amount > 0.0F) {
            graphics.fill(box.x(), box.bottom() - LINE, box.width(), LINE, ArkColors.multiplyAlpha(Theme.accent().base(), amount));
        }
    }

    @Override
    protected void onPress() {
        this.select.run();
    }

    @Override
    protected Component narrationMessage() {
        return this.label;
    }
}
