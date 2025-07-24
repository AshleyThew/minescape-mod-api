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
    implementation 'com.github.AshleyThew:minescape-mod-api:-SNAPSHOT'
}
```

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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// Read in on this plugin channel
String channelName = Channels.GENERAL.getChannelName();

// Initialize channel handler for general channel
ChannelDataHandler<GeneralType> handler = new ChannelDataHandler<>(Channels.GENERAL, GeneralType.class);

// Handle incoming JSON data, this will be from your channel handler
String jsonString = "{\"type\":\"LOGIN_SKILLS\",\"data\":{\"skillType\":\"ATTACK\",\"level\":75,\"experience\":1210421.0}}";
JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

// Parse and handle the data
Object result = handler.getData(jsonObject);
```

### Skills Experience Tracking

```java
import com.minescape.mod.api.channel.general.skills.LoginSkillsData;
import com.minescape.mod.api.channel.general.skills.GameplaySkillsExperienceData;
import com.minescape.mod.api.types.skills.SkillType;

// Initialize channel handler for general channel
ChannelDataHandler<GeneralType> handler = new ChannelDataHandler<>(Channels.GENERAL, GeneralType.class);

// Handle incoming JSON data with multiple skills
String jsonString = "{\"type\":\"LOGIN_SKILLS\",\"data\":{\"levels\":{\"MAGIC\":99,\"ATTACK\":75},\"experiences\":{\"MAGIC\":13034431.0,\"ATTACK\":1210421.0}}}";
JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

// Get the general type and data
GeneralType type = handler.getType(jsonObject);
Object data = handler.getData(jsonObject);

// Handle different data types using modern switch expression
switch (type) {
    case LOGIN_SKILLS -> {
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
    }
    case GAMEPLAY_SKILLS_EXPERIENCE -> {
        GameplaySkillsExperienceData expData = (GameplaySkillsExperienceData) data;
        SkillType skill = expData.skillType();           // Skill type
        double gained = expData.experienceGained();      // Experience gained
        double total = expData.totalExperience();        // Total experience
        System.out.println("Gained " + gained + " XP in " + skill + " (total: " + total + ")");
    }
}
```

### NeoForge Integration

Here's an example of how to integrate the MineScape API with NeoForge's custom packet system:

```java
import com.minescape.mod.api.channel.ChannelDataHandler;
import com.minescape.mod.api.channel.Channels;
import com.minescape.mod.api.channel.general.GeneralType;
import com.minescape.mod.api.channel.general.skills.LoginSkillsData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public record MinescapePacket(String jsonData) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MinescapePacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.parse(Channels.GENERAL.getChannelName()));

    private static final Gson GSON = new Gson();
    private static final ChannelDataHandler<GeneralType> HANDLER =
        new ChannelDataHandler<>(Channels.GENERAL, GeneralType.class);

    public static final StreamCodec<RegistryFriendlyByteBuf, MinescapePacket> STREAM_CODEC = StreamCodec.of(
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
                return new MinescapePacket(json);
            }
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

@SubscribeEvent
public static void register(RegisterPayloadHandlersEvent event) {
    PayloadRegistrar registrar = event.registrar("1").optional();
    registrar.playToClient(
            MinescapePacket.TYPE,
            MinescapePacket.STREAM_CODEC,
            MinescapeNetworking::handlePacketOnMain
    );
}

public static void handlePacketOnMain(final MinescapePacket packet, final IPayloadContext context) {
    try {
        // Parse the JSON data using MineScape API
        JsonObject jsonObject = JsonParser.parseString(packet.jsonData()).getAsJsonObject();
        GeneralType type = MinescapePacket.HANDLER.getType(jsonObject);
        Object data = MinescapePacket.HANDLER.getData(jsonObject);

        // Handle different data types
        switch (type) {
            case LOGIN_SKILLS -> {
                LoginSkillsData loginData = (LoginSkillsData) data;
                System.out.println("Received login skills data with " + loginData.levels().size() + " skills");

                // Process skill data
                loginData.levels().forEach((skill, level) -> {
                    Double experience = loginData.getExperience(skill);
                    System.out.println(skill + ": Level " + level + " (" + experience + " XP)");
                });
            }
            case GAMEPLAY_SKILLS_EXPERIENCE -> {
                // Handle other packet types...
            }
        }
    } catch (Exception e) {
        System.err.println("Failed to process MineScape packet: " + e.getMessage());
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

For detailed API documentation, see the Javadoc comments in the source code.

## Support

If you encounter any issues or have questions, please open an issue on GitHub.
