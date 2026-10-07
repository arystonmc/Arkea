package com.aryston.arkea.ui.widget;

import net.minecraft.network.chat.Component;

public interface CycleModel {
    int size();

    int index();

    void select(int index);

    Component label(int index);
}
