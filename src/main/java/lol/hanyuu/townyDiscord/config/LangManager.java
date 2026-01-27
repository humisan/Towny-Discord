package lol.hanyuu.townyDiscord.config;

import lol.hanyuu.townyDiscord.TownyDiscord;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LangManager {
    private final TownyDiscord plugin;
    private FileConfiguration langConfig;

    public LangManager(TownyDiscord plugin, String locale) {
        this.plugin = plugin;
        loadLanguage(locale);
    }

    public void loadLanguage(String locale) {
        String fileName = "messages_" + locale + ".yml";
        File langFile = new File(plugin.getDataFolder(), fileName);

        if (!langFile.exists()) {
            plugin.saveResource(fileName, false);
        }

        langConfig = YamlConfiguration.loadConfiguration(langFile);
        
        // Defaults loading
        InputStream defConfigStream = plugin.getResource(fileName);
        if (defConfigStream != null) {
            langConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));
        }
    }

    public String get(String key) {
        return langConfig.getString(key, "MISSING_KEY: " + key);
    }

    public String get(String key, Object... args) {
        String value = langConfig.getString(key);
        if (value == null) return "MISSING_KEY: " + key;
        return String.format(value, args);
    }
}
