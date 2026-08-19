# Action Values

Reference for the string values carried by `GAMEPLAY_ACTION` and `PLAYER_ACTION`:

- `GameplayActionData.action()` / `PlayerActionData.action()` — the **action** being performed
- `GameplayActionData.state()` / `PlayerActionData.state()` — what just happened to it

`GAMEPLAY_ACTION` describes the receiving player's own actions; `PLAYER_ACTION` is
the same information about another visible player, broadcast to everyone who can
see them and carrying that player's UUID.

Both are strings rather than enums on purpose: the server can add an action or a
state without requiring a new API release. **Treat any value you do not recognise
as valid** — ignore it (or display the raw name) rather than throwing.

The lists below are the complete set of names the server can send as of API `v1.0.16`.

## States (3)

| State | Meaning |
| --- | --- |
| `STARTED` | The player began the action. `durationMillis()` is how long it is expected to take, or 0 when it has no fixed length. |
| `CANCELLED` | The action ended before completing — the player moved away, ran out of materials, was interrupted, or started something else. |
| `FINISHED` | The action ran to completion and its reward was granted. |

Actions replace one another rather than stacking, so an action that is displaced by
a new one is `CANCELLED` before the new one is `STARTED`. An action that completes
and immediately repeats sends `FINISHED` and then a fresh `STARTED`, which is the
normal pattern for the repeating skills (mining, cooking, fletching and so on).

Every `STARTED` is eventually followed by exactly one `CANCELLED` or `FINISHED` for
the same action, with one exception: a player who logs out mid-action gets neither,
because the action ends with the session. `PLAYER_ACTION` has one more caveat: when
the acting player moves out of view mid-action, the terminal state never arrives,
so treat a stale action for a player you can no longer see as ended.

## Actions

Actions the server has not named yet report `UNKNOWN`. `NONE` exists in the server's
enum to mean "nothing is running" and is never sent over the channel.

### General and movement

| Action | Meaning |
| --- | --- |
| `AUTO_WALK` | Walking a path the client requested. |
| `FOLLOW` | Following another player. |
| `COMBAT` | Attacking a target. |
| `TELEPORT` | Casting or using a teleport. |
| `AFTER_TELEPORT` | The follow-up step some teleports run on arrival. |
| `GROUPING_TELEPORT` | A minigame or grouping teleport. |
| `ANTI_CHEAT` | The lockdown the anti-cheat chat challenge applies. |
| `HARVEST` | Picking a food block from the world. |
| `SEARCHING` | Searching an object or container. |
| `TRAVEL` | Travelling by boat, cart or similar. |
| `QUEST` | A quest step with its own timer. |
| `EATING` | Eating food. |
| `DRINKING` | Drinking a potion. |
| `HEALING` | Regenerating hitpoints. |

### Skilling

