package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.overlay.ColorPickerPopup;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class ArkColorButton extends ArkWidget {
    public static final float WIDTH = 140.0F;
    public static final float HEIGHT = 30.0F;
    private static final float PADDING = 6.0F;
    private static final float SWATCH = 18.0F;
    private static final float GAP = 8.0F;
    private static final int OPAQUE = 0xFF000000;
    private static final int SWATCH_BORDER = ArkColors.rgba(255, 255, 255, 0.20F);
    private static final TextStyle VALUE = TextStyle.of(12.0F);

    private final Component name;
    private final IntSupplier getter;
    private final IntConsumer setter;
    private @Nullable ColorPickerPopup picker;

    public ArkColorButton(UiHost host, Component name, IntSupplier getter, IntConsumer setter) {
        super(host);
        this.name = name;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        boolean open = this.picker != null && this.picker.isOpen();
        float hover = this.hoverProgress();
        graphics.fill(box, ArkColors.lerp(hover, ArkColors.CONTROL_FILL, ArkColors.CONTROL_HOVER));
        graphics.border(box, 1.0F, open ? Theme.accent().base() : ArkColors.lerp(hover, ArkColors.BORDER_DEFAULT, ArkColors.BORDER_OVERLAY));
        Box swatch = new Box(box.x() + PADDING, box.centerY() - SWATCH * 0.5F, SWATCH, SWATCH);
        graphics.fill(swatch, OPAQUE | this.getter.getAsInt());
        graphics.border(swatch, 1.0F, SWATCH_BORDER);
        String text = "#" + ColorPickerPopup.hex(this.getter.getAsInt());
        graphics.text(text, swatch.right() + GAP, box.centerY() - graphics.metrics().capHeight(VALUE) * 0.5F, VALUE, ArkColors.TEXT_PRIMARY);
    }

    @Override
    protected void onPress() {
        this.picker = new ColorPickerPopup(this.host, this.canvasBox(), this.getter, this.setter);
        this.picker.open(this.host.now());
        this.host.openPopup(this.picker);
    }

    @Override
    protected Component narrationMessage() {
        return CommonComponents.optionNameValue(this.name, Component.literal("#" + ColorPickerPopup.hex(this.getter.getAsInt())));
    }
}
