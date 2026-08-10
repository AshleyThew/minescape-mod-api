package com.minescape.mod.api.channel;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.minescape.mod.api.channel.general.skills.LoginSkillsData;
import com.minescape.mod.api.channel.general.skills.LoginSkillEffectData;
import com.minescape.mod.api.channel.general.skills.GameplaySkillsExperienceData;
import com.minescape.mod.api.channel.general.skills.GameplaySkillEffectData;
import com.minescape.mod.api.channel.general.target.PlayerTargetData;
import com.minescape.mod.api.channel.general.target.PlayerTargetDeathData;
import com.minescape.mod.api.channel.general.farming.FarmingPlotData;
import com.minescape.mod.api.channel.general.farming.GameplayFarmingPlantedData;
import com.minescape.mod.api.channel.general.farming.LoginFarmingPlotsData;
import com.minescape.mod.api.channel.general.item.GameplayItemConsumedData;
import com.minescape.mod.api.channel.general.mob.MobAttackData;
import com.minescape.mod.api.channel.general.GeneralType;
import com.minescape.mod.api.types.skills.SkillType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ChannelDataHandlerTest {

    private ChannelDataHandler<GeneralType> generalHandler;

    @BeforeEach
    void setUp() {
        generalHandler = new ChannelDataHandler<>(Channels.GENERAL, GeneralType.class);
    }

    @Test
    void testHandleGeneralChannelLoginSkills() {
        // Test JSON string with multiple skills
        String jsonString = "{\"type\":\"LOGIN_SKILLS\",\"data\":{\"levels\":{\"ATTACK\":75,\"DEFENCE\":60},\"experiences\":{\"ATTACK\":1210421.0,\"DEFENCE\":273742.0}}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Handle the data
        Object result = generalHandler.getData(jsonObject);

        // Verify the result
        assertTrue(result instanceof LoginSkillsData);
        LoginSkillsData loginSkillsData = (LoginSkillsData) result;

        // Test the maps
        assertEquals(Integer.valueOf(75), loginSkillsData.getLevel(SkillType.ATTACK));
        assertEquals(Integer.valueOf(60), loginSkillsData.getLevel(SkillType.DEFENCE));
        assertEquals(Double.valueOf(1210421.0), loginSkillsData.getExperience(SkillType.ATTACK));
        assertEquals(Double.valueOf(273742.0), loginSkillsData.getExperience(SkillType.DEFENCE));

        // Test accessing via maps
        assertEquals(2, loginSkillsData.levels().size());
        assertEquals(2, loginSkillsData.experiences().size());
    }

    @Test
    void testHandleGeneralChannelWithTypeSafeCasting() {
        String jsonString = "{\"type\":\"LOGIN_SKILLS\",\"data\":{\"levels\":{\"MAGIC\":99,\"COOKING\":85},\"experiences\":{\"MAGIC\":13034431.0,\"COOKING\":3258594.0}}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Use type-safe method
        LoginSkillsData result = generalHandler.getData(jsonObject, LoginSkillsData.class);

        assertNotNull(result);
        assertEquals(Integer.valueOf(99), result.getLevel(SkillType.MAGIC));
        assertEquals(Integer.valueOf(85), result.getLevel(SkillType.COOKING));
        assertEquals(Double.valueOf(13034431.0), result.getExperience(SkillType.MAGIC));
        assertEquals(Double.valueOf(3258594.0), result.getExperience(SkillType.COOKING));
    }

    @Test
    void testInvalidJsonFormat() {
        String invalidJson = "{\"wrongField\":\"LOGIN_SKILLS\",\"data\":{}}";
        JsonObject jsonObject = JsonParser.parseString(invalidJson).getAsJsonObject();

        assertThrows(IllegalArgumentException.class, () -> {
            generalHandler.getData(jsonObject);
        });
    }

    @Test
    void testInvalidType() {
        String jsonString = "{\"type\":\"INVALID_TYPE\",\"data\":{}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        assertThrows(IllegalArgumentException.class, () -> {
            generalHandler.getData(jsonObject);
        });
    }

    @Test
    void testTypeMismatch() {
        String jsonString = "{\"type\":\"LOGIN_SKILLS\",\"data\":{\"skillType\":\"ATTACK\",\"level\":75,\"experience\":1210421.0}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Try to cast to wrong type
        assertThrows(IllegalArgumentException.class, () -> {
            generalHandler.getData(jsonObject, String.class);
        });
    }

    @Test
    void testHandleGeneralChannelExperienceReceived() {
        // Test JSON string for experience received
        String jsonString = "{\"type\":\"GAMEPLAY_SKILLS_EXPERIENCE\",\"data\":{\"skillType\":\"WOODCUTTING\",\"experienceGained\":25.0,\"totalExperience\":1567.5}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Handle the data
        Object result = generalHandler.getData(jsonObject);

        // Verify the result
        assertTrue(result instanceof GameplaySkillsExperienceData);
        GameplaySkillsExperienceData experienceData = (GameplaySkillsExperienceData) result;

        assertEquals(SkillType.WOODCUTTING, experienceData.skillType());
        assertEquals(25.0, experienceData.experienceGained());
        assertEquals(1567.5, experienceData.totalExperience());
    }

    @Test
    void testHandleExperienceReceivedWithTypeSafeCasting() {
        String jsonString = "{\"type\":\"GAMEPLAY_SKILLS_EXPERIENCE\",\"data\":{\"skillType\":\"COOKING\",\"experienceGained\":120.5,\"totalExperience\":8975.0}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Use type-safe method
        GameplaySkillsExperienceData result = generalHandler.getData(jsonObject, GameplaySkillsExperienceData.class);

        assertNotNull(result);
        assertEquals(SkillType.COOKING, result.skillType());
        assertEquals(120.5, result.experienceGained());
        assertEquals(8975.0, result.totalExperience());
    }

    @Test
    void testHandleGeneralChannelSkillEffect() {
        // Test JSON string for skill effect data
        String jsonString = "{\"type\":\"GAMEPLAY_SKILL_EFFECT\",\"data\":{\"skillType\":\"STRENGTH\",\"previousModifier\":0,\"newModifier\":3,\"skillLevel\":75}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Handle the data
        Object result = generalHandler.getData(jsonObject);

        // Verify the result
        assertTrue(result instanceof GameplaySkillEffectData);
        GameplaySkillEffectData skillEffectData = (GameplaySkillEffectData) result;

        assertEquals(SkillType.STRENGTH, skillEffectData.skillType());
        assertEquals(0, skillEffectData.previousModifier());
        assertEquals(3, skillEffectData.newModifier());
        assertEquals(75, skillEffectData.skillLevel());
        assertEquals(75, skillEffectData.previousEffectiveLevel());
        assertEquals(78, skillEffectData.newEffectiveLevel());
        assertEquals(3, skillEffectData.modifierChange());
    }

    @Test
    void testHandleSkillEffectWithTypeSafeCasting() {
        String jsonString = "{\"type\":\"GAMEPLAY_SKILL_EFFECT\",\"data\":{\"skillType\":\"HITPOINTS\",\"previousModifier\":5,\"newModifier\":-2,\"skillLevel\":99}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Use type-safe method
        GameplaySkillEffectData result = generalHandler.getData(jsonObject, GameplaySkillEffectData.class);

        assertNotNull(result);
        assertEquals(SkillType.HITPOINTS, result.skillType());
        assertEquals(5, result.previousModifier());
        assertEquals(-2, result.newModifier());
        assertEquals(99, result.skillLevel());
        assertEquals(104, result.previousEffectiveLevel());
        assertEquals(97, result.newEffectiveLevel());
        assertEquals(-7, result.modifierChange());
    }

    @Test
    void testHandleGeneralChannelLoginSkillEffects() {
        // Test JSON string with multiple skill effects
        String jsonString = "{\"type\":\"LOGIN_SKILL_EFFECTS\",\"data\":{\"modifiers\":{\"ATTACK\":3,\"STRENGTH\":-2,\"HITPOINTS\":5}}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Handle the data
        Object result = generalHandler.getData(jsonObject);

        // Verify the result
        assertTrue(result instanceof LoginSkillEffectData);
        LoginSkillEffectData loginSkillEffectData = (LoginSkillEffectData) result;

        // Test the modifiers
        assertEquals(Integer.valueOf(3), loginSkillEffectData.getModifier(SkillType.ATTACK));
        assertEquals(Integer.valueOf(-2), loginSkillEffectData.getModifier(SkillType.STRENGTH));
        assertEquals(Integer.valueOf(5), loginSkillEffectData.getModifier(SkillType.HITPOINTS));

        // Test accessing via map
        assertEquals(3, loginSkillEffectData.modifiers().size());
        assertTrue(loginSkillEffectData.hasEffect(SkillType.ATTACK));
        assertTrue(loginSkillEffectData.hasEffect(SkillType.STRENGTH));
        assertTrue(loginSkillEffectData.hasEffect(SkillType.HITPOINTS));
        assertTrue(loginSkillEffectData.hasAnyEffect());
    }

    @Test
    void testHandleLoginSkillEffectsWithTypeSafeCasting() {
        String jsonString = "{\"type\":\"LOGIN_SKILL_EFFECTS\",\"data\":{\"modifiers\":{\"MAGIC\":4,\"DEFENCE\":-1}}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Use type-safe method
        LoginSkillEffectData result = generalHandler.getData(jsonObject, LoginSkillEffectData.class);

        assertNotNull(result);
        assertEquals(Integer.valueOf(4), result.getModifier(SkillType.MAGIC));
        assertEquals(Integer.valueOf(-1), result.getModifier(SkillType.DEFENCE));
        assertNull(result.getModifier(SkillType.COOKING)); // Not present in data
        
        // Test utility methods
        assertTrue(result.hasEffect(SkillType.MAGIC));
        assertTrue(result.hasEffect(SkillType.DEFENCE));
        assertFalse(result.hasEffect(SkillType.COOKING));
        assertTrue(result.hasAnyEffect());
    }

    @Test
    void testLoginSkillEffectsEmpty() {
        String jsonString = "{\"type\":\"LOGIN_SKILL_EFFECTS\",\"data\":{\"modifiers\":{}}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        LoginSkillEffectData result = generalHandler.getData(jsonObject, LoginSkillEffectData.class);

        assertNotNull(result);
        assertEquals(0, result.modifiers().size());
        assertFalse(result.hasAnyEffect());
        assertFalse(result.hasEffect(SkillType.ATTACK));
        assertNull(result.getModifier(SkillType.ATTACK));
    }

    @Test
    void testLoginSkillEffectsWithZeroModifiers() {
        String jsonString = "{\"type\":\"LOGIN_SKILL_EFFECTS\",\"data\":{\"modifiers\":{\"ATTACK\":0,\"STRENGTH\":5}}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        LoginSkillEffectData result = generalHandler.getData(jsonObject, LoginSkillEffectData.class);

        assertNotNull(result);
        assertEquals(Integer.valueOf(0), result.getModifier(SkillType.ATTACK));
        assertEquals(Integer.valueOf(5), result.getModifier(SkillType.STRENGTH));
        
        // Zero modifier should not count as having an effect
        assertFalse(result.hasEffect(SkillType.ATTACK));
        assertTrue(result.hasEffect(SkillType.STRENGTH));
        assertTrue(result.hasAnyEffect());
    }

    @Test
    void testGetTypeLoginSkills() {
        String jsonString = "{\"type\":\"LOGIN_SKILLS\",\"data\":{\"levels\":{\"ATTACK\":75},\"experiences\":{\"ATTACK\":1210421.0}}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        GeneralType type = generalHandler.getType(jsonObject);

        assertEquals(GeneralType.LOGIN_SKILLS, type);
    }

    @Test
    void testGetTypeLoginSkillEffects() {
        String jsonString = "{\"type\":\"LOGIN_SKILL_EFFECTS\",\"data\":{\"modifiers\":{\"ATTACK\":3,\"STRENGTH\":-2}}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        GeneralType type = generalHandler.getType(jsonObject);

        assertEquals(GeneralType.LOGIN_SKILL_EFFECTS, type);
    }

    @Test
    void testGetTypeGameplaySkillsExperience() {
        String jsonString = "{\"type\":\"GAMEPLAY_SKILLS_EXPERIENCE\",\"data\":{\"skillType\":\"COOKING\",\"experienceGained\":120.5,\"totalExperience\":8975.0}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        GeneralType type = generalHandler.getType(jsonObject);

        assertEquals(GeneralType.GAMEPLAY_SKILLS_EXPERIENCE, type);
    }

    @Test
    void testGetTypeSkillEffect() {
        String jsonString = "{\"type\":\"GAMEPLAY_SKILL_EFFECT\",\"data\":{\"skillType\":\"ATTACK\",\"previousModifier\":0,\"newModifier\":5,\"skillLevel\":80}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        GeneralType type = generalHandler.getType(jsonObject);

        assertEquals(GeneralType.GAMEPLAY_SKILL_EFFECT, type);
    }

    @Test
    void testGetTypeInvalidType() {
        String jsonString = "{\"type\":\"INVALID_TYPE\",\"data\":{}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        assertThrows(IllegalArgumentException.class, () -> {
            generalHandler.getType(jsonObject);
        });
    }

    @Test
    void testGetTypeMissingTypeField() {
        String jsonString = "{\"data\":{\"skillType\":\"ATTACK\",\"level\":75,\"experience\":1210421.0}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        assertThrows(IllegalArgumentException.class, () -> {
            generalHandler.getType(jsonObject);
        });
    }

    @Test
    void testSwitchExpressionWithLoginSkills() {

        String jsonString = "{\"type\":\"LOGIN_SKILLS\",\"data\":{\"levels\":{\"DEFENCE\":60},\"experiences\":{\"DEFENCE\":273742.0}}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Get type and data
        GeneralType type = generalHandler.getType(jsonObject);
        Object data = generalHandler.getData(jsonObject);

        // Test switch expression pattern
        String result = switch (type) {
        case LOGIN_SKILLS -> {
            LoginSkillsData loginData = (LoginSkillsData) data;
            // Get the first skill from the maps for demonstration
            SkillType firstSkill = loginData.levels().keySet().iterator().next();
            Integer level = loginData.getLevel(firstSkill);
            yield "Login: " + firstSkill + " level " + level;
        }
        default -> throw new IllegalArgumentException("Unexpected type: " + type);
        };

        assertEquals("Login: DEFENCE level 60", result);
    }

    @Test
    void testSwitchExpressionWithExperienceData() {

        String jsonString = "{\"type\":\"GAMEPLAY_SKILLS_EXPERIENCE\",\"data\":{\"skillType\":\"FISHING\",\"experienceGained\":50.0,\"totalExperience\":2500.0}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Get type and data
        GeneralType type = generalHandler.getType(jsonObject);
        Object data = generalHandler.getData(jsonObject);

        // Test switch expression with Map return type
        Object processedData = switch (type) {
        case GAMEPLAY_SKILLS_EXPERIENCE -> {
            GameplaySkillsExperienceData expData = (GameplaySkillsExperienceData) data;
            yield Map.of("type", "experience", "skill", expData.skillType().toString(), "gained",
                    expData.experienceGained(), "total", expData.totalExperience());
        }
        default -> throw new IllegalArgumentException("Unexpected type: " + type);
        };

        assertTrue(processedData instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) processedData;

        assertEquals("experience", resultMap.get("type"));
        assertEquals("FISHING", resultMap.get("skill"));
        assertEquals(50.0, resultMap.get("gained"));
        assertEquals(2500.0, resultMap.get("total"));
    }

    @Test
    void testSwitchStatementDataProcessing() {
        // Initialize channel handler for general channel
        ChannelDataHandler<GeneralType> handler = new ChannelDataHandler<>(Channels.GENERAL, GeneralType.class);

        String jsonString = "{\"type\":\"LOGIN_SKILLS\",\"data\":{\"levels\":{\"STRENGTH\":85},\"experiences\":{\"STRENGTH\":3258594.0}}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Get type and data
        GeneralType type = handler.getType(jsonObject);
        Object data = handler.getData(jsonObject);

        // Test switch statement for data processing
        StringBuilder output = new StringBuilder();
        switch (type) {
        case LOGIN_SKILLS -> {
            LoginSkillsData loginData = (LoginSkillsData) data;
            // Get the first skill for demonstration
            SkillType firstSkill = loginData.levels().keySet().iterator().next();
            Integer level = loginData.getLevel(firstSkill);
            Double experience = loginData.getExperience(firstSkill);
            output.append("Logged in with ").append(firstSkill).append(" at level ").append(level).append(" (")
                    .append(experience).append(" XP)");
        }
        default -> throw new IllegalArgumentException("Unexpected type: " + type);
        }

        assertEquals("Logged in with STRENGTH at level 85 (3258594.0 XP)", output.toString());
    }

    @Test
    void testSwitchExpressionWithLoginSkillEffects() {
        String jsonString = "{\"type\":\"LOGIN_SKILL_EFFECTS\",\"data\":{\"modifiers\":{\"DEFENCE\":2,\"HITPOINTS\":-3}}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Get type and data
        GeneralType type = generalHandler.getType(jsonObject);
        Object data = generalHandler.getData(jsonObject);

        // Test switch expression pattern
        String result = switch (type) {
        case LOGIN_SKILL_EFFECTS -> {
            LoginSkillEffectData effectsData = (LoginSkillEffectData) data;
            // Get the first skill with effects for demonstration
            SkillType firstSkill = effectsData.modifiers().keySet().iterator().next();
            Integer modifier = effectsData.getModifier(firstSkill);
            yield "Login effects: " + firstSkill + " modifier " + modifier;
        }
        default -> throw new IllegalArgumentException("Unexpected type: " + type);
        };

        // The result should contain one of the skills (DEFENCE or HITPOINTS) with its modifier
        assertTrue(result.startsWith("Login effects:"));
        assertTrue(result.contains("DEFENCE modifier 2") || result.contains("HITPOINTS modifier -3"));
    }

    @Test
    void testSwitchStatementWithLoginSkillEffects() {
        String jsonString = "{\"type\":\"LOGIN_SKILL_EFFECTS\",\"data\":{\"modifiers\":{\"ATTACK\":4}}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Get type and data
        GeneralType type = generalHandler.getType(jsonObject);
        Object data = generalHandler.getData(jsonObject);

        // Test switch statement for data processing
        StringBuilder output = new StringBuilder();
        switch (type) {
        case LOGIN_SKILL_EFFECTS -> {
            LoginSkillEffectData effectsData = (LoginSkillEffectData) data;
            SkillType firstSkill = effectsData.modifiers().keySet().iterator().next();
            Integer modifier = effectsData.getModifier(firstSkill);
            output.append("Logged in with ").append(firstSkill).append(" effect modifier ").append(modifier);
        }
        default -> throw new IllegalArgumentException("Unexpected type: " + type);
        }

        assertEquals("Logged in with ATTACK effect modifier 4", output.toString());
    }

    @Test
    void testHandleTargetData() {
        // Test JSON string for target data
        String jsonString = "{\"type\":\"PLAYER_TARGET\",\"data\":{\"uuid\":\"123e4567-e89b-12d3-a456-426614174000\",\"currentHp\":75,\"totalHp\":100}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Handle the data
        Object result = generalHandler.getData(jsonObject);

        // Verify the result
        assertTrue(result instanceof PlayerTargetData);
        PlayerTargetData playerTargetData = (PlayerTargetData) result;

        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), playerTargetData.uuid());
        assertEquals(75, playerTargetData.currentHp());
        assertEquals(100, playerTargetData.totalHp());
    }

    @Test
    void testHandleTargetDataWithTypeSafeCasting() {
        String jsonString = "{\"type\":\"PLAYER_TARGET\",\"data\":{\"uuid\":\"123e4567-e89b-12d3-a456-426614174000\",\"currentHp\":50,\"totalHp\":150}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Use type-safe method
        PlayerTargetData result = generalHandler.getData(jsonObject, PlayerTargetData.class);

        assertNotNull(result);
        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), result.uuid());
        assertEquals(50, result.currentHp());
        assertEquals(150, result.totalHp());
    }

    @Test
    void testHandleGeneralChannelWithTargetData() {
        // Test JSON string with target data
        String jsonString = "{\"type\":\"PLAYER_TARGET\",\"data\":{\"uuid\":\"123e4567-e89b-12d3-a456-426614174000\",\"currentHp\":200,\"totalHp\":300}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Handle the data
        Object result = generalHandler.getData(jsonObject);

        // Verify the result
        assertTrue(result instanceof PlayerTargetData);
        PlayerTargetData playerTargetData = (PlayerTargetData) result;

        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), playerTargetData.uuid());
        assertEquals(200, playerTargetData.currentHp());
        assertEquals(300, playerTargetData.totalHp());
    }

    @Test
    void testGetTypeTarget() {
        String jsonString = "{\"type\":\"PLAYER_TARGET\",\"data\":{\"uuid\":\"123e4567-e89b-12d3-a456-426614174000\",\"currentHp\":100,\"totalHp\":200}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        GeneralType type = generalHandler.getType(jsonObject);

        assertEquals(GeneralType.PLAYER_TARGET, type);
    }

    @Test
    void testSwitchExpressionWithTargetData() {

        String jsonString = "{\"type\":\"PLAYER_TARGET\",\"data\":{\"uuid\":\"123e4567-e89b-12d3-a456-426614174000\",\"currentHp\":75,\"totalHp\":100}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Get type and data
        GeneralType type = generalHandler.getType(jsonObject);
        Object data = generalHandler.getData(jsonObject);

        // Test switch expression pattern
        String result = switch (type) {
        case PLAYER_TARGET -> {
            PlayerTargetData playerTargetData = (PlayerTargetData) data;
            yield "Target: " + playerTargetData.uuid() + " HP: " + playerTargetData.currentHp() + "/" + playerTargetData.totalHp();
        }
        default -> throw new IllegalArgumentException("Unexpected type: " + type);
        };

        assertEquals("Target: 123e4567-e89b-12d3-a456-426614174000 HP: 75/100", result);
    }

    @Test
    void testHandleGeneralChannelPlayerTargetDeath() {
        // Test JSON string for player target death
        String jsonString = "{\"type\":\"PLAYER_TARGET_DEATH\",\"data\":{\"uuid\":\"123e4567-e89b-12d3-a456-426614174000\"}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Handle the data
        Object result = generalHandler.getData(jsonObject);

        // Verify the result
        assertTrue(result instanceof PlayerTargetDeathData);
        PlayerTargetDeathData playerTargetDeathData = (PlayerTargetDeathData) result;

        // Test the UUID
        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), playerTargetDeathData.uuid());
    }

    @Test
    void testHandleGeneralChannelPlayerTargetDeathWithTypeSafeCasting() {
        String jsonString = "{\"type\":\"PLAYER_TARGET_DEATH\",\"data\":{\"uuid\":\"550e8400-e29b-41d4-a716-446655440000\"}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Use type-safe method
        PlayerTargetDeathData result = generalHandler.getData(jsonObject, PlayerTargetDeathData.class);

        assertNotNull(result);
        assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), result.uuid());
    }

    @Test
    void testPlayerTargetDeathDataEquality() {
        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        PlayerTargetDeathData data1 = new PlayerTargetDeathData(uuid);
        PlayerTargetDeathData data2 = new PlayerTargetDeathData(uuid);
        PlayerTargetDeathData data3 = new PlayerTargetDeathData(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));

        // Test equality
        assertEquals(data1, data2);
        assertNotEquals(data1, data3);

        // Test hashCode
        assertEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    void testPlayerTargetDeathDataToString() {
        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        PlayerTargetDeathData data = new PlayerTargetDeathData(uuid);

        String result = data.toString();
        assertTrue(result.contains("PlayerTargetDeathData"));
        assertTrue(result.contains("123e4567-e89b-12d3-a456-426614174000"));
    }

    @Test
    void testHandleGeneralChannelItemConsumed() {
        String jsonString = "{\"type\":\"GAMEPLAY_ITEM_CONSUMED\",\"data\":{\"item\":\"SHARK\"}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        Object result = generalHandler.getData(jsonObject);

        assertTrue(result instanceof GameplayItemConsumedData);
        assertEquals("SHARK", ((GameplayItemConsumedData) result).item());
    }

    @Test
    void testHandleItemConsumedWithTypeSafeCasting() {
        String jsonString = "{\"type\":\"GAMEPLAY_ITEM_CONSUMED\",\"data\":{\"item\":\"SUPER_ATTACK_4\"}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        GameplayItemConsumedData result = generalHandler.getData(jsonObject, GameplayItemConsumedData.class);

        assertNotNull(result);
        assertEquals("SUPER_ATTACK_4", result.item());
        assertEquals(GeneralType.GAMEPLAY_ITEM_CONSUMED, generalHandler.getType(jsonObject));
    }

    @Test
    void testItemConsumedDataEquality() {
        GameplayItemConsumedData data1 = new GameplayItemConsumedData("SHARK");
        GameplayItemConsumedData data2 = new GameplayItemConsumedData("SHARK");
        GameplayItemConsumedData data3 = new GameplayItemConsumedData("LOBSTER");

        assertEquals(data1, data2);
        assertNotEquals(data1, data3);
        assertEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    void testHandleGeneralChannelFarmingPlanted() {
        String jsonString = "{\"type\":\"GAMEPLAY_FARMING_PLANTED\",\"data\":{\"patch\":\"CATHERBY_HERBS\"}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        Object result = generalHandler.getData(jsonObject);

        assertTrue(result instanceof GameplayFarmingPlantedData);
        GameplayFarmingPlantedData planted = (GameplayFarmingPlantedData) result;
        assertEquals("CATHERBY_HERBS", planted.patch());
        assertNull(planted.plotData());
    }

    @Test
    void testHandleFarmingPlantedWithTypeSafeCasting() {
        String jsonString = "{\"type\":\"GAMEPLAY_FARMING_PLANTED\",\"data\":{\"patch\":\"FALADOR_ALLOTMENT_NORTH\"}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        GameplayFarmingPlantedData result = generalHandler.getData(jsonObject, GameplayFarmingPlantedData.class);

        assertNotNull(result);
        assertEquals("FALADOR_ALLOTMENT_NORTH", result.patch());
        assertNull(result.plotData());
        assertEquals(GeneralType.GAMEPLAY_FARMING_PLANTED, generalHandler.getType(jsonObject));
    }

    @Test
    void testHandleFarmingPlantedWithPlotData() {
        String jsonString = "{\"type\":\"GAMEPLAY_FARMING_PLANTED\",\"data\":{\"patch\":\"CATHERBY_HERBS\","
                + "\"plotData\":{\"patch\":\"CATHERBY_HERBS\",\"product\":\"RANARR\",\"nextGrowthMillis\":60000}}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        GameplayFarmingPlantedData result = generalHandler.getData(jsonObject, GameplayFarmingPlantedData.class);

        assertNotNull(result);
        assertEquals("CATHERBY_HERBS", result.patch());
        assertNotNull(result.plotData());
        assertEquals("CATHERBY_HERBS", result.plotData().patch());
        assertEquals("RANARR", result.plotData().product());
        assertEquals(60000L, result.plotData().nextGrowthMillis());
        assertTrue(result.plotData().isPlanted());
        assertTrue(result.plotData().isGrowing());
    }

    @Test
    void testGameplayFarmingPlantedDataEquality() {
        FarmingPlotData plotData1 = new FarmingPlotData("CATHERBY_HERBS", "RANARR", 60000L);
        FarmingPlotData plotData2 = new FarmingPlotData("CATHERBY_HERBS", "RANARR", 60000L);

        GameplayFarmingPlantedData data1 = new GameplayFarmingPlantedData("CATHERBY_HERBS", plotData1);
        GameplayFarmingPlantedData data2 = new GameplayFarmingPlantedData("CATHERBY_HERBS", plotData2);
        GameplayFarmingPlantedData data3 = new GameplayFarmingPlantedData("CATHERBY_HERBS");
        GameplayFarmingPlantedData data4 = new GameplayFarmingPlantedData("FALADOR_ALLOTMENT_NORTH", plotData1);

        assertEquals(data1, data2);
        assertNotEquals(data1, data3);
        assertNotEquals(data1, data4);
        assertEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    void testHandleGeneralChannelLoginFarmingPlots() {
        String jsonString = "{\"type\":\"LOGIN_FARMING_PLOTS\",\"data\":{\"plots\":{"
                + "\"CATHERBY_HERBS\":{\"patch\":\"CATHERBY_HERBS\",\"product\":\"RANARR\",\"nextGrowthMillis\":600000},"
                + "\"PRIFDDINAS_ALLOTMENT_NORTH\":{\"patch\":\"PRIFDDINAS_ALLOTMENT_NORTH\",\"product\":\"WATERMELON\",\"nextGrowthMillis\":0},"
                + "\"FALADOR_HERBS\":{\"patch\":\"FALADOR_HERBS\",\"nextGrowthMillis\":0}}}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        Object result = generalHandler.getData(jsonObject);

        assertTrue(result instanceof LoginFarmingPlotsData);
        LoginFarmingPlotsData plotsData = (LoginFarmingPlotsData) result;

        assertEquals(3, plotsData.plots().size());

        FarmingPlotData catherby = plotsData.getPlot("CATHERBY_HERBS");
        assertNotNull(catherby);
        assertEquals("CATHERBY_HERBS", catherby.patch());
        assertEquals("RANARR", catherby.product());
        assertEquals(600000L, catherby.nextGrowthMillis());
        assertTrue(catherby.isPlanted());
        assertTrue(catherby.isGrowing());

        // Planted but no further growth stage pending
        FarmingPlotData prifddinas = plotsData.getPlot("PRIFDDINAS_ALLOTMENT_NORTH");
        assertNotNull(prifddinas);
        assertEquals("WATERMELON", prifddinas.product());
        assertTrue(prifddinas.isPlanted());
        assertFalse(prifddinas.isGrowing());

        // Empty plot
        FarmingPlotData falador = plotsData.getPlot("FALADOR_HERBS");
        assertNotNull(falador);
        assertNull(falador.product());
        assertEquals(0L, falador.nextGrowthMillis());
        assertFalse(falador.isPlanted());
        assertFalse(falador.isGrowing());

        assertTrue(plotsData.isPlanted("CATHERBY_HERBS"));
        assertFalse(plotsData.isPlanted("FALADOR_HERBS"));
        assertFalse(plotsData.isPlanted("ARDOUGNE_HERBS"));
        assertEquals(2, plotsData.plantedPlots().size());
    }

    @Test
    void testLoginFarmingPlotsEmpty() {
        String jsonString = "{\"type\":\"LOGIN_FARMING_PLOTS\",\"data\":{\"plots\":{}}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        LoginFarmingPlotsData result = generalHandler.getData(jsonObject, LoginFarmingPlotsData.class);

        assertNotNull(result);
        assertEquals(0, result.plots().size());
        assertNull(result.getPlot("CATHERBY_HERBS"));
        assertFalse(result.isPlanted("CATHERBY_HERBS"));
        assertTrue(result.plantedPlots().isEmpty());
        assertEquals(GeneralType.LOGIN_FARMING_PLOTS, generalHandler.getType(jsonObject));
    }

    @Test
    void testLoginFarmingPlotsMissingPlots() {
        String jsonString = "{\"type\":\"LOGIN_FARMING_PLOTS\",\"data\":{}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        LoginFarmingPlotsData result = generalHandler.getData(jsonObject, LoginFarmingPlotsData.class);

        assertNotNull(result);
        assertEquals(0, result.plots().size());
        assertFalse(result.isPlanted("CATHERBY_HERBS"));
    }

    @Test
    void testHandleGeneralChannelMobAttack() {
        String jsonString = "{\"type\":\"MOB_ATTACK\",\"data\":{\"uuid\":\"123e4567-e89b-12d3-a456-426614174000\",\"style\":\"MELEE\"}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        Object result = generalHandler.getData(jsonObject);

        assertTrue(result instanceof MobAttackData);
        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), ((MobAttackData) result).uuid());
        assertEquals("MELEE", ((MobAttackData) result).style());
    }

    @Test
    void testHandleGeneralChannelMobAttackWithoutStyle() {
        String jsonString = "{\"type\":\"MOB_ATTACK\",\"data\":{\"uuid\":\"123e4567-e89b-12d3-a456-426614174000\"}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        MobAttackData result = generalHandler.getData(jsonObject, MobAttackData.class);

        assertNotNull(result);
        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), result.uuid());
        assertNull(result.style());
    }

    @Test
    void testHandleMobAttackWithTypeSafeCasting() {
        String jsonString = "{\"type\":\"MOB_ATTACK\",\"data\":{\"uuid\":\"550e8400-e29b-41d4-a716-446655440000\"}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        MobAttackData result = generalHandler.getData(jsonObject, MobAttackData.class);

        assertNotNull(result);
        assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), result.uuid());
        assertEquals(GeneralType.MOB_ATTACK, generalHandler.getType(jsonObject));
    }

    @Test
    void testMobAttackDataEquality() {
        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        MobAttackData data1 = new MobAttackData(uuid);
        MobAttackData data2 = new MobAttackData(uuid);
        MobAttackData data3 = new MobAttackData(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        MobAttackData data4 = new MobAttackData(uuid, "MELEE");
        MobAttackData data5 = new MobAttackData(uuid, "MELEE");

        assertEquals(data1, data2);
        assertNotEquals(data1, data3);
        assertEquals(data1.hashCode(), data2.hashCode());
        assertEquals(data4, data5);
        assertNotEquals(data1, data4);
        assertEquals(data4.hashCode(), data5.hashCode());
        assertTrue(data1.toString().contains("MobAttackData"));
    }
}
