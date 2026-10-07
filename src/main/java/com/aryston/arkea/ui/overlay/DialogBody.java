package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.widget.ArkWidget;
import java.util.List;

public interface DialogBody {
    float height(float width);

    void layout(Box area);

    void render(UiGraphics graphics, float mouseX, float mouseY);

    List<? extends ArkWidget> widgets();
}
