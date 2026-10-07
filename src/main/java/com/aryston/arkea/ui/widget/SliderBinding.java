package com.aryston.arkea.ui.widget;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import net.minecraft.network.chat.Component;

public record SliderBinding(IntSupplier getter, IntConsumer setter, IntFunction<Component> label) {
}
