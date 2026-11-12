package com.minescape.mod.api.minimap;

/**
 * The player's minimap as the client has it: the latest of its three maps, kept from what the server sends.
 * <p>
 * The server sends each layer under a fixed map id ({@link MinimapLayer#mapId()}), as whole maps and then as
 * the box of each map that changed, the same as any map. Hand this either the whole map whenever your loader
 * has it ({@link #accept(int, byte[])}, from its map state's colours) or each update as it comes
 * ({@link #update(int, int, int, int, int, byte[])}, from the map packet's patch). Then read the layers, or
 * draw the lot with {@link #render(int, float)}.
 * <p>
 * Not thread safe: use it from one thread, or lock round it.
 */
public final class Minimap {

    private static final int AREA = MinimapFormat.SIZE * MinimapFormat.SIZE;

    private final byte[][] colours = new byte[MinimapLayer.values().length][];
    private final MinimapMap[] decoded = new MinimapMap[MinimapLayer.values().length];

    /**
     * Whether a map id is one of the minimap's.
     *
     * @param mapId a map id
     * @return true for the three ids the minimap is sent under
     */
    public static boolean isMinimapMap(int mapId) {
        return MinimapLayer.fromMapId(mapId) != null;
    }

    /**
     * Takes a whole map, if it's one of the minimap's: by its id, or failing that by the marks in its row 0.
     *
     * @param mapId   the map's id
     * @param colours the map's 128 x 128 colour bytes; copied
     * @return whether it was one of the minimap's
     */
    public boolean accept(int mapId, byte[] colours) {
        if (colours == null || colours.length < AREA) {
            return false;
        }
        MinimapLayer layer = MinimapLayer.fromMapId(mapId);
        if (layer == null) {
            layer = MinimapLayer.of(colours);
        }
        if (layer == null) {
            return false;
        }
        set(layer, colours.clone());
        return true;
    }

    /**
     * Takes a whole map, if its row 0 marks it as one of the minimap's.
     *
     * @param colours the map's 128 x 128 colour bytes; copied
     * @return whether it was one of the minimap's
     */
    public boolean accept(byte[] colours) {
        MinimapLayer layer = MinimapLayer.of(colours);
        if (layer == null) {
            return false;
        }
        set(layer, colours.clone());
        return true;
    }

    /**
     * Applies a map packet's patch, if it's to one of the minimap's maps: the box of the map that changed.
     * Before the first whole map the rest of the map is clear.
     *
     * @param mapId  the map's id
     * @param x      the box's left pixel
     * @param y      the box's top pixel
     * @param width  the box's width
     * @param height the box's height
     * @param data   the box's colour bytes, row by row
     * @return whether it was one of the minimap's
     */
    public boolean update(int mapId, int x, int y, int width, int height, byte[] data) {
        MinimapLayer layer = MinimapLayer.fromMapId(mapId);
        if (layer == null || data == null) {
            return false;
        }
        byte[] into = colours[layer.ordinal()] == null ? new byte[AREA] : colours[layer.ordinal()].clone();
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                int u = x + column, v = y + row, from = column + row * width;
                if (u >= 0 && v >= 0 && u < MinimapFormat.SIZE && v < MinimapFormat.SIZE && from < data.length) {
                    into[u + v * MinimapFormat.SIZE] = data[from];
                }
            }
        }
        set(layer, into);
        return true;
    }

    private void set(MinimapLayer layer, byte[] map) {
        colours[layer.ordinal()] = map;
        decoded[layer.ordinal()] = null;
    }

    /**
     * Forgets every layer, as on leaving the server or changing worlds.
     */
    public void clear() {
        for (int i = 0; i < colours.length; i++) {
            colours[i] = null;
            decoded[i] = null;
        }
    }

    /**
     * A layer, decoded.
     *
     * @param layer which
     * @return the layer's map, or {@code null} when it hasn't been sent, or its row 0 doesn't mark it as that layer
     */
    public MinimapMap layer(MinimapLayer layer) {
        int i = layer.ordinal();
        if (decoded[i] == null && colours[i] != null) {
            MinimapMap map = MinimapMap.of(colours[i]);
            decoded[i] = map != null && map.layer() == layer ? map : null;
        }
        return decoded[i];
    }

    /**
     * The ground's map.
     *
     * @return the map, or {@code null} until it's sent
     */
    public MinimapMap ground() {
        return layer(MinimapLayer.GROUND);
    }

    /**
     * The icons' map.
     *
     * @return the map, or {@code null} until it's sent
     */
    public MinimapMap icons() {
        return layer(MinimapLayer.ICONS);
    }

    /**
     * The dots' map.
     *
     * @return the map, or {@code null} until it's sent
     */
    public MinimapMap dots() {
        return layer(MinimapLayer.DOTS);
    }

    /**
     * Whether there's a minimap to show: the server has sent its ground.
     *
     * @return true once the ground's map is in
     */
    public boolean isShown() {
        return ground() != null;
    }

    /**
     * The player's settings, from whichever layer is in.
     *
     * @return the settings, or {@link MinimapSettings#DEFAULT} with no layer in
     */
    public MinimapSettings settings() {
        for (MinimapLayer layer : MinimapLayer.values()) {
            MinimapMap map = layer(layer);
            if (map != null) {
                return map.settings();
            }
        }
        return MinimapSettings.DEFAULT;
    }

    /**
     * Draws the minimap as the pack's shader does ({@link MinimapRenderer}).
     *
     * @param size             the image's width and height in pixels
     * @param cameraYawDegrees the camera's yaw as Minecraft has it (0 facing south, 90 west); ignored with north up
     * @return {@code size * size} colours as 0xAARRGGBB, row by row
     */
    public int[] render(int size, float cameraYawDegrees) {
        return MinimapRenderer.render(ground(), icons(), dots(), size, cameraYawDegrees);
    }
}
