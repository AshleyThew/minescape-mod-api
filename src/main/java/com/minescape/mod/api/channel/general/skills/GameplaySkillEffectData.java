package com.minescape.mod.api.channel.general.skills;

import com.minescape.mod.api.types.skills.SkillType;

/**
 * Data class representing a skill effect change, such as when a potion or
 * other temporary effect modifies a skill's effective level.
 */
public class GameplaySkillEffectData {
    private final SkillType skillType;
    private final int previousModifier;
    private final int newModifier;
    private final int skillLevel;

    /**
     * Creates a new GameplaySkillEffectData instance.
     * 
     * @param skillType        the skill that had its effect modified
     * @param previousModifier the previous modifier value
     * @param newModifier      the new modifier value
     * @param skillLevel       the base skill level (without modifiers)
     */
    public GameplaySkillEffectData(SkillType skillType, int previousModifier, int newModifier, int skillLevel) {
        this.skillType = skillType;
        this.previousModifier = previousModifier;
        this.newModifier = newModifier;
        this.skillLevel = skillLevel;
    }

    /**
     * Gets the skill type that had its effect modified.
     * 
     * @return the skill type
     */
    public SkillType skillType() {
        return skillType;
    }

    /**
     * Gets the previous modifier value.
     * 
     * @return the previous modifier
     */
    public int previousModifier() {
        return previousModifier;
    }

    /**
     * Gets the new modifier value.
     * 
     * @return the new modifier
     */
    public int newModifier() {
        return newModifier;
    }

    /**
     * Gets the base skill level (without modifiers).
     * 
     * @return the skill level
     */
    public int skillLevel() {
        return skillLevel;
    }

    /**
     * Gets the previous effective level (base level + previous modifier).
     * 
     * @return the previous effective level
     */
    public int previousEffectiveLevel() {
        return skillLevel + previousModifier;
    }

    /**
     * Gets the new effective level (base level + new modifier).
     * 
     * @return the new effective level
     */
    public int newEffectiveLevel() {
        return skillLevel + newModifier;
    }

    /**
     * Gets the change in modifier (new - previous).
     * 
     * @return the modifier change
     */
    public int modifierChange() {
        return newModifier - previousModifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        GameplaySkillEffectData that = (GameplaySkillEffectData) obj;
        return previousModifier == that.previousModifier 
                && newModifier == that.newModifier 
                && skillLevel == that.skillLevel 
                && skillType == that.skillType;
    }

    @Override
    public int hashCode() {
        int result = skillType != null ? skillType.hashCode() : 0;
        result = 31 * result + previousModifier;
        result = 31 * result + newModifier;
        result = 31 * result + skillLevel;
        return result;
    }

    @Override
    public String toString() {
        return "GameplaySkillEffectData{" + 
                "skillType=" + skillType + 
                ", previousModifier=" + previousModifier + 
                ", newModifier=" + newModifier + 
                ", skillLevel=" + skillLevel + 
                ", effectiveChange=" + modifierChange() + 
                '}';
    }
}
