# MineScape Mod API

[![](https://jitpack.io/v/AshleyThew/minescape-mod-api.svg)](https://jitpack.io/#AshleyThew/minescape-mod-api)

A Java API library for MineScape mod development, providing channel-based communication and data handling capabilities.

## Features

- Channel-based data communication system
- Skills and experience tracking
- Type-safe data structures
- Comprehensive testing suite

## Prerequisites

- Java 17 or later
- Gradle 8.0 or later

## Installation

### For Gradle Projects

Add the following to your `build.gradle` file:

```gradle
repositories {
    mavenCentral()
    maven {
        url 'https://jitpack.io'
    }
}

dependencies {
    implementation 'com.github.AshleyThew:minescape-mod-api:v1.0.15'
}
```

### For NeoForge Projects

For NeoForge mod development, add the following to your `build.gradle` file to properly handle the API at runtime:

```gradle
repositories {
    mavenCentral()
    maven {
        url 'https://jitpack.io'
    }
}

jarJar.enabled(true)

tasks.named('jarJar') {

}

dependencies {
    implementation 'com.github.AshleyThew:minescape-mod-api:v1.0.15'
    jarJar(group: 'com.github.AshleyThew', name: 'minescape-mod-api', version: '[v1.0.15]')
    jarJar(implementation("com.github.AshleyThew:minescape-mod-api")) {
        version {
            strictly '[v1.0.15,)'
            prefer 'v1.0.15'
        }
    }
    additionalRuntimeClasspath("com.github.AshleyThew:minescape-mod-api:v1.0.15")
}
```

The `jarJar` configuration ensures that the MineScape API is properly included in your mod's JAR file for distribution, while `additionalRuntimeClasspath` provides the dependency during development and testing.

### For Maven Projects

Add the following to your `pom.xml` file:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.AshleyThew</groupId>
        <artifactId>minescape-mod-api</artifactId>
        <version>-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### Version Options

JitPack supports several versioning strategies:

- **Latest commit from main branch**: Use `-SNAPSHOT`
- **Specific release tag**: Use the tag name (e.g., `1.0.0`, `v2.1.0`)
- **Specific commit**: Use the commit hash (e.g., `abc123def`)
- **Specific branch**: Use `branch-SNAPSHOT` (e.g., `develop-SNAPSHOT`)

For production environments, it's recommended to use specific release tags for stability. For development, `-SNAPSHOT` ensures you always get the latest changes.

## Usage

### Basic Channel Communication

```java
import com.minescape.mod.api.channel.ChannelDataHandler;
import com.minescape.mod.api.channel.Channels;
import com.minescape.mod.api.channel.general.GeneralType;
import com.minescape.mod.api.channel.general.skills.GameplaySkillsExperienceData;
import com.minescape.mod.api.channel.general.skills.LoginSkillsData;
import com.minescape.mod.api.types.skills.SkillType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// Get the channel name
String channelName = Channels.GENERAL.getChannelName(); // "minescape_server:general"

// Initialize channel handler for general channel
ChannelDataHandler<GeneralType> handler = new ChannelDataHandler<>(Channels.GENERAL, GeneralType.class);

// Handle incoming JSON data for gameplay skills experience
String jsonString = "{\"type\":\"GAMEPLAY_SKILLS_EXPERIENCE\",\"data\":{\"skillType\":\"ATTACK\",\"experienceGained\":25.0,\"totalExperience\":1210421.0}}";
JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

// Parse and handle the data
GeneralType type = handler.getType(jsonObject);
Object result = handler.getData(jsonObject);

// Cast to specific type based on the type enum
if (type == GeneralType.GAMEPLAY_SKILLS_EXPERIENCE) {
    GameplaySkillsExperienceData expData = (GameplaySkillsExperienceData) result;
    System.out.println("Gained " + expData.experienceGained() + " XP in " + expData.skillType());
    System.out.println("Total experience: " + expData.totalExperience());
}

// Example with LOGIN_SKILLS data
String loginJsonString = "{\"type\":\"LOGIN_SKILLS\",\"data\":{\"levels\":{\"ATTACK\":75,\"DEFENCE\":60},\"experiences\":{\"ATTACK\":1210421.0,\"DEFENCE\":273742.0}}}";
JsonObject loginJsonObject = JsonParser.parseString(loginJsonString).getAsJsonObject();

GeneralType loginType = handler.getType(loginJsonObject);
if (loginType == GeneralType.LOGIN_SKILLS) {
    LoginSkillsData loginData = (LoginSkillsData) handler.getData(loginJsonObject);
    System.out.println("Attack level: " + loginData.getLevel(SkillType.ATTACK));
    System.out.println("Defence level: " + loginData.getLevel(SkillType.DEFENCE));
}
```

### Skills Experience Tracking

```java
import com.minescape.mod.api.channel.ChannelDataHandler;
import com.minescape.mod.api.channel.Channels;
import com.minescape.mod.api.channel.general.GeneralType;
import com.minescape.mod.api.channel.general.skills.LoginSkillsData;
import com.minescape.mod.api.channel.general.skills.GameplaySkillsExperienceData;
import com.minescape.mod.api.types.skills.SkillType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Map;

// Initialize channel handler for general channel
ChannelDataHandler<GeneralType> handler = new ChannelDataHandler<>(Channels.GENERAL, GeneralType.class);

// Handle incoming JSON data with multiple skills
String jsonString = "{\"type\":\"LOGIN_SKILLS\",\"data\":{\"levels\":{\"MAGIC\":99,\"ATTACK\":75},\"experiences\":{\"MAGIC\":13034431.0,\"ATTACK\":1210421.0}}}";
JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

// Get the general type and data
GeneralType type = handler.getType(jsonObject);
Object data = handler.getData(jsonObject);

// Handle different data types using switch statement
switch (type) {
    case LOGIN_SKILLS:
        LoginSkillsData loginData = (LoginSkillsData) data;
        Map<SkillType, Integer> levels = loginData.levels();        // All skill levels
        Map<SkillType, Double> experiences = loginData.experiences(); // All skill experiences

        // Access specific skills
        Integer magicLevel = loginData.getLevel(SkillType.MAGIC);      // 99
        Double magicExp = loginData.getExperience(SkillType.MAGIC);    // 13034431.0

        System.out.println("Login skills loaded: " + levels.size() + " skills");
        levels.forEach((skill, level) -> {
            Double exp = experiences.get(skill);
            System.out.println(skill + " level " + level + " with " + exp + " XP");
        });
        break;

    case GAMEPLAY_SKILLS_EXPERIENCE:
        GameplaySkillsExperienceData expData = (GameplaySkillsExperienceData) data;
        SkillType skill = expData.skillType();           // Skill type
        double gained = expData.experienceGained();      // Experience gained
        double total = expData.totalExperience();        // Total experience
        System.out.println("Gained " + gained + " XP in " + skill + " (total: " + total + ")");
        break;
}

// Alternative: Type-safe data retrieval
// If you know the expected type, you can use type-safe casting
String loginJsonString = "{\"type\":\"LOGIN_SKILLS\",\"data\":{\"levels\":{\"MAGIC\":99,\"COOKING\":85},\"experiences\":{\"MAGIC\":13034431.0,\"COOKING\":3258594.0}}}";
JsonObject loginJsonObject = JsonParser.parseString(loginJsonString).getAsJsonObject();

// Direct type-safe retrieval
LoginSkillsData loginSkillsData = handler.getData(loginJsonObject, LoginSkillsData.class);
System.out.println("Magic level: " + loginSkillsData.getLevel(SkillType.MAGIC));
System.out.println("Cooking level: " + loginSkillsData.getLevel(SkillType.COOKING));
```

### Player Actions

```java
import com.minescape.mod.api.channel.ChannelDataHandler;
import com.minescape.mod.api.channel.Channels;
import com.minescape.mod.api.channel.general.GeneralType;
import com.minescape.mod.api.channel.general.action.GameplayActionData;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

ChannelDataHandler<GeneralType> handler = new ChannelDataHandler<>(Channels.GENERAL, GeneralType.class);

// One message each time the player starts, cancels or finishes an action —
// mining a rock, cooking a fish, climbing an agility obstacle. Sent only to
// the player performing it.
JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
if (handler.getType(jsonObject) == GeneralType.GAMEPLAY_ACTION) {
    GameplayActionData action = handler.getData(jsonObject, GameplayActionData.class);

    if (action.isStarted()) {
        // durationMillis() is how long it should take, 0 when it has no fixed length
        System.out.println("Started " + action.action() + " for " + action.durationMillis() + "ms");
    } else if (action.isFinished()) {
        System.out.println("Finished " + action.action());
    } else if (action.isCancelled()) {
        System.out.println("Cancelled " + action.action());
    }
}
```

Example payloads:

```json
{"type":"GAMEPLAY_ACTION","data":{"action":"MINING","state":"STARTED","durationMillis":3000}}
{"type":"GAMEPLAY_ACTION","data":{"action":"MINING","state":"FINISHED","durationMillis":0}}
{"type":"GAMEPLAY_ACTION","data":{"action":"COOKING","state":"CANCELLED","durationMillis":0}}
```

Actions replace one another rather than stacking, so starting something new while an
action is running sends `CANCELLED` for the old one before `STARTED` for the new one.
A repeating skill sends `FINISHED` and then a fresh `STARTED` for each cycle. Every
`STARTED` is followed by exactly one `CANCELLED` or `FINISHED`, except when the player
logs out mid-action.

Action and state names are plain strings so the server can add new ones without an API
release — treat unrecognised values as valid. For the full list, see
[docs/action-values.md](docs/action-values.md).

### Item Consumption, Farming and Mob Combat

```java
import com.minescape.mod.api.channel.ChannelDataHandler;
import com.minescape.mod.api.channel.Channels;
import com.minescape.mod.api.channel.general.GeneralType;
import com.minescape.mod.api.channel.general.farming.FarmingPlotData;
import com.minescape.mod.api.channel.general.farming.GameplayFarmingPlantedData;
import com.minescape.mod.api.channel.general.farming.LoginFarmingPlotsData;
import com.minescape.mod.api.channel.general.item.GameplayItemConsumedData;
import com.minescape.mod.api.channel.general.mob.MobAttackData;
import com.minescape.mod.api.channel.general.mob.MobDefenceData;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

ChannelDataHandler<GeneralType> handler = new ChannelDataHandler<>(Channels.GENERAL, GeneralType.class);

JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
GeneralType type = handler.getType(jsonObject);
Object data = handler.getData(jsonObject);

switch (type) {
    // The player ate a food item or drank a potion. The item is identified by
    // its item field name, e.g. "SHARK" or "SUPER_ATTACK_4".
    case GAMEPLAY_ITEM_CONSUMED:
        GameplayItemConsumedData consumed = (GameplayItemConsumedData) data;
        System.out.println("Consumed " + consumed.item());
        break;

    // A farming plot was seeded, identified by the plot name e.g. "CATHERBY_HERBS".
    // plotData() contains the updated plot state when provided by the server, or null
    // when the server omits it (treat the same as receiving a fresh LOGIN_FARMING_PLOTS
    // entry for that patch).
    case GAMEPLAY_FARMING_PLANTED:
        GameplayFarmingPlantedData planted = (GameplayFarmingPlantedData) data;
        System.out.println("Planted in " + planted.patch());
        if (planted.plotData() != null) {
            FarmingPlotData updatedPlot = planted.plotData();
            System.out.println("  product: " + updatedPlot.product()
                    + ", next growth in " + updatedPlot.nextGrowthMillis() + "ms");
        }
        break;

    // Sent on join with the state of every farming plot the player owns.
    case LOGIN_FARMING_PLOTS:
        LoginFarmingPlotsData plots = (LoginFarmingPlotsData) data;
        for (FarmingPlotData plot : plots.plantedPlots()) {
            System.out.println(plot.patch() + ": " + plot.product()
                    + " (next growth in " + plot.nextGrowthMillis() + "ms)");
        }
        break;

    // A mob attacked a player. Broadcast to every player that can see the mob,
    // not just the player being attacked. style() is the attack style used
    // (e.g. MELEE, RANGED, MAGIC, DRAGONFIRE), or null when unknown.
    case MOB_ATTACK:
        MobAttackData attack = (MobAttackData) data;
        System.out.println("Mob " + attack.uuid() + " attacked with " + attack.style());
        break;

    // A mob defended an attack made against it. Broadcast to every player that
    // can see the mob, the same way MOB_ATTACK is. style() is the style of the
    // incoming attack (or null when unknown) and damage() is what the mob took
    // from it, 0 when the hit did nothing — blocked() is the same check.
    case MOB_DEFENCE:
        MobDefenceData defence = (MobDefenceData) data;
        System.out.println("Mob " + defence.uuid() + " defended " + defence.style()
                + (defence.blocked() ? " and took nothing" : " for " + defence.damage()));
        break;
}
```

Example payloads:

```json
{"type":"GAMEPLAY_ITEM_CONSUMED","data":{"item":"SHARK"}}
{"type":"GAMEPLAY_FARMING_PLANTED","data":{"patch":"CATHERBY_HERBS"}}
{"type":"GAMEPLAY_FARMING_PLANTED","data":{"patch":"CATHERBY_HERBS","plotData":{"patch":"CATHERBY_HERBS","product":"RANARR","nextGrowthMillis":600000}}}
{"type":"MOB_ATTACK","data":{"uuid":"123e4567-e89b-12d3-a456-426614174000","style":"MELEE"}}
{"type":"MOB_DEFENCE","data":{"uuid":"123e4567-e89b-12d3-a456-426614174000","style":"MELEE","damage":7}}
{"type":"MOB_DEFENCE","data":{"uuid":"123e4567-e89b-12d3-a456-426614174000","style":"RANGED","damage":0}}
{"type":"LOGIN_FARMING_PLOTS","data":{"plots":{
  "CATHERBY_HERBS":{"patch":"CATHERBY_HERBS","product":"RANARR","nextGrowthMillis":600000},
  "FALADOR_HERBS":{"patch":"FALADOR_HERBS","nextGrowthMillis":0}
}}}
```

`FarmingPlotData.product()` is `null` when nothing is planted, and `nextGrowthMillis()` is the
time remaining until the next growth stage as of when the message was sent (0 when the plot
is empty or has no further stage pending).

Patch and product names are plain strings so the server can add new ones without an API
release — treat unrecognised values as valid. For the full list of values the server
currently sends, see [docs/farming-values.md](docs/farming-values.md).

### NeoForge Integration

Here's an example of how to integrate the MineScape API with NeoForge's custom packet system:

```java
import com.minescape.mod.api.channel.ChannelDataHandler;
import com.minescape.mod.api.channel.Channels;
import com.minescape.mod.api.channel.general.GeneralType;
import com.minescape.mod.api.channel.general.skills.LoginSkillsData;
import com.minescape.mod.api.channel.general.skills.GameplaySkillsExperienceData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class MineScapeAPIExample {

    public record MineScapePacket(String jsonData) implements CustomPacketPayload {

        public static final CustomPacketPayload.Type<MineScapePacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.parse(Channels.GENERAL.getChannelName()));

        private static final ChannelDataHandler<GeneralType> HANDLER =
            new ChannelDataHandler<>(Channels.GENERAL, GeneralType.class);

        public static final StreamCodec<RegistryFriendlyByteBuf, MineScapePacket> STREAM_CODEC = StreamCodec.of(
                (byteBuf, packet) -> {
                    ByteArrayDataOutput output = ByteStreams.newDataOutput();
                    output.writeUTF(packet.jsonData());
                    byteBuf.writeBytes(output.toByteArray());
                },
                (data) -> {
                    byte[] bytes = new byte[data.readableBytes()];
                    data.readBytes(bytes);
                    ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
                    String json = input.readUTF();
                    return new MineScapePacket(json);
                }
        );

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    @SubscribeEvent
    public void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        registrar.playToClient(
                MineScapePacket.TYPE,
                MineScapePacket.STREAM_CODEC,
                this::handlePacketOnMain
        );
    }

    public void handlePacketOnMain(final MineScapePacket packet, final IPayloadContext context) {
        try {
            // Parse the JSON data using MineScape API
            JsonObject jsonObject = JsonParser.parseString(packet.jsonData()).getAsJsonObject();
            GeneralType type = MineScapePacket.HANDLER.getType(jsonObject);
            Object data = MineScapePacket.HANDLER.getData(jsonObject);

            // Handle different data types
            switch (type) {
                case LOGIN_SKILLS:
                    LoginSkillsData loginData = (LoginSkillsData) data;
                    System.out.println("Received login skills data with " + loginData.levels().size() + " skills");

                    // Process skill data
                    loginData.levels().forEach((skill, level) -> {
                        Double experience = loginData.getExperience(skill);
                        System.out.println(skill + ": Level " + level + " (" + experience + " XP)");
                    });
                    break;

                case GAMEPLAY_SKILLS_EXPERIENCE:
                    GameplaySkillsExperienceData expData = (GameplaySkillsExperienceData) data;
                    System.out.println("Gained " + expData.experienceGained() + " XP in " + expData.skillType() +
                                     " (total: " + expData.totalExperience() + ")");
                    break;
            }
        } catch (Exception e) {
            System.err.println("Failed to process MineScape packet: " + e.getMessage());
        }
    }
}

```

## Building from Source

1. Clone the repository:

   ```bash
   git clone https://github.com/AshleyThew/minescape-mod-api.git
   cd minescape-mod-api
   ```

2. Build the project:

   ```bash
   ./gradlew build
   ```

3. Run tests:
   ```bash
   ./gradlew test
   ```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## API Documentation

### Core Classes

- `ChannelDataHandler`: Main handler for channel-based communication
- `Channels`: Channel management and registration
- `GameplaySkillsExperienceData`: Data structure for skills experience tracking
- `SkillType`: Enumeration of available skill types
- `GameplayItemConsumedData`: Item field name of a consumed item or potion
- `GameplayFarmingPlantedData`: Name of a farming plot that was seeded, plus an optional updated `FarmingPlotData` snapshot of that plot
- `LoginFarmingPlotsData` / `FarmingPlotData`: Farming plots, what is planted and time to next growth
- `MobAttackData`: UUID of a mob attacking a player, plus the attack style used
- `MobDefenceData`: UUID of a mob defending an attack made against it, plus the style of that attack and the damage it took
- `GameplayActionData`: the action the player started, cancelled or finished, plus how long a started action should take

For detailed API documentation, see the Javadoc comments in the source code.

### Reference

- [Farming patch and product values](docs/farming-values.md) — every `patch` and `product`
  string the server currently sends
- [Mob attack style values](docs/mob-attack-styles.md) — every `style` string the server
  currently sends with `MOB_ATTACK` and `MOB_DEFENCE`
- [Action values](docs/action-values.md) — every `action` and `state` string the server
  currently sends with `GAMEPLAY_ACTION`

## Support

If you encounter any issues or have questions, please open an issue on GitHub.
