package gamemode.enderdragonattack.LevelSystem;

import org.bukkit.entity.Player;
import org.bukkit.ChatColor;


public class LevelRank {



    public String getRankPrefix(int level) {
        if (level >= 100) return "GOD";
        if (level >= 70) return "Netherite";
        if (level >= 50) return "Diamond";
        if (level >= 35) return "Gold";
        if (level >= 25) return "Iron";
        if (level >= 15) return "Copper";
        if (level >= 5) return "Stone";
        return "";
    }

    public String getRankColor(Player player, int level) {
        if (level >= 100) {
            return "&6";
        }
        if (level >= 70) return "&8";
        if (level >= 50) return "&b";
        if (level >= 35) return "&e";
        if (level >= 25) return "&f";
        if (level >= 15) return "&6";
        if (level >= 5) return "&7";
        return "&7";
    }

    public String getFormattedName(Player player, int level) {
        String playerName = player.getName().replaceAll("[<>]", "");
        if (level >= 5) {
            String colorCode = getRankColor(player, level);
            return ChatColor.translateAlternateColorCodes('&', colorCode + playerName + "&r");
        }
        else {
            return ChatColor.translateAlternateColorCodes('&', ChatColor.GREEN + playerName + "&r");
        }
    }

    public String getTabListName(Player player, int level) {


        if (level >= 5) {
            String rankPrefix = getRankPrefix(level);
            String colorCode = getRankColor(player, level);
            return ChatColor.translateAlternateColorCodes('&', colorCode + rankPrefix + " | " + player.getName() + "&r");
        }
        else {
            return ChatColor.GREEN + player.getName();
        }
    }

    public int getNextRankLevel(int currentLevel) {
        if (currentLevel < 5) return 5;
        if (currentLevel < 15) return 15;
        if (currentLevel < 25) return 25;
        if (currentLevel < 35) return 35;
        if (currentLevel < 50) return 50;
        if (currentLevel < 70) return 70;
        if (currentLevel < 100) return 100;
        return currentLevel;
    }
}