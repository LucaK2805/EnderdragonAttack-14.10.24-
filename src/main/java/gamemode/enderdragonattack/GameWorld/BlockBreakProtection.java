package gamemode.enderdragonattack.GameWorld;

import gamemode.enderdragonattack.Color.Gradient;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakProtection implements Listener {

    Gradient pluginInstance = new Gradient();
    String gradientPrefix = pluginInstance.generateGradient("Dragon");
    String Prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";

    private final String protectedWorld = "GameWorld";
    private final int protectionRadius = 30;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (player.isOp()) {
            return;
        }

        World world = event.getBlock().getWorld();

        if (world.getName().equals(protectedWorld)) {
            int blockX = event.getBlock().getX();
            int blockY = event.getBlock().getY();
            int blockZ = event.getBlock().getZ();

            double distance = Math.sqrt(blockX * blockX + blockZ * blockZ);
            Material blockType = event.getBlock().getType();
            if (distance <= protectionRadius) {
                if (blockType == Material.TORCH || blockType == Material.END_STONE || blockType == Material.BEDROCK) {
                    event.setCancelled(true);
                    player.sendMessage(Prefix + ChatColor.RED + "You cannot break this block here!");
                }
            }
        }
    }
}