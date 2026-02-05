package com.minescape.mod.api.channel.general.target;

import java.util.Objects;
import java.util.UUID;

/**
 * Data class representing the player's current target mob.
 */
public class TargetData {
    private final UUID uuid;
    private final int currentHp;
    private final int totalHp;

    /**
     * Creates a new TargetData instance.
     *
     * @param uuid      the UUID of the target mob
     * @param currentHp the current hit points of the target mob
     * @param totalHp   the total hit points of the target mob
     */
    public TargetData(UUID uuid, int currentHp, int totalHp) {
        this.uuid = uuid;
        this.currentHp = currentHp;
        this.totalHp = totalHp;
    }

    /**
     * Gets the UUID of the target mob.
     *
     * @return the UUID of the target mob
     */
    public UUID uuid() {
        return uuid;
    }

    /**
     * Gets the current hit points of the target mob.
     *
     * @return the current HP of the target mob
     */
    public int currentHp() {
        return currentHp;
    }

    /**
     * Gets the total hit points of the target mob.
     *
     * @return the total HP of the target mob
     */
    public int totalHp() {
        return totalHp;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        TargetData that = (TargetData) obj;
        return currentHp == that.currentHp && totalHp == that.totalHp && Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, currentHp, totalHp);
    }

    @Override
    public String toString() {
        return "TargetData{" + "uuid=" + uuid + ", currentHp=" + currentHp + ", totalHp=" + totalHp + '}';
    }
}
