package gamemode.enderdragonattack.Achievement.AchievementLogic;

import gamemode.enderdragonattack.Achievement.AchievementManager;
import gamemode.enderdragonattack.Achievement.AchievementDatabase;
import gamemode.enderdragonattack.Stats.StatsDataBase;
import gamemode.enderdragonattack.Color.Gradient;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class PlayGames implements AchievementManager.Achievement {
    private final StatsDataBase statsDataBase;
    private final AchievementManager achievementManager;
    private final AchievementDatabase achievementDatabase;
    private final String prefix;

    public PlayGames(StatsDataBase statsDataBase, AchievementManager achievementManager, AchievementDatabase achievementDatabase) {
        this.statsDataBase = statsDataBase;
        this.achievementManager = achievementManager;
        this.achievementDatabase = achievementDatabase;

        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";
    }

    @Override
    public boolean update(Player player, Inventory gui) {
        if (achievementDatabase == null) {
            System.err.println("AchievementDatabase is null in PlayGames.update()");
            return false;
        }

        statsDataBase.reloadStats();

        String uuid = player.getUniqueId().toString();
        int gamesPlayed = statsDataBase.getGamesPlayed(player.getUniqueId());
        int newTier = getTier(gamesPlayed);
        String currentStatus = achievementDatabase.getAchievementStatus(uuid, "A Long Way");
        int currentTier = getTierFromStatus(currentStatus);

        boolean leveledUp = false;
        if (newTier != currentTier) {
            updateAchievementStatus(uuid, newTier);
            if (newTier > currentTier) {
                leveledUp = true;
            }
        }

        if (gui != null) {
            ItemStack achievementItem = createAchievementItem(newTier, gamesPlayed);
            achievementManager.addAchievement(gui, achievementItem, 2, 3);
        }

        return leveledUp;
    }

    @Override
    public void notifyLevelUp(Player player) {
        String uuid = player.getUniqueId().toString();
        String currentStatus = achievementDatabase.getAchievementStatus(uuid, "TieredAchievement1");
        int currentTier = getTierFromStatus(currentStatus);

        String tierName;
        String color;
        switch (currentTier) {
            case 1:
                tierName = "Copper";
                color = "§c";
                break;
            case 2:
                tierName = "Iron";
                color = "§f";
                break;
            case 3:
                tierName = "Gold";
                color = "§6";
                break;
            default:
                return; // Should not occur
        }

        player.sendMessage(prefix + "§a§l✦ Achievement Tier Increased! ✦");
        player.sendMessage(prefix + "§7You have reached the " + color + tierName + "§7 tier for 'A Long Way'!");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    private int getTier(int gamesPlayed) {
        if (gamesPlayed >= 100) return 3;
        if (gamesPlayed >= 50) return 2;
        if (gamesPlayed >= 10) return 1;
        return 0;
    }

    private int getTierFromStatus(String status) {
        switch (status) {
            case "GOLD": return 3;
            case "IRON": return 2;
            case "COPPER": return 1;
            default: return 0;
        }
    }

    private void updateAchievementStatus(String uuid, int tier) {
        switch (tier) {
            case 3:
                achievementDatabase.setAchievementGold(uuid, "TieredAchievement1");
                break;
            case 2:
                achievementDatabase.setAchievementIron(uuid, "TieredAchievement1");
                break;
            case 1:
                achievementDatabase.setAchievementCopper(uuid, "TieredAchievement1");
                break;
            default:
                achievementDatabase.setAchievementLocked(uuid, "TieredAchievement1");
                break;
        }
    }

    private ItemStack createAchievementItem(int tier, int gamesPlayed) {
        Material material;
        String tierName;
        String color;
        int nextTierRequirement;
        switch (tier) {
            case 3:
                material = Material.GOLD_INGOT;
                tierName = "Gold";
                color = "§6"; // Gold
                nextTierRequirement = 100;
                break;
            case 2:
                material = Material.IRON_INGOT;
                tierName = "Iron";
                color = "§f"; // White
                nextTierRequirement = 100;
                break;
            case 1:
                material = Material.COPPER_INGOT;
                tierName = "Copper";
                color = "§c"; // Red
                nextTierRequirement = 50;
                break;
            default:
                material = Material.GRAY_DYE;
                tierName = "Not Unlocked";
                color = "§8"; // Dark Gray
                nextTierRequirement = 10;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color + "§l✦ A Long Way ✦");

        String progressBar = createProgressBar(gamesPlayed, nextTierRequirement);
        int percentage = calculatePercentage(gamesPlayed, nextTierRequirement);

        meta.setLore(Arrays.asList(
                "§7Play games to get this Achievement",
                "",
                "§7Tier: " + color + tierName,
                "§7Games Played: §e" + gamesPlayed,
                "",
                color + getNextTierRequirement(tier),
                "",
                "§7Progress:",
                progressBar + " §e" + percentage + "%"
        ));
        item.setItemMeta(meta);
        return item;
    }

    private String createProgressBar(int current, int max) {
        int percentage = calculatePercentage(current, max);
        int filledBars = (int) Math.round(percentage / 10.0);
        StringBuilder bar = new StringBuilder("§a");
        for (int i = 0; i < 10; i++) {
            if (i < filledBars) {
                bar.append("■");
            } else {
                bar.append("§7■");
            }
        }
        return bar.toString();
    }

    private int calculatePercentage(int current, int max) {
        return (int) Math.min(100, Math.round((double) current / max * 100));
    }

    private String getNextTierRequirement(int currentTier) {
        switch (currentTier) {
            case 0: return "Next Tier: §c10 Games for Copper";
            case 1: return "Next Tier: §f50 Games for Iron";
            case 2: return "Next Tier: §6100 Games for Gold";
            default: return "§a§lHighest Tier Reached!";
        }
    }
}