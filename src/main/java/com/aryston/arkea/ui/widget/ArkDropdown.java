package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.overlay.DropdownMenu;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class ArkDropdown extends ArkWidget {
    public static final float WIDTH = 160.0F;
    public static final float HEIGHT = 30.0F;
    private static final float PADDING = 10.0F;
    private static final float CHEVRON_WIDTH = 8.0F;
    private static final float CHEVRON_HEIGHT = 5.0F;
    private static final float GAP = 8.0F;
    private static final TextStyle VALUE = TextStyle.of(12.0F);

    private final Component name;
    private final CycleModel model;
    private @Nullable DropdownMenu menu;

    public ArkDropdown(UiHost host, Component name, CycleModel model) {
        super(host);
        this.name = name;
        this.model = model;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        boolean open = this.menu != null && this.menu.isOpen();
        float hover = this.hoverProgress();
        graphics.fill(box, ArkColors.lerp(hover, ArkColors.CONTROL_FILL, ArkColors.CONTROL_HOVER));
        graphics.border(box, 1.0F, open ? Theme.accent().base() : ArkColors.lerp(hover, ArkColors.BORDER_DEFAULT, ArkColors.BORDER_OVERLAY));
        TextMetrics metrics = graphics.metrics();
        float available = box.width() - PADDING * 2.0F - CHEVRON_WIDTH - GAP;
        String text = metrics.ellipsize(this.model.label(this.model.index()).getString(), VALUE, available);
        graphics.text(text, box.x() + PADDING, box.centerY() - metrics.capHeight(VALUE) * 0.5F, VALUE, ArkColors.TEXT_PRIMARY);
        float chevronX = box.right() - PADDING - CHEVRON_WIDTH;
        int chevron = ArkColors.lerp(hover, ArkColors.TEXT_MUTED, ArkColors.TEXT_PRIMARY);
        graphics.icon(Icons.CHEVRON_DOWN, chevronX, box.centerY() - CHEVRON_HEIGHT * 0.5F, CHEVRON_WIDTH, CHEVRON_HEIGHT, chevron);
    }

    @Override
    protected void onPress() {
        this.menu = new DropdownMenu(this.host, this.canvasBox(), this.bounds().width(), this.model);
        this.menu.open(this.host.now());
        this.host.openPopup(this.menu);
    }

    @Override
    protected Component narrationMessage() {
        return CommonComponents.optionNameValue(this.name, this.model.label(this.model.index()));
    }
}
