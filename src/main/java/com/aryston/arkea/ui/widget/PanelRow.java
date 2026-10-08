package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.UiGraphics;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public interface PanelRow {
    float height();

    default float height(float width) {
        return this.height();
    }

    void place(Box bounds);

    Box bounds();

    List<ArkWidget> widgets();

    void render(UiGraphics graphics, float mouseX, float mouseY);

    boolean isHovered();

    default @Nullable Component tooltip() {
        return null;
    }

    default void hide() {
        for (ArkWidget widget : this.widgets()) {
            widget.hide();
        }
    }
}
