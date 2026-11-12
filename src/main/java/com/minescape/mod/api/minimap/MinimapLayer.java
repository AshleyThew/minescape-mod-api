package com.minescape.mod.api.minimap;

/**
 * The minimap's three maps, drawn one over the other in this order.
 * <p>
 * The server sends each as an ordinary map with a fixed id ({@link #mapId()}), shown in invisible item
 * frames riding the player so the client keeps it. Every layer's row 0 starts with marks saying which
 * layer it is ({@link #of(byte[])}), then the settings every layer shares ({@link MinimapSettings}). Row 0
 * is never part of the picture.
 */
public enum MinimapLayer {

    /** The ground round the player: one block a pixel, north up, the player at pixel 64, 64. */
    GROUND(1_000_000, 16),

    /**
     * OSRS's map icons. Not a picture: from row 1 a list of the icons in view, and from row 4 a sheet of
     * their sprites ({@link MinimapMap#icons()}, {@link MinimapMap#sprite(int)}).
     */
    ICONS(1_000_001, 17),

    /**
     * The dots: one pixel a marker, its colour saying what it is ({@link MinimapMap#dots()}), and in row 0
     * the hint arrow ({@link MinimapMap#hint()}).
     */
    DOTS(1_000_002, 19);

    private final int mapId;
    private final int mark;

    MinimapLayer(int mapId, int mark) {
        this.mapId = mapId;
        this.mark = mark;
    }

    /**
     * The id the server sends this layer's map under.
     *
     * @return the map id
     */
    public int mapId() {
        return mapId;
    }

    /**
     * The layer a map id is.
     *
     * @param mapId a map id
     * @return the layer, or {@code null} when the id is none of the minimap's
     */
    public static MinimapLayer fromMapId(int mapId) {
        for (MinimapLayer layer : values()) {
            if (layer.mapId == mapId) {
                return layer;
            }
        }
        return null;
    }

    /**
     * The layer a map is, by the marks its row 0 starts with. This is what the pack's shader goes by, and it
     * works whatever id the map came under.
     *
     * @param colours the map's 128 x 128 colour bytes
     * @return the layer, or {@code null} when the map isn't one of the minimap's
     */
    public static MinimapLayer of(byte[] colours) {
        if (colours == null || colours.length < MinimapFormat.SIZE * MinimapFormat.SIZE) {
            return null;
        }
        if ((colours[0] & 0xFF) != MinimapFormat.MARK_0 || (colours[1] & 0xFF) != MinimapFormat.MARK_1
                || (colours[3] & 0xFF) != MinimapFormat.MARK_3) {
            return null;
        }
        int mark = colours[2] & 0xFF;
        for (MinimapLayer layer : values()) {
            if (layer.mark == mark) {
                return layer;
            }
        }
        return null;
    }
}
