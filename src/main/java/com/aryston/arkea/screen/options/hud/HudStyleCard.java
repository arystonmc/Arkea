package com.aryston.arkea.screen.options.hud;

import com.aryston.arkea.hud.HudPainter;
import com.aryston.arkea.hud.HudPreview;
import com.aryston.arkea.hud.HudSettings;
import com.aryston.arkea.hud.HudSnapshot;
import com.aryston.arkea.hud.HudStyle;
import com.aryston.arkea.ui.anim.Easing;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Transition;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.UiHost;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;

final class HudStyleCard extends ArkWidget {
    static final float TEXT_AREA = 52.0F;
    private static final float PADDING = 12.0F;
    private static final float TITLE_GAP = 6.0F;
    private static final float LINE_GAP = 4.0F;
    private static final float LIFT = 2.0F;
    private static final float BADGE = 18.0F;
    private static final float BADGE_INSET = 8.0F;
    private static final float CHECK_WIDTH = 9.0F;
    private static final float CHECK_HEIGHT = 7.0F;
    private static final float GLOW_BLUR = 14.0F;
    private static final float GLOW_OFFSET = 4.0F;
    private static final int DESCRIPTION_LINES = 2;
    private static final float HALF = 0.5F;
    private static final float PREVIEW_ASPECT = 2.4F;
    private static final TextStyle TITLE = TextStyle.of(13.0F);
    private static final TextStyle DESCRIPTION = TextStyle.of(10.0F);

    private final HudStyle style;
    private final Supplier<HudSettings> settings;
    private final Consumer<HudStyle> select;
    private final HudPainter painter = new HudPainter();
    private final Transition selection;

    HudStyleCard(UiHost host, HudStyle style, Supplier<HudSettings> settings, Consumer<HudStyle> select) {
        super(host);
        this.style = style;
        this.settings = settings;
        this.select = select;
        this.selection = new Transition(this.selected() ? 1.0F : 0.0F, Motion.CHECK_POP, Easing.EASE);
        this.key("hud:" + style.name().toLowerCase(Locale.ROOT));
    }

    static float height(float width) {
        return width / PREVIEW_ASPECT + TEXT_AREA;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        float hover = this.hoverProgress();
        Box box = this.bounds().offset(0.0F, -LIFT * hover);
        this.selection.setTarget(this.selected() ? 1.0F : 0.0F, graphics.now());
        float selected = this.selection.value(graphics.now());
        if (selected > 0.0F) {
            graphics.shadow(box, GLOW_BLUR, GLOW_OFFSET, ArkColors.multiplyAlpha(Theme.accent().glow(), selected));
        }
        graphics.fill(box, ArkColors.lerp(selected, ArkColors.lerp(hover, ArkColors.ROW, ArkColors.ROW_HOVER), Theme.accent().tint()));
        Box preview = new Box(box.x() + 1.0F, box.y() + 1.0F, box.width() - 2.0F, box.height() - TEXT_AREA - 1.0F);
        HudPreview.draw(graphics, preview, this.settings.get().withStyle(this.style), this.painter, HudSnapshot.sample(this.style.hidesWhenFull()));
        graphics.fill(preview.x(), preview.bottom(), preview.width(), 1.0F, ArkColors.BORDER_DEFAULT);
        int border = ArkColors.lerp(hover, ArkColors.BORDER_SUBTLE, ArkColors.BORDER_OVERLAY);
        graphics.border(box, 1.0F, ArkColors.lerp(selected, border, Theme.accent().base()));
        TextMetrics metrics = graphics.metrics();
        float textWidth = box.width() - PADDING * 2.0F;
        float y = preview.bottom() + PADDING;
        graphics.text(metrics.ellipsize(this.style.title().getString(), TITLE, textWidth), box.x() + PADDING, y, TITLE,
            ArkColors.lerp(selected, ArkColors.TEXT_PRIMARY, Theme.accent().light()));
        y += metrics.capHeight(TITLE) + TITLE_GAP;
        List<String> lines = metrics.wrap(this.style.description().getString(), DESCRIPTION, textWidth);
        for (int index = 0; index < Math.min(DESCRIPTION_LINES, lines.size()); index++) {
            graphics.text(lines.get(index), box.x() + PADDING, y, DESCRIPTION, ArkColors.TEXT_DESCRIPTION);
            y += metrics.capHeight(DESCRIPTION) + LINE_GAP;
        }
        if (selected > 0.0F) {
            Box badge = new Box(box.right() - BADGE_INSET - BADGE, box.y() + BADGE_INSET, BADGE, BADGE);
            graphics.fill(badge, ArkColors.multiplyAlpha(Theme.accent().base(), selected));
            graphics.icon(Icons.CHECK, badge.centerX() - CHECK_WIDTH * HALF, badge.centerY() - CHECK_HEIGHT * HALF, CHECK_WIDTH, CHECK_HEIGHT,
                ArkColors.multiplyAlpha(ArkColors.TEXT_PRIMARY, selected));
        }
    }

    @Override
    protected void onPress() {
        this.select.accept(this.style);
    }

    @Override
    protected Component narrationMessage() {
        return this.style.title();
    }

    private boolean selected() {
        return this.settings.get().style() == this.style;
    }
}
