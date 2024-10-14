package gamemode.enderdragonattack.Achievement;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AchievementDatabase implements Listener {

    private final JavaPlugin plugin;
    private File achievementsFile;
    private FileConfiguration achievementsConfig;

    public AchievementDatabase(JavaPlugin plugin) {
        this.plugin = plugin;
        this.achievementsFile = new File(plugin.getDataFolder(), "achievements.yml");
        plugin.getLogger().info("Initializing AchievementDatabase...");
        loadConfig();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("AchievementDatabase initialized.");
    }

    private void loadConfig() {
        plugin.getLogger().info("Loading achievements.yml...");
        if (!achievementsFile.exists()) {
            plugin.getLogger().info("achievements.yml does not exist. Creating new file...");
            try {
                plugin.getDataFolder().mkdirs();
                achievementsFile.createNewFile();
                plugin.getLogger().info("Empty achievements.yml file created successfully.");
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create achievements.yml file: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }
        achievementsConfig = YamlConfiguration.loadConfiguration(achievementsFile);
        plugin.getLogger().info("achievements.yml loaded successfully.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        if (!achievementsConfig.contains(uuid)) {
            plugin.getLogger().info("Initializing achievements for player: " + player.getName());
            initializePlayerAchievements(uuid);
        }
    }

    private void initializePlayerAchievements(String uuid) {
        List<String> achievements = Arrays.asList(
                "TieredAchievement1", "TieredAchievement2", "TieredAchievement3",
                "TieredAchievement4", "TieredAchievement5", "TieredAchievement6",
                "TieredAchievement7", "TieredAchievement8", "TieredAchievement9",
                "Achievement1", "Achievement2", "Achievement3", "Achievement4",
                "Achievement5", "Achievement6", "Achievement7", "Achievement8", "Achievement9"
        );

        for (String achievement : achievements) {
            achievementsConfig.set(uuid + "." + achievement, "LOCKED");
        }

        saveConfig();
    }

    private void saveConfig() {
        try {
            plugin.getLogger().info("Saving achievements.yml...");
            achievementsConfig.save(achievementsFile);
            plugin.getLogger().info("achievements.yml saved successfully.");
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save achievements.yml file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getAchievementStatus(String uuid, String achievementName) {
        return achievementsConfig.getString(uuid + "." + achievementName, "LOCKED");
    }

    public void setAchievementCopper(String uuid, String achievementName) {
        setAchievementStatus(uuid, achievementName, "COPPER");
    }

    public void setAchievementIron(String uuid, String achievementName) {
        setAchievementStatus(uuid, achievementName, "IRON");
    }

    public void setAchievementGold(String uuid, String achievementName) {
        setAchievementStatus(uuid, achievementName, "GOLD");
    }

    public void setAchievementLocked(String uuid, String achievementName) {
        setAchievementStatus(uuid, achievementName, "LOCKED");
    }

    private void setAchievementStatus(String uuid, String achievementName, String status) {
        plugin.getLogger().info("Setting achievement " + achievementName + " to " + status + " for player " + uuid);
        achievementsConfig.set(uuid + "." + achievementName, status);
        saveConfig();
    }

    public boolean isAchievementUnlocked(String uuid, String achievementName) {
        String status = getAchievementStatus(uuid, achievementName);
        return !status.equals("LOCKED");
    }

    public boolean isAchievementGold(String uuid, String achievementName) {
        return getAchievementStatus(uuid, achievementName).equals("GOLD");
    }

    public void reloadConfig() {
        loadConfig();
    }

    public List<String> getPlayerAchievements(String uuid) {
        if (achievementsConfig.contains(uuid)) {
            return new ArrayList<>(achievementsConfig.getConfigurationSection(uuid).getKeys(false));
        }
        return new ArrayList<>();
    }
}