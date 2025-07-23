package com.minescape.mod.api.channel.general.skills;

import com.minescape.mod.api.types.skills.SkillType;

public class ExperienceReceivedData {
    private final SkillType skillType;
    private final double experienceGained;
    private final double totalExperience;

    public ExperienceReceivedData(SkillType skillType, double experienceGained, double totalExperience) {
        this.skillType = skillType;
        this.experienceGained = experienceGained;
        this.totalExperience = totalExperience;
    }

    public SkillType skillType() {
        return skillType;
    }

    public double experienceGained() {
        return experienceGained;
    }

    public double totalExperience() {
        return totalExperience;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ExperienceReceivedData that = (ExperienceReceivedData) obj;
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
        return "ExperienceReceivedData{" + 
               "skillType=" + skillType + 
               ", experienceGained=" + experienceGained + 
               ", totalExperience=" + totalExperience + 
               '}';
    }
}
