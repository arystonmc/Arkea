package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.anim.Oscillation;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class ArkKeycap extends ArkWidget {
    public static final float WIDTH = 140.0F;
    public static final float HEIGHT = 30.0F;
    private static final float PADDING = 8.0F;
    private static final float DOT = 6.0F;
    private static final float DOT_GAP = 8.0F;
    private static final int PULSE = 1400;
    private static final float PULSE_MIN = 0.35F;
    private static final int CONFLICT_BORDER = ArkColors.rgba(224, 112, 95, 0.70F);
    private static final int CONFLICT_TEXT = ArkColors.rgb(0xF0A094);
    private static final TextStyle LABEL = TextStyle.of(12.0F);

    private final Component name;
    private final Supplier<Component> label;
    private final BooleanSupplier listening;
    private final BooleanSupplier conflict;
    private final BooleanSupplier unbound;
    private final Runnable action;

    public ArkKeycap(UiHost host, Component name, Supplier<Component> label, BooleanSupplier listening, BooleanSupplier conflict,
        BooleanSupplier unbound, Runnable action) {
        super(host);
        this.name = name;
        this.label = label;
        this.listening = listening;
        this.conflict = conflict;
        this.unbound = unbound;
        this.action = action;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        boolean listen = this.listening.getAsBoolean();
        boolean clash = !listen && this.conflict.getAsBoolean();
        float hover = this.hoverProgress();
        graphics.fill(box, ArkColors.lerp(hover, ArkColors.CONTROL_FILL, ArkColors.CONTROL_HOVER));
        int border = listen ? Theme.accent().base() : clash ? CONFLICT_BORDER : ArkColors.lerp(hover, ArkColors.BORDER_DEFAULT, ArkColors.BORDER_OVERLAY);
        graphics.border(box, 1.0F, border);
        TextMetrics metrics = graphics.metrics();
        Component text = listen ? Component.translatable("arkea.keybinds.listening") : this.label.get();
        int color = listen ? Theme.accent().light() : clash ? CONFLICT_TEXT : this.unbound.getAsBoolean() ? ArkColors.TEXT_FAINT : ArkColors.TEXT_PRIMARY;
        float extra = listen ? DOT + DOT_GAP : 0.0F;
        String shown = metrics.ellipsize(text.getString(), LABEL, box.width() - PADDING * 2.0F - extra);
        float width = metrics.width(shown, LABEL) + extra;
        float x = box.centerX() - width * 0.5F;
        if (listen) {
            float pulse = PULSE_MIN + (1.0F - PULSE_MIN) * Oscillation.wave(graphics.now(), PULSE);
            graphics.fill(x, box.centerY() - DOT * 0.5F, DOT, DOT, ArkColors.multiplyAlpha(Theme.accent().light(), pulse));
        }
        graphics.text(shown, x + extra, box.centerY() - metrics.capHeight(LABEL) * 0.5F, LABEL, color);
    }

    @Override
    protected void onPress() {
        this.action.run();
    }

    @Override
    protected Component narrationMessage() {
        return this.name;
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
        Component message = this.unbound.getAsBoolean()
            ? Component.translatable("narrator.controls.unbound", this.name)
            : Component.translatable("narrator.controls.bound", this.name, this.label.get());
        output.add(NarratedElementType.TITLE, message);
        if (this.tooltip() != null) {
            output.add(NarratedElementType.HINT, this.tooltip());
        }
    }
}
