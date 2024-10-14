package gamemode.enderdragonattack.Utilitis;

import gamemode.enderdragonattack.Color.Gradient;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldTeleporter implements CommandExecutor {

    Gradient pluginInstance = new Gradient();
    String gradientPrefix = pluginInstance.generateGradient("Dragon");
    String Prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Prefix + ChatColor.RED + "Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(Prefix + ChatColor.RED + "Usage: /teleport <player> <world>");
            return false;
        }

        String playerName = args[0];
        String worldName = args[1];

        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage(Prefix + ChatColor.RED + "Player " + playerName + " is not online.");
            return false;
        }

        World targetWorld = Bukkit.getWorld(worldName);
        if (targetWorld == null) {
            player.sendMessage(Prefix + ChatColor.RED + "World " + worldName + " does not exist.");
            return false;
        }

        targetPlayer.teleport(targetWorld.getSpawnLocation());
        player.sendMessage(Prefix + ChatColor.GREEN + "Teleported " + playerName + " to " + worldName + ".");
        return true;
    }
}