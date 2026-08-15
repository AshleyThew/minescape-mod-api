# Action Values

Reference for the string values carried by `GAMEPLAY_ACTION`:

- `GameplayActionData.action()` — the **action** the player is performing
- `GameplayActionData.state()` — what just happened to it

Both are strings rather than enums on purpose: the server can add an action or a
state without requiring a new API release. **Treat any value you do not recognise
as valid** — ignore it (or display the raw name) rather than throwing.

The lists below are the complete set the server sends as of API `v1.0.15`.

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
because the action ends with the session.

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
| `HEALING` | Regenerating hitpoints. |

### Skilling

| Action | Skill |
| --- | --- |
| `AGILITY_OBSTACLE`, `AGILITY_SHORTCUT` | Agility |
| `CONSTRUCTION_BUILD`, `CONSTRUCTION_BUILD_MODE`, `CONSTRUCTION_REMOVE`, `CONSTRUCTION_REPAIR`, `CONSTRUCTION_SAW`, `CONSTRUCTION_LECTERN_TABLET` | Construction |
| `COOKING` | Cooking |
| `CRAFTING_AMETHYST`, `CRAFTING_COMBINE`, `CRAFTING_DRAGON_LEATHER`, `CRAFTING_DRAGON_LEATHER_SHIELD`, `CRAFTING_GEM_CUTTING`, `CRAFTING_GLASSBLOWING`, `CRAFTING_GOLD_MOULD`, `CRAFTING_HARD_LEATHER`, `CRAFTING_LEATHER`, `CRAFTING_LOOM`, `CRAFTING_MOLTEN_GLASS`, `CRAFTING_POTTERY_FIRING`, `CRAFTING_POTTERY_SHAPING`, `CRAFTING_SAND_BUCKET`, `CRAFTING_SHEARING`, `CRAFTING_SILVER_MOULD`, `CRAFTING_SPINNING`, `CRAFTING_STRINGING` | Crafting |
| `FARMING` | Farming |
| `FIREMAKING` | Firemaking |
| `FISHING` | Fishing |
| `FLETCHING`, `FLETCHING_COMBINE`, `FLETCHING_CROSSBOW`, `FLETCHING_CUTTING`, `FLETCHING_STRINGING` | Fletching |
| `HERBLORE` | Herblore |
| `HUNTER_BUTTERFLY`, `HUNTER_IMPLING`, `HUNTER_TRACKING`, `HUNTER_TRAP_BIRD_SNARE`, `HUNTER_TRAP_DEADFALL`, `HUNTER_TRAP_NET`, `HUNTER_TRAP_PITFALL` | Hunter |
| `MAGIC`, `MAGIC_ENCHANT` | Magic |
| `MINING`, `MINING_SHOOTING_STAR` | Mining |
| `PRAYER` | Prayer |
| `RUNECRAFTING_ABYSS` | Runecrafting |
| `SMELTING`, `SMITHING` | Smithing |
| `THIEVING_PICKPOCKET`, `THIEVING_STALL`, `THIEVING_WALL_SAFE` | Thieving |
| `WOODCUTTING` | Woodcutting |
