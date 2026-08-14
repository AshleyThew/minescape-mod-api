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

The lists below are the complete set the server sends as of API `v1.0.14`.

## Core styles (4)

Every standard combat style reports one of these, and custom mobs reuse them
whenever one fits the attack they are making.

| Style | Meaning |
| --- | --- |
| `MELEE` | A melee hit (stab, slash or crush). |
| `RANGED` | A thrown or fired projectile (arrows, bolts, magical ranged attacks). |
| `MAGIC` | A cast spell, including mob-only spell attacks. |
| `DRAGONFIRE` | Dragon breath, both the melee-range breath and the fired fireball. |

`MELEE`, `RANGED` and `MAGIC` line up with the three protection prayers;
`DRAGONFIRE` is mitigated by antifire potions and dragonfire shields rather
than a prayer.

## Mob-specific styles (1)

Some mobs have attacks that none of the core styles describe. Those mobs
define their own style names, which are sent the same way.

| Style | Mob | Meaning |
| --- | --- | --- |
| `SCREECH` | Banshee | The stat-draining scream used against players not wearing earmuffs. Not blocked by protection prayers. |

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
| Aberrant spectre | `MAGIC` |

Mobs with a single combat style always send that same value, and any mob whose
handler does not report a style yet sends no style at all.

## Styles on `MOB_DEFENCE`

A defence carries the style of the hit the mob just took, so in practice it is one
of the three player combat styles — `MELEE`, `RANGED` or `MAGIC` — or `null` when
the server cannot attribute the hit to a style (damage from another mob, a ring of
recoil, poison and other non-combat sources). Handle the same full list anyway: a
mob damaging another mob can send any style above.
