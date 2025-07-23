package com.minescape.mod.api.channel.general.skills;

import com.minescape.mod.api.types.skills.SkillType;

public class LoginSkillsData {
    private final SkillType skillType;
    private final int level;
    private final double experience;

    public LoginSkillsData(SkillType skillType, int level, double experience) {
        this.skillType = skillType;
        this.level = level;
        this.experience = experience;
    }

    public SkillType skillType() {
        return skillType;
    }

    public int level() {
        return level;
    }

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