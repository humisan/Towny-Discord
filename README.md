# TownyDiscord

TownyDiscord is a powerful Spigot/Paper plugin that integrates [Towny Advanced](https://github.com/TownyAdvanced/Towny) with Discord. It provides rich embed messages for towns, nations, and residents, along with leaderboards, status updates, and event notifications.

## Features

- **Rich Information Embeds**: View detailed information about Towns, Nations, and Residents directly in Discord using slash commands.
- **Leaderboards**: Display lists of Towns and Nations sorted by population via `/townlist` and `/nationlist`.
- **Event Notifications**: Automatically send notifications to specified Discord channels when:
    - A new Town/Nation is created.
    - A Town/Nation is deleted or falls into ruins.
- **Bot Activity Status**: Updates the bot's status with server statistics (Online players, Town counts, etc.).
- **Multi-Language Support**: Supports English (`en`) and Japanese (`ja`) out of the box.

## Requirements

- Java 21 or higher
- Minecraft 1.21+
- [Towny Advanced](https://www.spigotmc.org/resources/towny-advanced.72694/)

## Installation

1. Download the latest `TownyDiscord-1.0.jar` from the releases page (or build it yourself).
2. Place the jar file into your server's `plugins` folder.
3. Restart the server to generate the configuration files.
4. Open `plugins/TownyDiscord/config.yml` and set your **Discord Bot Token**.
5. Restart the server or reload the plugin.

## Configuration

### `config.yml`

```yaml
# Discord Bot Token
discord_token: "YOUR_BOT_TOKEN_HERE"

# Language setting (en, ja)
language: en

# Bot Activity Status Settings
activity:
  enabled: true
  update_interval: 60
  messages:
    - "Minecraft Towny Server"
    - "{online}/{max_players} Players Online"
    - "{towns} Towns | {nations} Nations"

# Notification Settings
notifications:
  town:
    created:
      enabled: true
      channel_id: "123456789012345678" 
    deleted:
      enabled: true
      channel_id: "123456789012345678"
  nation:
    created:
      enabled: true
      channel_id: "123456789012345678"
    deleted:
      enabled: true
      channel_id: "123456789012345678"
```

## Discord Commands

Once the bot is running, it will register the following slash commands:

- `/town [name]`: Displays information about a specific town.
- `/nation [name]`: Displays information about a specific nation.
- `/res [name]`: Displays information about a specific resident.
- `/townlist [page]`: Displays a leaderboard of towns.
- `/nationlist [page]`: Displays a leaderboard of nations.

## Building from Source

```bash
./gradlew clean build
```

The compiled jar will be located in `build/libs/`.

## License

This project is licensed under the **GNU General Public License v3.0 (GPLv3)**.  
See the [LICENSE](LICENSE) file for details.

Copyright (C) 2026 humisan
