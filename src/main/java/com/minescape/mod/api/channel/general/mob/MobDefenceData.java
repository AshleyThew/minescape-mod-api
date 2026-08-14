package com.minescape.mod.api.channel.general.mob;

import java.util.Objects;
import java.util.UUID;

/**
 * Data class representing a mob defending an attack made against it.
 * <p>
 * This is the counterpart to {@link MobAttackData}: where that reports the mob
 * swinging at a player, this reports the mob on the receiving end of a hit.
 * Like the attack, it is broadcast to every player that can see the mob, not
 * just the player who landed the hit, so onlookers can react to the defence as
 * well.
 */
public class MobDefenceData {
    private final UUID uuid;
    private final String style;
    private final int damage;

    /**
     * Creates a new MobDefenceData instance with no style and no damage.
     *
     * @param uuid the UUID of the defending mob
     */
    public MobDefenceData(UUID uuid) {
        this(uuid, null, 0);
    }

    /**
     * Creates a new MobDefenceData instance with no damage.
     *
     * @param uuid  the UUID of the defending mob
     * @param style the style of the attack that was defended, or {@code null}
     *              when unknown
     */
    public MobDefenceData(UUID uuid, String style) {
        this(uuid, style, 0);
    }

    /**
     * Creates a new MobDefenceData instance.
     *
     * @param uuid   the UUID of the defending mob
     * @param style  the style of the attack that was defended, or {@code null}
     *               when unknown
     * @param damage the damage the mob took from the defended attack, 0 when the
     *               hit did nothing
     */
    public MobDefenceData(UUID uuid, String style, int damage) {
        this.uuid = uuid;
        this.style = style;
        this.damage = damage;
    }

    /**
     * Gets the UUID of the defending mob.
     *
     * @return the UUID of the defending mob
     */
    public UUID uuid() {
        return uuid;
    }

    /**
     * Gets the style of the attack the mob defended, such as {@code MELEE},
     * {@code RANGED} or {@code MAGIC}.
     * <p>
     * This is the same vocabulary {@link MobAttackData#style()} uses, describing
     * the incoming attack rather than one the mob made. It is a string rather
     * than an enum on purpose: the server can add a style without requiring a
     * new API release. Treat any value you do not recognise as valid, and expect
     * {@code null} from older servers or for attacks whose style is unknown.
     *
     * @return the attack style name, or {@code null} when unknown
     * @see <a href=
     *      "https://github.com/AshleyThew/minescape-mod-api/blob/main/docs/mob-attack-styles.md">Mob
     *      attack style values</a>
     */
    public String style() {
        return style;
    }

    /**
     * Gets the damage the mob took from the attack it defended.
     * <p>
     * Zero means the mob took nothing from the hit, either because the attack
     * missed or because the server did not report a damage figure.
     *
     * @return the damage taken, or 0 when the hit did nothing
     */
    public int damage() {
        return damage;
    }

    /**
     * Checks whether the mob came out of the defence unharmed.
     *
     * @return true if the defended attack dealt no damage, false otherwise
     */
    public boolean blocked() {
        return damage <= 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        MobDefenceData that = (MobDefenceData) obj;
        return damage == that.damage && Objects.equals(uuid, that.uuid) && Objects.equals(style, that.style);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, style, damage);
    }

    @Override
    public String toString() {
        return "MobDefenceData{" + "uuid=" + uuid + ", style=" + style + ", damage=" + damage + '}';
    }
}
