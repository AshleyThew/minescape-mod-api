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
    implementation 'com.github.AshleyThew:minescape-mod-api:1.0.0'
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
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

## Usage

### Basic Channel Communication

```java
import com.minescape.mod.api.channel.ChannelDataHandler;
import com.minescape.mod.api.channel.Channels;
import com.minescape.mod.api.channel.general.GeneralType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// Initialize channel handler for general channel
ChannelDataHandler<GeneralType> handler = new ChannelDataHandler<>(Channels.GENERAL, GeneralType.class);

// Handle incoming JSON data
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

// Handle incoming JSON data
String jsonString = "{\"type\":\"LOGIN_SKILLS\",\"data\":{\"skillType\":\"MAGIC\",\"level\":99,\"experience\":13034431.0}}";
JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

// Get the general type and data
GeneralType type = handler.getType(jsonObject);
Object data = handler.getData(jsonObject);

// Handle different data types using modern switch expression
switch (type) {
    case LOGIN_SKILLS -> {
        LoginSkillsData loginData = (LoginSkillsData) data;
        SkillType skillType = loginData.skillType();  // MAGIC
        int level = loginData.level();               // 99
        double experience = loginData.experience();   // 13034431.0
        System.out.println("Login skills: " + skillType + " level " + level + " with " + experience + " XP");
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
