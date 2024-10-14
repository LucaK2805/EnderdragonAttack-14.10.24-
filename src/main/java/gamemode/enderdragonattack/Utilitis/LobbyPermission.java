package gamemode.enderdragonattack.Utilitis;

import gamemode.enderdragonattack.Color.Gradient;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class LobbyPermission implements Listener {

    Gradient pluginInstance = new Gradient();
    String gradientPrefix = pluginInstance.generateGradient("Dragon");
    String Prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager().getWorld().getName().equalsIgnoreCase("Lobby") && event.getDamager() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equalsIgnoreCase("Lobby") && !player.isOp()) {
            event.setCancelled(true);
            player.sendMessage(Prefix + ChatColor.RED + "You cannot break blocks here!");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equalsIgnoreCase("Lobby") && !player.isOp()) {
            event.setCancelled(true);
            player.sendMessage(Prefix + ChatColor.RED + "You cannot place blocks here!");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equalsIgnoreCase("Lobby")) {
            player.getInventory().clear();
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equalsIgnoreCase("Lobby")) {
            player.getInventory().clear();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null) return;

        if (block.getWorld().getName().equalsIgnoreCase("Lobby")) {
            Material blockType = block.getType();

            boolean isDoorOrTrapdoor =
                    blockType == Material.OAK_DOOR || blockType == Material.SPRUCE_DOOR ||
                            blockType == Material.BIRCH_DOOR || blockType == Material.JUNGLE_DOOR ||
                            blockType == Material.ACACIA_DOOR || blockType == Material.DARK_OAK_DOOR ||
                            blockType == Material.CRIMSON_DOOR || blockType == Material.WARPED_DOOR ||
                            blockType == Material.OAK_TRAPDOOR || blockType == Material.SPRUCE_TRAPDOOR ||
                            blockType == Material.BIRCH_TRAPDOOR || blockType == Material.JUNGLE_TRAPDOOR ||
                            blockType == Material.ACACIA_TRAPDOOR || blockType == Material.DARK_OAK_TRAPDOOR ||
                            blockType == Material.CRIMSON_TRAPDOOR || blockType == Material.WARPED_TRAPDOOR;

            if (!player.isOp() && isDoorOrTrapdoor) {
                event.setCancelled(true);
                player.sendMessage(Prefix + ChatColor.RED + "You cannot interact with this!");
            }
        }
    }
}