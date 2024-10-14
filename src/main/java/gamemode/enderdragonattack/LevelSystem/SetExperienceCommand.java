package gamemode.enderdragonattack.LevelSystem;

import gamemode.enderdragonattack.Config.PlayerDataBase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetExperienceCommand implements CommandExecutor {

    private LevelSystem levelSystem;
    private PlayerDataBase playerDataBase;

    public SetExperienceCommand(LevelSystem levelSystem, PlayerDataBase playerDataBase) {
        this.levelSystem = levelSystem;
        this.playerDataBase = playerDataBase;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("levelsystem.setexperience")) {
            if (args.length != 2) {
                sender.sendMessage("Usage: /setExperience <playername> <amount>");
                return false;
            }

            String playerName = args[0];
            int experienceToSet;

            try {
                experienceToSet = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid experience amount. Please enter a number.");
                return false;
            }

            Player targetPlayer = sender.getServer().getPlayer(playerName);

            if (targetPlayer == null) {
                sender.sendMessage("Player not found.");
                return false;
            }

            playerDataBase.setPlayerExperience(targetPlayer, experienceToSet);
            sender.sendMessage("Set " + playerName + "'s experience to " + experienceToSet);
            return true;
        } else {
            sender.sendMessage("You do not have permission to use this command.");
            return false;
        }
    }
}