package com.aryston.arkea.screen.options.arkea;

import com.aryston.arkea.background.ImportJob;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.UiHost;
import net.minecraft.network.chat.Component;

final class JobTile extends MediaTile {
    private static final float BAR_WIDTH = 0.6F;
    private static final float BAR_HEIGHT = 4.0F;
    private static final float GAP = 12.0F;
    private static final float ICON = 18.0F;
    private static final TextStyle PERCENT = TextStyle.of(16.0F);

    private final ImportJob job;

    JobTile(UiHost host, ImportJob job) {
        super(host);
        this.job = job;
    }

    @Override
    protected void renderThumbnail(UiGraphics graphics, Box thumbnail, float hover) {
        graphics.fill(thumbnail, ArkColors.CONTROL_FILL);
        TextMetrics metrics = graphics.metrics();
        if (this.job.state() == ImportJob.State.FAILED) {
            graphics.icon(Icons.WARNING, thumbnail.centerX() - ICON * 0.5F, thumbnail.centerY() - ICON * 0.5F, ICON, ICON, ArkColors.ERROR);
            return;
        }
        String percent = Math.round(this.job.progress() * 100.0F) + "%";
        float block = metrics.capHeight(PERCENT) + GAP + BAR_HEIGHT;
        float top = thumbnail.centerY() - block * 0.5F;
        graphics.text(percent, thumbnail.centerX() - metrics.width(percent, PERCENT) * 0.5F, top, PERCENT, ArkColors.TEXT_PRIMARY);
        float width = thumbnail.width() * BAR_WIDTH;
        Box bar = new Box(thumbnail.centerX() - width * 0.5F, top + metrics.capHeight(PERCENT) + GAP, width, BAR_HEIGHT);
        graphics.fill(bar, ArkColors.TRACK_EMPTY);
        graphics.fill(bar.x(), bar.y(), bar.width() * this.job.progress(), bar.height(), Theme.accent().base());
    }

    @Override
    protected Component name() {
        return Component.literal(this.job.name());
    }

    @Override
    protected Component sub() {
        return switch (this.job.state()) {
            case WAITING -> Component.translatable("arkea.background.job.waiting");
            case FAILED -> Component.translatable("arkea.background.job.failed", this.job.error());
            default -> Component.translatable("arkea.background.job.converting");
        };
    }

    @Override
    protected int subColor() {
        return this.job.state() == ImportJob.State.FAILED ? ArkColors.ERROR : ArkColors.TEXT_DESCRIPTION;
    }

    @Override
    protected void onPress() {
    }

    @Override
    protected Component narrationMessage() {
        return Component.empty().append(this.name()).append(", ").append(this.sub());
    }
}
