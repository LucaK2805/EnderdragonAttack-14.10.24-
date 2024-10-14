package gamemode.enderdragonattack.Listener;

import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.Scoreboard.LobbyScoreboard;
import gamemode.enderdragonattack.LevelSystem.LevelSystem;
import gamemode.enderdragonattack.Utilitis.LobbyAreaMarker;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PlayerJoin implements Listener {

    private final LobbyAreaMarker lobbyAreaMarker;
    private final JavaPlugin plugin;
    private final LobbyScoreboard lobbyScoreboard;

    Gradient pluginInstance = new Gradient();
    String gradientPrefix = pluginInstance.generateGradient("Dragon");
    String Prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";

    public PlayerJoin(JavaPlugin plugin, LevelSystem levelSystem, LobbyAreaMarker lobbyAreaMarker) {
        this.plugin = plugin;
        this.lobbyScoreboard = new LobbyScoreboard(plugin, levelSystem);
        this.lobbyAreaMarker = lobbyAreaMarker;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (loadLobbyWorld()) {
            teleportToLobby(player);
            player.setGameMode(GameMode.ADVENTURE);
        } else {
            player.sendMessage(ChatColor.RED + "The Lobby world is not available. Please contact an admin.");
        }

        lobbyScoreboard.createLobbyScoreboard(player);

        event.setJoinMessage(Prefix + ChatColor.GRAY + "The player " + ChatColor.GREEN + player.getName() + ChatColor.GRAY + " joined the Server!");

        lobbyAreaMarker.initialize();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(Prefix + ChatColor.GRAY + "The player " + ChatColor.GREEN + player.getName() + ChatColor.GRAY + " left the Server!");
    }

    private void teleportToLobby(Player player) {
        World lobbyWorld = Bukkit.getWorld("Lobby");

        if (lobbyWorld != null) {
            Location lobbySpawn = new Location(lobbyWorld, -0.5, 118, -0.5);
            player.teleport(lobbySpawn);
        }
    }

    private boolean loadLobbyWorld() {
        World lobbyWorld = Bukkit.getWorld("Lobby");

        if (lobbyWorld == null) {
            File lobbyWorldFolder = new File(Bukkit.getWorldContainer(), "Lobby");
            if (lobbyWorldFolder.exists()) {
                lobbyWorld = Bukkit.createWorld(new WorldCreator("Lobby"));
                return lobbyWorld != null;
            } else {
                return false;
            }
        }
        return true;
    }
}