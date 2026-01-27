package lol.hanyuu.townyDiscord.listeners;

import com.gmail.goosius.siegewar.SiegeController;
import com.gmail.goosius.siegewar.enums.SiegeSide;
import com.gmail.goosius.siegewar.events.BattleSessionEndedEvent;
import com.gmail.goosius.siegewar.events.BattleSessionStartedEvent;
import com.gmail.goosius.siegewar.events.SiegeEndEvent;
import com.gmail.goosius.siegewar.events.SiegeWarStartEvent;
import com.gmail.goosius.siegewar.objects.Siege;
import lol.hanyuu.townyDiscord.TownyDiscord;
import lol.hanyuu.townyDiscord.config.LangManager;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.Color;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.List;

public class SiegeWarEventListener implements Listener {

    private final TownyDiscord plugin;

    public SiegeWarEventListener(TownyDiscord plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSiegeStart(SiegeWarStartEvent event) {
        if (!plugin.getConfigManager().isNotificationEnabled("siege.started")) return;
        String channelId = plugin.getConfigManager().getNotificationChannelId("siege.started");
        if (channelId.isEmpty()) return;

        Siege siege = event.getSiege();
        String townName = siege.getTown().getName();
        String attackerName = siege.getAttackerName();

        LangManager lang = plugin.getLangManager();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(lang.get("notification.siege_started.title"));
        eb.setDescription(MessageFormat.format(lang.get("notification.siege_started.desc"), townName, attackerName));
        
        // Detail: Siege Type
        try {
            String type = siege.getSiegeType().name();
            eb.addField(lang.get("notification.info.type"), type, true);
        } catch (Exception ignored) {}

        eb.setColor(Color.RED);
        eb.setTimestamp(Instant.now());
        eb.setFooter("TownyDiscord", null);

        plugin.getDiscordManager().sendEmbed(channelId, eb.build());
    }

    @EventHandler
    public void onSiegeEnd(SiegeEndEvent event) {
        Siege siege = event.getSiege();
        // Check if winner is null
        SiegeSide winner = siege.getSiegeWinner();
        
        String townName = siege.getTown().getName();
        String attackerName = siege.getAttackerName();
        LangManager lang = plugin.getLangManager();

        if (winner == SiegeSide.ATTACKERS) {
            if (!plugin.getConfigManager().isNotificationEnabled("siege.captured")) return;
            String channelId = plugin.getConfigManager().getNotificationChannelId("siege.captured");
            if (channelId.isEmpty()) return;

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(lang.get("notification.siege_captured.title"));
            eb.setDescription(MessageFormat.format(lang.get("notification.siege_captured.desc"), townName, attackerName));
            
            // Battle Points
            addPointField(eb, siege, lang);
            
            // War Chest
            try {
                double chest = siege.getWarChestAmount();
                if (chest > 0) {
                    eb.addField(lang.get("notification.info.chest"), String.format("%,.2f", chest), true);
                }
            } catch (Exception ignored) {}

            eb.setColor(Color.DARK_GRAY);
            eb.setTimestamp(Instant.now());
            eb.setFooter("TownyDiscord", null);

            plugin.getDiscordManager().sendEmbed(channelId, eb.build());

        } else if (winner == SiegeSide.DEFENDERS) {
            if (!plugin.getConfigManager().isNotificationEnabled("siege.defended")) return;
            String channelId = plugin.getConfigManager().getNotificationChannelId("siege.defended");
            if (channelId.isEmpty()) return;

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(lang.get("notification.siege_defended.title"));
            eb.setDescription(MessageFormat.format(lang.get("notification.siege_defended.desc"), townName, attackerName));
            
            // Battle Points
            addPointField(eb, siege, lang);

            eb.setColor(Color.CYAN);
            eb.setTimestamp(Instant.now());
            eb.setFooter("TownyDiscord", null);

            plugin.getDiscordManager().sendEmbed(channelId, eb.build());
        }
    }

    @EventHandler
    public void onSessionStart(BattleSessionStartedEvent event) {
        if (!plugin.getConfigManager().isNotificationEnabled("siege.session_started")) return;
        String channelId = plugin.getConfigManager().getNotificationChannelId("siege.session_started");
        if (channelId.isEmpty()) return;

        LangManager lang = plugin.getLangManager();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(lang.get("notification.siege_session_started.title"));
        eb.setDescription(lang.get("notification.siege_session_started.desc"));
        eb.setColor(Color.YELLOW); // Warning color for session start
        eb.setTimestamp(Instant.now());
        eb.setFooter("TownyDiscord", null);

        plugin.getDiscordManager().sendEmbed(channelId, eb.build());
    }

    @EventHandler
    public void onSessionEnd(BattleSessionEndedEvent event) {
        if (!plugin.getConfigManager().isNotificationEnabled("siege.session_ended")) return;
        String channelId = plugin.getConfigManager().getNotificationChannelId("siege.session_ended");
        if (channelId.isEmpty()) return;

        LangManager lang = plugin.getLangManager();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(lang.get("notification.siege_session_ended.title"));
        
        StringBuilder desc = new StringBuilder();
        desc.append(lang.get("notification.siege_session_ended.desc")).append("\n\n");
        
        List<Siege> sieges = SiegeController.getSieges();
        if (sieges == null || sieges.isEmpty()) {
            desc.append(lang.get("notification.siege_session_ended.empty"));
        } else {
            for (Siege s : sieges) {
                String town = s.getTown().getName();
                String attacker = s.getAttackerName();
                String points = s.getFormattedAttackerBattlePoints() + " - " + s.getFormattedDefenderBattlePoints();
                desc.append(MessageFormat.format(lang.get("notification.siege_session_ended.format"), town, attacker, points)).append("\n");
            }
        }
        
        eb.setDescription(desc.toString());
        eb.setColor(Color.GREEN); // Safe color for session end
        eb.setTimestamp(Instant.now());
        eb.setFooter("TownyDiscord", null);

        plugin.getDiscordManager().sendEmbed(channelId, eb.build());
    }
    
    private void addPointField(EmbedBuilder eb, Siege siege, LangManager lang) {
        try {
            String points = String.format("%s - %s", 
                siege.getFormattedAttackerBattlePoints(), 
                siege.getFormattedDefenderBattlePoints());
            eb.addField(lang.get("notification.info.points"), points, false);
        } catch (Exception ignored) {}
    }
}
