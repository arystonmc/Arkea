package com.aryston.arkea.ui.widget;

import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.TextMetrics;

public interface UiHost {
    UiScale uiScale();

    TextMetrics metrics();

    long now();

    boolean showsKeyboardFocus();
}
