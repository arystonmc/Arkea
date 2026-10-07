package com.aryston.arkea.ui.render;

final class PathTokenizer {
    private final String path;
    private int position;

    PathTokenizer(String path) {
        this.path = path;
    }

    boolean hasMore() {
        this.skipSeparators();
        return this.position < this.path.length();
    }

    boolean peekIsCommand() {
        this.skipSeparators();
        return this.position < this.path.length() && isCommand(this.path.charAt(this.position));
    }

    char nextCommand() {
        this.skipSeparators();
        return this.path.charAt(this.position++);
    }

    boolean nextFlag() {
        this.skipSeparators();
        char flag = this.path.charAt(this.position++);
        if (flag != '0' && flag != '1') {
            throw new IllegalArgumentException("Invalid arc flag at " + (this.position - 1) + " in " + this.path);
        }
        return flag == '1';
    }

    float nextNumber() {
        this.skipSeparators();
        int start = this.position;
        if (this.position < this.path.length() && isSign(this.path.charAt(this.position))) {
            this.position++;
        }
        boolean seenDot = false;
        boolean seenExponent = false;
        while (this.position < this.path.length()) {
            char character = this.path.charAt(this.position);
            if (Character.isDigit(character)) {
                this.position++;
            } else if (character == '.' && !seenDot && !seenExponent) {
                seenDot = true;
                this.position++;
            } else if ((character == 'e' || character == 'E') && !seenExponent) {
                seenExponent = true;
                this.position++;
                if (this.position < this.path.length() && isSign(this.path.charAt(this.position))) {
                    this.position++;
                }
            } else {
                break;
            }
        }
        if (start == this.position) {
            throw new IllegalArgumentException("Expected a number at " + start + " in " + this.path);
        }
        return Float.parseFloat(this.path.substring(start, this.position));
    }

    private void skipSeparators() {
        while (this.position < this.path.length()) {
            char character = this.path.charAt(this.position);
            if (character != ',' && !Character.isWhitespace(character)) {
                return;
            }
            this.position++;
        }
    }

    private static boolean isSign(char character) {
        return character == '-' || character == '+';
    }

    private static boolean isCommand(char character) {
        return "MmLlHhVvCcSsQqTtAaZz".indexOf(character) >= 0;
    }
}
