package gamemode.enderdragonattack.Scoreboard;

import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.Config.PlayerDataBase;
import gamemode.enderdragonattack.LevelSystem.LevelSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LobbyScoreboard implements Listener {

    private Plugin plugin;
    private PlayerDataBase playerDataBase;
    private LevelSystem levelSystem;
    private Map<UUID, Scoreboard> playerScoreboards;
    private String gradientPrefix;

    public LobbyScoreboard(Plugin plugin, LevelSystem levelSystem) {
        this.plugin = plugin;
        this.playerDataBase = new PlayerDataBase(plugin);
        this.levelSystem = levelSystem;
        this.playerScoreboards = new HashMap<>();
        this.gradientPrefix = new Gradient().generateGradient("★ Enderdragonattack ★");

        Bukkit.getPluginManager().registerEvents(this, plugin);
        startUpdatingScoreboards();
    }

    public void createLobbyScoreboard(Player player) {
        if (!player.getWorld().getName().equalsIgnoreCase("lobby")) {
            return;
        }

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();

        Objective objective = board.registerNewObjective("lobby", "dummy", gradientPrefix);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        createOrUpdateTeam(board, "playerName", ChatColor.WHITE + " ", player.getName());
        createOrUpdateTeam(board, "playerLevel", ChatColor.WHITE + "  ", String.valueOf(levelSystem.getPlayerLevel(player)));
        createOrUpdateTeam(board, "playerCoins", ChatColor.WHITE + "   ", String.valueOf(playerDataBase.getPlayerCoins(player)));
        createOrUpdateTeam(board, "playerPlaytime", ChatColor.WHITE + "    ", formatPlaytime(playerDataBase.getPlayerPlaytime(player)));

        objective.getScore("     ").setScore(11);
        objective.getScore(ChatColor.YELLOW + "✔ Name: ").setScore(10);
        objective.getScore(ChatColor.WHITE + " ").setScore(9);
        objective.getScore(" ").setScore(8);
        objective.getScore(ChatColor.GREEN + "♦ Level: ").setScore(7);
        objective.getScore(ChatColor.WHITE + "  ").setScore(6);
        objective.getScore("  ").setScore(5);
        objective.getScore(ChatColor.GOLD + "♦ Coins: ").setScore(4);
        objective.getScore(ChatColor.WHITE + "   ").setScore(3);
        objective.getScore("   ").setScore(2);
        objective.getScore(ChatColor.BLUE + "♥ Playtime: ").setScore(1);
        objective.getScore(ChatColor.WHITE + "    ").setScore(0);

        player.setScoreboard(board);
        playerScoreboards.put(player.getUniqueId(), board);
    }

    private void createOrUpdateTeam(Scoreboard board, String name, String entry, String suffix) {
        Team team = board.getTeam(name);
        if (team == null) {
            team = board.registerNewTeam(name);
        }
        team.addEntry(entry);
        team.setSuffix(suffix);
    }

    private void startUpdatingScoreboards() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getWorld().getName().equalsIgnoreCase("lobby")) {
                        updateScoreboard(player);
                    } else {
                        removeLobbyScoreboard(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void updateScoreboard(Player player) {
        Scoreboard board = playerScoreboards.get(player.getUniqueId());
        if (board == null) {
            createLobbyScoreboard(player);
            return;
        }

        updateTeamSuffix(board, "playerLevel", String.valueOf(levelSystem.getPlayerLevel(player)));
        updateTeamSuffix(board, "playerCoins", String.valueOf(playerDataBase.getPlayerCoins(player)));
        updateTeamSuffix(board, "playerPlaytime", formatPlaytime(playerDataBase.getPlayerPlaytime(player)));
    }

    private void updateTeamSuffix(Scoreboard board, String name, String suffix) {
        Team team = board.getTeam(name);
        if (team != null) {
            team.setSuffix(suffix);
        }
    }

    private String formatPlaytime(long playtime) {
        long days = playtime / 86400;
        long remainingSecondsAfterDays = playtime % 86400;
        long hours = remainingSecondsAfterDays / 3600;
        long minutes = (remainingSecondsAfterDays % 3600) / 60;

        return days + "d " + hours + "h " + minutes + "m";
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerDataBase.addNewPlayer(player);
        createLobbyScoreboard(player);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equalsIgnoreCase("lobby")) {
            createLobbyScoreboard(player);
        } else {
            removeLobbyScoreboard(player);
        }
    }

    private void removeLobbyScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("lobby");

        if (objective != null) {
            objective.unregister();
        }

        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        playerScoreboards.remove(player.getUniqueId());
    }
}