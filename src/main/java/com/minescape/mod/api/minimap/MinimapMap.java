package com.minescape.mod.api.minimap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * One of the minimap's three maps, decoded from its colour bytes.
 * <p>
 * Only the ground's map is a picture. Drawn as it is, the icons' map shows its icon list and sprite sheet as
 * rows of coloured pixels along the top, and the dots' map shows single pixels. Read them with
 * {@link #icons()}, {@link #sprite(int)}, {@link #dots()} and {@link #hint()}, or let {@link MinimapRenderer}
 * draw the three together as the pack's shader does.
 */
public final class MinimapMap {

    /** How far from the middle the map shows anything; the stone ring is round it, to {@link MinimapPlacement#REACH}. */
    public static final double RADIUS = 60;

    private final MinimapLayer layer;
    private final byte[] colours;
    private final MinimapSettings settings;
    private final List<MinimapIcon> icons;
    private final List<MinimapDot> dots;
    private final MinimapHint hint;

    private MinimapMap(MinimapLayer layer, byte[] colours) {
        this.layer = layer;
        this.colours = colours;
        this.settings = MinimapSettings.of(colours);
        this.icons = layer == MinimapLayer.ICONS ? readIcons() : Collections.emptyList();
        this.dots = layer == MinimapLayer.DOTS ? readDots() : Collections.emptyList();
        this.hint = layer == MinimapLayer.DOTS ? readHint() : null;
    }

    /**
     * Decodes a map, if it's one of the minimap's.
     *
     * @param colours the map's 128 x 128 colour bytes, row by row; copied, so the caller can keep changing its own
     * @return the map, or {@code null} when it isn't one of the minimap's
     */
    public static MinimapMap of(byte[] colours) {
        MinimapLayer layer = MinimapLayer.of(colours);
        if (layer == null) {
            return null;
        }
        return new MinimapMap(layer, colours.clone());
    }

    /**
     * Which of the three maps this is.
     *
     * @return the layer
     */
    public MinimapLayer layer() {
        return layer;
    }

    /**
     * The settings in its row 0.
     *
     * @return the settings
     */
    public MinimapSettings settings() {
        return settings;
    }

    /**
     * A copy of the map's colour bytes.
     *
     * @return 128 x 128 bytes, row by row
     */
    public byte[] colours() {
        return colours.clone();
    }

    /**
     * A pixel's colour byte.
     *
     * @param u across, 0 to 127
     * @param v down, 0 to 127
     * @return the byte, or 0 (clear) off the map
     */
    public byte colour(int u, int v) {
        if (u < 0 || v < 0 || u >= MinimapFormat.SIZE || v >= MinimapFormat.SIZE) {
            return 0;
        }
        return colours[u + v * MinimapFormat.SIZE];
    }

    /**
     * The ground as a picture: each pixel's colour inside {@link #RADIUS}, ground not mapped black, and clear
     * outside it (which leaves out row 0's settings). On the other layers this is their raw pixels, which
     * aren't a picture.
     *
     * @return 128 x 128 colours as 0xAARRGGBB, row by row
     */
    public int[] groundArgb() {
        int[] argb = new int[MinimapFormat.SIZE * MinimapFormat.SIZE];
        for (int v = 0; v < MinimapFormat.SIZE; v++) {
            for (int u = 0; u < MinimapFormat.SIZE; u++) {
                double du = u + 0.5 - 64, dv = v + 0.5 - 64;
                if (du * du + dv * dv >= RADIUS * RADIUS) {
                    continue;
                }
                int colour = MapPalette.argb(colours[u + v * MinimapFormat.SIZE]);
                argb[u + v * MinimapFormat.SIZE] = colour == 0 ? 0xFF000000 : colour;
            }
        }
        return argb;
    }

    /**
     * The icons in view, nearest the player first. Only on the icons' map.
     *
     * @return the icons; none on the other layers
     */
    public List<MinimapIcon> icons() {
        return icons;
    }

    private List<MinimapIcon> readIcons() {
        List<MinimapIcon> icons = new ArrayList<>();
        for (int i = 0; i < MinimapFormat.MAX_ICONS; i++) {
            long value = MinimapFormat.number(colours, MinimapFormat.ICON_LIST + i * MinimapFormat.ICON_DIGITS, MinimapFormat.ICON_DIGITS);
            if (value < 0) {
                break;
            }
            int slot = (int)(value >> 14);
            if (slot >= MinimapFormat.SLOTS) {
                break;
            }
            icons.add(new MinimapIcon(slot, (int)(value >> 7 & 127), (int)(value & 127)));
        }
        return Collections.unmodifiableList(icons);
    }

    /**
     * An icon's sprite, from the icons' map: 13 x 13 pixels, the icon centred in it.
     *
     * @param slot the icon's slot ({@link MinimapIcon#slot()})
     * @return 13 x 13 colours as 0xAARRGGBB, row by row, clear where the sprite is
     */
    public int[] sprite(int slot) {
        int[] argb = new int[MinimapFormat.SPRITE * MinimapFormat.SPRITE];
        if (slot < 0 || slot >= MinimapFormat.SLOTS) {
            return argb;
        }
        int left = slot % MinimapFormat.SPRITES_PER_ROW * MinimapFormat.SPRITE;
        int top = MinimapFormat.ATLAS_ROW + slot / MinimapFormat.SPRITES_PER_ROW * MinimapFormat.SPRITE;
        for (int y = 0; y < MinimapFormat.SPRITE; y++) {
            for (int x = 0; x < MinimapFormat.SPRITE; x++) {
                argb[x + y * MinimapFormat.SPRITE] = MapPalette.argb(colour(left + x, top + y));
            }
        }
        return argb;
    }

    /**
     * An icon's sprite as the pack's shader draws it: the 13 x 13 sprite inside a 15 x 15 square whose edge
     * is black along its middle five pixels a side and clear at the corners. The shader draws a sprite pixel
     * three quarters of a map pixel across, centred on the middle of the icon's map pixel, upright however
     * the map turns.
     *
     * @param slot the icon's slot ({@link MinimapIcon#slot()})
     * @return 15 x 15 colours as 0xAARRGGBB, row by row
     */
    public int[] spriteWithEdge(int slot) {
        int[] inner = sprite(slot);
        int size = MinimapFormat.SPRITE + 2;
        int[] argb = new int[size * size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                argb[x + y * size] = edgedPixel(inner, x, y);
            }
        }
        return argb;
    }

    /** A pixel of {@link #spriteWithEdge(int)}: the edge's, or the sprite's inside it. */
    static int edgedPixel(int[] inner, int x, int y) {
        int last = MinimapFormat.SPRITE + 1;
        if (x == 0 || y == 0 || x == last || y == last) {
            int along = x == 0 || x == last ? y : x;
            return along >= 5 && along <= 9 ? 0xFF000000 : 0;
        }
        return inner[x - 1 + (y - 1) * MinimapFormat.SPRITE];
    }

    /**
     * The dots on the map. Only on the dots' map.
     *
     * @return the dots, row by row; none on the other layers
     */
    public List<MinimapDot> dots() {
        return dots;
    }

    private List<MinimapDot> readDots() {
        List<MinimapDot> dots = new ArrayList<>();
        // row 0 is the settings, whose bytes would read as dots
        for (int v = 1; v < MinimapFormat.SIZE; v++) {
            for (int u = 0; u < MinimapFormat.SIZE; u++) {
                MinimapDotColour colour = MinimapDotColour.of(colours[u + v * MinimapFormat.SIZE]);
                if (colour != null) {
                    dots.add(new MinimapDot(u, v, colour));
                }
            }
        }
        return Collections.unmodifiableList(dots);
    }

    /**
     * The hint arrow, while it's shown. Only on the dots' map.
     *
     * @return the arrow, or {@code null} when there's none, it's blinked off, or this is another layer
     */
    public MinimapHint hint() {
        return hint;
    }

    private MinimapHint readHint() {
        long u = MinimapFormat.number(colours, MinimapFormat.HINT_U_AT, MinimapFormat.NUMBER_DIGITS);
        long v = MinimapFormat.number(colours, MinimapFormat.HINT_V_AT, MinimapFormat.NUMBER_DIGITS);
        if (u < 0 || v < 0) {
            return null;
        }
        boolean away = u >= MinimapFormat.AWAY;
        return new MinimapHint((int)(away ? u - MinimapFormat.AWAY : u), (int)v, away);
    }
}
