package gamemode.enderdragonattack.Enderdragon;

import gamemode.enderdragonattack.Bossbar.DragonBossBar;
import gamemode.enderdragonattack.CoinSystem.DamageTracker;
import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.Config.PlayerDataBase;
import gamemode.enderdragonattack.Stats.StatsDataBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DragonSpawner {

    private static EnderDragon enderDragon;
    private static DragonBossBar bossBar;

    public static EnderDragon getEnderDragon() {
        return enderDragon;
    }

    public static void setEnderDragon(EnderDragon dragon) {
        enderDragon = dragon;
    }

    public static DragonBossBar getBossBar() {
        return bossBar;
    }

    public static void setBossBar(DragonBossBar bar) {
        bossBar = bar;
    }

    public static void spawnEnderDragon(Plugin plugin, double dragonHealth) {
        World world = Bukkit.getWorld("GameWorld");
        if (world == null) {
            Bukkit.getLogger().severe("The world named 'GameWorld' does not exist.");
            return;
        }

        int highestBlockYAtZeroZero = world.getHighestBlockYAt(0, 0);
        int spawnY = highestBlockYAtZeroZero + 100;
        Location spawnLocation = new Location(world, 0, spawnY, 0);

        enderDragon = (EnderDragon) world.spawnEntity(spawnLocation, EntityType.ENDER_DRAGON);
        enderDragon.setHealth(dragonHealth);

        enderDragon.setAI(false);
        enderDragon.setPhase(EnderDragon.Phase.HOVER);
        enderDragon.setInvulnerable(true);

        bossBar = new DragonBossBar(enderDragon, plugin);
        bossBar.startFillingBossBar();

        countdown(20, plugin);

        new BukkitRunnable() {
            @Override
            public void run() {
                enderDragon.setAI(true);
                enderDragon.setInvulnerable(false);
                enderDragon.setPhase(EnderDragon.Phase.CHARGE_PLAYER);
                bossBar.updateBossBar();

                PlayerDataBase playerDataBase = new PlayerDataBase(plugin);
            }
        }.runTaskLater(plugin, 20 * 20);

        setWorldBorder(world, spawnLocation, 200);
    }

    private static void countdown(int seconds, Plugin plugin) {
        new BukkitRunnable() {
            int count = seconds;

            @Override
            public void run() {
                Gradient pluginInstance = new Gradient();
                String gradientPrefix = pluginInstance.generateGradient("Dragon");
                String prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";

                if (count > 0 && (count == 20 || count == 10 || count == 5 || count == 3 || count == 2 || count == 1)) {
                    sendMessageToWorld("GameWorld", prefix + ChatColor.YELLOW + "The Dragon will be active in " + ChatColor.RED + count + " seconds...");
                } else if (count == 0) {
                    sendMessageToWorld("GameWorld", prefix + ChatColor.YELLOW + "The Dragon is now active!");
                    cancel();
                }
                count--;
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private static void sendMessageToWorld(String worldName, String message) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            world.getPlayers().forEach(player -> player.sendMessage(message));
        }
    }

    private static void setWorldBorder(World world, Location center, double size) {
        WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setCenter(center);
        worldBorder.setSize(size);
        worldBorder.setDamageBuffer(0);
        worldBorder.setWarningDistance(5);
        worldBorder.setWarningTime(10);
    }
}