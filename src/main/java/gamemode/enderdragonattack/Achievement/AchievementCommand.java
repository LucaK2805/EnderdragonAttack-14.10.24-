package gamemode.enderdragonattack.Achievement;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class AchievementCommand implements CommandExecutor {

    private final AchievementManager achievementManager;

    public AchievementCommand(AchievementManager achievementManager) {
        this.achievementManager = achievementManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {

            return true;
        }

        Player player = (Player) sender;
        Inventory achievementGUI = achievementManager.createAndUpdateAchievementGUI(player);
        player.openInventory(achievementGUI);


        return true;
    }
}