package lol.hanyuu.townyDiscord.discord;

import com.palmergames.bukkit.towny.TownyAPI;
import lol.hanyuu.townyDiscord.TownyDiscord;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.List;

public class DiscordManager {
    private final TownyDiscord plugin;
    private JDA jda;
    private BukkitTask activityTask;
    private int currentMsgIndex = 0;

    public DiscordManager(TownyDiscord plugin) {
        this.plugin = plugin;
    }

    public void start() {
        String token = plugin.getConfigManager().getDiscordToken();
        if (token == null || token.equals("YOUR_BOT_TOKEN_HERE") || token.isEmpty()) {
            plugin.getLogger().warning("DiscordのBotトークンが設定されていません！Discord連携は無効化されます。");
            return;
        }

        try {
            jda = JDABuilder.createLight(token, Collections.emptyList())
                    .addEventListeners(new DiscordCommandListener(plugin))
                    .build(); // Activityは後でタスクで設定するためここでは設定しない

            jda.updateCommands().addCommands(
                    Commands.slash("town", "指定した町の情報を表示します")
                            .addOption(OptionType.STRING, "name", "町名", false),
                    Commands.slash("nation", "指定した国の情報を表示します")
                            .addOption(OptionType.STRING, "name", "国名", false),
                    Commands.slash("res", "指定した住人の情報を表示します")
                            .addOption(OptionType.STRING, "name", "プレイヤー名", false),
                    Commands.slash("townlist", "町のリストを表示します")
                            .addOption(OptionType.INTEGER, "page", "ページ番号", false),
                    Commands.slash("nationlist", "国のリストを表示します")
                            .addOption(OptionType.INTEGER, "page", "ページ番号", false)
            ).queue();
            
            startActivityTask();
            
            plugin.getLogger().info("Discord Botが起動しました！");

        } catch (Exception e) {
            plugin.getLogger().severe("Discord Botの起動に失敗しました: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        if (activityTask != null && !activityTask.isCancelled()) {
            activityTask.cancel();
        }
        if (jda != null) {
            jda.shutdown();
            try {
                if (!jda.awaitShutdown(java.time.Duration.ofSeconds(10))) {
                    plugin.getLogger().warning("Discord Bot did not shutdown in 10 seconds, forcing shutdown...");
                    jda.shutdownNow();
                    jda.awaitShutdown(java.time.Duration.ofSeconds(5));
                }
            } catch (InterruptedException e) {
                plugin.getLogger().warning("Interrupted while waiting for Discord Bot to shutdown");
                Thread.currentThread().interrupt();
            }
        }
    }

    private void startActivityTask() {
        if (!plugin.getConfigManager().isActivityEnabled()) return;

        long interval = plugin.getConfigManager().getActivityUpdateInterval() * 20L; // 秒 -> Tick変換
        List<String> messages = plugin.getConfigManager().getActivityMessages();
        
        if (messages.isEmpty()) return;

        activityTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (jda == null || jda.getStatus() != JDA.Status.CONNECTED) return;

            if (currentMsgIndex >= messages.size()) {
                currentMsgIndex = 0;
            }
            
            String msg = messages.get(currentMsgIndex);
            msg = replacePlaceholders(msg);
            
            jda.getPresence().setActivity(Activity.playing(msg));
            
            currentMsgIndex++;
        }, 0L, interval);
    }
    
    private String replacePlaceholders(String msg) {
        msg = msg.replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()));
        msg = msg.replace("{max_players}", String.valueOf(Bukkit.getMaxPlayers()));
        
        if (TownyAPI.getInstance() != null) {
            msg = msg.replace("{towns}", String.valueOf(TownyAPI.getInstance().getTowns().size()));
            msg = msg.replace("{nations}", String.valueOf(TownyAPI.getInstance().getNations().size()));
            msg = msg.replace("{residents}", String.valueOf(TownyAPI.getInstance().getResidents().size()));
        } else {
            msg = msg.replace("{towns}", "?");
            msg = msg.replace("{nations}", "?");
            msg = msg.replace("{residents}", "?");
        }
        
        return msg;
    }

    public void sendEmbed(String channelId, MessageEmbed embed) {
        if (jda == null || jda.getStatus() != JDA.Status.CONNECTED) return;
        
        try {
            TextChannel channel = jda.getTextChannelById(channelId);
            if (channel != null) {
                channel.sendMessageEmbeds(embed).queue();
            } else {
                plugin.getLogger().warning("Channel with ID " + channelId + " was not found.");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send message to channel " + channelId + ": " + e.getMessage());
        }
    }
}