| Action | Skill |
| --- | --- |
| `AGILITY_OBSTACLE`, `AGILITY_SHORTCUT` | Agility |
| `CONSTRUCTION_BUILD`, `CONSTRUCTION_BUILD_MODE`, `CONSTRUCTION_REMOVE`, `CONSTRUCTION_REPAIR`, `CONSTRUCTION_SAW`, `CONSTRUCTION_LECTERN_TABLET` | Construction |
| `COOKING`, `COOKING_CHURN`, `COOKING_FIRE`, `COOKING_KNIFE`, `COOKING_PREPARE`, `COOKING_RANGE` | Cooking |
| `CRAFTING_AMETHYST`, `CRAFTING_COMBINE`, `CRAFTING_DRAGON_LEATHER`, `CRAFTING_DRAGON_LEATHER_SHIELD`, `CRAFTING_GEM_CUTTING`, `CRAFTING_GLASSBLOWING`, `CRAFTING_GOLD_MOULD`, `CRAFTING_HARD_LEATHER`, `CRAFTING_LEATHER`, `CRAFTING_LOOM`, `CRAFTING_MOLTEN_GLASS`, `CRAFTING_POTTERY_FIRING`, `CRAFTING_POTTERY_SHAPING`, `CRAFTING_SAND_BUCKET`, `CRAFTING_SHEARING`, `CRAFTING_SILVER_MOULD`, `CRAFTING_SPINNING`, `CRAFTING_STRINGING` | Crafting |
| `FARMING`, `FARMING_CLEAR`, `FARMING_COMPOST`, `FARMING_COMPOST_BIN`, `FARMING_CURE`, `FARMING_FILL_POT`, `FARMING_HARVEST`, `FARMING_PLANT`, `FARMING_RAKE`, `FARMING_WATER` | Farming |
| `FIREMAKING` | Firemaking |
| `FISHING`, `FISHING_BAIT`, `FISHING_BARBARIAN`, `FISHING_BAREHAND`, `FISHING_BIG_NET`, `FISHING_CAGE`, `FISHING_HARPOON`, `FISHING_LURE`, `FISHING_NET` | Fishing |
| `FLETCHING`, `FLETCHING_COMBINE`, `FLETCHING_CROSSBOW`, `FLETCHING_CUTTING`, `FLETCHING_STRINGING` | Fletching |
| `HERBLORE_GRIND`, `HERBLORE_MIX`, `HERBLORE_POTION`, `HERBLORE_TAR`, `HERBLORE_UNFINISHED` | Herblore |
| `HUNTER_BUTTERFLY`, `HUNTER_IMPLING`, `HUNTER_TRACKING`, `HUNTER_TRAP_BIRD_SNARE`, `HUNTER_TRAP_DEADFALL`, `HUNTER_TRAP_NET`, `HUNTER_TRAP_PITFALL` | Hunter |
| `MAGIC`, `MAGIC_ALCHEMY`, `MAGIC_ENCHANT`, `MAGIC_SUPERHEAT` | Magic |
| `MINING`, `MINING_AMETHYST`, `MINING_SHOOTING_STAR` | Mining |
| `PRAYER`, `PRAYER_ALTAR`, `PRAYER_BURY`, `PRAYER_GRIND`, `PRAYER_OFFER`, `PRAYER_SCATTER`, `PRAYER_SLIME`, `PRAYER_WORSHIP` | Prayer |
| `RUNECRAFTING_ABYSS` | Runecrafting |
| `SMELTING`, `SMITHING` | Smithing |
| `THIEVING_PICKPOCKET`, `THIEVING_STALL`, `THIEVING_WALL_SAFE` | Thieving |
| `WOODCUTTING` | Woodcutting |

### Names worth spelling out

Most action names say what they are. These do not:

| Action | What the player is doing |
| --- | --- |
| `COOKING_CHURN` | Churning milk into cream, butter or cheese. |
| `COOKING_FIRE` | Cooking on a fire, and burning an item on one. |
| `COOKING_KNIFE` | Cutting with a knife - pie shells, pizza bases, leaping fish. |
| `COOKING_PREPARE` | Preparing an ingredient that never sees heat, such as adding water to flour. |
| `COOKING_RANGE` | Cooking on a range. |
| `FARMING_CLEAR` | Clearing a patch, or digging up a stump. |
| `FARMING_COMPOST` | Emptying compost onto a patch. |
| `FARMING_COMPOST_BIN` | Filling a compost bin, or taking from one. |
| `FARMING_CURE` | Curing a diseased plant. |
| `FARMING_FILL_POT` | Filling an empty plant pot with soil. |
| `FARMING_HARVEST` | Harvesting a patch, picking fruit, or cutting branches. |
| `FISHING_BAIT` | Fishing with a fishing rod. |
| `FISHING_BARBARIAN` | Fishing with a barbarian rod. |
| `FISHING_BAREHAND` | Catching fish barehanded, after Barbarian Training. |
| `FISHING_BIG_NET` | Fishing with a big fishing net. |
| `FISHING_CAGE` | Fishing with a lobster pot. |
| `FISHING_HARPOON` | Fishing with a harpoon or barb-tail harpoon. |
| `FISHING_LURE` | Fishing with a fly fishing rod. |
| `FISHING_NET` | Fishing with a small fishing net. |
| `HERBLORE_GRIND` | Grinding an ingredient with a pestle and mortar. |
| `HERBLORE_MIX` | Making a barbarian mix. |
| `HERBLORE_POTION` | Making a finished potion. |
| `HERBLORE_TAR` | Making swamp tar. |
| `HERBLORE_UNFINISHED` | Making an unfinished potion. |
| `PRAYER_ALTAR` | Offering bones at an altar in the world. |
| `PRAYER_BURY` | Burying bones on the ground. |
| `PRAYER_GRIND` | Grinding bones at the Ectofuntus. |
| `PRAYER_OFFER` | Offering bones at a player-owned house altar. |
| `PRAYER_SCATTER` | Scattering ashes. |
| `PRAYER_SLIME` | Drawing ectoplasm from the Ectofuntus slime pool. |
| `PRAYER_WORSHIP` | Worshipping at the Ectofuntus. |

`FISHING`, `COOKING`, `FARMING` and `PRAYER` are the bare skill names, kept for anything
in those skills that has no more specific action of its own. Match them the way you would
match a name you do not recognise.
