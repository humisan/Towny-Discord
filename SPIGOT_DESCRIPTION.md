# Spigot Resource Description (BBCode)

Copy the text below into the Spigot resource description editor.

```bbcode
[CENTER][SIZE=6][B]TownyDiscord[/B][/SIZE]
[SIZE=4][I]Seamlessly integrate Towny Advanced & SiegeWar with Discord[/I][/SIZE]

[IMG]https://via.placeholder.com/600x150?text=TownyDiscord+Banner[/IMG]
[/CENTER]

[SIZE=5][B]📢 Overview[/B][/SIZE]
TownyDiscord is a modern, lightweight, and powerful bridge between your [B]Towny Advanced[/B] server and your Discord community. Unlike other integration plugins, TownyDiscord focuses on providing **Rich Embeds** via Discord Slash Commands and offering extensive support for **SiegeWar** notifications.

Whether you want to check a town's bank balance from your phone or get notified instantly when a siege begins, TownyDiscord has you covered.

[SIZE=5][B]✅ Requirements[/B][/SIZE]
Please ensure you have the following installed before using this plugin:
[LIST]
[*] [B]Java 21[/B] or higher (Required)
[*] [B]Spigot/Paper 1.21+[/B] (Required)
[*] [URL='https://www.spigotmc.org/resources/towny-advanced.72694/'][B]Towny Advanced[/B][/URL] (Required)
[*] [URL='https://www.spigotmc.org/resources/siegewar.73463/'][B]SiegeWar[/B][/URL] (Optional - Required only for Siege notifications)
[/LIST]

[SIZE=5][B]✨ Key Features[/B][/SIZE]
[LIST]
[*] [B]Discord Slash Commands[/B]: No more clunky chat commands. Use modern UI-friendly slash commands directly in Discord.
[*] [B]Rich Embed Profiles[/B]: Beautifully formatted cards for:
[LIST]
[*]🏘️ Towns (Mayor, Residents, Balance, Taxes, etc.)
[*]🏰 Nations (King, Capital, Towns, Allies, Enemies)
[*]👤 Residents (Rank, Last Online, Friends)
[/LIST]
[*] [B]⚔️ SiegeWar Integration[/B]: Full support for SiegeWar events! Get real-time alerts for:
[LIST]
[*]Siege Started / Captured / Defended
[*]Battle Session Start / End
[/LIST]
[*] [B]🏆 Leaderboards[/B]: Display pagination-supported leaderboards for Towns and Nations directly in Discord.
[*] [B]🤖 Live Bot Status[/B]: Automatically updates the bot's activity with server stats (e.g., "5/100 Players Online", "10 Towns | 2 Nations").
[*] [B]🌐 Multi-Language Support[/B]: Comes with built-in English ([CODE]en[/CODE]) and Japanese ([CODE]ja[/CODE]) localization.
[/LIST]

[SIZE=5][B]📸 Screenshots[/B][/SIZE]
[SPOILER="Click to view Screenshots"]
[I](Please upload screenshots of your Discord Embeds here)[/I]
[/SPOILER]

[SIZE=5][B]🛠️ Commands[/B][/SIZE]
All commands are Discord Slash Commands:
[CODE]
/town [name]       - View detailed info about a Town
/nation [name]     - View detailed info about a Nation
/res [name]        - View detailed info about a Resident
/townlist [page]   - View the Town leaderboard
/nationlist [page] - View the Nation leaderboard
[/CODE]

[SIZE=5][B]⚙️ Configuration[/B][/SIZE]
Simple and easy to configure via [CODE]config.yml[/CODE].

[CODE]
# Discord Bot Token
discord_token: "YOUR_TOKEN_HERE"

# Language (en or ja)
language: en

# Activity Status
activity:
  enabled: true
  messages:
    - "{online}/{max_players} Players Online"
    - "{towns} Towns | {nations} Nations"

# Event Notifications (Enable/Disable specific events)
notifications:
  town:
    created:
      enabled: true
      channel_id: "123456789..."
  siege:
    started:
      enabled: true
      channel_id: "987654321..."
[/CODE]

[SIZE=5][B]📥 Installation[/B][/SIZE]
[LIST=1]
[*]Ensure you have installed [B]Java 21[/B] and [B]Towny Advanced[/B].
[*]Download [B]TownyDiscord[/B] and drop it into your [CODE]/plugins[/CODE] folder.
[*]Restart your server.
[*]Edit [CODE]plugins/TownyDiscord/config.yml[/CODE] and add your Discord Bot Token.
[*]Restart the server or plugin. Done!
[/LIST]

[SIZE=5][B]🐛 Bug Reports & Support[/B][/SIZE]
If you find any bugs or have feature requests, please report them on our [URL='YOUR_GITHUB_LINK_HERE']GitHub Issue Tracker[/URL] or in the discussion tab.
```
