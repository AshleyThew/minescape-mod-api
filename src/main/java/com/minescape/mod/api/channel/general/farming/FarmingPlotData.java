package com.minescape.mod.api.channel.general.farming;

import java.util.Objects;

/**
 * Data class representing the current state of a single farming plot.
 * <p>
 * The plot and the product growing in it are identified by name (for example
 * {@code CATHERBY_HERBS} and {@code RANARR}) rather than by enum so new patches
 * and products can be added server side without requiring an API release.
 */
public class FarmingPlotData {
    private final String patch;
    private final String product;
    private final long nextGrowthMillis;

    /**
     * Creates a new FarmingPlotData instance.
     *
     * @param patch            the name of the farming plot
     * @param product          the name of the product planted in the plot, or
     *                         null if the plot is empty
     * @param nextGrowthMillis the milliseconds remaining until the next growth
     *                         stage, or 0 if the plot is not growing
     */
    public FarmingPlotData(String patch, String product, long nextGrowthMillis) {
        this.patch = patch;
        this.product = product;
        this.nextGrowthMillis = nextGrowthMillis;
    }

    /**
     * Gets the name of the farming plot.
     *
     * @return the farming plot name
     */
    public String patch() {
        return patch;
    }

    /**
     * Gets the name of the product planted in the plot.
     *
     * @return the product name, or null if the plot is empty
     */
    public String product() {
        return product;
    }

    /**
     * Gets the milliseconds remaining until the next growth stage, measured from
     * when the message was sent.
     *
     * @return the milliseconds until the next growth stage, or 0 if the plot is
     *         not growing
     */
    public long nextGrowthMillis() {
        return nextGrowthMillis;
    }

    /**
     * Checks whether a product is planted in the plot.
     *
     * @return true if a product is planted, false otherwise
     */
    public boolean isPlanted() {
        return product != null;
    }

    /**
     * Checks whether the plot is still growing.
     *
     * @return true if the plot has a product with a growth stage pending, false
     *         otherwise
     */
    public boolean isGrowing() {
        return isPlanted() && nextGrowthMillis > 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        FarmingPlotData that = (FarmingPlotData) obj;
        return nextGrowthMillis == that.nextGrowthMillis
                && Objects.equals(patch, that.patch)
                && Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patch, product, nextGrowthMillis);
    }

    @Override
    public String toString() {
        return "FarmingPlotData{" +
                "patch=" + patch +
                ", product=" + product +
                ", nextGrowthMillis=" + nextGrowthMillis +
                '}';
    }
}
