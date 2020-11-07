package com.github.bhlangonijr.chesslib.pgn;

import com.github.bhlangonijr.chesslib.util.StringUtil;

import java.util.regex.Pattern;

/**
 * The type Pgn property.
 */
public class PgnProperty {

    public final static String UTF8_BOM = "\uFEFF";

    private final static Pattern propertyPattern = Pattern.compile("\\[.* \".*\"\\]");
    /**
     * The Name.
     */
    public String name;
    /**
     * The Value.
     */
    public String value;

    /**
     * Instantiates a new Pgn property.
     */
    public PgnProperty() {
    }

    /**
     * Instantiates a new Pgn property.
     *
     * @param name  the name
     * @param value the value
     */
    public PgnProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static boolean isProperty(String line) {
        return propertyPattern.matcher(line).matches();
    }

    /**
     * Parse a PGN property line
     *
     * @param line the pgn property line
     * @return PgnProperty object read from the string
     */
    public static PgnProperty parsePgnProperty(String line) {
        try {

            String l = line.replace("[", "");
            l = l.replace("]", "");
            l = l.replace("\"", "");

            return new PgnProperty(StringUtil.beforeSequence(l, " "),
                    StringUtil.afterSequence(l, " "));
        } catch (Exception e) {
            // do nothing
        }

        return null;
    }
}