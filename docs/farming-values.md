# Farming Patch and Product Values

Reference for the string values carried by the farming data types:

- `GameplayFarmingPlantedData.patch()` — a **patch** name
- `GameplayFarmingPlantedData.plotData()` — updated plot state as a `FarmingPlotData`, or `null` when omitted by the server
- `FarmingPlotData.patch()` — a **patch** name
- `FarmingPlotData.product()` — a **product** name, or `null` when the plot is empty

These are strings rather than enums on purpose: the server can add a patch or a
crop without requiring a new API release. **Treat any value you do not recognise
as valid** — fall back to displaying the raw name rather than throwing.

The lists below are the complete set the server sends as of API `v1.0.12`.

## Patches

Patch names are `<LOCATION>_<PATCH TYPE>`. Grouped here by patch type, which is
not sent over the channel but is derivable from the suffix.

### Allotment (14)

| Patch |
| --- |
| `FALADOR_ALLOTMENT_NORTH` |
| `FALADOR_ALLOTMENT_SOUTH` |
| `CATHERBY_ALLOTMENT_NORTH` |
| `CATHERBY_ALLOTMENT_SOUTH` |
| `PORT_PHASMATYS_ALLOTMENT_NORTH` |
| `PORT_PHASMATYS_ALLOTMENT_SOUTH` |
| `ARDOUGNE_ALLOTMENT_NORTH` |
| `ARDOUGNE_ALLOTMENT_SOUTH` |
| `HOSIDIUS_ALLOTMENT_NORTH` |
| `HOSIDIUS_ALLOTMENT_SOUTH` |
| `FARMING_GUILD_ALLOTMENT_NORTH` |
| `FARMING_GUILD_ALLOTMENT_SOUTH` |
| `PRIFDDINAS_ALLOTMENT_NORTH` |
| `PRIFDDINAS_ALLOTMENT_SOUTH` |

### Herbs (9)

| Patch |
| --- |
| `FALADOR_HERBS` |
| `CATHERBY_HERBS` |
| `PORT_PHASMATYS_HERBS` |
| `ARDOUGNE_HERBS` |
| `HOSIDIUS_HERBS` |
| `TROLL_STRONGHOLD_HERBS` |
| `HARMONY_ISLAND_HERBS` |
| `WEISS_HERBS` |
| `FARMING_GUILD_HERBS` |

### Flowers (7)

| Patch |
| --- |
| `FALADOR_FLOWERS` |
| `CATHERBY_FLOWERS` |
| `PORT_PHASMATYS_FLOWERS` |
| `ARDOUGNE_FLOWERS` |
| `HOSIDIUS_FLOWERS` |
| `FARMING_GUILD_FLOWERS` |
| `PRIFDDINAS_FLOWERS` |

### Hops (4)

| Patch |
| --- |
| `LUMBRIDGE_HOPS` |
| `MCGRUBORS_WOOD_HOPS` |
| `YANILLE_HOPS` |
| `ENTRANA_HOPS` |

### Bushes (5)

| Patch |
| --- |
| `CHAMPIONS_GUILD_BUSHES` |
| `RIMMINGTON_BUSHES` |
| `ARDOUGNE_BUSHES` |
| `ETCETERIA_BUSHES` |
| `FARMING_GUILD_BUSHES` |

### Trees (6)

| Patch |
| --- |
| `LUMBRIDGE_TREES` |
| `VARROCK_TREES` |
| `FALADOR_TREES` |
| `TAVERLEY_TREES` |
| `TREE_GNOME_STRONGHOLD_TREES` |
| `FARMING_GUILD_TREES` |

### Fruit trees (6)

| Patch |
| --- |
| `TREE_GNOME_STRONGHOLD_FRUIT_TREES` |
| `CATHERBY_FRUIT_TREES` |
| `TREE_GNOME_MAZE_FRUIT_TREES` |
| `BRIMHAVEN_FRUIT_TREES` |
| `LLETYA_FRUIT_TREES` |
| `FARMING_GUILD_FRUIT_TREES` |

### Spirit trees (5)

| Patch |
| --- |
| `ETCETERIA_SPIRIT_TREE` |
| `BRIMHAVEN_SPIRIT_TREE` |
| `PORT_SARIM_SPIRIT_TREE` |
| `HOSIDIUS_SPIRIT_TREE` |
| `FARMING_GUILD_SPIRIT_TREE` |

### Compost bins (6)

