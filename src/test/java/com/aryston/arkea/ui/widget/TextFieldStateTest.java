package com.aryston.arkea.ui.widget;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TextFieldStateTest {
    @Test
    void insertsAtTheCursorAndRespectsTheLimit() {
        TextFieldState state = new TextFieldState(8);
        state.insert("jump");
        state.setCursor(0);
        state.insert("auto ");
        assertEquals("autojump", state.text());
        assertEquals(4, state.cursor());
    }

    @Test
    void deletesCharactersAndWords() {
        TextFieldState state = new TextFieldState(64);
        state.insert("open chat now");
        state.deleteBackward(false);
        assertEquals("open chat no", state.text());
        state.deleteBackward(true);
        assertEquals("open chat ", state.text());
        state.setCursor(0);
        state.deleteForward(true);
        assertEquals(" chat ", state.text());
    }

    @Test
    void selectAllIsReplacedByTypingAndClearedByDelete() {
        TextFieldState state = new TextFieldState(64);
        state.insert("sneak");
        state.selectAll();
        assertTrue(state.isAllSelected());
        state.insert("s");
        assertEquals("s", state.text());
        state.selectAll();
        state.deleteBackward(false);
        assertTrue(state.isEmpty());
        assertFalse(state.isAllSelected());
    }

    @Test
    void movesByWordsAndLeavesSelectionAtTheEdges() {
        TextFieldState state = new TextFieldState(64);
        state.insert("drop selected item");
        state.moveCursor(-1, true);
        assertEquals(14, state.cursor());
        state.selectAll();
        state.moveCursor(-1, false);
        assertEquals(0, state.cursor());
        assertFalse(state.isAllSelected());
    }
}
