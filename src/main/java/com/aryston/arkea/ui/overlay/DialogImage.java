package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.UiGraphics;

@FunctionalInterface
public interface DialogImage {
    void draw(UiGraphics graphics, Box box);
}
