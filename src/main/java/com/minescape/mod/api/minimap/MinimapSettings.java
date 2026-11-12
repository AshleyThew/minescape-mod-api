package com.minescape.mod.api.minimap;

/**
 * How the player has the minimap set: the same in every layer's row 0.
 *
 * @param northUp whether the map stays with north up, instead of turning with the camera
 * @param corner  where on the screen it goes
 * @param size    how big it is
 * @param offsetX how far it's moved right from where its corner puts it, in GUI pixels
 * @param offsetY how far it's moved up from where its corner puts it, in GUI pixels
 */
public record MinimapSettings(boolean northUp, MinimapCorner corner, MinimapSize size, int offsetX, int offsetY) {

    /** What a map with no settings in it means: the defaults. */
    public static final MinimapSettings DEFAULT = new MinimapSettings(false, MinimapCorner.TOP_RIGHT, MinimapSize.NORMAL, 0, 0);

    /**
     * Reads the settings from a layer's row 0.
     *
     * @param colours one of the minimap's maps, 128 x 128 colour bytes
     * @return the settings
     */
    public static MinimapSettings of(byte[] colours) {
        boolean northUp = (colours[MinimapFormat.NORTH_UP_AT] & 0xFF) == MinimapFormat.NORTH_UP;
        MinimapCorner corner = MinimapCorner.of(colours[MinimapFormat.CORNER_AT]);
        MinimapSize size = MinimapSize.of(colours[MinimapFormat.SIZE_AT]);
        long x = MinimapFormat.number(colours, MinimapFormat.OFFSET_X_AT, MinimapFormat.NUMBER_DIGITS);
        long y = MinimapFormat.number(colours, MinimapFormat.OFFSET_Y_AT, MinimapFormat.NUMBER_DIGITS);
        // an older server sends no offsets, which is not moved
        return new MinimapSettings(northUp, corner, size, x < 0 ? 0 : (int)x - 128, y < 0 ? 0 : (int)y - 128);
    }

    /**
     * Where the pack's shader puts the minimap on a screen of this size, at Minecraft's automatic GUI scale
     * (all a shader can work out).
     *
     * @param screenWidth  the screen's width in pixels
     * @param screenHeight the screen's height in pixels
     * @return where it goes
     */
    public MinimapPlacement placement(int screenWidth, int screenHeight) {
        int gui = Math.max(1, Math.min(screenWidth / 320, screenHeight / 240));
        return placement(screenWidth, screenHeight, gui);
    }

    /**
     * Where the pack's shader would put the minimap on a screen of this size, at this GUI scale. The shader
     * itself always uses the automatic scale ({@link #placement(int, int)}); a mod that knows the real one
     * can use it here, which moves only what is measured in GUI pixels: the offsets and the gap to the
     * hotbar.
     *
     * @param screenWidth  the screen's width in pixels
     * @param screenHeight the screen's height in pixels
     * @param guiScale     screen pixels a GUI pixel
     * @return where it goes
     */
    public MinimapPlacement placement(int screenWidth, int screenHeight, int guiScale) {
        // all in clip space, as the shader works: -1 to 1 across and up, the screen's height 2
        double gap = 0.03, hotbar = 91, hotbarGap = 2;
        double half = size.screenHeight() * MinimapPlacement.REACH / 64.0;
        double shape = (double)screenHeight / screenWidth;
        double pixelX = 2.0 * guiScale / screenWidth, pixelY = 2.0 * guiScale / screenHeight;
        double cx, cy;
        if (corner == MinimapCorner.LEFT_OF_HOTBAR || corner == MinimapCorner.RIGHT_OF_HOTBAR) {
            double side = corner == MinimapCorner.LEFT_OF_HOTBAR ? -1 : 1;
            cx = side * ((hotbar + hotbarGap) * pixelX + half * shape);
            cy = -1 + half + gap;
        } else {
            double sx = corner == MinimapCorner.TOP_LEFT || corner == MinimapCorner.BOTTOM_LEFT ? -1 : 1;
            double sy = corner == MinimapCorner.BOTTOM_LEFT || corner == MinimapCorner.BOTTOM_RIGHT ? -1 : 1;
            cx = sx * (1 - (half + gap) * shape);
            cy = sy * (1 - half - gap);
        }
        cx += offsetX * pixelX;
        cy += offsetY * pixelY;
        // to screen pixels from the top left; the frame is 2 * half of the screen's height across
        return new MinimapPlacement((cx + 1) / 2 * screenWidth, (1 - cy) / 2 * screenHeight, half * screenHeight);
    }
}
