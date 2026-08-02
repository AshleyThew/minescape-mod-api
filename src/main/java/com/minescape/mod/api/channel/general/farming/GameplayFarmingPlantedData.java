package com.minescape.mod.api.channel.general.farming;

import java.util.Objects;

/**
 * Data class representing a farming plot that has just been seeded.
 * <p>
 * The plot is identified by its name (for example {@code CATHERBY_HERBS}).
 */
public class GameplayFarmingPlantedData {
    private final String patch;

    /**
     * Creates a new GameplayFarmingPlantedData instance.
     *
     * @param patch the name of the farming plot that was seeded
     */
    public GameplayFarmingPlantedData(String patch) {
        this.patch = patch;
    }

    /**
     * Gets the name of the farming plot that was seeded.
     *
     * @return the farming plot name
     */
    public String patch() {
        return patch;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        GameplayFarmingPlantedData that = (GameplayFarmingPlantedData) obj;
        return Objects.equals(patch, that.patch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patch);
    }

    @Override
    public String toString() {
        return "GameplayFarmingPlantedData{" + "patch=" + patch + '}';
    }
}
