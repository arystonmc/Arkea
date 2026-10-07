package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.render.UiGraphics;
import net.minecraft.client.input.KeyEvent;

public interface Popup {
    void render(UiGraphics graphics, float mouseX, float mouseY);

    boolean contains(float x, float y);

    void mouseClicked(float x, float y);

    void mouseScrolled(double amount);

    boolean keyPressed(KeyEvent event);

    void close(long now);

    boolean isOpen();

    boolean isGone(long now);
}
