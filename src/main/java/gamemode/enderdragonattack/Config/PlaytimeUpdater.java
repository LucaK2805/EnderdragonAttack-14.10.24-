package gamemode.enderdragonattack.Config;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlaytimeUpdater {

    private Plugin plugin;
    private PlayerDataBase playerDataBase;

    public PlaytimeUpdater(Plugin plugin, PlayerDataBase playerDataBase) {
        this.plugin = plugin;
        this.playerDataBase = playerDataBase;
        startPlaytimeUpdater();
    }

    private void startPlaytimeUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    long currentPlaytime = playerDataBase.getPlayerPlaytime(player);
                    playerDataBase.setPlayerPlaytime(player, currentPlaytime + 60);
                }
            }
        }.runTaskTimer(plugin, 0L, 1200L);
    }
}