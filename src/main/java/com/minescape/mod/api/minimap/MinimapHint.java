package com.minescape.mod.api.minimap;

/**
 * OSRS's yellow hint arrow, over where the player is being led. The server blinks it, sending it and then
 * nothing, so it comes and goes on its own.
 *
 * @param u    the map pixel across: 64 is the player's column, more is east
 * @param v    the map pixel down: 64 is the player's row, more is south
 * @param away whether the place is past the map: then {@code u, v} is a point near the ring the way it
 *             lies, and the arrow stands at the ring pointing that way rather than over the place
 */
public record MinimapHint(int u, int v, boolean away) {
}
