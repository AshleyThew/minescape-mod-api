package com.minescape.mod.api.channel.general;

import java.util.function.Supplier;

import com.minescape.mod.api.channel.general.skills.LoginSkillsData;
import com.minescape.mod.api.channel.general.skills.GameplaySkillsExperienceData;

/**
 * Enum representing different types of data that can be sent through the general channel.
 */
public enum GeneralType implements Supplier<Class<?>> {
    /**
     * Data type for login skills information.
     */
    LOGIN_SKILLS(LoginSkillsData.class),
    /**
     * Data type for gameplay skills experience information.
     */
    GAMEPLAY_SKILLS_EXPERIENCE(GameplaySkillsExperienceData.class),;

    private final Class<?> dataClass;

    GeneralType(Class<?> dataClass) {
        this.dataClass = dataClass;
    }

    @Override
    public Class<?> get() {
        return dataClass;
    }

}
