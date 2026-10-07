package com.aryston.arkea.ui.widget;

public final class TextFieldState {
    private final int maxLength;
    private String text = "";
    private int cursor;
    private boolean allSelected;

    public TextFieldState(int maxLength) {
        this.maxLength = maxLength;
    }

    public String text() {
        return this.text;
    }

    public int cursor() {
        return this.cursor;
    }

    public boolean isAllSelected() {
        return this.allSelected && !this.text.isEmpty();
    }

    public boolean isEmpty() {
        return this.text.isEmpty();
    }

    public void setText(String value) {
        this.text = value.length() > this.maxLength ? value.substring(0, this.maxLength) : value;
        this.cursor = this.text.length();
        this.allSelected = false;
    }

    public void selectAll() {
        this.allSelected = true;
        this.cursor = this.text.length();
    }

    public void deselect() {
        this.allSelected = false;
    }

    public void insert(String value) {
        if (this.isAllSelected()) {
            this.text = "";
            this.cursor = 0;
        }
        this.allSelected = false;
        int room = this.maxLength - this.text.length();
        String inserted = value.length() > room ? value.substring(0, Math.max(0, room)) : value;
        this.text = this.text.substring(0, this.cursor) + inserted + this.text.substring(this.cursor);
        this.cursor += inserted.length();
    }

    public void deleteBackward(boolean word) {
        if (this.removeSelection()) {
            return;
        }
        int start = word ? this.wordStart(this.cursor) : Math.max(0, this.cursor - 1);
        this.text = this.text.substring(0, start) + this.text.substring(this.cursor);
        this.cursor = start;
    }

    public void deleteForward(boolean word) {
        if (this.removeSelection()) {
            return;
        }
        int end = word ? this.wordEnd(this.cursor) : Math.min(this.text.length(), this.cursor + 1);
        this.text = this.text.substring(0, this.cursor) + this.text.substring(end);
    }

    private boolean removeSelection() {
        if (!this.isAllSelected()) {
            this.allSelected = false;
            return false;
        }
        this.setText("");
        return true;
    }

    public void moveCursor(int delta, boolean word) {
        if (this.isAllSelected()) {
            this.allSelected = false;
            this.cursor = delta < 0 ? 0 : this.text.length();
            return;
        }
        if (word) {
            this.cursor = delta < 0 ? this.wordStart(this.cursor) : this.wordEnd(this.cursor);
        } else {
            this.cursor = Math.clamp(this.cursor + delta, 0, this.text.length());
        }
    }

    public void setCursor(int position) {
        this.allSelected = false;
        this.cursor = Math.clamp(position, 0, this.text.length());
    }

    private int wordStart(int from) {
        int index = from;
        while (index > 0 && this.text.charAt(index - 1) == ' ') {
            index--;
        }
        while (index > 0 && this.text.charAt(index - 1) != ' ') {
            index--;
        }
        return index;
    }

    private int wordEnd(int from) {
        int index = from;
        while (index < this.text.length() && this.text.charAt(index) == ' ') {
            index++;
        }
        while (index < this.text.length() && this.text.charAt(index) != ' ') {
            index++;
        }
        return index;
    }
}
