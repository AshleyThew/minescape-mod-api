package com.minescape.mod.api.channel.general.skills;

import com.minescape.mod.api.types.skills.SkillType;
import java.util.Map;
import java.util.Objects;

/**
 * Data class representing skill effect information available at login.
 * This includes current modifiers for each skill.
 */
public class LoginSkillEffectData {
    private final Map<SkillType, Integer> modifiers;

    /**
     * Creates a new LoginSkillEffectData instance.
     * 
     * @param modifiers a map of skill types to their current modifier values
     */
    public LoginSkillEffectData(Map<SkillType, Integer> modifiers) {
        this.modifiers = Map.copyOf(modifiers);
    }

    /**
     * Gets the map of skill types to their current modifier values.
     * 
     * @return an immutable map of skill modifiers
     */
    public Map<SkillType, Integer> modifiers() {
        return modifiers;
    }

    /**
     * Gets the modifier for a specific skill type.
     * 
     * @param skillType the skill type
     * @return the modifier for the skill, or null if not present
     */
    public Integer getModifier(SkillType skillType) {
        return modifiers.get(skillType);
    }

    /**
     * Checks if a skill has an active effect (non-zero modifier).
     * 
     * @param skillType the skill type to check
     * @return true if the skill has an active effect, false otherwise
     */
    public boolean hasEffect(SkillType skillType) {
        Integer modifier = modifiers.get(skillType);
        return modifier != null && modifier != 0;
    }

    /**
     * Checks if any skill has an active effect.
     * 
     * @return true if any skill has an active effect, false otherwise
     */
    public boolean hasAnyEffect() {
        return modifiers.values().stream().anyMatch(modifier -> modifier != 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        LoginSkillEffectData that = (LoginSkillEffectData) obj;
        return Objects.equals(modifiers, that.modifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modifiers);
    }

    @Override
    public String toString() {
        return "LoginSkillEffectData{" + "modifiers=" + modifiers + '}';
    }
}
