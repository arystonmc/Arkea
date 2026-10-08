package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.widget.ArkWidget;
import java.util.List;
import net.minecraft.network.chat.Component;

public interface Overlay {
    void layout(UiScale scale);

    void open(long now);

    void close(long now);

    void cancel();

    boolean isOpen();

    boolean isGone(long now);

    boolean dismissesOnScrimClick();

    Component title();

    Component body();

    List<? extends ArkWidget> widgets();

    boolean contains(float x, float y);

    void render(UiGraphics graphics, float mouseX, float mouseY);
}
