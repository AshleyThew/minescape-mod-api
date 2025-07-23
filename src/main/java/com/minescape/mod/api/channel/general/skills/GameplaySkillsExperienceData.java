package com.minescape.mod.api.channel.general.skills;

import com.minescape.mod.api.types.skills.SkillType;

/**
 * Data class representing experience gained during gameplay for a specific skill.
 */
public class GameplaySkillsExperienceData {
    private final SkillType skillType;
    private final double experienceGained;
    private final double totalExperience;

    /**
     * Creates a new GameplaySkillsExperienceData instance.
     * 
     * @param skillType the skill that gained experience
     * @param experienceGained the amount of experience gained
     * @param totalExperience the total experience after gaining
     */
    public GameplaySkillsExperienceData(SkillType skillType, double experienceGained, double totalExperience) {
        this.skillType = skillType;
        this.experienceGained = experienceGained;
        this.totalExperience = totalExperience;
    }

    /**
     * Gets the skill type that gained experience.
     * 
     * @return the skill type
     */
    public SkillType skillType() {
        return skillType;
    }

    /**
     * Gets the amount of experience gained.
     * 
     * @return the experience gained
     */
    public double experienceGained() {
        return experienceGained;
    }

    /**
     * Gets the total experience after gaining.
     * 
     * @return the total experience
     */
    public double totalExperience() {
        return totalExperience;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        GameplaySkillsExperienceData that = (GameplaySkillsExperienceData) obj;
        return Double.compare(that.experienceGained, experienceGained) == 0 
            && Double.compare(that.totalExperience, totalExperience) == 0 
            && skillType == that.skillType;
    }

    @Override
    public int hashCode() {
        int result = skillType != null ? skillType.hashCode() : 0;
        long temp = Double.doubleToLongBits(experienceGained);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(totalExperience);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "GameplaySkillsExperienceData{" + 
               "skillType=" + skillType + 
               ", experienceGained=" + experienceGained + 
               ", totalExperience=" + totalExperience + 
               '}';
    }
}
