package gamemode.enderdragonattack.Config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlayerDataBase {

    private Plugin plugin;
    private File file;
    private FileConfiguration config;

    public PlayerDataBase(Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "players.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllPlayerNames() {
        List<String> playerNames = new ArrayList<>();
        Set<String> keys = config.getKeys(false);
        for (String key : keys) {
            String playerName = config.getString(key + ".Playername");
            if (playerName != null) {
                playerNames.add(playerName);
            }
        }
        return playerNames;
    }

    public int getPlayerLevel(Player player) {
        reloadConfig();
        return config.getInt(player.getUniqueId().toString() + ".level", 1);
    }

    public void setPlayerLevel(Player player, int level) {
        if (level < 1) {
            level = 1;
        }
        config.set(player.getUniqueId().toString() + ".level", level);
        save();
    }

    public void addPlayerLevel(Player player, int levelsToAdd) {
        int currentLevel = getPlayerLevel(player);
        int newLevel = currentLevel + levelsToAdd;
        setPlayerLevel(player, newLevel);
    }

    public int getPlayerExperience(Player player) {
        reloadConfig();
        return config.getInt(player.getUniqueId().toString() + ".experience", 100);
    }

    public void setPlayerExperience(Player player, int experience) {
        config.set(player.getUniqueId().toString() + ".experience", experience);
        save();
    }

    public void addPlayerExperience(Player player, int experienceToAdd) {
        int currentExperience = getPlayerExperience(player);
        int newExperience = currentExperience + experienceToAdd;
        setPlayerExperience(player, newExperience);
    }

    public int getPlayerCoins(Player player) {
        reloadConfig();
        return config.getInt(player.getUniqueId().toString() + ".coins", 100);
    }

    public void setPlayerCoins(Player player, int coins) {
        config.set(player.getUniqueId().toString() + ".coins", coins);
        save();
    }

    public void addPlayerCoins(Player player, int coinsToAdd) {
        int currentCoins = getPlayerCoins(player);
        int newCoins = currentCoins + coinsToAdd;
        setPlayerCoins(player, newCoins);
    }

    public long getPlayerPlaytime(Player player) {
        reloadConfig();
        return config.getLong(player.getUniqueId().toString() + ".playtime", 0);
    }

    public void setPlayerPlaytime(Player player, long playtime) {
        config.set(player.getUniqueId().toString() + ".playtime", playtime);
        save();
    }

    public void addNewPlayer(Player player) {
        String playerUUIDString = player.getUniqueId().toString();
        if (!config.contains(playerUUIDString)) {
            config.set(playerUUIDString + ".Playername", player.getName());
            config.set(playerUUIDString + ".experience", 100);
            config.set(playerUUIDString + ".coins", 100);
            config.set(playerUUIDString + ".playtime", 0);
            save();
        }
    }

    private void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }
}