package com.aryston.arkea.screen.worlds;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.Tag;
import com.aryston.arkea.ui.widget.UiHost;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelSummary;

final class WorldRow extends ArkWidget {
    static final float HEIGHT = 72.0F;
    private static final float IMAGE_WIDTH = 128.0F;
    private static final float GAP = 16.0F;
    private static final float PADDING_RIGHT = 16.0F;
    private static final float LINE_GAP = 7.0F;
    private static final float BADGE = 32.0F;
    private static final float BADGE_ICON = 10.0F;
    private static final float SELECTED_FILL = 0.12F;
    private static final int BORDER = ArkColors.rgba(255, 255, 255, 0.07F);
    private static final TextStyle NAME = TextStyle.of(14.0F);
    private static final TextStyle DETAILS = TextStyle.of(11.0F);
    private static final TextStyle LAST = TextStyle.of(10.0F);

    private final LevelSummary summary;
    private final WorldImage image;
    private final List<Tag> tags;
    private final Component details;
    private final Component lastPlayed;
    private final BooleanSupplier selected;
    private final Runnable select;
    private final Runnable play;

    WorldRow(UiHost host, LevelSummary summary, WorldImage image, BooleanSupplier selected, Runnable select, Runnable play) {
        super(host);
        this.summary = summary;
        this.image = image;
        this.tags = WorldTags.of(summary);
        this.details = WorldText.details(summary);
        this.lastPlayed = WorldText.lastPlayed(summary);
        this.selected = selected;
        this.select = select;
        this.play = play;
        if (summary.isDisabled() || !summary.primaryActionActive()) {
            this.setTooltip(summary.getInfo());
        }
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        boolean isSelected = this.selected.getAsBoolean();
        int accent = Theme.accent().base();
        int fill = isSelected ? ArkColors.withAlpha(accent, SELECTED_FILL) : ArkColors.lerp(this.hoverProgress(), ArkColors.ROW, ArkColors.ROW_HOVER);
        graphics.fill(box, fill);
        Box picture = new Box(box.x() + 1.0F, box.y() + 1.0F, IMAGE_WIDTH, box.height() - 2.0F);
        this.image.draw(graphics, picture);
        graphics.border(box, 1.0F, isSelected ? accent : BORDER);
        TextMetrics metrics = graphics.metrics();
        float textX = picture.right() + GAP;
        float right = box.right() - PADDING_RIGHT - (isSelected ? BADGE + GAP : 0.0F);
        float block = metrics.capHeight(NAME) + LINE_GAP + metrics.capHeight(DETAILS) + LINE_GAP + metrics.capHeight(LAST);
        float y = box.centerY() - block * 0.5F;
        float tagsWidth = 0.0F;
        for (Tag tag : this.tags) {
            tagsWidth += Tag.GAP + tag.width(metrics);
        }
        String name = metrics.ellipsize(this.summary.getLevelName(), NAME, right - textX - tagsWidth);
        graphics.text(name, textX, y, NAME, this.summary.isDisabled() ? ArkColors.TEXT_DISABLED : ArkColors.TEXT_PRIMARY);
        float tagX = textX + metrics.width(name, NAME);
        for (Tag tag : this.tags) {
            tagX = tag.draw(graphics, tagX + Tag.GAP, y + metrics.capHeight(NAME) * 0.5F);
        }
        y += metrics.capHeight(NAME) + LINE_GAP;
        graphics.text(metrics.ellipsize(this.details.getString(), DETAILS, right - textX), textX, y, DETAILS, ArkColors.TEXT_MUTED);
        y += metrics.capHeight(DETAILS) + LINE_GAP;
        graphics.text(metrics.ellipsize(this.lastPlayed.getString(), LAST, right - textX), textX, y, LAST, ArkColors.TEXT_LABEL);
        if (isSelected) {
            Box badge = this.badge();
            graphics.fill(badge, ArkColors.brighten(accent, 1.0F + 0.15F * this.hoverProgress()));
            graphics.icon(Icons.PLAY, badge.centerX() - BADGE_ICON * 0.5F + 1.0F, badge.centerY() - BADGE_ICON * 0.5F, BADGE_ICON, BADGE_ICON,
                ArkColors.TEXT_PRIMARY);
        }
    }

    private Box badge() {
        Box box = this.bounds();
        return new Box(box.right() - PADDING_RIGHT - BADGE, box.centerY() - BADGE * 0.5F, BADGE, BADGE);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean handled = super.mouseClicked(event, doubleClick);
        if (handled && doubleClick) {
            this.play.run();
        }
        return handled;
    }

    @Override
    protected void onPress() {
        Box badge = this.badge();
        boolean overBadge = this.localMouseX() >= badge.x();
        if (this.selected.getAsBoolean() && (overBadge || this.host.showsKeyboardFocus())) {
            this.play.run();
            return;
        }
        this.select.run();
    }

    @Override
    protected Component narrationMessage() {
        return Component.translatable("narrator.select.world_info", this.summary.getLevelName(), this.lastPlayed, this.details);
    }
}