| Patch |
| --- |
| `FALADOR_COMPOST` |
| `CATHERBY_COMPOST` |
| `PORT_PHASMATYS_COMPOST` |
| `ARDOUGNE_COMPOST` |
| `HOSIDIUS_COMPOST` |
| `PRIFDDINAS_COMPOST` |

### Special (2)

| Patch |
| --- |
| `DRAYNOR_BELLADONNA_PATCH` |
| `MORYTANIA_MUSHROOMS_PATCH` |

## Products

The farming level column is the level required to plant, included only to help
with display ordering — it is not sent over the channel.

### Allotment (8)

| Product | Level |
| --- | --- |
| `POTATO` | 1 |
| `ONION` | 5 |
| `CABBAGE` | 7 |
| `TOMATO` | 12 |
| `SWEETCORN` | 20 |
| `STRAWBERRY` | 31 |
| `WATERMELON` | 47 |
| `SNAPE_GRASS` | 61 |

### Herbs (14)

| Product | Level |
| --- | --- |
| `GUAM` | 9 |
| `MARRENTILL` | 14 |
| `TARROMIN` | 19 |
| `HARRALANDER` | 26 |
| `RANARR` | 32 |
| `TOADFLAX` | 38 |
| `IRIT` | 44 |
| `AVANTOE` | 50 |
| `KWUARM` | 56 |
| `SNAPDRAGON` | 62 |
| `CADANTINE` | 67 |
| `LANTADYME` | 73 |
| `DWARF` | 79 |
| `TORSTOL` | 85 |

### Flowers (6)

| Product | Level |
| --- | --- |
| `MARIGOLD` | 2 |
| `ROSEMARY` | 11 |
| `NASTURTIUM` | 24 |
| `WOAD` | 25 |
| `LIMPWURT` | 26 |
| `WHITE_LILY` | 58 |

### Hops (7)

| Product | Level |
| --- | --- |
| `BARLEY` | 3 |
| `HAMMERSTONE` | 4 |
| `ASGARNIAN` | 8 |
| `JUTE` | 13 |
| `YANILLIAN` | 16 |
| `KRANDORIAN` | 21 |
| `WILDBOOD` | 28 |

### Bushes (6)

| Product | Level |
| --- | --- |
| `REDBERRY` | 10 |
| `CADAVABERRY` | 22 |
| `DWELLBERRY` | 36 |
| `JANGERBERRY` | 48 |
| `WHITEBERRY` | 59 |
| `POISON_IVY` | 70 |

### Trees (5)

| Product | Level |
| --- | --- |
| `OAK` | 15 |
| `WILLOW` | 30 |
| `MAPLE` | 45 |
| `YEW` | 60 |
| `MAGIC` | 75 |

### Fruit trees (8)

| Product | Level |
| --- | --- |
| `APPLE` | 27 |
| `BANANA` | 33 |
| `ORANGE` | 39 |
| `CURRY` | 42 |
| `PINEAPPLE` | 51 |
| `PAPAYA` | 57 |
| `PALM` | 68 |
| `DRAGONFRUIT` | 81 |

### Spirit trees (1)

| Product | Level |
| --- | --- |
| `SPIRIT_TREE` | 83 |

### Special (4)

| Product | Level | Patch |
| --- | --- | --- |
| `BELLADONNA` | 63 | `DRAYNOR_BELLADONNA_PATCH` |
| `MUSHROOM` | 53 | `MORYTANIA_MUSHROOMS_PATCH` |
| `CACTUS` | 55 | — no cactus patch exists yet |
| `POTATO_CACTUS` | 64 | — no cactus patch exists yet |

### Compost bins (3)

A compost bin reports its contents through the same `product` field, so a
`*_COMPOST` patch will report one of these rather than a crop.

| Product | Level |
| --- | --- |
| `COMPOST` | 1 |
| `SUPERCOMPOST` | 1 |
| `ULTRACOMPOST` | 1 |

## Which products go in which patch

A patch only ever reports a product whose group matches the patch's own suffix —
a `*_HERBS` patch reports one of the 14 herbs, a `*_ALLOTMENT_*` patch one of the
8 allotment crops, and so on. The two exceptions are the special patches above,
which each accept exactly one product.

`CACTUS` and `POTATO_CACTUS` are defined server side but have no patch to grow
in yet, so they will not appear in `LOGIN_FARMING_PLOTS` until a cactus patch is
added.
