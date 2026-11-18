# Mob Attack Style Values

Reference for the string values carried by the mob data types:

- `MobAttackData.style()` — the **attack style** the mob used, or `null` when unknown
- `MobDefenceData.style()` — the **attack style** the mob defended against, or `null`
  when unknown

Both draw from the same list. On `MOB_ATTACK` the style describes an attack the mob
made; on `MOB_DEFENCE` it describes the incoming attack it took, so a player hitting a
mob with a scimitar produces `MELEE` on the defence.

These are strings rather than enums on purpose: the server can add a style
without requiring a new API release. **Treat any value you do not recognise as
valid** — fall back to ignoring the style (or displaying the raw name) rather
than throwing. Older servers, and attacks whose style the server does not know,
send no style at all, so `style()` must also tolerate `null`.

The lists below are the complete set the server sends as of API `v1.0.19`.

`MOB_ATTACK` goes out when the mob makes the attack, not when it lands. For an
attack that takes time to arrive — a projectile, the marble gargoyle's stone
ball, one of Brutus's telegraphed specials — that is the moment it is thrown or
the warning starts. An attack of several hits sends one `MOB_ATTACK` per hit as
each is made: the greater abyssal demon's flurry is four `MELEE` a game tick
apart. An attack that kills its target is sent like any other.

## Core styles (4)

Every standard combat style reports one of these, and custom mobs reuse them
whenever one fits the attack they are making.

| Style | Meaning |
| --- | --- |
| `MELEE` | A melee hit (stab, slash or crush). |
| `RANGED` | A thrown or fired projectile (arrows, bolts, magical ranged attacks). |
| `MAGIC` | A cast spell, including mob-only spell attacks and stat-draining curses. |
| `DRAGONFIRE` | Dragon breath, both the melee-range breath and the fired fireball. |

`MELEE`, `RANGED` and `MAGIC` line up with the three protection prayers;
`DRAGONFIRE` is mitigated by antifire potions and dragonfire shields rather
than a prayer.

## Mob-specific styles (7)

Some mobs have attacks that none of the core styles describe: they ignore
protection prayers, call for a particular item or reaction, or do something
other than damage. Those get their own style names, sent the same way. A special
that is only a stronger melee, ranged or magic hit keeps the core style.

Each has a closest core style, which is what to treat it as if you have nothing
particular to do for it. (The server's own animated mobs fall back the same way
when a mob has no animation for the style.)

| Style | Mob | Meaning | Closest core style |
| --- | --- | --- | --- |
| `SCREECH` | Banshee | The stat-draining scream used against players not wearing earmuffs. Not blocked by protection prayers. | `MELEE` |
| `ROT` | Mort Myre ghast (invisible) | Turns a piece of food in the player's inventory into rotten food. No damage; prevented only by a blessed silver sickle. | `MELEE` |
| `STENCH` | Aberrant spectre, abhorrent spectre | The stench used against players not wearing a nose peg: a fixed hit no prayer blocks, plus a drain of the combat stats and Prayer. | `MAGIC` |
| `SUMMON` | Nechryarch | Calls up three chaotic death spawns around the player in place of an attack. | `MAGIC` |
| `TELEPORT` | Abyssal demon | Teleports itself next to the player, or the player next to it, in place of an attack. | `MELEE` |
| `CHARGE` | Brutus | Growls, then dashes at where the player stood; hits whoever is still in the way, whatever their prayer. | `MELEE` |
| `STOMP` | Brutus | Snorts, then stamps on the player's spot and up to two more around it; hits whoever is still on one, whatever their prayer. | `MELEE` |

## Which mobs send which styles

A mob that mixes attacks reports the style of the attack it actually used, per
attack. Some examples of mobs that send more than one style:

| Mob | Styles |
| --- | --- |
| Chromatic dragons (green, blue, red, black) | `MELEE`, `DRAGONFIRE` |
| Metallic dragons (bronze, iron, steel) | `MELEE`, `MAGIC`, `DRAGONFIRE` |
| Brutal and mithril dragons | `MELEE`, `MAGIC`, `RANGED`, `DRAGONFIRE` |
| Waterfiend | `MAGIC`, `RANGED` |
| Banshee | `SCREECH`, `MELEE` |
| Aberrant and abhorrent spectres | `STENCH` without a nose peg, `MAGIC` with one |
| Mort Myre ghast | `ROT` and `MELEE` (its touch) while invisible, `MELEE` once visible |
| Nechryarch | `MELEE`, `SUMMON` |
| Abyssal demon | `MELEE`, `TELEPORT` |
| Brutus | `MELEE`, `CHARGE`, `STOMP` |
| Marble gargoyle | `MELEE`, `RANGED`, `MAGIC` (the stone ball that traps a player still on its tile) |

Mobs with a single combat style always send that same value, and any mob whose
handler does not report a style yet sends no style at all.

## Styles on `MOB_DEFENCE`

A defence carries the style of the hit the mob just took, so in practice it is one
of the three player combat styles — `MELEE`, `RANGED` or `MAGIC` — or `null` when
the server cannot attribute the hit to a style (damage from another mob, a ring of
recoil, poison and other non-combat sources). Handle the same full list anyway: a
mob damaging another mob can send any style above.
