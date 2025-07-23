package com.minescape.mod.api.channel.general.skills;

import com.minescape.mod.api.types.skills.SkillType;

/**
 * Data class representing skill information available at login.
 */
public class LoginSkillsData {
    private final SkillType skillType;
    private final int level;
    private final double experience;

    /**
     * Creates a new LoginSkillsData instance.
     * 
     * @param skillType the skill type
     * @param level the current level of the skill
     * @param experience the current experience in the skill
     */
    public LoginSkillsData(SkillType skillType, int level, double experience) {
        this.skillType = skillType;
        this.level = level;
        this.experience = experience;
    }

    /**
     * Gets the skill type.
     * 
     * @return the skill type
     */
    public SkillType skillType() {
        return skillType;
    }

    /**
     * Gets the current level of the skill.
     * 
     * @return the skill level
     */
    public int level() {
        return level;
    }

    /**
     * Gets the current experience in the skill.
     * 
     * @return the skill experience
     */
    public double experience() {
        return experience;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        LoginSkillsData that = (LoginSkillsData) obj;
        return level == that.level && Double.compare(that.experience, experience) == 0 && skillType == that.skillType;
    }

    @Override
    public int hashCode() {
        int result = skillType != null ? skillType.hashCode() : 0;
        result = 31 * result + level;
        long temp = Double.doubleToLongBits(experience);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "LoginSkillsData{" + "skillType=" + skillType + ", level=" + level + ", experience=" + experience + '}';
    }
}