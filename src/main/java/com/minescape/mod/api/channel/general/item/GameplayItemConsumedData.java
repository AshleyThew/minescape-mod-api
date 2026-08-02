package com.minescape.mod.api.channel.general.item;

import java.util.Objects;

/**
 * Data class representing an item or potion the player consumed.
 * <p>
 * The item is identified by its item field name (for example
 * {@code SHARK} or {@code SUPER_ATTACK_4}) rather than by numeric id, so the
 * client can match against a stable, human readable identifier.
 */
public class GameplayItemConsumedData {
    private final String item;

    /**
     * Creates a new GameplayItemConsumedData instance.
     *
     * @param item the item field name of the consumed item
     */
    public GameplayItemConsumedData(String item) {
        this.item = item;
    }

    /**
     * Gets the item field name of the consumed item.
     *
     * @return the item field name
     */
    public String item() {
        return item;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        GameplayItemConsumedData that = (GameplayItemConsumedData) obj;
        return Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item);
    }

    @Override
    public String toString() {
        return "GameplayItemConsumedData{" + "item=" + item + '}';
    }
}
