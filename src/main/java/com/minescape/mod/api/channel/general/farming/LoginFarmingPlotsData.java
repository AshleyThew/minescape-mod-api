package com.minescape.mod.api.channel.general.farming;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Data class representing the state of every farming plot the player owns,
 * sent when the player joins.
 */
public class LoginFarmingPlotsData {
    private final Map<String, FarmingPlotData> plots;

    /**
     * Creates a new LoginFarmingPlotsData instance.
     *
     * @param plots a map of farming plot names to their current state
     */
    public LoginFarmingPlotsData(Map<String, FarmingPlotData> plots) {
        this.plots = Map.copyOf(plots);
    }

    /**
     * Gets the map of farming plot names to their current state.
     *
     * @return a map of farming plots, never null
     */
    public Map<String, FarmingPlotData> plots() {
        return plots == null ? Map.of() : plots;
    }

    /**
     * Gets the state of a specific farming plot.
     *
     * @param patch the name of the farming plot
     * @return the plot state, or null if the player has no state for that plot
     */
    public FarmingPlotData getPlot(String patch) {
        return plots().get(patch);
    }

    /**
     * Gets every plot that currently has a product planted in it.
     *
     * @return the planted plots, never null
     */
    public Collection<FarmingPlotData> plantedPlots() {
        return plots().values().stream().filter(FarmingPlotData::isPlanted).toList();
    }

    /**
     * Checks whether a specific farming plot has a product planted in it.
     *
     * @param patch the name of the farming plot
     * @return true if the plot has a product planted, false otherwise
     */
    public boolean isPlanted(String patch) {
        FarmingPlotData plot = getPlot(patch);
        return plot != null && plot.isPlanted();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        LoginFarmingPlotsData that = (LoginFarmingPlotsData) obj;
        return Objects.equals(plots, that.plots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plots);
    }

    @Override
    public String toString() {
        return "LoginFarmingPlotsData{" + "plots=" + plots + '}';
    }
}
