package com.minescape.mod.api.channel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.function.Supplier;

/**
 * Handler for processing channel data from JSON strings and creating
 * appropriate data objects.
 * 
 * @param <T> The enum type that implements Supplier&lt;Class&lt;?&gt;&gt;
 */
public class ChannelDataHandler<T extends Enum<T> & Supplier<Class<?>>> {

    private static final Gson gson = new Gson();
    private final Channels channel;
    private final Class<T> typeClass;

    /**
     * Constructor for ChannelDataHandler.
     * 
     * @param channel   The channel this handler will process
     * @param typeClass The type class associated with the channel
     */
    public ChannelDataHandler(Channels channel, Class<T> typeClass) {
        this.channel = channel;
        this.typeClass = typeClass;
    }

    /**
     * Processes a JSON object for the configured channel and returns the
     * appropriate data object.
     * 
     * @param jsonObject JSON object in format: {"type":"LOGIN_SKILLS",
     *                   "data":object}
     * @return The created data object based on the type
     * @throws IllegalArgumentException if the JSON format is invalid or type is not
     *                                  supported
     */
    public Object getData(JsonObject jsonObject) {
        try {
            // Extract type and data
            if (!jsonObject.has("type") || !jsonObject.has("data")) {
                throw new IllegalArgumentException("JSON must contain 'type' and 'data' fields");
            }

            T type = getType(jsonObject);
            JsonObject data = jsonObject.get("data").getAsJsonObject();

            // Handle the data for this channel
            return getData(type, data);

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to process channel data: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts the type from a JSON object without processing the data.
     * 
     * @param jsonObject JSON object in format: {"type":"LOGIN_SKILLS",
     *                   "data":object}
     * @return The enum type from the JSON
     * @throws IllegalArgumentException if the JSON format is invalid or type field
     *                                  is missing
     */
    public T getType(JsonObject jsonObject) {
        try {
            // Check if type field exists
            if (!jsonObject.has("type")) {
                throw new IllegalArgumentException("JSON must contain 'type' field");
            }

            String typeString = jsonObject.get("type").getAsString();

            // Find the enum constant that matches the type string
            T[] enumConstants = typeClass.getEnumConstants();
            for (T enumConstant : enumConstants) {
                if (enumConstant.name().equals(typeString)) {
                    return enumConstant;
                }
            }

            throw new IllegalArgumentException("Unknown type: " + typeString + " for channel: " + channel);

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to extract type from JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Handles data for the configured channel.
     * 
     * @param type The enum type from the channel
     * @param data The data object from the JSON
     * @return The created data object
     */
    private Object getData(T type, JsonObject data) {
        try {
            // Get the class for this type
            Class<?> dataClass = type.get();

            // Deserialize the data to the appropriate class
            return gson.fromJson(data, dataClass);

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create object for type " + type + ": " + e.getMessage(), e);
        }
    }

    /**
     * Generic method to handle channel data with type-safe casting.
     * 
     * @param <R>           The expected return type
     * @param jsonObject    The JSON object to process
     * @param expectedClass The expected class of the result
     * @return The created data object cast to the expected type
     */
    @SuppressWarnings("unchecked")
    public <R> R getData(JsonObject jsonObject, Class<R> expectedClass) {
        Object result = getData(jsonObject);

        if (!expectedClass.isInstance(result)) {
            throw new IllegalArgumentException(
                    "Expected " + expectedClass.getSimpleName() + " but got " + result.getClass().getSimpleName());
        }

        return (R) result;
    }
}
