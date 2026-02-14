package com.minescape.mod.api.channel.general.target;

import java.util.Objects;
import java.util.UUID;

/**
 * Data class representing when the player kills their target mob.
 */
public class PlayerTargetDeathData {
    private final UUID uuid;

    /**
     * Creates a new PlayerTargetDeathData instance.
     *
     * @param uuid the UUID of the killed target mob
     */
    public PlayerTargetDeathData(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets the UUID of the killed target mob.
     *
     * @return the UUID of the killed target mob
     */
    public UUID uuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PlayerTargetDeathData that = (PlayerTargetDeathData) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "PlayerTargetDeathData{" +
                "uuid=" + uuid +
                '}';
    }
}
