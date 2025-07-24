package com.minescape.mod.api.channel.general.skills;

import com.minescape.mod.api.types.skills.SkillType;
import java.util.Map;
import java.util.Objects;

/**
 * Data class representing skill information available at login.
 */
public class LoginSkillsData {
    private final Map<SkillType, Integer> levels;
    private final Map<SkillType, Double> experiences;

    /**
     * Creates a new LoginSkillsData instance.
     * 
     * @param levels      a map of skill types to their current levels
     * @param experiences a map of skill types to their current experience
     */
    public LoginSkillsData(Map<SkillType, Integer> levels, Map<SkillType, Double> experiences) {
        this.levels = Map.copyOf(levels);
        this.experiences = Map.copyOf(experiences);
    }

    /**
     * Gets the map of skill types to their current levels.
     * 
     * @return an immutable map of skill levels
     */
    public Map<SkillType, Integer> levels() {
        return levels;
    }

    /**
     * Gets the map of skill types to their current experience.
     * 
     * @return an immutable map of skill experiences
     */
    public Map<SkillType, Double> experiences() {
        return experiences;
    }

    /**
     * Gets the level for a specific skill type.
     * 
     * @param skillType the skill type
     * @return the level for the skill, or null if not present
     */
    public Integer getLevel(SkillType skillType) {
        return levels.get(skillType);
    }

    /**
     * Gets the experience for a specific skill type.
     * 
     * @param skillType the skill type
     * @return the experience for the skill, or null if not present
     */
    public Double getExperience(SkillType skillType) {
        return experiences.get(skillType);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        LoginSkillsData that = (LoginSkillsData) obj;
        return Objects.equals(levels, that.levels) && Objects.equals(experiences, that.experiences);
    }

    @Override
    public int hashCode() {
        return Objects.hash(levels, experiences);
    }

    @Override
    public String toString() {
        return "LoginSkillsData{" + "levels=" + levels + ", experiences=" + experiences + '}';
    }
}