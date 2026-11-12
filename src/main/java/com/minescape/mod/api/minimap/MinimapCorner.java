package com.minescape.mod.api.minimap;

/** Where on the screen the player has chosen to put the minimap. */
public enum MinimapCorner {
    /** The top right, where it goes unless the player moves it. */
    TOP_RIGHT,
    /** The top left. */
    TOP_LEFT,
    /** The bottom left. */
    BOTTOM_LEFT,
    /** The bottom right. */
    BOTTOM_RIGHT,
    /** Along the bottom, just left of the hotbar. */
    LEFT_OF_HOTBAR,
    /** Along the bottom, just right of the hotbar. */
    RIGHT_OF_HOTBAR;

    static MinimapCorner of(byte colour) {
        switch (colour & 0xFF) {
        case 16:
            return TOP_LEFT;
        case 17:
            return BOTTOM_LEFT;
        case 18:
            return BOTTOM_RIGHT;
        case 19:
            return LEFT_OF_HOTBAR;
        case 20:
            return RIGHT_OF_HOTBAR;
        default:
            return TOP_RIGHT;
        }
    }
}
