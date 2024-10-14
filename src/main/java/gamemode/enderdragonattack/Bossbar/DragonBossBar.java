package gamemode.enderdragonattack.Bossbar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class DragonBossBar {

    private BossBar bossBar;
    private EnderDragon enderDragon;
    private Plugin plugin;
    private String worldName = "GameWorld";

    public DragonBossBar(EnderDragon enderDragon, Plugin plugin) {
        this.enderDragon = enderDragon;
        this.plugin = plugin;

        bossBar = Bukkit.createBossBar(ChatColor.YELLOW + "Ender Dragon", BarColor.PURPLE, BarStyle.SOLID);
        bossBar.setProgress(0.0);
        bossBar.setVisible(true);

        addOnlinePlayers();
    }

    private void addOnlinePlayers() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (player.getWorld().getName().equals(worldName)) {
                bossBar.addPlayer(player);
            }
        }
    }

    private void updateOnlinePlayers() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (player.getWorld().getName().equals(worldName)) {
                if (!bossBar.getPlayers().contains(player)) {
                    bossBar.addPlayer(player);
                }
            } else {
                if (bossBar.getPlayers().contains(player)) {
                    bossBar.removePlayer(player);
                }
            }
        }
    }

    public void startFillingBossBar() {
        new BukkitRunnable() {
            double progress = 0.0;
            int count = 0;

            @Override
            public void run() {
                if (count < 100) {
                    progress += 0.01;
                    if (progress > 1.0) {
                        progress = 1.0;
                    }
                    bossBar.setProgress(progress);
                    count++;
                } else {
                    bossBar.setProgress(1.0);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 4);
    }

    public void updateBossBar() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (enderDragon.isValid()) {
                    double healthPercentage = enderDragon.getHealth() / enderDragon.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                    bossBar.setTitle(ChatColor.YELLOW + "Ender Dragon - " + (int) enderDragon.getHealth() + " / " + (int) enderDragon.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getBaseValue() + " HP");
                    bossBar.setProgress(healthPercentage);
                } else {
                    bossBar.removeAll();
                    cancel();
                }

                updateOnlinePlayers();
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void removeBossBar() {
        bossBar.removeAll();
    }
}