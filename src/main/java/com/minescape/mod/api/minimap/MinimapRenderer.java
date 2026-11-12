package com.minescape.mod.api.minimap;

/**
 * Draws the minimap as the resource pack's shader does ({@code assets/minescape/stitches/minimap.frag.glsl}),
 * into an image a client can upload as a texture and draw at {@link MinimapSettings#placement(int, int)}.
 * <p>
 * The image is the whole frame, {@link MinimapPlacement#REACH} map pixels either side of the middle: the ground
 * inside a stone ring with N, E, S and W on it, OSRS's icons over the ground, then the dots, the hint arrow and
 * the player's own white dot in the middle. Unless the settings keep north up, the map turns with the camera
 * while the icons, dots and letters stay upright. Outside the ring the image is clear.
 * <p>
 * Every pixel is worked out on its own, as the shader's are, so a bigger image is sharper, not blurrier: the
 * shader draws at the screen's resolution, {@link MinimapPlacement#side()} pixels across.
 */
public final class MinimapRenderer {

    private static final double REACH = MinimapPlacement.REACH;
    private static final double RADIUS = MinimapMap.RADIUS;

    private MinimapRenderer() {
    }

    /**
     * Draws the minimap.
     *
     * @param ground           the ground's map, or {@code null} for none (then nothing is drawn under the rest)
     * @param icons            the icons' map, or {@code null} for none
     * @param dots             the dots' map, or {@code null} for none
     * @param size             the image's width and height in pixels
     * @param cameraYawDegrees the camera's yaw as Minecraft has it (0 facing south, 90 west); ignored with north up
     * @return {@code size * size} colours as 0xAARRGGBB, row by row
     */
    public static int[] render(MinimapMap ground, MinimapMap icons, MinimapMap dots, int size, float cameraYawDegrees) {
        int[] image = new int[size * size];
        MinimapMap any = ground != null ? ground : icons != null ? icons : dots;
        if (any == null || size <= 0) {
            return image;
        }
        Turn turn = new Turn(any.settings().northUp(), cameraYawDegrees);
        for (int py = 0; py < size; py++) {
            for (int px = 0; px < size; px++) {
                // where on the frame, in map pixels from the middle, down being +
                double x = ((px + 0.5) / size - 0.5) * 2 * REACH, y = ((py + 0.5) / size - 0.5) * 2 * REACH;
                int colour = ground != null ? groundColour(ground, turn, x, y) : 0;
                if (icons != null) {
                    int over = iconsColour(icons, turn, x, y);
                    colour = over != 0 ? over : colour;
                }
                if (dots != null) {
                    int over = dotsColour(dots, turn, x, y);
                    colour = over != 0 ? over : colour;
                }
                image[px + py * size] = colour;
            }
        }
        return image;
    }

    /**
     * The map's turn with the camera, as the shader works it out: by the yaw of the camera's right, so facing north
     * is no turn.
     */
    static final class Turn {
        final boolean northUp;
        final double cos, sin;

        Turn(boolean northUp, float cameraYawDegrees) {
            this.northUp = northUp;
            double yaw = Math.toRadians(cameraYawDegrees) + Math.PI;
            this.cos = Math.cos(yaw);
            this.sin = Math.sin(yaw);
        }

        /** The shader's {@code msTurned * v}: from the screen to the map. */
        double[] toMap(double vx, double vy) {
            return northUp ? new double[] { vx, vy } : new double[] { cos * vx + sin * vy, -sin * vx + cos * vy };
        }

        /** The shader's {@code v * msTurned}: from the map to the screen. */
        double[] toScreen(double vx, double vy) {
            return northUp ? new double[] { vx, vy } : new double[] { vx * cos - vy * sin, vx * sin + vy * cos };
        }
    }

    // --- the ground and the ring ---------------------------------------------------------------------

    private static final int[] LETTERS = { 18667121, 32010782, 16267326, 18405233 };

