package com.minescape.mod.api.minimap;

/**
 * Minecraft's map palette (1.18.2, which MineScape runs): what colour each of a map's bytes is.
 * <p>
 * A byte is a base colour's id times four plus a shade. The shades are, in byte order, 180, 220, 255 and 135
 * parts in 255 of the base colour. Bytes 0 to 3 are clear. Your loader's own palette (Yarn's
 * {@code MapColor.getRenderColor}, Mojang's {@code MaterialColor.calculateRGBColor}) gives the same colours
 * in its own channel order; this one is here so nothing else is needed.
 */
public final class MapPalette {

    /** Each base colour by id, as 0xRRGGBB. */
    private static final int[] BASE = {
            0x000000, 0x7FB238, 0xF7E9A3, 0xC7C7C7, 0xFF0000, 0xA0A0FF, 0xA7A7A7, 0x007C00,
            0xFFFFFF, 0xA4A8B8, 0x976D4D, 0x707070, 0x4040FF, 0x8F7748, 0xFFFCF5, 0xD87F33,
            0xB24CD8, 0x6699D8, 0xE5E533, 0x7FCC19, 0xF27FA5, 0x4C4C4C, 0x999999, 0x4C7F99,
            0x7F3FB2, 0x334CB2, 0x664C33, 0x667F33, 0x993333, 0x191919, 0xFAEE4D, 0x5CDBD5,
            0x4A80FF, 0x00D93A, 0x815631, 0x700200, 0xD1B1A1, 0x9F5224, 0x95576C, 0x706C8A,
            0xBA8524, 0x677535, 0xA04D4E, 0x392923, 0x876B62, 0x575C5C, 0x7A4958, 0x4C3E5C,
            0x4C3223, 0x4C522A, 0x8E3C2E, 0x251610, 0xBD3031, 0x943F61, 0x5C191D, 0x167E86,
            0x3A8E8C, 0x562C3E, 0x14B485, 0x646464, 0xD8AF93, 0x7FA796
    };

    private static final int[] SHADES = { 180, 220, 255, 135 };

    private static final int[] ARGB = new int[256];

    static {
        for (int id = 1; id < BASE.length; id++) {
            for (int shade = 0; shade < 4; shade++) {
                int base = BASE[id], m = SHADES[shade];
                int r = (base >> 16 & 0xFF) * m / 255, g = (base >> 8 & 0xFF) * m / 255, b = (base & 0xFF) * m / 255;
                ARGB[id * 4 + shade] = 0xFF000000 | r << 16 | g << 8 | b;
            }
        }
    }

    private MapPalette() {
    }

    /**
     * The colour of a map byte.
     *
     * @param colour a map's colour byte
     * @return the colour as 0xAARRGGBB: 0 (clear) for bytes 0 to 3 and any byte past the palette
     */
    public static int argb(byte colour) {
        return ARGB[colour & 0xFF];
    }

    /**
     * Whether a map byte is clear: no colour, as unmapped parts of a map are.
     *
     * @param colour a map's colour byte
     * @return true for bytes 0 to 3 and any byte past the palette
     */
    public static boolean isClear(byte colour) {
        return ARGB[colour & 0xFF] == 0;
    }
}
