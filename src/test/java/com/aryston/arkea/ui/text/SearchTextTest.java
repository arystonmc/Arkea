package com.aryston.arkea.ui.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SearchTextTest {
    @Test
    void ignoresCaseAndAccents() {
        assertEquals("turkce", SearchText.normalize("Türkçe"));
        assertEquals("isik", SearchText.normalize("Işık"));
        assertEquals("francais", SearchText.normalize(" Français "));
        assertEquals("strasse", SearchText.normalize("Straße"));
    }

    @Test
    void matchesAnyField() {
        String query = SearchText.normalize("turk");
        assertTrue(SearchText.matches(query, "Türkçe", "Türkiye"));
        assertFalse(SearchText.matches(query, "Deutsch", "Deutschland"));
        assertTrue(SearchText.matches("", "anything"));
    }
}
