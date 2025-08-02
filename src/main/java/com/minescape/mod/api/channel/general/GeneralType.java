package com.minescape.mod.api.channel.general;

import java.util.function.Supplier;

import com.minescape.mod.api.channel.general.skills.LoginSkillsData;
import com.minescape.mod.api.channel.general.skills.GameplaySkillsExperienceData;
import com.minescape.mod.api.channel.general.skills.GameplaySkillEffectData;

/**
 * Enum representing different types of data that can be sent through the
 * general channel.
 */
public enum GeneralType implements Supplier<Class<?>> {
    /**
     * Data type for login skills information.
     */
    LOGIN_SKILLS(LoginSkillsData.class),
    /**
     * Data type for gameplay skills experience information.
     */
    GAMEPLAY_SKILLS_EXPERIENCE(GameplaySkillsExperienceData.class),
    /**
     * Data type for gameplay skill effect changes (modifiers from potions, spells, etc.).
     */
    GAMEPLAY_SKILL_EFFECT(GameplaySkillEffectData.class),;

    private final Class<?> dataClass;

    GeneralType(Class<?> dataClass) {
        this.dataClass = dataClass;
    }

    @Override
    public Class<?> get() {
        return dataClass;
    }

}
