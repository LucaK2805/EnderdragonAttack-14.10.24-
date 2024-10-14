package gamemode.enderdragonattack.GameWorld;

import gamemode.enderdragonattack.Color.Gradient;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathListener implements Listener {

    Gradient pluginInstance = new Gradient();
    String gradientPrefix = pluginInstance.generateGradient("Dragon");
    String Prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";

    private final JavaPlugin plugin;

    public DeathListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        World gameWorld = Bukkit.getWorld("GameWorld");
        World lobbyWorld = Bukkit.getWorld("Lobby");

        if (player.getWorld().equals(gameWorld)) {
            String customDeathMessage = Prefix + ChatColor.RED + "The player " + ChatColor.GRAY + player.getName() + ChatColor.RED + " was eliminated from the game!";

            event.setDeathMessage(null);

            for (Player onlinePlayer : gameWorld.getPlayers()) {
                onlinePlayer.sendMessage(customDeathMessage);
            }
        }

        if (lobbyWorld != null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                player.spigot().respawn();
                player.teleport(lobbyWorld.getSpawnLocation());
            }, 1L);
        }
    }
}