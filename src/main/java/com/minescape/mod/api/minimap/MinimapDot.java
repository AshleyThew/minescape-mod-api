package com.minescape.mod.api.minimap;

/**
 * A dot on the minimap: a marker, from the dots' map. The player's own dot isn't one; it's always in the
 * middle.
 *
 * @param u      the map pixel across: 64 is the player's column, more is east
 * @param v      the map pixel down: 64 is the player's row, more is south
 * @param colour what it is
 */
public record MinimapDot(int u, int v, MinimapDotColour colour) {
}
