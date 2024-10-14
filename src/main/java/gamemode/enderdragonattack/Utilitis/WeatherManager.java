package gamemode.enderdragonattack.Utilitis;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WeatherManager {

    private final Plugin plugin;

    public WeatherManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void startWeatherControl() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    world.setStorm(false);
                    world.setThundering(false);
                    world.setWeatherDuration(0);

                    world.setTime(1000);
                }
            }
        }.runTaskTimer(plugin, 0L, 200L);
    }
}