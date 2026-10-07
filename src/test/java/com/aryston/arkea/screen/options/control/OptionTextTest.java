package com.aryston.arkea.screen.options.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.junit.jupiter.api.Test;

class OptionTextTest {
    private static final Component CAPTION = Component.literal("Render Distance");

    @Test
    void genericLabelsKeepOnlyTheValue() {
        Component value = Component.literal("12 Chunks");
        assertSame(value, OptionText.value(CAPTION, Component.translatable("options.generic_value", CAPTION, value)));
    }

    @Test
    void percentLabelsUseTheArkeaValueKey() {
        Component label = OptionText.value(CAPTION, Component.translatable("options.percent_value", CAPTION, 50));
        TranslatableContents contents = (TranslatableContents) label.getContents();
        assertEquals("arkea.value.percent_value", contents.getKey());
        assertEquals(50, contents.getArgs()[0]);
    }

    @Test
    void captionPrefixIsStrippedFromPlainLabels() {
        assertEquals("Fast", OptionText.value(CAPTION, Component.literal("Render Distance: Fast")).getString());
    }

    @Test
    void unknownLabelsStayAsTheyAre() {
        Component device = Component.literal("Speakers: Front");
        assertSame(device, OptionText.value(CAPTION, device));
    }
}
