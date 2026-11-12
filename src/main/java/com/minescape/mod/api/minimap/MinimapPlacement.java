package com.minescape.mod.api.minimap;

/**
 * Where on the screen the minimap goes, in screen pixels from the top left.
 * <p>
 * The square is the whole frame: the map and the stone ring round it. It is {@link #REACH} map pixels across,
 * the map's own 128 in its middle, which is what {@link MinimapRenderer} draws into an image of any size.
 *
 * @param centreX where its middle is across
 * @param centreY where its middle is down
 * @param side    how wide and tall the frame is
 */
public record MinimapPlacement(double centreX, double centreY, double side) {

    /** The frame's width in map pixels, from the middle to either edge: the ring reaches 2 past the map's 64. */
    public static final double REACH = 66;

    /**
     * The frame's left edge.
     *
     * @return screen pixels from the left
     */
    public double left() {
        return centreX - side / 2;
    }

    /**
     * The frame's top edge.
     *
     * @return screen pixels from the top
     */
    public double top() {
        return centreY - side / 2;
    }
}
