package gamemode.enderdragonattack.Stats;

import gamemode.enderdragonattack.Core;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class StatsDataBase implements Listener {

    private final Core plugin;
    private File statsFile;
    private FileConfiguration statsConfig;
    private File playerFile;
    private FileConfiguration playerConfig;

    public StatsDataBase(Core plugin) {
        this.plugin = plugin;
        this.statsFile = new File(plugin.getDataFolder(), "stats.yml");
        this.playerFile = new File(plugin.getDataFolder(), "players.yml");
        loadConfigs();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void reloadStats() {
        loadConfigs();
    }

    private void loadConfigs() {
        if (!statsFile.exists()) {
            plugin.saveResource("stats.yml", false);
        }
        if (!playerFile.exists()) {
            plugin.saveResource("players.yml", false);
        }

        statsConfig = YamlConfiguration.loadConfiguration(statsFile);
        playerConfig = YamlConfiguration.loadConfiguration(playerFile);
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        reloadStats();
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        if (!statsConfig.contains(uuid)) {
            statsConfig.set(uuid + ".Playername", player.getName());
            statsConfig.set(uuid + ".GamesPlayed", 0);
            statsConfig.set(uuid + ".TotalDamage", 0.0);
        } else {
            // Update player name in case it has changed
            statsConfig.set(uuid + ".Playername", player.getName());
        }
    }

    public void incrementGamesPlayed(Player player) {
        reloadStats();
        String uuid = player.getUniqueId().toString();
        int currentGames = statsConfig.getInt(uuid + ".GamesPlayed", 0);
        statsConfig.set(uuid + ".GamesPlayed", currentGames + 1);
    }

    public void addTotalDamage(Player player, double damage) {
        String uuid = player.getUniqueId().toString();
        double currentDamage = statsConfig.getDouble(uuid + ".TotalDamage", 0.0);
        statsConfig.set(uuid + ".TotalDamage", currentDamage + damage);
    }

    public int getGamesPlayed(UUID playerUUID) {
        String uuid = playerUUID.toString();
        return statsConfig.getInt(uuid + ".GamesPlayed", 0);
    }

    public double getTotalDamage(UUID playerUUID) {
        String uuid = playerUUID.toString();
        return statsConfig.getDouble(uuid + ".TotalDamage", 0.0);
    }

    public List<String> getAllPlayerNames() {
        List<String> playerNames = new ArrayList<>();
        Set<String> keys = statsConfig.getKeys(false);
        for (String key : keys) {
            String playerName = statsConfig.getString(key + ".Playername");
            if (playerName != null) {
                playerNames.add(playerName);
            }
        }
        return playerNames;
    }

    public int getPlayerExperience(String playerName) {
        reloadStats();
        for (String key : playerConfig.getKeys(false)) {
            String storedPlayerName = playerConfig.getString(key + ".Playername");
            if (storedPlayerName != null && storedPlayerName.equalsIgnoreCase(playerName)) {
                return playerConfig.getInt(key + ".experience", 100);
            }
        }
        return 0;
    }

    public int getGamesPlayed(String playerName) {
        for (String key : statsConfig.getKeys(false)) {
            if (statsConfig.getString(key + ".Playername", "").equalsIgnoreCase(playerName)) {
                return statsConfig.getInt(key + ".GamesPlayed", 0);
            }
        }
        return 0;
    }

    public double getTotalDamage(String playerName) {
        for (String key : statsConfig.getKeys(false)) {
            if (statsConfig.getString(key + ".Playername", "").equalsIgnoreCase(playerName)) {
                return statsConfig.getDouble(key + ".TotalDamage", 0.0);
            }
        }
        return 0.0;
    }

    // Add this method to handle plugin reloads
    public void onReload() {
        loadConfigs();
    }
}