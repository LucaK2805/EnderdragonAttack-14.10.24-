package gamemode.enderdragonattack.LevelSystem;

import gamemode.enderdragonattack.Color.Gradient;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LevelCommand implements CommandExecutor {

    Gradient pluginInstance = new Gradient();
    String gradientPrefix = pluginInstance.generateGradient("Dragon");
    String Prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";

    private LevelSystem levelSystem;

    public LevelCommand(LevelSystem levelSystem) {
        this.levelSystem = levelSystem;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 && !(sender instanceof Player)) {
            sender.sendMessage(Prefix + ChatColor.RED + "This command can only be run by a player or with a player name specified.");
            return true;
        }

        Player targetPlayer;
        if (args.length == 0) {
            targetPlayer = (Player) sender;
        } else {
            targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null) {
                sender.sendMessage(Prefix + ChatColor.RED + "Player not found or not online.");
                return true;
            }
        }

        int currentLevel = levelSystem.getPlayerLevel(targetPlayer);
        int totalExperienceForNextLevel = levelSystem.calculateExperienceForLevel(currentLevel + 1);
        int experienceGainedTowardsNextLevel = levelSystem.getExperienceGainedTowardsNextLevel(targetPlayer);

        int experienceGainedTowardsNextLevelCalculated = experienceGainedTowardsNextLevel - (currentLevel * 100);

        String message = Prefix + ChatColor.YELLOW + targetPlayer.getName() + ChatColor.GRAY + " is level " +
                ChatColor.YELLOW + currentLevel + ChatColor.GRAY + " with " +
                ChatColor.YELLOW + experienceGainedTowardsNextLevelCalculated + ChatColor.RED + "/" +
                ChatColor.YELLOW + totalExperienceForNextLevel + ChatColor.GRAY + " experience for the next level.";

        sender.sendMessage(message);

        // If the sender is not the target player, also send the message to the target player
        if (sender != targetPlayer && targetPlayer.isOnline()) {
            targetPlayer.sendMessage(Prefix + ChatColor.GRAY + sender.getName() + " checked your level.");
        }

        return true;
    }
}