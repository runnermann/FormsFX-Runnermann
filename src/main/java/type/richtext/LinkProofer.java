package type.richtext;

import java.util.regex.Pattern;

/**
 * Provides patterns for hyperlinks.
 */
public enum LinkProofer {
    PTN {
        String[] get() {
           return new String[] {
                   "\\.com", "\\.xyz", "\\.ai", "\\.io", "\\.net", "\\.org", "\\.info", "\\.mil", "\\.edu",
                   "\\.de", "\\.ru", "\\.co", "\\.uk", "\\.se", "\\.fr", "\\.eu", "\\.ch"
            };
        }
    };

    private static final String PATTERN = "\\b[www.]*[a-zA-Z0-9\\-]+(" + String.join("|", PTN.get()) + ")+[/a-zA-Z0-9\\-\\.]*\\b";

    abstract String[] get();

    /**
     * Call to retrieve a compiled pattern matcher capable of
     * matching web addresses similar to google.com, www.google.com, www.flashmonkey.xyz/abc-123/marvinTheMartian.jpg
     * Not: does not match an http nor https
     */
    public static final Pattern LINK_PATTERN = Pattern.compile(
            "(?<HYPERLINK>" + PATTERN + ")"
    );

}
