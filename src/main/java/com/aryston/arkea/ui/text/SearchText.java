package com.aryston.arkea.ui.text;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class SearchText {
    private static final Pattern MARKS = Pattern.compile("\\p{M}+");

    private SearchText() {
    }

    public static String normalize(String text) {
        String lower = text.toLowerCase(Locale.ROOT)
            .replace('ı', 'i')
            .replace('ø', 'o')
            .replace('ł', 'l')
            .replace("ß", "ss")
            .replace("æ", "ae")
            .replace("œ", "oe");
        return MARKS.matcher(Normalizer.normalize(lower, Normalizer.Form.NFD)).replaceAll("").trim();
    }

    public static boolean matches(String normalizedQuery, String... fields) {
        if (normalizedQuery.isEmpty()) {
            return true;
        }
        for (String field : fields) {
            if (normalize(field).contains(normalizedQuery)) {
                return true;
            }
        }
        return false;
    }
}
