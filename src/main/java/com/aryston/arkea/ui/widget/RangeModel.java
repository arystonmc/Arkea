package com.aryston.arkea.ui.widget;

import net.minecraft.network.chat.Component;

public interface RangeModel {
    float low();

    float high();

    void setLow(float fraction);

    void setHigh(float fraction);

    void step(boolean highHandle, int direction, boolean large);

    Component label();
}
