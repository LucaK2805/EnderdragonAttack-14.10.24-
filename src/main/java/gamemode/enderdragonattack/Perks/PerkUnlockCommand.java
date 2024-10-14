package gamemode.enderdragonattack.Perks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class PerkUnlockCommand implements CommandExecutor {

    private final PerkManager perkManager;
    private final PerkStop perkStop;
    private final Set<String> validPerks;

    public PerkUnlockCommand(PerkManager perkManager, PerkStop perkStop) {
        this.perkManager = perkManager;
        this.perkStop = perkStop;
        this.validPerks = new HashSet<>();
        initializeValidPerks();
    }

    private void initializeValidPerks() {
        validPerks.add("Nightvision");
        validPerks.add("Jumpboost");
        validPerks.add("Speed");
        validPerks.add("Haste");
        validPerks.add("Slowfalling");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /perk <give|remove> <player> <perkName>");
            return false;
        }

        String action = args[0];
        String playerName = args[1];
        String perkName = args[2];

        if (!validPerks.contains(perkName)) {
            sender.sendMessage(ChatColor.RED + "Invalid perk. Available perks: " + String.join(", ", validPerks));
            return false;
        }

        Player targetPlayer = Bukkit.getPlayer(playerName);

        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player " + playerName + " not found.");
            return false;
        }

        if (action.equalsIgnoreCase("give")) {
            perkManager.unlockPerkForPlayer(targetPlayer, perkName);
            sender.sendMessage(ChatColor.GREEN + "Perk " + perkName + " has been unlocked for " + playerName + ".");
        } else if (action.equalsIgnoreCase("remove")) {
            perkManager.removePerkFromPlayer(targetPlayer, perkName);
            sender.sendMessage(ChatColor.GREEN + "Perk " + perkName + " has been removed from " + playerName + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid action. Use either 'give' or 'remove'.");
        }

        return true;
    }
}