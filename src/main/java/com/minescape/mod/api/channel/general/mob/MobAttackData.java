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

    /**
     * Creates a new MobAttackData instance.
     *
     * @param uuid the UUID of the attacking mob
     */
    public MobAttackData(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets the UUID of the attacking mob.
     *
     * @return the UUID of the attacking mob
     */
    public UUID uuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        MobAttackData that = (MobAttackData) obj;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "MobAttackData{" + "uuid=" + uuid + '}';
    }
}
