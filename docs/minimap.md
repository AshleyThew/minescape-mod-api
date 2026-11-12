# Minimap

The MineScape minimap is drawn by the server into three ordinary maps. The resource pack's shader finds them in
invisible item frames riding the player and draws them in a corner of the screen. A client mod can draw the same
minimap itself from those maps, with everything in `com.minescape.mod.api.minimap`.

Only the ground's map is a picture. If you draw the icons' map as it is, its icons come out "all in a line at the
top": you are seeing its icon list and its sprite sheet, which the shader reads to place each icon. The dots' map
is single pixels. Use the decoders below rather than drawing the maps.

## Quick start

```java
import com.minescape.mod.api.minimap.*;

Minimap minimap = new Minimap();

// Each tick (or whenever a map updates), hand it the three maps from the client's map states.
for (MinimapLayer layer : MinimapLayer.values()) {
    byte[] colours = /* the map's 128 x 128 colour bytes for layer.mapId(), see below */;
    if (colours != null) {
        minimap.accept(layer.mapId(), colours);
    }
}

if (minimap.isShown()) {
    MinimapPlacement where = minimap.settings().placement(screenWidth, screenHeight);
    int size = (int) Math.ceil(where.side());
    int[] pixels = minimap.render(size, cameraYaw);   // 0xAARRGGBB, row by row
    // upload as a texture and draw it at where.left(), where.top(), where.side() wide
}
```

`render` draws what the pack's shader draws:

- the ground inside a stone ring with N, E, S and W on it
- OSRS's icons, upright, edged black, nearest on top
- the dots
- the yellow hint arrow
- the player's white dot

Unless the player keeps north up, the map turns with the camera. Outside the ring the image is clear.

Each pixel is worked out on its own, as the shader does, so render at the size it will be drawn: scaling a small
render up blurs it. At normal size on a 1080p screen that's about 330 x 330 pixels, all on the CPU, so redraw only
when a map changes or the camera has turned by a degree or so, not every frame.

### Getting the maps on the client

The three maps come under fixed ids (`MinimapLayer.mapId()`: 1000000, 1000001 and 1000002). The client stores
them like any other map, whether or not a frame is in view.

Fabric, 1.18.2 (Yarn):

```java
MapState state = MinecraftClient.getInstance().world.getMapState(FilledMapItem.getMapName(layer.mapId()));
byte[] colours = state == null ? null : state.colors;
```

Forge or NeoForge, 1.18.2 (Mojang names):

```java
MapItemSavedData data = Minecraft.getInstance().level.getMapData(MapItem.makeKey(layer.mapId()));
byte[] colours = data == null ? null : data.colors;
```

You can also apply each map packet's patch as it arrives, with
`minimap.update(mapId, x, y, width, height, data)`.

### Drawing the image

To make a texture of it, copy `pixels` into a `NativeImage`. Its pixel setter (`setColor` in Yarn,
`setPixelRGBA` in Mojang names) takes 0xAABBGGRR, which `MinimapRenderer.toAbgr(pixels)` converts to. Then upload it through a `NativeImageBackedTexture`
(Yarn) or `DynamicTexture` (Mojang), and draw it as a quad at the placement.

`cameraYaw` is the camera's yaw as Minecraft has it: 0 facing south, 90 west, 180 north. Use the camera's, not
the player's body, so the map follows where the player looks, including in third person.

## Reading the layers yourself

`minimap.ground()`, `minimap.icons()` and `minimap.dots()` return a `MinimapMap`, or `null` until that map has
been sent.

| Method | Layer | What it is |
| --- | --- | --- |
| `settings()` | any | `MinimapSettings`: north up, corner, size, offsets in GUI pixels |
| `groundArgb()` | ground | the ground as a 128 x 128 picture, clear outside the ring and black where unmapped |
| `icons()` | icons | `List<MinimapIcon>`: slot, `u`, `v`, nearest first, at most 24 |
| `sprite(slot)` / `spriteWithEdge(slot)` | icons | an icon's 13 x 13 sprite, or the 15 x 15 the shader draws |
| `dots()` | dots | `List<MinimapDot>`: `u`, `v` and a `MinimapDotColour` |
| `hint()` | dots | `MinimapHint` or `null`: `u`, `v` and whether it's past the map |

`u` and `v` are map pixels. The player is at 64, 64, and each pixel is a block, with `u` increasing east and `v`
south. The icons, dots and arrow should be drawn upright: if you turn the map, turn their positions, not their
sprites. An icon sprite pixel is three quarters of a map pixel, centred on the middle of the icon's pixel.

Icon slots are handed out per player as kinds of icon come into view, and the least lately shown is reused.
Always read the sprite from the current map. Don't cache it by slot.

| Dot colour | Means |
| --- | --- |
| `WHITE` | other players |
| `RED` | items on the ground, or a target past the edge of the map |
| `YELLOW` | NPCs, or the player's target |
| `GREEN` | friends |
| `ORANGE` | clan members |
| `BLUE` | team mates |

## The format

The decoders handle all of this; it's here for anyone working without them. Everything is in a map's raw colour
bytes, 128 x 128, row by row (`colours[u + v * 128]`). Numbers are written in base 4, one digit a pixel, with the
largest digit first. Bytes 16 to 19, which are the map palette's fire colour in its four shades, are the digits 0
to 3. Any other byte ends the number.

Row 0 of every layer:

| Pixels | Holds |
| --- | --- |
| 0–3 | the marks: 18, 22, *layer*, 23. The layer byte is 16 for the ground, 17 for icons and 19 for dots |
| 4 | 19 if the map keeps north up |
| 5 | the corner: 16 top left, 17 bottom left, 18 bottom right, 19 left of the hotbar, 20 right of the hotbar, anything else top right |
| 6 | the size: 16 small, 17 large, 18 extra large, anything else normal |
| 7–10 | how far it's moved right, in GUI pixels, plus 128 |
| 11–14 | how far it's moved up, in GUI pixels, plus 128 |
| 15–18 | dots map only: the hint arrow's `u`, plus 128 when the place is past the map |
| 19–22 | dots map only: the hint arrow's `v` |

Icons map, from flat index 128 (row 1): 11 digits for each icon, in the order the icons are listed. The list ends at
the first icon whose digits aren't all digits. The value packs the icon's slot and position as
`slot << 14 | u << 7 | v`. The sprites start at row 4: 13 x 13 each, nine to a row, with slot *n* at
`x = (n % 9) * 13`, `y = 4 + (n / 9) * 13`. Bytes 0 to 3 are clear.

Dots map: each marker is one pixel, at its `u`, `v`, in byte 16 to 21 (see `MinimapDotColour`). Everything else
is 0.

The ground and the icons and dots are cut off 60 map pixels from the middle. The stone ring runs from there out
to 65.
