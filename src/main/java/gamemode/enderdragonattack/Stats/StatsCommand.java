package gamemode.enderdragonattack.Stats;

import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatsCommand implements CommandExecutor {

    private final Core plugin;
    private final StatsDataBase statsDataBase;
    private final String prefix;

    public StatsCommand(Core plugin, StatsDataBase statsDataBase) {
        this.plugin = plugin;
        this.statsDataBase = statsDataBase;
        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = ChatColor.GRAY + "[" + gradientPrefix + ChatColor.GRAY + "] " + ChatColor.RESET;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Reload stats data before processing the command
        statsDataBase.reloadStats();

        OfflinePlayer targetPlayer;

        if (args.length > 0) {
            targetPlayer = Bukkit.getOfflinePlayer(args[0]);
            if (targetPlayer == null || (!targetPlayer.hasPlayedBefore() && !targetPlayer.isOnline())) {
                sender.sendMessage(prefix + ChatColor.RED + "Player not found.");
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(prefix + ChatColor.RED + "Please specify a player name.");
                return true;
            }
            targetPlayer = (Player) sender;
        }

        UUID playerUUID = targetPlayer.getUniqueId();
        int gamesPlayed = statsDataBase.getGamesPlayed(playerUUID);
        double totalDamage = statsDataBase.getTotalDamage(playerUUID);

        sender.sendMessage(" ");
        sender.sendMessage(prefix + ChatColor.GOLD + "=== Statistics for " + targetPlayer.getName() + " ===");
        sender.sendMessage(" ");
        sender.sendMessage(prefix + ChatColor.YELLOW + "Games played: " + ChatColor.WHITE + gamesPlayed);
        sender.sendMessage(prefix + ChatColor.YELLOW + "Total damage: " + ChatColor.WHITE + String.format("%.2f", totalDamage));
        sender.sendMessage(" ");

        return true;
    }
}