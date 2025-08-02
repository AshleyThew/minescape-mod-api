package com.minescape.mod.api.channel;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.minescape.mod.api.channel.general.skills.LoginSkillsData;
import com.minescape.mod.api.channel.general.skills.GameplaySkillsExperienceData;
import com.minescape.mod.api.channel.general.skills.GameplaySkillEffectData;
import com.minescape.mod.api.channel.general.GeneralType;
import com.minescape.mod.api.types.skills.SkillType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Map;
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
    void testGetTypeLoginSkills() {
        String jsonString = "{\"type\":\"LOGIN_SKILLS\",\"data\":{\"levels\":{\"ATTACK\":75},\"experiences\":{\"ATTACK\":1210421.0}}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        GeneralType type = generalHandler.getType(jsonObject);

        assertEquals(GeneralType.LOGIN_SKILLS, type);
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
        case GAMEPLAY_SKILLS_EXPERIENCE -> {
            GameplaySkillsExperienceData expData = (GameplaySkillsExperienceData) data;
            yield "Experience: " + expData.experienceGained() + " in " + expData.skillType();
        }
        case GAMEPLAY_SKILL_EFFECT -> {
            GameplaySkillEffectData effectData = (GameplaySkillEffectData) data;
            yield "Effect: " + effectData.skillType() + " modifier changed from " + effectData.previousModifier() + " to " + effectData.newModifier();
        }
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
        case LOGIN_SKILLS -> {
            LoginSkillsData loginData = (LoginSkillsData) data;
            // For demonstration, get info about all skills
            yield Map.of("type", "login", "skillCount", loginData.levels().size(), "levels", loginData.levels(),
                    "experiences", loginData.experiences());
        }
        case GAMEPLAY_SKILLS_EXPERIENCE -> {
            GameplaySkillsExperienceData expData = (GameplaySkillsExperienceData) data;
            yield Map.of("type", "experience", "skill", expData.skillType().toString(), "gained",
                    expData.experienceGained(), "total", expData.totalExperience());
        }
        case GAMEPLAY_SKILL_EFFECT -> {
            GameplaySkillEffectData effectData = (GameplaySkillEffectData) data;
            yield Map.of("type", "effect", "skill", effectData.skillType().toString(), "previousModifier",
                    effectData.previousModifier(), "newModifier", effectData.newModifier(), "change", effectData.modifierChange());
        }
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
        case GAMEPLAY_SKILLS_EXPERIENCE -> {
            GameplaySkillsExperienceData expData = (GameplaySkillsExperienceData) data;
            output.append("Gained ").append(expData.experienceGained()).append(" XP in ").append(expData.skillType())
                    .append(" (total: ").append(expData.totalExperience()).append(")");
        }
        case GAMEPLAY_SKILL_EFFECT -> {
            GameplaySkillEffectData effectData = (GameplaySkillEffectData) data;
            output.append("Applied effect to ").append(effectData.skillType()).append(": ")
                    .append(effectData.previousEffectiveLevel()).append(" -> ").append(effectData.newEffectiveLevel())
                    .append(" (modifier: ").append(effectData.newModifier()).append(")");
        }
        }

        assertEquals("Logged in with STRENGTH at level 85 (3258594.0 XP)", output.toString());
    }
}
