package lol.hanyuu.townyDiscord;

import lol.hanyuu.townyDiscord.config.ConfigManager;
import lol.hanyuu.townyDiscord.config.LangManager;
import lol.hanyuu.townyDiscord.discord.DiscordManager;
import lol.hanyuu.townyDiscord.listeners.SiegeWarEventListener;
import lol.hanyuu.townyDiscord.listeners.TownyEventListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class TownyDiscord extends JavaPlugin {

    private ConfigManager configManager;
    private LangManager langManager;
    private DiscordManager discordManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.configManager = new ConfigManager(this);
        this.langManager = new LangManager(this, configManager.getLanguage());
        this.discordManager = new DiscordManager(this);
        
        this.discordManager.start();
        
        getServer().getPluginManager().registerEvents(new TownyEventListener(this), this);
        
        if (getServer().getPluginManager().getPlugin("SiegeWar") != null) {
            getServer().getPluginManager().registerEvents(new SiegeWarEventListener(this), this);
            getLogger().info("SiegeWar hook enabled!");
        }
        
        getLogger().info("TownyDiscord が有効化されました！");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (this.discordManager != null) {
            this.discordManager.stop();
        }
        getLogger().info("TownyDiscord が無効化されました！");
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public LangManager getLangManager() {
        return langManager;
    }
    
    public DiscordManager getDiscordManager() {
        return discordManager;
    }
}
