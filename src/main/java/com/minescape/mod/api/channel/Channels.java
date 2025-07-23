package com.minescape.mod.api.channel;

import com.minescape.mod.api.channel.general.GeneralType;
import java.util.function.Supplier;

/**
 * Enum representing different communication channels for mod data exchange.
 */
public enum Channels {
    /**
     * General channel for basic game data communication.
     */
    GENERAL("general", GeneralType.class)
    // Support for other channels in future
    ;

    private final String channelName;
    private Class<? extends Enum<? extends Supplier<Class<?>>>> channelType;

    Channels(String name, Class<? extends Enum<? extends Supplier<Class<?>>>> channelType) {
        this.channelName = "minescape_server:" + name;
        this.channelType = channelType;
    }

    /**
     * Gets the name of this channel.
     * 
     * @return the channel name
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * Gets the channel type class for this channel.
     * 
     * @return the channel type class
     */
    public Class<? extends Enum<? extends Supplier<Class<?>>>> getChannelType() {
        return channelType;
    }

}
