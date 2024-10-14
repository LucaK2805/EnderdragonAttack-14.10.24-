package gamemode.enderdragonattack.LevelSystem;

import gamemode.enderdragonattack.Config.PlayerDataBase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddExperienceCommand implements CommandExecutor {

    private LevelSystem levelSystem;
    private PlayerDataBase playerDataBase;

    public AddExperienceCommand(LevelSystem levelSystem, PlayerDataBase playerDataBase) {
        this.levelSystem = levelSystem;
        this.playerDataBase = playerDataBase;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("levelsystem.addexperience")) {
            if (args.length != 2) {
                sender.sendMessage("Usage: /addExperience <playername> <amount>");
                return false;
            }

            String playerName = args[0];
            int experienceToAdd;

            try {
                experienceToAdd = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid experience amount. Please enter a number.");
                return false;
            }

            Player targetPlayer = sender.getServer().getPlayer(playerName);

            if (targetPlayer == null) {
                sender.sendMessage("Player not found.");
                return false;
            }

            levelSystem.addExperience(targetPlayer, experienceToAdd);
            sender.sendMessage("Added " + experienceToAdd + " experience to " + playerName);
            return true;
        } else {
            sender.sendMessage("You do not have permission to use this command.");
            return false;
        }
    }
}