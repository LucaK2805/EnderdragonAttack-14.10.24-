package gamemode.enderdragonattack.Stats;

import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.Core;
import gamemode.enderdragonattack.LevelSystem.LevelSystem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class StatsTop implements CommandExecutor, TabCompleter {

    private final Core plugin;
    private final StatsDataBase statsDataBase;
    private final LevelSystem levelSystem;
    private final String prefix;
    private final List<String> STAT_TYPES = Arrays.asList("Games", "Damage", "Level");

    public StatsTop(Core plugin, StatsDataBase statsDataBase, LevelSystem levelSystem) {
        this.plugin = plugin;
        this.statsDataBase = statsDataBase;
        this.levelSystem = levelSystem;
        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = ChatColor.GRAY + "[" + gradientPrefix + ChatColor.GRAY + "] " + ChatColor.RESET;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        statsDataBase.onReload();
        if (args.length != 1) {
            sender.sendMessage(prefix + ChatColor.RED + "Usage: /top <Games|Damage|Level>");
            return true;
        }

        String statType = args[0].toLowerCase();
        List<Map.Entry<String, Double>> topList;

        switch (statType) {
            case "games":
                topList = getTopList(StatType.GAMES);
                break;
            case "damage":
                topList = getTopList(StatType.DAMAGE);
                break;
            case "level":
                topList = getTopList(StatType.LEVEL);
                break;
            default:
                sender.sendMessage(prefix + ChatColor.RED + "Invalid stat type. Use Games, Damage, or Level.");
                return true;
        }

        sender.sendMessage(" ");
        sender.sendMessage(prefix + ChatColor.GOLD + "=== Top 10 " + statType.toUpperCase() + " ===");
        sender.sendMessage(" ");
        for (int i = 0; i < topList.size() && i < 10; i++) {
            Map.Entry<String, Double> entry = topList.get(i);
            sender.sendMessage(formatLeaderboardEntry(i + 1, entry.getKey(), entry.getValue()));
        }
        sender.sendMessage(" ");

        // Add player's own ranking
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int playerRank = getPlayerRank(player.getName(), topList);
            if (playerRank > 10) {
                Map.Entry<String, Double> playerEntry = topList.get(playerRank - 1);
                sender.sendMessage(formatLeaderboardEntry(playerRank, playerEntry.getKey(), playerEntry.getValue()));
            }
        }

        sender.sendMessage(prefix + ChatColor.GOLD + "--------------------");

        return true;
    }

    private String formatLeaderboardEntry(int rank, String playerName, double value) {
        return prefix + String.format("%s%d. %s%s: %s%.0f",
                ChatColor.YELLOW, rank,
                ChatColor.WHITE, playerName,
                ChatColor.GREEN, value);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return STAT_TYPES.stream()
                    .filter(type -> type.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private List<Map.Entry<String, Double>> getTopList(StatType statType) {
        Map<String, Double> statsMap = new HashMap<>();
        List<String> playerNames;

        if (statType == StatType.LEVEL) {
            playerNames = levelSystem.getAllPlayerNames();
        } else {
            playerNames = statsDataBase.getAllPlayerNames();
        }

        for (String playerName : playerNames) {
            double value = 0;
            switch (statType) {
                case GAMES:
                    value = statsDataBase.getGamesPlayed(playerName);
                    break;
                case DAMAGE:
                    value = statsDataBase.getTotalDamage(playerName);
                    break;
                case LEVEL:
                    value = levelSystem.getOfflinePlayerLevel(playerName);
                    break;
            }
            statsMap.put(playerName, value);
        }

        List<Map.Entry<String, Double>> sortedList = new ArrayList<>(statsMap.entrySet());
        sortedList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        return sortedList;
    }

    private int getPlayerRank(String playerName, List<Map.Entry<String, Double>> topList) {
        for (int i = 0; i < topList.size(); i++) {
            if (topList.get(i).getKey().equals(playerName)) {
                return i + 1;
            }
        }
        return topList.size() + 1; // If player is not in the list, they are ranked last
    }

    private enum StatType {
        GAMES, DAMAGE, LEVEL
    }
}