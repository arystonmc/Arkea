package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class ArkChoiceTile extends ArkWidget {
    public static final float HEIGHT = 48.0F;
    private static final float PADDING = 14.0F;
    private static final float GAP = 12.0F;
    private static final float LINE_GAP = 6.0F;
    private static final float CHECK_WIDTH = 10.0F;
    private static final float CHECK_HEIGHT = 8.0F;
    private static final float SELECTED_FILL_ALPHA = 0.12F;
    private static final int BORDER = ArkColors.rgba(255, 255, 255, 0.07F);
    private static final TextStyle NAME = TextStyle.of(13.0F);
    private static final TextStyle SUB = TextStyle.of(10.0F);

    private final Component name;
    private final Component sub;
    private final BooleanSupplier selected;
    private final Runnable action;
    private @Nullable Runnable doubleClickAction;
    private boolean doubleClicked;

    public ArkChoiceTile(UiHost host, Component name, Component sub, BooleanSupplier selected, Runnable action) {
        super(host);
        this.name = name;
        this.sub = sub;
        this.selected = selected;
        this.action = action;
    }

    public ArkChoiceTile onDoubleClick(Runnable value) {
        this.doubleClickAction = value;
        return this;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        boolean chosen = this.selected.getAsBoolean();
        float hover = this.hoverProgress();
        int fill = chosen ? ArkColors.withAlpha(Theme.accent().base(), SELECTED_FILL_ALPHA) : ArkColors.lerp(hover, ArkColors.ROW, ArkColors.ROW_HOVER);
        graphics.fill(box, fill);
        graphics.border(box, 1.0F, chosen ? Theme.accent().base() : ArkColors.lerp(hover, BORDER, ArkColors.BORDER_OVERLAY));
        TextMetrics metrics = graphics.metrics();
        float textWidth = box.width() - PADDING * 2.0F - (chosen ? CHECK_WIDTH + GAP : 0.0F);
        float block = metrics.capHeight(NAME) + LINE_GAP + metrics.capHeight(SUB);
        float nameY = box.centerY() - block * 0.5F;
        graphics.text(metrics.ellipsize(this.name.getString(), NAME, textWidth), box.x() + PADDING, nameY, NAME, ArkColors.TEXT_PRIMARY);
        float subY = nameY + metrics.capHeight(NAME) + LINE_GAP;
        graphics.text(metrics.ellipsize(this.sub.getString(), SUB, textWidth), box.x() + PADDING, subY, SUB, ArkColors.TEXT_FAINT);
        if (chosen) {
            graphics.icon(Icons.CHECK, box.right() - PADDING - CHECK_WIDTH, box.centerY() - CHECK_HEIGHT * 0.5F, CHECK_WIDTH, CHECK_HEIGHT,
                Theme.accent().light());
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        this.doubleClicked = doubleClick;
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    protected void onPress() {
        this.action.run();
        if (this.doubleClicked && this.doubleClickAction != null) {
            this.doubleClickAction.run();
        }
        this.doubleClicked = false;
    }

    @Override
    protected Component narrationMessage() {
        return Component.empty().append(this.name).append(" ").append(this.sub);
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
        super.updateNarration(output);
        if (this.selected.getAsBoolean()) {
            output.add(NarratedElementType.HINT, Component.translatable("narrator.select", this.name));
        }
    }
}
