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

// Initialize channel handler
ChannelDataHandler handler = new ChannelDataHandler();

// Register channels and handle data
// Example usage would go here based on your specific implementation
```

### Skills Experience Tracking

```java
import com.minescape.mod.api.channel.general.skills.GameplaySkillsExperienceData;
import com.minescape.mod.api.types.skills.SkillType;

// Create experience data
GameplaySkillsExperienceData expData = new GameplaySkillsExperienceData(
    SkillType.MINING,
    15.5,      // experience gained
    1250.0     // total experience
);

// Access data
SkillType skill = expData.skillType();
double gained = expData.experienceGained();
double total = expData.totalExperience();
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
