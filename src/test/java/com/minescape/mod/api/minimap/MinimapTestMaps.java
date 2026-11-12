package com.minescape.mod.api.minimap;

/**
 * Builds minimap maps the way the server does (the plugin's PlayerMinimap): marks and settings in row 0, the
 * icon list from row 1 and sprites from row 4, dots as single pixels, the hint arrow in the dots' row 0.
 */
final class MinimapTestMaps {

    static final int SIZE = 128;

    private MinimapTestMaps() {
    }

    static byte[] map(MinimapLayer layer) {
        byte[] map = new byte[SIZE * SIZE];
        int mark = layer == MinimapLayer.GROUND ? 16 : layer == MinimapLayer.ICONS ? 17 : 19;
        map[0] = 18;
        map[1] = 22;
        map[2] = (byte)mark;
        map[3] = 23;
        // no offsets moved: 128 each
        writeDigits(map, 7, 128, 4);
        writeDigits(map, 11, 128, 4);
        return map;
    }

    static void settings(byte[] map, boolean northUp, int corner, int size, int offsetX, int offsetY) {
        map[4] = (byte)(northUp ? 19 : 0);
        map[5] = (byte)corner;
        map[6] = (byte)size;
        writeDigits(map, 7, offsetX + 128, 4);
        writeDigits(map, 11, offsetY + 128, 4);
    }

    static void writeDigits(byte[] into, int from, int value, int digits) {
        for (int i = digits - 1; i >= 0; i--) {
            into[from + i] = (byte)(16 + (value & 3));
            value >>= 2;
        }
    }

    /** An icon at its place in the list, and its sprite in its slot: a solid square of one colour. */
    static void icon(byte[] map, int index, int slot, int u, int v, byte colour) {
        writeDigits(map, SIZE + index * 11, slot << 14 | u << 7 | v, 11);
        int left = slot % 9 * 13, top = 4 + slot / 9 * 13;
        for (int y = 0; y < 13; y++) {
            for (int x = 0; x < 13; x++) {
                map[left + x + (top + y) * SIZE] = colour;
            }
        }
    }

    static void hint(byte[] map, int u, int v) {
        writeDigits(map, 15, u, 4);
        writeDigits(map, 19, v, 4);
    }

    static void fill(byte[] map, byte colour) {
        for (int v = 1; v < SIZE; v++) {
            for (int u = 0; u < SIZE; u++) {
                map[u + v * SIZE] = colour;
            }
        }
    }
}
