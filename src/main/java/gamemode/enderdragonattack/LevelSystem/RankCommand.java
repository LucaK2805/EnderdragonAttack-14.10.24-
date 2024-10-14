package gamemode.enderdragonattack.LevelSystem;

import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.LevelSystem.LevelRank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand implements CommandExecutor {

    private final LevelRank levelRank;
    private final String prefix;

    public RankCommand() {
        this.levelRank = new LevelRank();
        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = ChatColor.translateAlternateColorCodes('&', "&f[" + gradientPrefix + "&f] " + ChatColor.RESET);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + ChatColor.RED + "Dieser Befehl kann nur von Spielern ausgeführt werden.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("ranklist")) {
            sendRankList(sender);
            return true;
        }

        return false;
    }

    private void sendRankList(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(prefix + ChatColor.BLACK + ChatColor.MAGIC + "$kGOD " + ChatColor.RESET + formatRankInfo("GOD", 100, "&6") + ChatColor.BLACK + ChatColor.MAGIC  + " $kGOD");
        sender.sendMessage(prefix + formatRankInfo("Netherite", 70, "&8"));
        sender.sendMessage(prefix + formatRankInfo("Diamond", 50, "&b"));
        sender.sendMessage(prefix + formatRankInfo("Gold", 35, "&e"));
        sender.sendMessage(prefix + formatRankInfo("Iron", 25, "&f"));
        sender.sendMessage(prefix + formatRankInfo("Copper", 15, "&6"));
        sender.sendMessage(prefix + formatRankInfo("Stone", 5, "&7"));
        sender.sendMessage(prefix + formatRankInfo("Starter", 0, "&a"));
        sender.sendMessage("");

    }

    private String formatRankInfo(String rankName, int level, String colorCode) {
        return ChatColor.translateAlternateColorCodes('&',
                colorCode + rankName + "&r: Level " + level + "+");
    }
}