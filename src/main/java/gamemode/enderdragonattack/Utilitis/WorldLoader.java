package gamemode.enderdragonattack.Utilitis;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldLoader {

    private final JavaPlugin plugin;

    public WorldLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Bukkit.createWorld(new org.bukkit.WorldCreator(worldName));
            plugin.getLogger().info("World " + worldName + " has been loaded.");
        } else {
            plugin.getLogger().info("World " + worldName + " is already loaded.");
        }
    }

    public void unloadWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            Bukkit.unloadWorld(world, false);
            plugin.getLogger().info("World " + worldName + " has been unloaded.");
        } else {
            plugin.getLogger().info("World " + worldName + " is not loaded.");
        }
    }
}