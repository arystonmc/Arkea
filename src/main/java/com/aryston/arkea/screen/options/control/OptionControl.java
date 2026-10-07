package com.aryston.arkea.screen.options.control;

import com.aryston.arkea.ui.widget.ArkWidget;
import java.util.function.BooleanSupplier;

public record OptionControl(ArkWidget widget, float width, float height, BooleanSupplier lit, Runnable reset) {
}
