package lol.hanyuu.townyDiscord.config;

import lol.hanyuu.townyDiscord.TownyDiscord;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final TownyDiscord plugin;
    private FileConfiguration config;

    public ConfigManager(TownyDiscord plugin) {
        this.plugin = plugin;
        this.plugin.saveDefaultConfig();
        this.config = this.plugin.getConfig();
    }

    public void reload() {
        this.plugin.reloadConfig();
        this.config = this.plugin.getConfig();
    }

    public String getDiscordToken() {
        return config.getString("discord_token");
    }

    public String getLanguage() {
        return config.getString("language", "ja");
    }
    
    public boolean isActivityEnabled() {
        return config.getBoolean("activity.enabled", true);
    }
    
    public int getActivityUpdateInterval() {
        return config.getInt("activity.update_interval", 60);
    }
    
    public java.util.List<String> getActivityMessages() {
        return config.getStringList("activity.messages");
    }

    public boolean isNotificationEnabled(String type) {
        return config.getBoolean("notifications." + type + ".enabled", false);
    }

    public String getNotificationChannelId(String type) {
        return config.getString("notifications." + type + ".channel_id", "");
    }
}