    static int groundColour(MinimapMap map, Turn turn, double x, double y) {
        double atX = Math.floor(x) + 0.5, atY = Math.floor(y) + 0.5;
        double radius = Math.hypot(atX, atY);
        if (radius < RADIUS) {
            double[] m = turn.toMap(x, -y);
            byte colour = map.colour((int)Math.floor(m[0] + 64), (int)Math.floor(-m[1] + 64));
            int argb = MapPalette.argb(colour);
            return argb == 0 ? 0xFF000000 : argb;
        }
        if (radius > 65) {
            return 0;
        }
        double lit = (atX / radius) * -0.7071 + (atY / radius) * -0.7071;
        double grain = fract(Math.sin(Math.floor(atX / 2) * 12.9898 + Math.floor(atY / 2) * 78.233) * 43758.5453);
        double[] stone = grain < 0.2 ? rgb(0.365, 0.329, 0.290) : grain < 0.8 ? rgb(0.298, 0.263, 0.220) : rgb(0.243, 0.204, 0.141);
        if (radius > 64 || radius < 61) {
            stone = rgb(0.075, 0.067, 0.055);
        } else if (fract((Math.atan2(atY, atX) / 6.28318 + 0.5) * 16) < 0.03) {
            stone = rgb(0.125, 0.129, 0.125);
        } else if (radius > 63) {
            stone = lit > 0.25 ? rgb(0.545, 0.506, 0.427) : lit < -0.25 ? rgb(0.176, 0.173, 0.161) : stone;
        } else if (radius < 62) {
            stone = lit < -0.25 ? rgb(0.494, 0.455, 0.380) : lit > 0.25 ? rgb(0.176, 0.173, 0.161) : stone;
        }
        // N, E, S and W on the ring, upright, where those ways lie on the map
        for (int k = 0; k < 4; k++) {
            double wx = k == 1 ? 1 : k == 3 ? -1 : 0, wy = k == 0 ? 1 : k == 2 ? -1 : 0;
            if (!turn.northUp) {
                double tx = turn.cos * wx - turn.sin * wy, ty = turn.sin * wx + turn.cos * wy;
                wx = tx;
                wy = ty;
            }
            int cellX = (int)Math.floor(x - wx * 62.5 + 2.5), cellY = (int)Math.floor(y + wy * 62.5 + 2.5);
            for (int shade = 0; shade < 2; shade++) {
                int qx = cellX - shade, qy = cellY - shade;
                if (qx >= 0 && qx < 5 && qy >= 0 && qy < 5 && (LETTERS[k] >> (24 - qy * 5 - qx) & 1) != 0) {
                    stone = shade == 1 ? min(stone, rgb(0.075, 0.067, 0.055)) : k == 0 ? rgb(0.906, 0.157, 0.118) : rgb(0.941, 0.910, 0.784);
                    break;
                }
            }
        }
        return argb(stone);
    }

    // --- the icons -----------------------------------------------------------------------------------

    static int iconsColour(MinimapMap map, Turn turn, double x, double y) {
        double atX = Math.floor(x) + 0.5, atY = Math.floor(y) + 0.5;
        if (Math.hypot(atX, atY) >= RADIUS) {
            return 0;
        }
        // nearest first, and so on top
        for (MinimapIcon icon : map.icons()) {
            double spotX = icon.u() + 0.5 - 64, spotY = icon.v() + 0.5 - 64;
            double[] on = turn.toScreen(spotX, -spotY);
            int pX = (int)Math.floor((x - on[0]) / 0.75 + 7.5), pY = (int)Math.floor((y + on[1]) / 0.75 + 7.5);
            if (pX < 0 || pY < 0 || pX > 14 || pY > 14) {
                continue;
            }
            int colour;
            if (pX == 0 || pY == 0 || pX == 14 || pY == 14) {
                colour = MinimapMap.edgedPixel(null, pX, pY);
            } else {
                int left = icon.slot() % MinimapFormat.SPRITES_PER_ROW * MinimapFormat.SPRITE;
                int top = MinimapFormat.ATLAS_ROW + icon.slot() / MinimapFormat.SPRITES_PER_ROW * MinimapFormat.SPRITE;
                colour = MapPalette.argb(map.colour(left + pX - 1, top + pY - 1));
            }
            if (colour != 0) {
                return colour;
            }
        }
        return 0;
    }

    // --- the dots, the hint arrow and the player -------------------------------------------------------

