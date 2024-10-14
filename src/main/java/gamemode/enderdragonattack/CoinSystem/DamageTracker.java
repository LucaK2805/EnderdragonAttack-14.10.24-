package gamemode.enderdragonattack.CoinSystem;

import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.Config.PlayerDataBase;
import gamemode.enderdragonattack.Stats.StatsDataBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class DamageTracker implements Listener {

    Gradient pluginInstance = new Gradient();
    String gradientPrefix = pluginInstance.generateGradient("Dragon");
    String Prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";

    private final Plugin plugin;
    private final PlayerDataBase playerDataBase;
    private final StatsDataBase statsDataBase;
    private final Map<UUID, Double> damageMap;
    private double totalDamage;

    public DamageTracker(Plugin plugin, PlayerDataBase playerDataBase, StatsDataBase statsDataBase) {
        this.plugin = plugin;
        this.playerDataBase = playerDataBase;
        this.statsDataBase = statsDataBase;
        this.damageMap = new HashMap<>();
        this.totalDamage = 0.0;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            if (event.getDamager() instanceof Player) {
                Player player = (Player) event.getDamager();
                processPlayerDamage(player, event);
            } else if (event.getDamager() instanceof org.bukkit.entity.Projectile) {
                org.bukkit.entity.Projectile projectile = (org.bukkit.entity.Projectile) event.getDamager();
                if (projectile.getShooter() instanceof Player) {
                    Player player = (Player) projectile.getShooter();
                    processPlayerDamage(player, event);
                }
            }
        }
    }

    private void processPlayerDamage(Player player, EntityDamageByEntityEvent event) {
        double damage = event.getDamage();

        if (totalDamage + damage > 200.0) {
            damage = 200.0 - totalDamage;
        }

        if (damage > 0) {
            totalDamage += damage;
            damageMap.merge(player.getUniqueId(), damage, Double::sum);
            event.setDamage(damage);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            plugin.getLogger().info("EnderDragon death detected");

            // Verhindern, dass das Leaderboard mehrfach ausgegeben wird
            if (!damageMap.isEmpty()) {
                List<Map.Entry<UUID, Double>> sortedDamageList = new ArrayList<>(damageMap.entrySet());
                sortedDamageList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

                StringBuilder leaderboard = new StringBuilder();

                leaderboard.append(ChatColor.GOLD).append(" \n");
                leaderboard.append(ChatColor.WHITE + Prefix).append(ChatColor.GOLD).append("---------------------\n");
                leaderboard.append(ChatColor.WHITE + Prefix).append(ChatColor.GOLD).append(" \n");
                leaderboard.append(ChatColor.WHITE + Prefix).append(ChatColor.GOLD).append("Dragon Damage Leaderboard:\n");
                leaderboard.append(ChatColor.WHITE + Prefix).append(ChatColor.GOLD).append(" \n");

                for (int i = 0; i < Math.min(sortedDamageList.size(), 10); i++) {
                    Map.Entry<UUID, Double> entry = sortedDamageList.get(i);
                    Player player = Bukkit.getPlayer(entry.getKey());
                    if (player != null) {
                        leaderboard.append(ChatColor.WHITE + Prefix).append(ChatColor.YELLOW).append(i + 1).append(". ")
                                .append(ChatColor.WHITE).append(player.getName()).append(": ")
                                .append(ChatColor.GREEN).append(Math.round(entry.getValue())).append(" damage\n");
                    }
                }
                leaderboard.append(ChatColor.WHITE + Prefix).append(ChatColor.GOLD).append(" \n");
                leaderboard.append(ChatColor.WHITE + Prefix).append(ChatColor.GOLD).append("---------------------\n");
                leaderboard.append(ChatColor.GOLD).append(" \n");

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.sendMessage(leaderboard.toString());
                }

                for (Map.Entry<UUID, Double> entry : sortedDamageList) {
                    Player player = Bukkit.getPlayer(entry.getKey());
                    if (player != null) {
                        double damage = entry.getValue();

                        if (damage > 200.0) {
                            damage = 200.0;
                        }

                        int coinsToAdd = (int) Math.round(damage);
                        int experienceToAdd = coinsToAdd;

                        if (damage > 0) {
                            playerDataBase.addPlayerCoins(player, coinsToAdd);
                            playerDataBase.addPlayerExperience(player, experienceToAdd);

                            String title = ChatColor.GOLD + " #1 ";
                            String subtitle = ChatColor.YELLOW + "#" + (sortedDamageList.indexOf(entry) + 1)
                                    + ChatColor.GOLD + " out of " + ChatColor.YELLOW + sortedDamageList.size();

                            player.sendTitle(title, subtitle, 10, 70, 20);

                            player.sendMessage(Prefix + ChatColor.GREEN + "You dealt " + ChatColor.YELLOW + coinsToAdd + " damage" +
                                    ChatColor.GREEN + " and earned " + ChatColor.GOLD + coinsToAdd + " coins and " +
                                    experienceToAdd + " Experience!");

                            // Füge hier den Total Damage Adder ein
                            try {
                                statsDataBase.addTotalDamage(player, damage);
                                plugin.getLogger().info("Added total damage for player: " + player.getName() + ", damage: " + damage);
                            } catch (Exception e) {
                                plugin.getLogger().severe("Error adding total damage for player " + player.getName() + ": " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                }

                damageMap.clear();
                totalDamage = 0.0;
            }
        }
    }
}
