package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.Meter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class SettingRow implements PanelRow {
    public static final float HEIGHT = 52.0F;
    public static final float PADDING_RIGHT = 12.0F;
    private static final float PADDING_LEFT = 14.0F;
    private static final float ICON_BOX = 28.0F;
    private static final float ICON_SIZE = 14.0F;
    private static final float GAP = 12.0F;
    private static final float LINE_GAP = 6.0F;
    private static final float CONTROL_SPACE = 160.0F;
    private static final int ICON_TIME = 200;
    private static final float DISABLED_OPACITY = 0.4F;
    private static final int ICON_FILL = ArkColors.rgba(255, 255, 255, 0.04F);
    private static final int ICON_BORDER = ArkColors.rgba(255, 255, 255, 0.07F);
    private static final float RESET_SIZE = 24.0F;
    private static final float RESET_GAP = 8.0F;
    private static final int COST_LEVELS = 3;
    private static final TextStyle NAME = TextStyle.of(13.0F);
    private static final TextStyle DESCRIPTION = TextStyle.of(10.0F);

    private final Icon icon;
    private final Component name;
    private final Component description;
    private final Transition hover = new Transition(0.0F, Motion.HOVER, Easing.EASE);
    private final Transition lit;
    private BooleanSupplier litWhen = () -> true;
    private Supplier<@Nullable Component> tooltip = () -> null;
    private Box bounds = Box.EMPTY;
    private float controlSpace = CONTROL_SPACE;
    private boolean hovered;
    private @Nullable ArkWidget control;
    private float controlWidth;
    private float controlHeight;
    private List<Tag> tags = List.of();
    private int cost;
    private final List<Accessory> accessories = new ArrayList<>();

    public SettingRow(ItemContent content) {
        this.icon = content.icon();
        this.name = content.label();
        this.description = content.sub() != null ? content.sub() : Component.empty();
        this.lit = new Transition(1.0F, ICON_TIME, Easing.EASE);
    }

    public SettingRow litWhen(BooleanSupplier condition) {
        this.litWhen = condition;
        this.lit.snap(condition.getAsBoolean() ? 1.0F : 0.0F);
        return this;
    }

    public SettingRow tooltip(Supplier<@Nullable Component> text) {
        this.tooltip = text;
        return this;
    }

    public SettingRow control(ArkWidget widget, float width, float height) {
        this.control = widget;
        this.controlWidth = width;
        this.controlHeight = height;
        return this;
    }

    public SettingRow tags(List<Tag> values) {
        this.tags = List.copyOf(values);
        return this;
    }

    public SettingRow cost(int level) {
        this.cost = Math.clamp(level, 0, COST_LEVELS);
        return this;
    }

    public SettingRow resettable(UiHost host, BooleanSupplier isModified, Runnable resetAction) {
        Component label = Component.translatable("arkea.row.reset", this.name);
        ArkIconButton button = new ArkIconButton(host, Icons.UNDO, label, IconButtonStyle.QUIET, resetAction);
        button.setTooltip(Component.translatable("arkea.row.reset.tooltip"));
        return this.accessory(button, isModified);
    }

    public SettingRow accessory(ArkIconButton button, BooleanSupplier visible) {
        this.accessories.add(new Accessory(button, visible));
        return this;
    }

    @Override
    public @Nullable Component tooltip() {
        return this.tooltip.get();
    }

    @Override
    public float height() {
        return HEIGHT;
    }

    @Override
    public void place(Box newBounds) {
        this.bounds = newBounds;
        if (this.control != null) {
            this.control.setBounds(this.controlSlot(this.controlWidth, this.controlHeight));
        }
        this.placeAccessories();
    }

    private void placeAccessories() {
        float x = this.bounds.right() - PADDING_RIGHT - this.controlWidth;
        for (Accessory accessory : this.accessories) {
            if (this.isShown(accessory)) {
                x -= RESET_GAP + RESET_SIZE;
                accessory.button().setBounds(new Box(x, this.bounds.centerY() - RESET_SIZE * 0.5F, RESET_SIZE, RESET_SIZE));
            }
        }
    }

    private boolean isShown(Accessory accessory) {
        return (this.control == null || this.control.isActive()) && accessory.visible().getAsBoolean();
    }

    private int shownAccessories() {
        return (int) this.accessories.stream().filter(this::isShown).count();
    }

    @Override
    public void hide() {
        PanelRow.super.hide();
        this.hovered = false;
    }

    @Override
    public List<ArkWidget> widgets() {
        List<ArkWidget> widgets = new ArrayList<>(1 + this.accessories.size());
        if (this.control != null) {
            widgets.add(this.control);
        }
        for (Accessory accessory : this.accessories) {
            widgets.add(accessory.button());
        }
        return widgets;
    }

    public Icon icon() {
        return this.icon;
    }

    public Component description() {
        return this.description;
    }

    public Component name() {
        return this.name;
    }

    @Override
    public boolean isHovered() {
        return this.hovered;
    }

    @Override
    public Box bounds() {
        return this.bounds;
    }

    public void setBounds(Box newBounds) {
        this.bounds = newBounds;
    }

    public Box controlSlot(float controlWidth, float controlHeight) {
        this.controlSpace = Math.max(CONTROL_SPACE, controlWidth);
        float x = this.bounds.right() - PADDING_RIGHT - controlWidth;
        return new Box(x, this.bounds.centerY() - controlHeight * 0.5F, controlWidth, controlHeight);
    }

    @Override
    public void render(UiGraphics graphics, float mouseX, float mouseY) {
        float opacity = this.control == null || this.control.isActive() ? 1.0F : DISABLED_OPACITY;
        this.render(graphics, mouseX, mouseY, opacity);
        if (this.control != null) {
            graphics.push();
            graphics.fade(opacity);
            this.control.render(graphics, mouseX, mouseY);
            graphics.pop();
        }
        this.renderAccessories(graphics, mouseX, mouseY);
    }

    private void renderAccessories(UiGraphics graphics, float mouseX, float mouseY) {
        this.placeAccessories();
        for (Accessory accessory : this.accessories) {
            boolean shown = this.isShown(accessory);
            accessory.button().setActive(shown);
            if (shown) {
                accessory.button().render(graphics, mouseX, mouseY);
            } else {
                accessory.button().hide();
            }
        }
    }

    private float extrasWidth(TextMetrics metrics) {
        float width = 0.0F;
        for (Tag tag : this.tags) {
            width += tag.width(metrics) + Tag.GAP;
        }
        return this.cost > 0 ? width + Meter.pipsWidth(COST_LEVELS) + Tag.GAP : width;
    }

    private void renderExtras(UiGraphics graphics, float x, float centerY) {
        float position = x;
        for (Tag tag : this.tags) {
            position = tag.draw(graphics, position, centerY) + Tag.GAP;
        }
        if (this.cost > 0) {
            int color = this.cost >= COST_LEVELS ? ArkColors.WARNING : Theme.accent().light();
            Meter.pips(graphics, position, centerY, this.cost, COST_LEVELS, color);
        }
    }

    private void render(UiGraphics graphics, float mouseX, float mouseY, float contentOpacity) {
        Box box = this.bounds;
        long now = graphics.now();
        this.hovered = graphics.visible(graphics.canvasBox(box)).contains(mouseX, mouseY);
        this.hover.setTarget(this.hovered ? 1.0F : 0.0F, now);
        this.lit.setTarget(this.litWhen.getAsBoolean() ? 1.0F : 0.0F, now);
        float hover = this.hover.value(now);
        graphics.fill(box, ArkColors.lerp(hover, ArkColors.ROW, ArkColors.ROW_HOVER));
        graphics.border(box, 1.0F, ArkColors.lerp(hover, ArkColors.BORDER_SUBTLE, ArkColors.BORDER_OVERLAY));
        graphics.push();
        graphics.fade(contentOpacity);
        Box iconBox = new Box(box.x() + PADDING_LEFT, box.centerY() - ICON_BOX * 0.5F, ICON_BOX, ICON_BOX);
        graphics.fill(iconBox, ICON_FILL);
        graphics.border(iconBox, 1.0F, ICON_BORDER);
        float iconOffset = (ICON_BOX - ICON_SIZE) * 0.5F;
        int iconColor = ArkColors.lerp(this.lit.value(now), ArkColors.TEXT_LABEL, Theme.accent().light());
        graphics.icon(this.icon, iconBox.x() + iconOffset, iconBox.y() + iconOffset, ICON_SIZE, ICON_SIZE, iconColor);
        TextMetrics metrics = graphics.metrics();
        float textX = iconBox.right() + GAP;
        float resetSpace = this.shownAccessories() * (RESET_SIZE + RESET_GAP);
        float textWidth = box.right() - PADDING_RIGHT - this.controlSpace - resetSpace - GAP - textX;
        float blockHeight = metrics.capHeight(NAME) + LINE_GAP + metrics.capHeight(DESCRIPTION);
        float nameY = box.centerY() - blockHeight * 0.5F;
        float extras = this.extrasWidth(metrics);
        boolean extrasFit = metrics.width(this.name.getString(), NAME) + extras <= textWidth;
        String name = metrics.ellipsize(this.name.getString(), NAME, textWidth);
        graphics.text(name, textX, nameY, NAME, ArkColors.TEXT_PRIMARY);
        if (extrasFit) {
            this.renderExtras(graphics, textX + metrics.width(name, NAME) + Tag.GAP, nameY + metrics.capHeight(NAME) * 0.5F);
        }
        float descriptionY = nameY + metrics.capHeight(NAME) + LINE_GAP;
        String description = metrics.ellipsize(this.description.getString(), DESCRIPTION, textWidth);
        graphics.text(description, textX, descriptionY, DESCRIPTION, ArkColors.TEXT_DESCRIPTION);
        graphics.pop();
    }

    private record Accessory(ArkIconButton button, BooleanSupplier visible) {
    }
}
