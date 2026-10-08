package com.aryston.arkea.screen.debug;

import com.aryston.arkea.Arkea;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.TextStyle;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringDecomposer;

record DebugFont(TextStyle style, Style body, Style title) {
    private static final float[] OVERSAMPLES = {1.0F, 1.5F, 2.0F, 2.5F, 3.0F, 4.0F};
    private static final String[] SUFFIXES = {"1", "1_5", "2", "2_5", "3", "4"};
    private static final String BODY_FONT = "debug_medium_";
    private static final String TITLE_FONT = "debug_semibold_";

    static DebugFont pick(UiScale scale, float designSize) {
        float pixels = designSize / TextStyle.GLYPH_HEIGHT * scale.scale();
        int best = 0;
        for (int index = 1; index < OVERSAMPLES.length; index++) {
            if (Math.abs(OVERSAMPLES[index] - pixels) < Math.abs(OVERSAMPLES[best] - pixels)) {
                best = index;
            }
        }
        TextStyle style = TextStyle.of(OVERSAMPLES[best] / scale.scale() * TextStyle.GLYPH_HEIGHT);
        return new DebugFont(style, font(BODY_FONT + SUFFIXES[best]), font(TITLE_FONT + SUFFIXES[best]));
    }

    FormattedCharSequence line(String text, boolean heading) {
        Style style = heading ? this.title : this.body;
        return sink -> StringDecomposer.iterateFormatted(text, style, sink);
    }

    private static Style font(String name) {
        return Style.EMPTY.withFont(new FontDescription.Resource(Identifier.fromNamespaceAndPath(Arkea.MOD_ID, name)));
    }
}
