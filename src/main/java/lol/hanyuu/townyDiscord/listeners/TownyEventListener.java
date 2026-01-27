package lol.hanyuu.townyDiscord.listeners;

import com.palmergames.bukkit.towny.event.DeleteNationEvent;
import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.event.NewNationEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import lol.hanyuu.townyDiscord.TownyDiscord;
import lol.hanyuu.townyDiscord.config.LangManager;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.Color;
import java.time.Instant;

public class TownyEventListener implements Listener {

    private final TownyDiscord plugin;

    public TownyEventListener(TownyDiscord plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNewTown(NewTownEvent event) {
        if (!plugin.getConfigManager().isNotificationEnabled("town.created")) return;
        String channelId = plugin.getConfigManager().getNotificationChannelId("town.created");
        if (channelId.isEmpty()) return;

        LangManager lang = plugin.getLangManager();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(lang.get("notification.town_created.title"));
        eb.setDescription(String.format(lang.get("notification.town_created.desc"), event.getTown().getName(), event.getTown().getMayor().getName()));
        eb.setColor(Color.GREEN);
        eb.setTimestamp(Instant.now());
        eb.setFooter("TownyDiscord", null);

        plugin.getDiscordManager().sendEmbed(channelId, eb.build());
    }

    @EventHandler
    public void onDeleteTown(DeleteTownEvent event) {
        if (!plugin.getConfigManager().isNotificationEnabled("town.deleted")) return;
        String channelId = plugin.getConfigManager().getNotificationChannelId("town.deleted");
        if (channelId.isEmpty()) return;

        LangManager lang = plugin.getLangManager();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(lang.get("notification.town_deleted.title"));
        eb.setDescription(String.format(lang.get("notification.town_deleted.desc"), event.getTownName()));
        eb.setColor(Color.RED);
        eb.setTimestamp(Instant.now());
        eb.setFooter("TownyDiscord", null);

        plugin.getDiscordManager().sendEmbed(channelId, eb.build());
    }

    @EventHandler
    public void onNewNation(NewNationEvent event) {
        if (!plugin.getConfigManager().isNotificationEnabled("nation.created")) return;
        String channelId = plugin.getConfigManager().getNotificationChannelId("nation.created");
        if (channelId.isEmpty()) return;

        LangManager lang = plugin.getLangManager();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(lang.get("notification.nation_created.title"));
        eb.setDescription(String.format(lang.get("notification.nation_created.desc"), event.getNation().getName(), event.getNation().getKing().getName()));
        eb.setColor(Color.ORANGE);
        eb.setTimestamp(Instant.now());
        eb.setFooter("TownyDiscord", null);

        plugin.getDiscordManager().sendEmbed(channelId, eb.build());
    }

    @EventHandler
    public void onDeleteNation(DeleteNationEvent event) {
        if (!plugin.getConfigManager().isNotificationEnabled("nation.deleted")) return;
        String channelId = plugin.getConfigManager().getNotificationChannelId("nation.deleted");
        if (channelId.isEmpty()) return;

        LangManager lang = plugin.getLangManager();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(lang.get("notification.nation_deleted.title"));
        eb.setDescription(String.format(lang.get("notification.nation_deleted.desc"), event.getNationName()));
        eb.setColor(Color.DARK_GRAY);
        eb.setTimestamp(Instant.now());
        eb.setFooter("TownyDiscord", null);

        plugin.getDiscordManager().sendEmbed(channelId, eb.build());
    }
}
