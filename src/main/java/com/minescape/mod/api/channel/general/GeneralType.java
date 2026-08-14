package com.minescape.mod.api.channel.general;

import java.util.function.Supplier;

import com.minescape.mod.api.channel.general.farming.GameplayFarmingPlantedData;
import com.minescape.mod.api.channel.general.farming.LoginFarmingPlotsData;
import com.minescape.mod.api.channel.general.item.GameplayItemConsumedData;
import com.minescape.mod.api.channel.general.mob.MobAttackData;
import com.minescape.mod.api.channel.general.mob.MobDefenceData;
import com.minescape.mod.api.channel.general.skills.LoginSkillsData;
import com.minescape.mod.api.channel.general.skills.LoginSkillEffectData;
import com.minescape.mod.api.channel.general.skills.GameplaySkillsExperienceData;
import com.minescape.mod.api.channel.general.skills.GameplaySkillEffectData;
import com.minescape.mod.api.channel.general.target.PlayerTargetData;
import com.minescape.mod.api.channel.general.target.PlayerTargetDeathData;

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
     * Data type for login skill effect information (modifiers and timers).
     */
    LOGIN_SKILL_EFFECTS(LoginSkillEffectData.class),
    /**
     * Data type for gameplay skills experience information.
     */
    GAMEPLAY_SKILLS_EXPERIENCE(GameplaySkillsExperienceData.class),
    /**
     * Data type for gameplay skill effect changes (modifiers from potions, spells, etc.).
     */
    GAMEPLAY_SKILL_EFFECT(GameplaySkillEffectData.class),
    /**
     * Data type for the player's current target mob.
     */
    PLAYER_TARGET(PlayerTargetData.class),
    /**
     * Data type for when the player kills their target mob.
     */
    PLAYER_TARGET_DEATH(PlayerTargetDeathData.class),
    /**
     * Data type for when the player consumes an item or potion.
     */
    GAMEPLAY_ITEM_CONSUMED(GameplayItemConsumedData.class),
    /**
     * Data type for when a farming plot is seeded.
     */
    GAMEPLAY_FARMING_PLANTED(GameplayFarmingPlantedData.class),
    /**
     * Data type for the player's farming plots and their statuses at login.
     */
    LOGIN_FARMING_PLOTS(LoginFarmingPlotsData.class),
    /**
     * Data type for when a mob attacks a player, broadcast to every player that
     * can see the mob.
     */
    MOB_ATTACK(MobAttackData.class),
    /**
     * Data type for when a mob defends an attack made against it, broadcast to
     * every player that can see the mob.
     */
    MOB_DEFENCE(MobDefenceData.class),
    ;

    private final Class<?> dataClass;

    GeneralType(Class<?> dataClass) {
        this.dataClass = dataClass;
    }

    @Override
    public Class<?> get() {
        return dataClass;
    }

}
