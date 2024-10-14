package gamemode.enderdragonattack.Achievement;

import gamemode.enderdragonattack.Achievement.AchievementLogic.PlayGames;
import gamemode.enderdragonattack.Achievement.AchievementLogic.DealDamage;
import gamemode.enderdragonattack.Stats.StatsDataBase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class AchievementManager implements Listener {

    private static final int GUI_ROWS = 5;
    private static final int GUI_COLUMNS = 9;
    private static final String GUI_TITLE = "Achievements";

    private final JavaPlugin plugin;
    private final StatsDataBase statsDataBase;
    private final AchievementDatabase achievementDatabase;
    private final Map<String, Achievement> achievements = new HashMap<>();
    private final Map<Player, BukkitTask> updateTasks = new HashMap<>();

    public AchievementManager(JavaPlugin plugin, StatsDataBase statsDataBase, AchievementDatabase achievementDatabase) {
        this.plugin = plugin;
        this.statsDataBase = statsDataBase;
        this.achievementDatabase = achievementDatabase;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        initializeAchievements();
    }

    private void initializeAchievements() {
        achievements.put("PlayGames", new PlayGames(statsDataBase, this, achievementDatabase));
        achievements.put("DealDamage", new DealDamage(statsDataBase, this, achievementDatabase));
    }

    public Inventory createAndUpdateAchievementGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, GUI_ROWS * GUI_COLUMNS, GUI_TITLE);

        ItemStack glassPane = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glassPane.getItemMeta();
        glassMeta.setDisplayName(" ");
        glassPane.setItemMeta(glassMeta);

        for (int i = 0; i < GUI_ROWS * GUI_COLUMNS; i++) {
            gui.setItem(i, glassPane);
        }

        updateAchievements(player, gui);
        startUpdateTask(player, gui);
        return gui;
    }

    private void updateAchievements(Player player, Inventory gui) {
        for (Achievement achievement : achievements.values()) {
            boolean leveledUp = achievement.update(player, gui);
            if (leveledUp) {
                achievement.notifyLevelUp(player);
            }
        }
    }

    private void startUpdateTask(Player player, Inventory gui) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline() && player.getOpenInventory().getTitle().equals(GUI_TITLE)) {
                    updateAchievements(player, gui);
                } else {
                    this.cancel();
                    updateTasks.remove(player);
                }
            }
        }.runTaskTimer(plugin, 100L, 100L); // 100 Ticks = 5 Sekunden

        updateTasks.put(player, task);
    }

    public void addAchievement(Inventory gui, ItemStack achievementItem, int row, int col) {
        if (gui != null && row >= 1 && row <= GUI_ROWS && col >= 1 && col <= GUI_COLUMNS) {
            int slot = (row - 1) * GUI_COLUMNS + col;
            gui.setItem(slot, achievementItem);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(GUI_TITLE)) {
            event.setCancelled(true);

            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.LIGHT_GRAY_STAINED_GLASS_PANE) {
                Player player = (Player) event.getWhoClicked();
                player.sendMessage("You clicked on the achievement '" + event.getCurrentItem().getItemMeta().getDisplayName() + "'!");
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(GUI_TITLE)) {
            Player player = (Player) event.getPlayer();
            BukkitTask task = updateTasks.remove(player);
            if (task != null) {
                task.cancel();
            }
        }
    }

    public interface Achievement {
        boolean update(Player player, Inventory gui);
        void notifyLevelUp(Player player);
    }

    public AchievementDatabase getAchievementDatabase() {
        return achievementDatabase;
    }
}