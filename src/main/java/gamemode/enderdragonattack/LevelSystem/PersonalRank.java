package gamemode.enderdragonattack.LevelSystem;

import gamemode.enderdragonattack.Color.Gradient;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PersonalRank implements CommandExecutor {

    private final LevelSystem levelSystem;
    private final LevelRank levelRank;
    private final String prefix;

    public PersonalRank(LevelSystem levelSystem) {
        this.levelSystem = levelSystem;
        this.levelRank = new LevelRank();
        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = ChatColor.translateAlternateColorCodes('&', "&f[" + gradientPrefix + "&f] " + ChatColor.RESET);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;
        int currentLevel = levelSystem.getPlayerLevel(player);
        int totalExperienceForNextLevel = levelSystem.calculateExperienceForLevel(currentLevel + 1);
        int experienceGainedTowardsNextLevel = levelSystem.getExperienceGainedTowardsNextLevel(player);

        int experienceGainedTowardsNextLevelCalculated = experienceGainedTowardsNextLevel - (currentLevel * 100);

        String rankPrefix = levelRank.getRankPrefix(currentLevel);
        String rankColor = levelRank.getRankColor(player, currentLevel);
        int nextRankLevel = levelRank.getNextRankLevel(currentLevel);

        sender.sendMessage("");
        sender.sendMessage(prefix + ChatColor.GOLD + "Your Rank Information:");
        sender.sendMessage("");
        sender.sendMessage(prefix + ChatColor.YELLOW + "Current Rank: " + ChatColor.translateAlternateColorCodes('&', rankColor + rankPrefix));
        sender.sendMessage(prefix + ChatColor.YELLOW + "Current Level: " + ChatColor.WHITE + currentLevel);

        String expProgress = ChatColor.AQUA + "" + experienceGainedTowardsNextLevelCalculated + ChatColor.GRAY + "/" + ChatColor.GREEN + totalExperienceForNextLevel;
        sender.sendMessage(prefix + ChatColor.YELLOW + "Experience: " + expProgress);

        if (currentLevel < 100) {
            sender.sendMessage(prefix + ChatColor.YELLOW + "Next Rank at Level: " + ChatColor.WHITE + nextRankLevel);
            sender.sendMessage("");
        } else {
            sender.sendMessage(prefix + ChatColor.GREEN + "You've reached the highest rank!");
            sender.sendMessage("");
        }

        return true;
    }
}