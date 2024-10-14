package gamemode.enderdragonattack.LevelSystem;

import gamemode.enderdragonattack.Config.PlayerDataBase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveExperienceCommand implements CommandExecutor {

    private LevelSystem levelSystem;
    private PlayerDataBase playerDataBase;

    public RemoveExperienceCommand(LevelSystem levelSystem, PlayerDataBase playerDataBase) {
        this.levelSystem = levelSystem;
        this.playerDataBase = playerDataBase;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("levelsystem.removeexperience")) {
            if (args.length != 2) {
                sender.sendMessage("Usage: /removeExperience <playername> <amount>");
                return false;
            }

            String playerName = args[0];
            int experienceToRemove;

            try {
                experienceToRemove = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid experience amount. Please enter a number.");
                return false;
            }

            Player targetPlayer = sender.getServer().getPlayer(playerName);

            if (targetPlayer == null) {
                sender.sendMessage("Player not found.");
                return false;
            }

            int currentExperience = playerDataBase.getPlayerExperience(targetPlayer);
            int newExperience = Math.max(0, currentExperience - experienceToRemove);
            playerDataBase.setPlayerExperience(targetPlayer, newExperience);
            sender.sendMessage("Removed " + experienceToRemove + " experience from " + playerName);
            return true;
        } else {
            sender.sendMessage("You do not have permission to use this command.");
            return false;
        }
    }
}