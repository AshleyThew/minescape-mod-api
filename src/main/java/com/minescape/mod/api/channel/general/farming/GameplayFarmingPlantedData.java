package com.minescape.mod.api.channel.general.farming;

import java.util.Objects;

/**
 * Data class representing a farming plot that has just been seeded.
 * <p>
 * The plot is identified by its name (for example {@code CATHERBY_HERBS}).
 * An optional {@link FarmingPlotData} snapshot of the updated plot state may
 * also be included.
 */
public class GameplayFarmingPlantedData {
    private final String patch;
    private final FarmingPlotData plotData;

    /**
     * Creates a new GameplayFarmingPlantedData instance without updated plot data.
     *
     * @param patch the name of the farming plot that was seeded
     */
    public GameplayFarmingPlantedData(String patch) {
        this(patch, null);
    }

    /**
     * Creates a new GameplayFarmingPlantedData instance with updated plot data.
     *
     * @param patch    the name of the farming plot that was seeded
     * @param plotData the updated state of the farming plot, or null if not provided
     */
    public GameplayFarmingPlantedData(String patch, FarmingPlotData plotData) {
        this.patch = patch;
        this.plotData = plotData;
    }

    /**
     * Gets the name of the farming plot that was seeded.
     *
     * @return the farming plot name
     */
    public String patch() {
        return patch;
    }

    /**
     * Gets the updated state of the farming plot after planting.
     *
     * @return the updated plot data, or null if not provided
     */
    public FarmingPlotData plotData() {
        return plotData;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        GameplayFarmingPlantedData that = (GameplayFarmingPlantedData) obj;
        return Objects.equals(patch, that.patch)
                && Objects.equals(plotData, that.plotData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patch, plotData);
    }

    @Override
    public String toString() {
        return "GameplayFarmingPlantedData{" + "patch=" + patch + ", plotData=" + plotData + '}';
    }
}
