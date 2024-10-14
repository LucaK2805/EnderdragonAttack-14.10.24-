package gamemode.enderdragonattack.SetAddRemoveCommands;

import gamemode.enderdragonattack.Config.PlayerDataBase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SetCoinsCommand implements CommandExecutor {

    private Plugin plugin;
    private PlayerDataBase playerDataBase;

    public SetCoinsCommand(Plugin plugin, PlayerDataBase playerDataBase) {
        this.plugin = plugin;
        this.playerDataBase = playerDataBase;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("enderdragonattack.setcoins")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /setcoins <player> <amount>");
            return true;
        }

        String playerName = args[0];
        int amount;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid amount. Please enter a valid number.");
            return true;
        }

        Player target = plugin.getServer().getPlayer(playerName);

        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found or not online.");
            return true;
        }

        playerDataBase.setPlayerCoins(target, amount);

        player.sendMessage(ChatColor.GREEN + "Coins set to " + amount + " for player " + target.getName());

        return true;
    }
}