package gamemode.enderdragonattack.Perks;

import gamemode.enderdragonattack.Perks.PerkShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PerkCommand implements CommandExecutor {

    private final PerkShop perkShop;

    public PerkCommand(PerkShop perkShop) {
        this.perkShop = perkShop;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            perkShop.openPerkShop(player);
            return true;
        } else {
            sender.sendMessage("This command can only be used by players.");
            return false;
        }
    }
}
