package com.aryston.arkea.api.config;

import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import net.minecraft.network.chat.Component;

public record ConfigMeter(Component label, ToDoubleFunction<ConfigValues> fraction, Function<ConfigValues, Component> value, Component note) {
}
