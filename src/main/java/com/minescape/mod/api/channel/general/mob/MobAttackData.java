package com.minescape.mod.api.channel.general.mob;

import java.util.Objects;
import java.util.UUID;

/**
 * Data class representing a mob attacking a player.
 * <p>
 * This is broadcast to every player that can see the mob, not just the player
 * being attacked, so onlookers can react to the attack as well.
 */
public class MobAttackData {
    private final UUID uuid;
    private final String style;

    /**
     * Creates a new MobAttackData instance with no attack style.
     *
     * @param uuid the UUID of the attacking mob
     */
    public MobAttackData(UUID uuid) {
        this(uuid, null);
    }

    /**
     * Creates a new MobAttackData instance.
     *
     * @param uuid  the UUID of the attacking mob
     * @param style the attack style used, or {@code null} when unknown
     */
    public MobAttackData(UUID uuid, String style) {
        this.uuid = uuid;
        this.style = style;
    }

    /**
     * Gets the UUID of the attacking mob.
     *
     * @return the UUID of the attacking mob
     */
    public UUID uuid() {
        return uuid;
    }

    /**
     * Gets the attack style the mob used, such as {@code MELEE}, {@code RANGED},
     * {@code MAGIC} or {@code DRAGONFIRE}.
     * <p>
     * This is a string rather than an enum on purpose: the server can add a
     * style without requiring a new API release. Treat any value you do not
     * recognise as valid, and expect {@code null} from older servers or for
     * attacks whose style is unknown.
     *
     * @return the attack style name, or {@code null} when unknown
     * @see <a href=
     *      "https://github.com/AshleyThew/minescape-mod-api/blob/main/docs/mob-attack-styles.md">Mob
     *      attack style values</a>
     */
    public String style() {
        return style;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        MobAttackData that = (MobAttackData) obj;
        return Objects.equals(uuid, that.uuid) && Objects.equals(style, that.style);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, style);
    }

    @Override
    public String toString() {
        return "MobAttackData{" + "uuid=" + uuid + ", style=" + style + '}';
    }
}
