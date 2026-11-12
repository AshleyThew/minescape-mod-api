package com.minescape.mod.api.minimap;

/** A dot's colour on the minimap, and what OSRS shows in it. */
public enum MinimapDotColour {
    /** Other players. */
    WHITE(16, 0xFFFFFFFF),
    /** Items on the ground, and a target past the map's edge. */
    RED(17, 0xFFFF0000),
    /** NPCs, and the player's target. */
    YELLOW(18, 0xFFFFFF00),
    /** Friends. */
    GREEN(19, 0xFF00FF00),
    /** Clan members. */
    ORANGE(20, 0xFFFF8000),
    /** Team mates. */
    BLUE(21, 0xFF3373FF);

    private final int colour;
    private final int argb;

    MinimapDotColour(int colour, int argb) {
        this.colour = colour;
        this.argb = argb;
    }

    /**
     * The colour the pack's shader draws the dot in: not the map pixel's own, which only says which dot it is.
     *
     * @return the colour as 0xAARRGGBB
     */
    public int argb() {
        return argb;
    }

    /**
     * The dot a map byte on the dots' map is.
     *
     * @param colour a map byte
     * @return the dot's colour, or {@code null} when the byte isn't a dot
     */
    public static MinimapDotColour of(byte colour) {
        int value = colour & 0xFF;
        for (MinimapDotColour dot : values()) {
            if (dot.colour == value) {
                return dot;
            }
        }
        return null;
    }
}
