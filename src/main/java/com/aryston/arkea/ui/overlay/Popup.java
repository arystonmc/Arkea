package com.aryston.arkea.ui.overlay;

import com.aryston.arkea.ui.render.UiGraphics;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;

public interface Popup {
    void render(UiGraphics graphics, float mouseX, float mouseY);

    boolean contains(float x, float y);

    void mouseClicked(float x, float y);

    default void mouseDragged(float x, float y) {
    }

    default void mouseReleased() {
    }

    void mouseScrolled(double amount);

    boolean keyPressed(KeyEvent event);

    default boolean charTyped(CharacterEvent event) {
        return false;
    }

    void close(long now);

    boolean isOpen();

    boolean isGone(long now);
}
