package com.minescape.mod.api.channel;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.minescape.mod.api.channel.general.skills.LoginSkillsData;
import com.minescape.mod.api.channel.general.skills.GameplaySkillsExperienceData;
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
        // Test JSON string
        String jsonString = "{\"type\":\"LOGIN_SKILLS\",\"data\":{\"skillType\":\"ATTACK\",\"level\":75,\"experience\":1210421.0}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Handle the data
        Object result = generalHandler.getData(jsonObject);

        // Verify the result
        assertTrue(result instanceof LoginSkillsData);
        LoginSkillsData loginSkillsData = (LoginSkillsData) result;

        assertEquals(SkillType.ATTACK, loginSkillsData.skillType());
        assertEquals(75, loginSkillsData.level());
        assertEquals(1210421.0, loginSkillsData.experience());
    }

    @Test
    void testHandleGeneralChannelWithTypeSafeCasting() {
        String jsonString = "{\"type\":\"LOGIN_SKILLS\",\"data\":{\"skillType\":\"MAGIC\",\"level\":99,\"experience\":13034431.0}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Use type-safe method
        LoginSkillsData result = generalHandler.getData(jsonObject, LoginSkillsData.class);

        assertNotNull(result);
        assertEquals(SkillType.MAGIC, result.skillType());
        assertEquals(99, result.level());
        assertEquals(13034431.0, result.experience());
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
    void testGetTypeLoginSkills() {
        String jsonString = "{\"type\":\"LOGIN_SKILLS\",\"data\":{\"skillType\":\"ATTACK\",\"level\":75,\"experience\":1210421.0}}";
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

        String jsonString = "{\"type\":\"LOGIN_SKILLS\",\"data\":{\"skillType\":\"DEFENCE\",\"level\":60,\"experience\":273742.0}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Get type and data
        GeneralType type = generalHandler.getType(jsonObject);
        Object data = generalHandler.getData(jsonObject);

        // Test switch expression pattern
        String result = switch (type) {
        case LOGIN_SKILLS -> {
            LoginSkillsData loginData = (LoginSkillsData) data;
            yield "Login: " + loginData.skillType() + " level " + loginData.level();
        }
        case GAMEPLAY_SKILLS_EXPERIENCE -> {
            GameplaySkillsExperienceData expData = (GameplaySkillsExperienceData) data;
            yield "Experience: " + expData.experienceGained() + " in " + expData.skillType();
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
            yield Map.of("type", "login", "skill", loginData.skillType().toString(), "level", loginData.level());
        }
        case GAMEPLAY_SKILLS_EXPERIENCE -> {
            GameplaySkillsExperienceData expData = (GameplaySkillsExperienceData) data;
            yield Map.of("type", "experience", "skill", expData.skillType().toString(), "gained",
                    expData.experienceGained(), "total", expData.totalExperience());
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

        String jsonString = "{\"type\":\"LOGIN_SKILLS\",\"data\":{\"skillType\":\"STRENGTH\",\"level\":85,\"experience\":3258594.0}}";
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Get type and data
        GeneralType type = handler.getType(jsonObject);
        Object data = handler.getData(jsonObject);

        // Test switch statement for data processing
        StringBuilder output = new StringBuilder();
        switch (type) {
        case LOGIN_SKILLS -> {
            LoginSkillsData loginData = (LoginSkillsData) data;
            output.append("Logged in with ").append(loginData.skillType()).append(" at level ")
                    .append(loginData.level()).append(" (").append(loginData.experience()).append(" XP)");
        }
        case GAMEPLAY_SKILLS_EXPERIENCE -> {
            GameplaySkillsExperienceData expData = (GameplaySkillsExperienceData) data;
            output.append("Gained ").append(expData.experienceGained()).append(" XP in ").append(expData.skillType())
                    .append(" (total: ").append(expData.totalExperience()).append(")");
        }
        }

        assertEquals("Logged in with STRENGTH at level 85 (3258594.0 XP)", output.toString());
    }
}
