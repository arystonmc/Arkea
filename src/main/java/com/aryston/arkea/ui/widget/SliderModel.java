package com.aryston.arkea.ui.widget;

import net.minecraft.network.chat.Component;

public interface SliderModel {
    float fraction();

    void drag(float fraction);

    default void release() {
    }

    void step(int direction, boolean large);

    Component label();

    Component labelAt(float fraction);
}
