package backend.security;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class InputSanitizer {

    // Strips all HTML tags, leaving only raw text
    public static String sanitizeText(String input) {
        if (input == null) {
            return null;
        }
        return Jsoup.clean(input, Safelist.none());
    }

    // Allows basic formatting (b, i, u, p) but strips scripts
    public static String sanitizeBasicHtml(String input) {
        if (input == null) {
            return null;
        }
        return Jsoup.clean(input, Safelist.basic());
    }
}