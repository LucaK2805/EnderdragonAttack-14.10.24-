package gamemode.enderdragonattack.Start_Stop;

import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.GameWorld.WorldRegenerateCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StartTimer {

    Gradient pluginInstance = new Gradient();
    String gradientPrefix = pluginInstance.generateGradient("Dragon");
    String Prefix = "[" + gradientPrefix + org.bukkit.ChatColor.RESET + "] ";

    private final Plugin plugin;
    private final Set<UUID> playersInLobby;
    private boolean timerRunning;
    private int countdownTime;
    private final Start startCommand;
    private final WorldRegenerateCommand worldRegenerateCommand;

    public StartTimer(Plugin plugin, Start startCommand, WorldRegenerateCommand worldRegenerateCommand) {
        this.plugin = plugin;
        this.playersInLobby = new HashSet<>();
        this.timerRunning = false;
        this.countdownTime = 30; // 30 seconds timer
        this.startCommand = startCommand;
        this.worldRegenerateCommand = worldRegenerateCommand;
    }

    public void playerEnteredLobby(UUID playerUUID) {
        playersInLobby.add(playerUUID);
        if (!timerRunning && !playersInLobby.isEmpty()) {
            startCountdown();
        }
    }

    public void playerLeftLobby(UUID playerUUID) {
        playersInLobby.remove(playerUUID);
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null && player.isOnline()) {
            player.setLevel(0);
        }
        if (playersInLobby.isEmpty()) {
            stopCountdown();
        }
    }

    private void startCountdown() {
        timerRunning = true;
        new BukkitRunnable() {
            int timeLeft = countdownTime;

            @Override
            public void run() {
                if (playersInLobby.isEmpty()) {
                    cancel();
                    timerRunning = false;
                    return;
                }

                for (UUID playerUUID : playersInLobby) {
                    Player player = Bukkit.getPlayer(playerUUID);
                    if (player != null && player.isOnline()) {
                        player.setLevel(timeLeft);
                    }
                }

                if (timeLeft <= 0) {
                    cancel();
                    timerRunning = false;

                    if (worldRegenerateCommand.resetWorld("GameWorld", "ExampleWorld", "Lobby")) {
                        Bukkit.getScheduler().runTaskLater(plugin, StartTimer.this::startGame, 100L); // 5 seconds delay
                    } else {
                        Bukkit.getLogger().severe("World could not be reset. The game will not start.");
                    }
                }

                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void stopCountdown() {
        timerRunning = false;
    }

    private void startGame() {
        for (UUID playerUUID : playersInLobby) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null && player.isOnline()) {
                startCommand.getParticipants().add(player);
            }
        }

        if (!startCommand.getParticipants().isEmpty()) {
            startCommand.startGame();
        }

        playersInLobby.clear();
    }
}