    static int dotsColour(MinimapMap map, Turn turn, double x, double y) {
        double atX = Math.floor(x) + 0.5, atY = Math.floor(y) + 0.5;
        if (Math.hypot(atX, atY) >= RADIUS) {
            return 0;
        }
        int pixel = 0;
        double[] m = turn.toMap(x, -y);
        int homeU = (int)Math.floor(m[0] + 64), homeV = (int)Math.floor(-m[1] + 64);
        double[] dot = null;
        boolean shadow = false;
        double halfX = (Math.floor(x * 2) + 0.5) / 2, halfY = (Math.floor(y * 2) + 0.5) / 2;
        for (int dv = -2; dv <= 2; dv++) {
            for (int du = -2; du <= 2; du++) {
                int u = homeU + du, v = homeV + dv;
                // row 0 is the settings, never inside the ring
                if (v < 1) {
                    continue;
                }
                MinimapDotColour colour = MinimapDotColour.of(map.colour(u, v));
                if (colour == null) {
                    continue;
                }
                double[] on = turn.toScreen(u + 0.5 - 64, -(v + 0.5 - 64));
                double fromX = halfX - Math.floor(on[0] * 2) / 2, fromY = halfY - Math.floor(-on[1] * 2) / 2;
                if (Math.hypot(fromX, fromY) < 1.6) {
                    double shade = fromX + fromY > 1 ? 0.75 : 1;
                    int c = colour.argb();
                    dot = rgb((c >> 16 & 0xFF) / 255.0 * shade, (c >> 8 & 0xFF) / 255.0 * shade, (c & 0xFF) / 255.0 * shade);
                } else if (Math.hypot(fromX - 0.5, fromY - 0.5) < 1.6) {
                    shadow = true;
                }
            }
        }
        if (dot != null) {
            pixel = argb(dot);
        } else if (shadow) {
            pixel = 0xFF000000;
        }
        MinimapHint hint = map.hint();
        if (hint != null) {
            int arrow = hintColour(hint, turn, halfX, halfY);
            pixel = arrow != 0 ? arrow : pixel;
        }
        if (Math.abs(atX) < 2 && Math.abs(atY) < 2) {
            pixel = 0xFFFFFFFF;
        }
        return pixel;
    }

    /** OSRS's yellow arrow: over the place pointing down on to it, or at the ring pointing the way it lies. */
    private static int hintColour(MinimapHint hint, Turn turn, double halfX, double halfY) {
        double[] on = turn.toScreen(hint.u() + 0.5 - 64, -(hint.v() + 0.5 - 64));
        double whereX = on[0], whereY = -on[1];
        double aheadX = 0, aheadY = 1, tipX = whereX, tipY = whereY;
        if (hint.away()) {
            double length = Math.hypot(whereX, whereY);
            aheadX = length == 0 ? 0 : whereX / length;
            aheadY = length == 0 ? 1 : whereY / length;
            tipX = aheadX * 58.5;
            tipY = aheadY * 58.5;
        }
        double qx = halfX - tipX, qy = halfY - tipY;
        double back = -(qx * aheadX + qy * aheadY);
        double side = qx * aheadY - qy * aheadX;
        double wide = Math.abs(side);
        double over = (wide - 0.6 * back) / 1.166;
        if (back >= 0 && (back <= 5 && over <= 0 || back <= 8.5 && wide <= 1)) {
            return side > 0 ? argb(rgb(0.8, 0.7, 0.0)) : 0xFFFFFF00;
        }
        if (back <= 5.75 && over <= 0.75 || back >= 0 && back <= 9.25 && wide <= 1.75) {
            return 0xFF000000;
        }
        return 0;
    }

    /**
     * Swaps red and blue in place, for a texture that keeps its pixels as 0xAABBGGRR, as Minecraft's
     * {@code NativeImage.setPixelRGBA} takes them.
     *
     * @param argb colours as 0xAARRGGBB
     * @return the same array, now 0xAABBGGRR
     */
    public static int[] toAbgr(int[] argb) {
        for (int i = 0; i < argb.length; i++) {
            int c = argb[i];
            argb[i] = c & 0xFF00FF00 | (c & 0xFF) << 16 | c >> 16 & 0xFF;
        }
        return argb;
    }

    // --- colours ---------------------------------------------------------------------------------------

    private static double fract(double value) {
        return value - Math.floor(value);
    }

    private static double[] rgb(double r, double g, double b) {
        return new double[] { r, g, b };
    }

    private static double[] min(double[] a, double[] b) {
        return rgb(Math.min(a[0], b[0]), Math.min(a[1], b[1]), Math.min(a[2], b[2]));
    }

    private static int argb(double[] rgb) {
        return 0xFF000000 | channel(rgb[0]) << 16 | channel(rgb[1]) << 8 | channel(rgb[2]);
    }

    private static int channel(double value) {
        return (int)Math.round(Math.max(0, Math.min(1, value)) * 255);
    }
}
