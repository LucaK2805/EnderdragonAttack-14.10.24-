package gamemode.enderdragonattack.LevelSystem;

import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.Config.PlayerDataBase;
import gamemode.enderdragonattack.Core;
import gamemode.enderdragonattack.Stats.StatsDataBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LevelSystem {

    private final Gradient pluginInstance = new Gradient();
    private final String gradientPrefix = pluginInstance.generateGradient("Dragon");
    private final String prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";

    private PlayerDataBase playerDataBase;
    private StatsDataBase statsDataBase;
    private Core plugin;
    private Map<UUID, Integer> lastKnownLevels;
    private LevelRank levelRank;

    public LevelSystem(Core plugin, PlayerDataBase playerDataBase, StatsDataBase statsDataBase) {
        this.plugin = plugin;
        this.playerDataBase = playerDataBase;
        this.statsDataBase = statsDataBase;
        this.lastKnownLevels = new HashMap<>();
        this.levelRank = new LevelRank();
        startLevelChecker();
    }

    private void startLevelChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    checkAndUpdatePlayerRank(player);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void checkAndUpdatePlayerRank(Player player) {
        int currentLevel = getPlayerLevel(player);
        UUID playerUUID = player.getUniqueId();

        if (!lastKnownLevels.containsKey(playerUUID) || lastKnownLevels.get(playerUUID) != currentLevel) {
            updatePlayerRank(player, currentLevel);

            if (lastKnownLevels.containsKey(playerUUID) && currentLevel > lastKnownLevels.get(playerUUID)) {
                handleLevelUp(player, currentLevel);
                updatePlayerRank(player, currentLevel);
            }

            lastKnownLevels.put(playerUUID, currentLevel);
        }
        updatePlayerRank(player, currentLevel);
    }

    public void updatePlayerRank(Player player, int level) {
        String tabListName = levelRank.getTabListName(player, level);
        String chatName = levelRank.getFormattedName(player, level);

        player.setPlayerListName(tabListName);
        player.setDisplayName(chatName);
        player.setCustomName(chatName);
        player.setCustomNameVisible(true);
    }

    private void handleLevelUp(Player player, int newLevel) {
        player.sendMessage(prefix + ChatColor.YELLOW + "Congratulations! " + ChatColor.GRAY + "You've reached " + ChatColor.GOLD + "Level " + newLevel + ChatColor.GRAY + "!");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    public int getPlayerLevel(Player player) {
        int experience = playerDataBase.getPlayerExperience(player);
        return calculateLevelFromExperience(experience);
    }

    public int getOfflinePlayerLevel(String playerName) {
        int experience = statsDataBase.getPlayerExperience(playerName);
        return calculateLevelFromExperience(experience);
    }

    private int calculateLevelFromExperience(int experience) {
        int level = 0;
        int remainingExperience = experience;

        while (remainingExperience >= calculateExperienceForLevel(level + 1)) {
            remainingExperience -= calculateExperienceForLevel(level + 1);
            level++;
        }

        return level;
    }

    public int calculateExperienceForLevel(int level) {
        return level * 100;
    }

    public int getExperienceForNextLevel(int currentLevel) {
        return currentLevel * 100 + 100;
    }

    public int getTotalExperienceForLevel(int level) {
        return (level - 1) * level * 50;
    }

    public int getExperienceToNextLevel(Player player) {
        int currentExperience = playerDataBase.getPlayerExperience(player);
        return calculateExperienceToNextLevel(currentExperience);
    }

    public void addExperience(Player player, int experienceToAdd) {
        int currentExperience = playerDataBase.getPlayerExperience(player);
        int newExperience = currentExperience + experienceToAdd;
        playerDataBase.setPlayerExperience(player, newExperience);
        checkAndUpdatePlayerRank(player);
    }

    public int calculateExperienceToNextLevel(int currentExperience) {
        int level = calculateLevelFromExperience(currentExperience);
        int experienceForNextLevel = calculateExperienceForLevel(level + 1);
        int currentLevelExperience = currentExperience - getTotalExperienceForLevel(level);

        return experienceForNextLevel - currentLevelExperience;
    }

    public int getExperienceGainedTowardsNextLevel(Player player) {
        int currentExperience = playerDataBase.getPlayerExperience(player);
        int currentLevel = calculateLevelFromExperience(currentExperience);
        int experienceNeededForCurrentLevel = getTotalExperienceForLevel(currentLevel);

        return currentExperience - experienceNeededForCurrentLevel;
    }

    public List<String> getAllPlayerNames() {
        return playerDataBase.getAllPlayerNames();
    }



    public String getFormattedName(Player player) {
        int level = getPlayerLevel(player);
        return levelRank.getFormattedName(player, level);
    }



    public String getRankPrefix(Player player) {
        int level = getPlayerLevel(player);
        return levelRank.getRankPrefix(level);
    }
}