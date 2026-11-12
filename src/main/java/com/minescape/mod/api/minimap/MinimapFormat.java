package com.minescape.mod.api.minimap;

/**
 * How the server lays out the minimap's maps: the constants shared by the decoders. Everything is in a
 * map's raw colour bytes (128 x 128, row by row), which hold numbers as base 4 digits, one a pixel: the
 * map palette's fire colour in its four shades, bytes 16 to 19, are the digits 0 to 3.
 */
final class MinimapFormat {

    /** A map's width and height. */
    static final int SIZE = 128;

    /** The first digit's byte: bytes {@code DIGIT} to {@code DIGIT + 3} are the digits 0 to 3. */
    static final int DIGIT = 16;

    /** Row 0 of every layer starts with these, the third byte saying which layer it is. */
    static final int MARK_0 = 18, MARK_1 = 22, MARK_3 = 23;

    /** Row 0, after the marks: north up, then the corner, then the size. */
    static final int NORTH_UP_AT = 4, CORNER_AT = 5, SIZE_AT = 6;

    /** Row 0: the offsets right and up, then (on the dots' map) the hint arrow, four digits each. */
    static final int OFFSET_X_AT = 7, OFFSET_Y_AT = 11, HINT_U_AT = 15, HINT_V_AT = 19, NUMBER_DIGITS = 4;

    /** The byte at {@link #NORTH_UP_AT} when the map keeps north up. */
    static final int NORTH_UP = 19;

    /** The icons' list: from the second row, {@link #ICON_DIGITS} digits an icon, at most {@link #MAX_ICONS}. */
    static final int ICON_LIST = SIZE, ICON_DIGITS = 11, MAX_ICONS = 24;

    /** The icons' sprites: {@link #SPRITE} pixels square, nine to a row, from row {@link #ATLAS_ROW}. */
    static final int SPRITE = 13, ATLAS_ROW = 4, SPRITES_PER_ROW = 9, SLOTS = 81;

    /** An across value this much more on the hint arrow means the place is past the map. */
    static final int AWAY = 128;

    private MinimapFormat() {
    }

    /** The digit a byte is, or -1 when it isn't one. */
    static int digit(byte colour) {
        int value = (colour & 0xFF) - DIGIT;
        return value >= 0 && value <= 3 ? value : -1;
    }

    /**
     * A number in base 4 digits from {@code from} in the map, the first the largest, or -1 when any of its
     * pixels isn't a digit.
     */
    static long number(byte[] colours, int from, int digits) {
        long value = 0;
        for (int i = 0; i < digits; i++) {
            int at = from + i;
            int digit = at < colours.length ? digit(colours[at]) : -1;
            if (digit < 0) {
                return -1;
            }
            value = value * 4 + digit;
        }
        return value;
    }
}
