package com.minescape.mod.api.minimap;

import static com.minescape.mod.api.minimap.MinimapTestMaps.SIZE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class MinimapTest {

    // grass (1) at its brightest shade, and blue wool (25)
    private static final byte GRASS = 1 * 4 + 2, BLUE = 25 * 4 + 2;

    @Test
    void paletteMatchesMinecraft() {
        assertEquals(0, MapPalette.argb((byte)0));
        assertTrue(MapPalette.isClear((byte)3));
        assertEquals(0xFF7FB238, MapPalette.argb(GRASS));
        // fire's shades, which the digits are: 180, 220, 255 and 135 parts of red
        assertEquals(0xFFB40000, MapPalette.argb((byte)16));
        assertEquals(0xFFDC0000, MapPalette.argb((byte)17));
        assertEquals(0xFFFF0000, MapPalette.argb((byte)18));
        assertEquals(0xFF870000, MapPalette.argb((byte)19));
        // ice's darkest, the fourth mark
        assertEquals(0xFF545487, MapPalette.argb((byte)23));
        // glow lichen is the last colour; past it is clear
        assertEquals(0xFF7FA796, MapPalette.argb((byte)(61 * 4 + 2)));
        assertEquals(0, MapPalette.argb((byte)(62 * 4)));
    }

    @Test
    void layersAreFoundByMarksAndIds() {
        for (MinimapLayer layer : MinimapLayer.values()) {
            assertEquals(layer, MinimapLayer.of(MinimapTestMaps.map(layer)));
            assertEquals(layer, MinimapLayer.fromMapId(layer.mapId()));
        }
        byte[] plain = new byte[SIZE * SIZE];
        MinimapTestMaps.fill(plain, GRASS);
        assertNull(MinimapLayer.of(plain));
        assertNull(MinimapMap.of(plain));
        assertNull(MinimapLayer.of(new byte[10]));
        assertNull(MinimapLayer.fromMapId(5));
        assertEquals(1_000_000, MinimapLayer.GROUND.mapId());
    }

    @Test
    void settingsAreRead() {
        byte[] map = MinimapTestMaps.map(MinimapLayer.GROUND);
        assertEquals(MinimapSettings.DEFAULT, MinimapMap.of(map).settings());
        MinimapTestMaps.settings(map, true, 17, 18, -40, 25);
        MinimapSettings settings = MinimapMap.of(map).settings();
        assertTrue(settings.northUp());
        assertEquals(MinimapCorner.BOTTOM_LEFT, settings.corner());
        assertEquals(MinimapSize.EXTRA_LARGE, settings.size());
        assertEquals(-40, settings.offsetX());
        assertEquals(25, settings.offsetY());
        // an older server sends no offsets
        map[7] = 0;
        assertEquals(0, MinimapSettings.of(map).offsetX());
        assertEquals(25, MinimapSettings.of(map).offsetY());
    }

    @Test
    void iconsAndSpritesAreRead() {
        byte[] map = MinimapTestMaps.map(MinimapLayer.ICONS);
        MinimapTestMaps.icon(map, 0, 0, 70, 60, GRASS);
        MinimapTestMaps.icon(map, 1, 80, 127, 0, BLUE);
        MinimapMap icons = MinimapMap.of(map);
        assertEquals(List.of(new MinimapIcon(0, 70, 60), new MinimapIcon(80, 127, 0)), icons.icons());
        int[] sprite = icons.sprite(80);
        assertEquals(13 * 13, sprite.length);
        assertEquals(MapPalette.argb(BLUE), sprite[0]);
        assertEquals(MapPalette.argb(BLUE), sprite[13 * 13 - 1]);
        assertEquals(MapPalette.argb(GRASS), icons.sprite(0)[6 + 6 * 13]);
        int[] edged = icons.spriteWithEdge(0);
        assertEquals(0, edged[0]);
        assertEquals(0xFF000000, edged[7]);
        assertEquals(MapPalette.argb(GRASS), edged[1 + 15]);
        // the other layers have none
        assertTrue(MinimapMap.of(MinimapTestMaps.map(MinimapLayer.DOTS)).icons().isEmpty());
        assertTrue(MinimapMap.of(MinimapTestMaps.map(MinimapLayer.ICONS)).icons().isEmpty());
    }

    @Test
    void dotsAndHintAreRead() {
        byte[] map = MinimapTestMaps.map(MinimapLayer.DOTS);
        MinimapTestMaps.settings(map, false, 20, 16, 0, 0);
        map[70 + 64 * SIZE] = 17;
        map[60 + 50 * SIZE] = 21;
        MinimapMap dots = MinimapMap.of(map);
        // row 0's settings aren't dots, though their bytes are dot colours
        assertEquals(List.of(new MinimapDot(60, 50, MinimapDotColour.BLUE), new MinimapDot(70, 64, MinimapDotColour.RED)), dots.dots());
        assertNull(dots.hint());
        MinimapTestMaps.hint(map, 70, 64);
        assertEquals(new MinimapHint(70, 64, false), MinimapMap.of(map).hint());
        MinimapTestMaps.hint(map, 128 + 100, 64);
        assertEquals(new MinimapHint(100, 64, true), MinimapMap.of(map).hint());
    }

    @Test
    void minimapKeepsLayersAndAppliesUpdates() {
        Minimap minimap = new Minimap();
        assertFalse(minimap.isShown());
        assertEquals(MinimapSettings.DEFAULT, minimap.settings());
        byte[] plain = new byte[SIZE * SIZE];
        assertFalse(minimap.accept(42, plain));
        assertFalse(minimap.accept(plain));

        byte[] ground = MinimapTestMaps.map(MinimapLayer.GROUND);
        MinimapTestMaps.settings(ground, true, 0, 0, 0, 0);
        assertTrue(minimap.accept(MinimapLayer.GROUND.mapId(), ground));
        assertTrue(minimap.isShown());
        assertTrue(minimap.settings().northUp());
        // the caller's array is copied
        ground[64 + 64 * SIZE] = GRASS;
        assertEquals(0, minimap.ground().colour(64, 64));

        // a patch: a 2 x 2 box at 64, 64
        assertTrue(minimap.update(MinimapLayer.GROUND.mapId(), 64, 64, 2, 2, new byte[] { GRASS, BLUE, BLUE, GRASS }));
        assertEquals(GRASS, minimap.ground().colour(64, 64));
        assertEquals(BLUE, minimap.ground().colour(65, 64));
        assertEquals(BLUE, minimap.ground().colour(64, 65));
        assertFalse(minimap.update(7, 0, 0, 1, 1, new byte[] { 1 }));

        // patches before any whole map build it up: the dots' row 0 first, as the server's first send has it
        byte[] dots = MinimapTestMaps.map(MinimapLayer.DOTS);
        minimap.update(MinimapLayer.DOTS.mapId(), 0, 0, SIZE, 1, java.util.Arrays.copyOf(dots, SIZE));
        assertEquals(MinimapLayer.DOTS, minimap.dots().layer());
        assertNull(minimap.icons());

        minimap.clear();
        assertNull(minimap.ground());
        assertNull(minimap.dots());
    }

    @Test
    void placementMatchesTheShader() {
        MinimapPlacement top = MinimapSettings.DEFAULT.placement(1920, 1080);
        // the map's 128 pixels are 0.3 of the screen's height, the frame 132 of them
        assertEquals(0.3 * 1080 * 132 / 128, top.side(), 1e-9);
        // as far from the top as from the right
        assertEquals(top.top(), 1920 - (top.left() + top.side()), 1e-9);
        assertEquals(0.03 * 540, top.top(), 1e-9);

        // moved 10 GUI pixels left and 5 up, at the automatic scale (4 at 1920 x 1080)
        MinimapSettings moved = new MinimapSettings(false, MinimapCorner.TOP_RIGHT, MinimapSize.NORMAL, -10, 5);
        MinimapPlacement there = moved.placement(1920, 1080);
        assertEquals(top.centreX() - 40, there.centreX(), 1e-9);
        assertEquals(top.centreY() - 20, there.centreY(), 1e-9);

        MinimapSettings bottomLeft = new MinimapSettings(false, MinimapCorner.BOTTOM_LEFT, MinimapSize.SMALL, 0, 0);
        MinimapPlacement low = bottomLeft.placement(1920, 1080);
        assertEquals(0.03 * 540, low.left(), 1e-9);
        assertEquals(1080 - 0.03 * 540, low.top() + low.side(), 1e-9);

        // beside the hotbar: 93 GUI pixels from the middle, along the bottom
        MinimapSettings hotbar = new MinimapSettings(false, MinimapCorner.RIGHT_OF_HOTBAR, MinimapSize.NORMAL, 0, 0);
        assertEquals(960 + 93 * 2, hotbar.placement(1920, 1080, 2).left(), 1e-9);
    }

    // --- the renderer ---------------------------------------------------------------------------------

    private static final int IMAGE = 264;

    /** The image pixel at a spot on the frame, in map pixels from the middle (down being +). */
    private static int at(int[] image, double x, double y) {
        int px = (int)Math.floor((x / 132 + 0.5) * IMAGE), py = (int)Math.floor((y / 132 + 0.5) * IMAGE);
        return image[px + py * IMAGE];
    }

    private static MinimapMap ground(boolean northUp) {
        byte[] map = MinimapTestMaps.map(MinimapLayer.GROUND);
        MinimapTestMaps.settings(map, northUp, 0, 0, 0, 0);
        MinimapTestMaps.fill(map, GRASS);
        // a blue block 10 east of the player
        map[74 + 64 * SIZE] = BLUE;
        return MinimapMap.of(map);
    }

    @Test
    void groundTurnsWithTheCamera() {
        // facing north the map isn't turned: east is to the right
        int[] north = MinimapRenderer.render(ground(false), null, null, IMAGE, 180);
        assertEquals(MapPalette.argb(BLUE), at(north, 10.5, 0.5));
        assertEquals(MapPalette.argb(GRASS), at(north, 0.5, -10.5));
        // facing east, east is up
        int[] east = MinimapRenderer.render(ground(false), null, null, IMAGE, -90);
        assertEquals(MapPalette.argb(BLUE), at(east, 0.5, -10.5));
        assertEquals(MapPalette.argb(GRASS), at(east, 10.5, 0.5));
        // facing south, east is to the left
        int[] south = MinimapRenderer.render(ground(false), null, null, IMAGE, 0);
        assertEquals(MapPalette.argb(BLUE), at(south, -10.5, -0.5));
        // north up ignores the camera
        int[] fixed = MinimapRenderer.render(ground(true), null, null, IMAGE, -90);
        assertEquals(MapPalette.argb(BLUE), at(fixed, 10.5, 0.5));
        // the ring is stone, and past it clear
        assertNotEquals(0, at(north, 0, -62.5));
        assertEquals(0, at(north, 0, -65.9));
        assertEquals(0, north[0]);
    }

    @Test
    void iconsStayUprightWhereTheyStand() {
        byte[] map = MinimapTestMaps.map(MinimapLayer.ICONS);
        // an icon 20 east of the player
        MinimapTestMaps.icon(map, 0, 3, 84, 64, BLUE);
        MinimapMap icons = MinimapMap.of(map);
        int[] north = MinimapRenderer.render(ground(false), icons, null, IMAGE, 180);
        assertEquals(MapPalette.argb(BLUE), at(north, 20.5, 0.5));
        // its sprite is 13 pixels at three quarters of a map pixel each, edged black
        assertEquals(MapPalette.argb(BLUE), at(north, 20.5 + 4.6, 0.5));
        assertEquals(0xFF000000, at(north, 20.5 + 5.4, 0.5));
        assertEquals(MapPalette.argb(GRASS), at(north, 20.5 + 6, 0.5));
        // facing east the icon is up, as the ground under it is
        int[] east = MinimapRenderer.render(ground(false), icons, null, IMAGE, -90);
        assertEquals(MapPalette.argb(BLUE), at(east, 0.5, -19.5));
        assertEquals(MapPalette.argb(GRASS), at(east, 20.5, 0.5));
    }

    @Test
    void abgrSwapsRedAndBlue() {
        assertArrayEquals(new int[] { 0xFF3322AA, 0 }, MinimapRenderer.toAbgr(new int[] { 0xFFAA2233, 0 }));
    }

    @Test
    void dotsHintAndPlayerAreDrawn() {
        byte[] map = MinimapTestMaps.map(MinimapLayer.DOTS);
        // a friend 10 south of the player
        map[64 + 74 * SIZE] = 19;
        MinimapTestMaps.hint(map, 64, 40);
        MinimapMap dots = MinimapMap.of(map);
        int[] image = MinimapRenderer.render(ground(false), null, dots, IMAGE, 180);
        assertEquals(0xFFFFFFFF, at(image, 0.2, 0.2));
        assertEquals(MinimapDotColour.GREEN.argb(), at(image, 0.6, 10.6));
        // the arrow points down on to the place 24 north: its tip there, its tail above
        assertEquals(0xFFFFFF00, at(image, 0.3, -24.0));
        assertEquals(0xFFFFFF00, at(image, 0.3, -31.0));
        assertEquals(MapPalette.argb(GRASS), at(image, 0.3, -36.0));
    }
}
