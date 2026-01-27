package lol.hanyuu.townyDiscord.discord;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import lol.hanyuu.townyDiscord.TownyDiscord;
import lol.hanyuu.townyDiscord.config.LangManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DiscordCommandListener extends ListenerAdapter {

    private final TownyDiscord plugin;

    public DiscordCommandListener(TownyDiscord plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        if (!command.equals("town") && !command.equals("nation") && !command.equals("res") 
                && !command.equals("townlist") && !command.equals("nationlist")) {
            return;
        }

        event.deferReply().queue();
        InteractionHook hook = event.getHook();

        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                LangManager lang = plugin.getLangManager();
                TownyAPI towny = TownyAPI.getInstance();

                if (towny == null) {
                     hook.editOriginal(lang.get("error.towny_disabled")).queue();
                     return;
                }

                switch (command) {
                    case "town":
                        hook.editOriginalEmbeds(handleTown(event, towny, lang).build()).queue();
                        break;
                    case "nation":
                        hook.editOriginalEmbeds(handleNation(event, towny, lang).build()).queue();
                        break;
                    case "res":
                        hook.editOriginalEmbeds(handleResident(event, towny, lang).build()).queue();
                        break;
                    case "townlist":
                        int tPage = event.getOption("page") != null ? event.getOption("page").getAsInt() : 1;
                        sendTownList(hook, towny, lang, tPage);
                        break;
                    case "nationlist":
                        int nPage = event.getOption("page") != null ? event.getOption("page").getAsInt() : 1;
                        sendNationList(hook, towny, lang, nPage);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
                hook.editOriginal(plugin.getLangManager().get("error.generic")).queue();
            }
        });
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String[] idParts = event.getComponentId().split(":");
        if (idParts.length != 2) return;
        
        String type = idParts[0];
        int page;
        try {
            page = Integer.parseInt(idParts[1]);
        } catch (NumberFormatException e) {
            return;
        }

        event.deferEdit().queue();
        InteractionHook hook = event.getHook();

        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                LangManager lang = plugin.getLangManager();
                TownyAPI towny = TownyAPI.getInstance();

                if (towny == null) {
                    hook.editOriginal(lang.get("error.towny_disabled")).queue();
                    return;
                }

                if (type.equals("townlist")) {
                    sendTownList(hook, towny, lang, page);
                } else if (type.equals("nationlist")) {
                    sendNationList(hook, towny, lang, page);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private String formatBoolean(boolean bool, String trueKey, String falseKey, LangManager lang) {
        return bool ? lang.get(trueKey) : lang.get(falseKey);
    }
    
    private String formatDate(long timestamp) {
        return new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm").format(new java.util.Date(timestamp));
    }
    
    private String formatBalance(double balance) {
        return String.format("%,.2f", balance);
    }

    private void sendTownList(InteractionHook hook, TownyAPI towny, LangManager lang, int page) {
        if (page < 1) page = 1;
        
        List<Town> towns = towny.getTowns();
        towns.sort((t1, t2) -> Integer.compare(t2.getNumResidents(), t1.getNumResidents()));
        
        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) towns.size() / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(String.format(lang.get("embed.list.town_title"), page, totalPages));
        eb.setColor(Color.GREEN);
        
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, towns.size());
        
        StringBuilder desc = new StringBuilder();
        if (towns.isEmpty()) {
            desc.append(lang.get("embed.list.empty"));
        } else {
            for (int i = startIndex; i < endIndex; i++) {
                Town t = towns.get(i);
                String line = String.format(lang.get("embed.list.format"), t.getName(), t.getNumResidents(), t.getMayor().getName());
                desc.append(line).append("\n");
            }
        }
        
        eb.setDescription(desc.toString());
        eb.setFooter("TownyDiscord", null);
        eb.setTimestamp(Instant.now());
        
        List<Button> buttons = new ArrayList<>();
        if (page > 1) {
            buttons.add(Button.primary("townlist:" + (page - 1), lang.get("embed.list.prev")));
        } else {
            buttons.add(Button.primary("townlist:prev", lang.get("embed.list.prev")).asDisabled());
        }
        
        if (page < totalPages) {
            buttons.add(Button.primary("townlist:" + (page + 1), lang.get("embed.list.next")));
        } else {
            buttons.add(Button.primary("townlist:next", lang.get("embed.list.next")).asDisabled());
        }

        hook.editOriginalEmbeds(eb.build()).setActionRow(buttons).queue();
    }

    private void sendNationList(InteractionHook hook, TownyAPI towny, LangManager lang, int page) {
        if (page < 1) page = 1;
        
        List<Nation> nations = towny.getNations();
        nations.sort((n1, n2) -> Integer.compare(n2.getNumResidents(), n1.getNumResidents()));
        
        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) nations.size() / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(String.format(lang.get("embed.list.nation_title"), page, totalPages));
        eb.setColor(Color.ORANGE);
        
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, nations.size());
        
        StringBuilder desc = new StringBuilder();
        if (nations.isEmpty()) {
            desc.append(lang.get("embed.list.empty"));
        } else {
            for (int i = startIndex; i < endIndex; i++) {
                Nation n = nations.get(i);
                String line = String.format(lang.get("embed.list.format"), n.getName(), n.getNumResidents(), n.getKing().getName());
                desc.append(line).append("\n");
            }
        }
        
        eb.setDescription(desc.toString());
        eb.setFooter("TownyDiscord", null);
        eb.setTimestamp(Instant.now());

        List<Button> buttons = new ArrayList<>();
        if (page > 1) {
            buttons.add(Button.primary("nationlist:" + (page - 1), lang.get("embed.list.prev")));
        } else {
            buttons.add(Button.primary("nationlist:prev", lang.get("embed.list.prev")).asDisabled());
        }
        
        if (page < totalPages) {
            buttons.add(Button.primary("nationlist:" + (page + 1), lang.get("embed.list.next")));
        } else {
            buttons.add(Button.primary("nationlist:next", lang.get("embed.list.next")).asDisabled());
        }

        hook.editOriginalEmbeds(eb.build()).setActionRow(buttons).queue();
    }

    private EmbedBuilder handleTown(SlashCommandInteractionEvent event, TownyAPI towny, LangManager lang) {
        String name = event.getOption("name") != null ? event.getOption("name").getAsString() : null;
        
        if (name == null) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.RED);
            eb.setDescription(lang.get("error.specify_name"));
            return eb;
        }

        Town town = towny.getTown(name);

        if (town == null) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.RED);
            eb.setDescription(lang.get("error.town_not_found", name));
            return eb;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(lang.get("embed.town.title", town.getName()));
        eb.setColor(Color.GREEN);
        
        eb.setThumbnail("https://minotar.net/helm/" + town.getMayor().getName() + "/100.png");
        
        eb.addField(lang.get("embed.town.mayor"), town.getMayor().getName(), true);
        
        if (town.hasNation()) {
            try {
                eb.addField(lang.get("embed.town.nation"), town.getNation().getName(), true);
            } catch (Exception ignored) {}
        } else {
             eb.addField(lang.get("embed.town.nation"), lang.get("embed.common.none"), true);
        }

        eb.addField(lang.get("embed.town.residents"), town.getNumResidents() + "人", true);
        String plots = town.getTownBlocks().size() + " / " + town.getMaxTownBlocks();
        eb.addField(lang.get("embed.town.plots"), plots, true);

        try {
             eb.addField(lang.get("embed.town.balance"), formatBalance(town.getAccount().getHoldingBalance()), true);
        } catch (Exception ignored) {
             eb.addField(lang.get("embed.town.balance"), "N/A", true);
        }
        
        eb.addField(lang.get("embed.town.taxes"), town.getTaxes() + "%", true);
        
        if (town.hasHomeBlock()) {
            try {
                com.palmergames.bukkit.towny.object.TownBlock home = town.getHomeBlock();
                String loc = String.format("%s (%d, %d)", home.getWorld().getName(), home.getX(), home.getZ());
                eb.addField(lang.get("embed.town.location"), loc, true);
            } catch (Exception ignored) {}
        }

        StringBuilder status = new StringBuilder();
        status.append(formatBoolean(town.isOpen(), "embed.common.open", "embed.common.closed", lang)).append("\n");
        status.append(formatBoolean(town.isPVP(), "embed.common.pvp_on", "embed.common.pvp_off", lang)).append("\n");
        status.append(formatBoolean(town.isPublic(), "embed.common.public", "embed.common.private", lang));
        eb.addField(lang.get("embed.town.status"), status.toString(), true);
        
        StringBuilder flags = new StringBuilder();
        flags.append(town.isFire() ? "🔥 " : "");
        flags.append(town.isExplosion() ? "💣 " : "");
        flags.append(town.hasMobs() ? "🧟 " : "");
        if (flags.length() == 0) flags.append(lang.get("embed.common.none"));
        eb.addField(lang.get("embed.town.flags"), flags.toString(), true);

        eb.addField(lang.get("embed.town.founded"), formatDate(town.getRegistered()), true);

        if (!town.getBoard().isEmpty()) {
            eb.addField(lang.get("embed.town.board"), town.getBoard(), false);
        }

        eb.setFooter("TownyDiscord", null);
        eb.setTimestamp(Instant.now());
        
        return eb;
    }

    private EmbedBuilder handleNation(SlashCommandInteractionEvent event, TownyAPI towny, LangManager lang) {
        String name = event.getOption("name") != null ? event.getOption("name").getAsString() : null;
        
        if (name == null) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.RED);
            eb.setDescription(lang.get("error.specify_name"));
            return eb;
        }

        Nation nation = towny.getNation(name);

        if (nation == null) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.RED);
            eb.setDescription(lang.get("error.nation_not_found", name));
            return eb;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(lang.get("embed.nation.title", nation.getName()));
        eb.setColor(Color.ORANGE);
        
        eb.setThumbnail("https://minotar.net/helm/" + nation.getKing().getName() + "/100.png");

        eb.addField(lang.get("embed.nation.king"), nation.getKing().getName(), true);

        if (nation.hasCapital()) {
            eb.addField(lang.get("embed.nation.capital"), nation.getCapital().getName(), true);
        }
        
        eb.addField(lang.get("embed.nation.residents"), nation.getNumResidents() + "人", true);
        eb.addField(lang.get("embed.nation.towns"), nation.getNumTowns() + "町", true);
        
        try {
             eb.addField(lang.get("embed.nation.balance"), formatBalance(nation.getAccount().getHoldingBalance()), true);
        } catch (Exception ignored) {
             eb.addField(lang.get("embed.nation.balance"), "N/A", true);
        }
        
        eb.addField(lang.get("embed.nation.taxes"), nation.getTaxes() + "%", true);
        
        String spawnStatus = nation.isPublic() ? lang.get("embed.common.public") : lang.get("embed.common.private");
        if (nation.isNeutral()) spawnStatus += " / " + lang.get("embed.common.neutral");
        eb.addField(lang.get("embed.nation.spawn"), spawnStatus, true);
        
        eb.addField(lang.get("embed.nation.founded"), formatDate(nation.getRegistered()), true);

        // Allies
        if (!nation.getAllies().isEmpty()) {
            String allies = nation.getAllies().stream().map(Nation::getName).collect(java.util.stream.Collectors.joining(", "));
            eb.addField(lang.get("embed.nation.allies"), allies, false);
        } else {
            eb.addField(lang.get("embed.nation.allies"), lang.get("embed.common.none"), false);
        }

        // Enemies
        if (!nation.getEnemies().isEmpty()) {
            String enemies = nation.getEnemies().stream().map(Nation::getName).collect(java.util.stream.Collectors.joining(", "));
            eb.addField(lang.get("embed.nation.enemies"), enemies, false);
        } else {
            eb.addField(lang.get("embed.nation.enemies"), lang.get("embed.common.none"), false);
        }

        eb.setFooter("TownyDiscord", null);
        eb.setTimestamp(Instant.now());
        
        return eb;
    }

    private EmbedBuilder handleResident(SlashCommandInteractionEvent event, TownyAPI towny, LangManager lang) {
        String name = event.getOption("name") != null ? event.getOption("name").getAsString() : null;
        
        if (name == null) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.RED);
            eb.setDescription(lang.get("error.specify_name"));
            return eb;
        }

        Resident resident = towny.getResident(name);

        if (resident == null) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.RED);
            eb.setDescription(lang.get("error.resident_not_found", name));
            return eb;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(lang.get("embed.resident.title", resident.getName()));
        eb.setColor(Color.BLUE);
        
        eb.setThumbnail("https://minotar.net/helm/" + resident.getName() + "/100.png");

        // Town & Rank
        if (resident.hasTown()) {
             try {
                eb.addField(lang.get("embed.resident.town"), resident.getTown().getName(), true);
             } catch (Exception ignored) {}
        } else {
             eb.addField(lang.get("embed.resident.town"), lang.get("embed.common.none"), true);
        }
        
        // Title / Surname
        if (resident.hasTitle()) {
             eb.addField(lang.get("embed.resident.title_name"), resident.getTitle(), true);
        }
        if (resident.hasSurname()) {
             eb.addField(lang.get("embed.resident.surname"), resident.getSurname(), true);
        }
        
        // Ranks
        if (!resident.getTownRanks().isEmpty() || !resident.getNationRanks().isEmpty()) {
            java.util.List<String> ranks = new java.util.ArrayList<>(resident.getTownRanks());
            ranks.addAll(resident.getNationRanks());
            eb.addField(lang.get("embed.resident.rank"), String.join(", ", ranks), true);
        }

        // Dates & Status
        eb.addField(lang.get("embed.resident.last_online"), formatDate(resident.getLastOnline()), true);
        eb.addField(lang.get("embed.resident.joined"), formatDate(resident.getRegistered()), true);

        boolean isOnline = Bukkit.getPlayer(resident.getName()) != null;
        eb.addField(lang.get("embed.resident.status"), isOnline ? lang.get("embed.common.online") : lang.get("embed.common.offline"), true);

        // Plots
        eb.addField(lang.get("embed.resident.plots"), resident.getTownBlocks().size() + " chunk(s)", true);

        // Friends
        if (!resident.getFriends().isEmpty()) {
            String friends = resident.getFriends().stream().map(Resident::getName).collect(java.util.stream.Collectors.joining(", "));
            if (friends.length() > 1024) friends = friends.substring(0, 1020) + "..."; // Avoid limit
            eb.addField(lang.get("embed.resident.friends"), friends, false);
        }

        eb.setFooter("TownyDiscord", null);
        eb.setTimestamp(Instant.now());
        
        return eb;
    }
}
