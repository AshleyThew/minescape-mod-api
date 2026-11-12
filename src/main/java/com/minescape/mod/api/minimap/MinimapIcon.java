package com.minescape.mod.api.minimap;

/**
 * One of OSRS's icons in view, from the icons' map.
 * <p>
 * Its sprite is in the same map ({@link MinimapMap#sprite(int)}). Slots are handed out per player as kinds
 * of icon come into view and reused for the least lately shown, so read the sprite from the map each time
 * rather than keeping it by slot.
 *
 * @param slot which sprite it is, 0 to 80
 * @param u    the map pixel across it stands on: 64 is the player's column, more is east
 * @param v    the map pixel down it stands on: 64 is the player's row, more is south
 */
public record MinimapIcon(int slot, int u, int v) {
}
