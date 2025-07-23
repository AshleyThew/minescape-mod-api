package com.minescape.mod.api.channel;

import com.minescape.mod.api.channel.general.GeneralType;
import java.util.function.Supplier;

public enum Channels {
    GENERAL("general", GeneralType.class)
    // Support for other channels in future
    ;

    private final String name;
    private Class<? extends Enum<? extends Supplier<Class<?>>>> channelType;

    Channels(String name, Class<? extends Enum<? extends Supplier<Class<?>>>> channelType) {
        this.name = "minescape_server:" + name;
        this.channelType = channelType;
    }

    public String getName() {
        return name;
    }

    public Class<? extends Enum<? extends Supplier<Class<?>>>> getChannelType() {
        return channelType;
    }

}
