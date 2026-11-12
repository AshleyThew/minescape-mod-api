package com.minescape.mod.api.minimap;

/** How big the player has chosen the minimap to be. */
public enum MinimapSize {
    /** The usual size: the map 0.3 of the screen tall. */
    NORMAL(0.3),
    /** 0.2 of the screen tall. */
    SMALL(0.2),
    /** 0.4 of the screen tall. */
    LARGE(0.4),
    /** 0.5 of the screen tall. */
    EXTRA_LARGE(0.5);

    private final double screenHeight;

    MinimapSize(double screenHeight) {
        this.screenHeight = screenHeight;
    }

    /**
     * The map's 128 pixels across, as a share of the screen's height: the pack's shader draws the map this
     * tall, and its ring round it a little bigger ({@link MinimapPlacement}).
     *
     * @return the share of the screen's height
     */
    public double screenHeight() {
        return screenHeight;
    }

    static MinimapSize of(byte colour) {
        switch (colour & 0xFF) {
        case 16:
            return SMALL;
        case 17:
            return LARGE;
        case 18:
            return EXTRA_LARGE;
        default:
            return NORMAL;
        }
    }
}
