package gamemode.enderdragonattack.Utilitis;

import gamemode.enderdragonattack.Start_Stop.Start;
import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.Start_Stop.StartTimer;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LobbyAreaMarker implements CommandExecutor {

    private final Plugin plugin;
    private World lobbyWorld;
    private Location corner1;
    private Location corner2;
    private final List<UUID> playersInArea = new ArrayList<>();
    private final StartTimer startTimer;

    Gradient pluginInstance = new Gradient();
    String gradientPrefix = pluginInstance.generateGradient("Dragon");
    String Prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";

    public LobbyAreaMarker(Plugin plugin, Start startCommand) {
        this.plugin = plugin;
        this.startTimer = startCommand.getStartTimer();
    }

    public void initialize() {
        new BukkitRunnable() {
            @Override
            public void run() {
                lobbyWorld = Bukkit.getWorld("lobby");

                if (lobbyWorld == null) {
                    plugin.getLogger().severe("World 'lobby' is not loaded or does not exist!");
                    return;
                }

                corner1 = new Location(lobbyWorld, -3, 117, -5);
                corner2 = new Location(lobbyWorld, 2, 117, -11);

                startParticleTask();
                startPlayerTrackingTask();
            }
        }.runTaskLater(plugin, 10L);
    }

    private void startParticleTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (lobbyWorld == null) return;

                for (double x = corner1.getX(); x <= corner2.getX(); x += 0.5) {
                    lobbyWorld.spawnParticle(Particle.VILLAGER_HAPPY, new Location(lobbyWorld, x, corner1.getY() - 0.8, corner1.getZ()), 1);
                    lobbyWorld.spawnParticle(Particle.VILLAGER_HAPPY, new Location(lobbyWorld, x, corner1.getY() - 0.8, corner2.getZ()), 1);
                }

                for (double z = corner1.getZ(); z >= corner2.getZ(); z -= 0.5) {
                    lobbyWorld.spawnParticle(Particle.VILLAGER_HAPPY, new Location(lobbyWorld, corner1.getX(), corner1.getY() - 0.8, z), 1);
                    lobbyWorld.spawnParticle(Particle.VILLAGER_HAPPY, new Location(lobbyWorld, corner2.getX(), corner1.getY() - 0.8, z), 1);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void startPlayerTrackingTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (lobbyWorld == null) return;

                List<UUID> toRemove = new ArrayList<>();
                List<UUID> toAdd = new ArrayList<>();

                for (UUID uuid : playersInArea) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null || !player.isOnline() || !isInArea(player.getLocation())) {
                        toRemove.add(uuid);
                    }
                }

                for (UUID uuid : toRemove) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.isOnline()) {
                        player.sendMessage(Prefix + ChatColor.RED + "You left the starting zone");
                        playersInArea.remove(uuid);
                        startTimer.playerLeftLobby(uuid);
                    }
                }

                for (Player player : lobbyWorld.getPlayers()) {
                    UUID uuid = player.getUniqueId();
                    if (isInArea(player.getLocation()) && !playersInArea.contains(uuid)) {
                        toAdd.add(uuid);
                    }
                }

                for (UUID uuid : toAdd) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.isOnline()) {
                        if (Start.isGameRunning()) {
                            player.sendMessage(Prefix + ChatColor.RED + "The game is currently running. Please wait.");
                        } else {
                            player.sendMessage(Prefix + ChatColor.GREEN + "You entered the starting zone");
                            playersInArea.add(uuid);
                            startTimer.playerEnteredLobby(uuid);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    private boolean isInArea(Location location) {
        return location.getX() >= corner1.getX() && location.getX() <= corner2.getX() &&
                location.getZ() <= corner1.getZ() && location.getZ() >= corner2.getZ();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("list")) {
            sender.sendMessage(Prefix + ChatColor.GRAY + "Players in the starting zone:");
            for (UUID uuid : playersInArea) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    sender.sendMessage(Prefix + ChatColor.GRAY + "- " + player.getName());
                }
            }
            return true;
        }
        return false;
    }
}