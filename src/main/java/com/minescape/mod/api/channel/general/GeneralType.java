package com.minescape.mod.api.channel.general;

import java.util.function.Supplier;

import com.minescape.mod.api.channel.general.skills.LoginSkillsData;
import com.minescape.mod.api.channel.general.skills.GameplaySkillsExperienceData;

public enum GeneralType implements Supplier<Class<?>> {
    LOGIN_SKILLS(LoginSkillsData.class),
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
