package gamemode.enderdragonattack.Start_Stop;

import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.Enderdragon.DragonSpawner;
import gamemode.enderdragonattack.GameWorld.WorldRegenerateCommand;
import gamemode.enderdragonattack.Kit.KitStart;
import gamemode.enderdragonattack.Perks.PerkStart;
import gamemode.enderdragonattack.Core;
import gamemode.enderdragonattack.Stats.StatsDataBase;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Start {

    Gradient pluginInstance = new Gradient();
    String gradientPrefix = pluginInstance.generateGradient("Dragon");
    String Prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";
    private final JavaPlugin plugin;
    private final double defaultDragonHealth;
    private List<Player> participants;
    private StartTimer startTimer;
    private static boolean isGameRunning = false;
    private Stop stopInstance;
    private final PerkStart perkStart;
    private final StatsDataBase statsDataBase;

    public Start(JavaPlugin plugin, double defaultDragonHealth, PerkStart perkStart, StatsDataBase statsDataBase) {
        this.plugin = plugin;
        this.defaultDragonHealth = defaultDragonHealth;
        this.participants = new ArrayList<>();
        this.perkStart = perkStart;
        this.statsDataBase = statsDataBase;

        if (this.statsDataBase == null) {
            plugin.getLogger().severe("StatsDataBase is null in Start constructor!");
        } else {
            plugin.getLogger().info("StatsDataBase successfully initialized in Start constructor.");
        }

        if (this.perkStart == null) {
            plugin.getLogger().severe("PerkStart is null in Start constructor!");
        } else {
            plugin.getLogger().info("PerkStart successfully initialized in Start constructor.");
        }

        WorldRegenerateCommand worldRegenerateCommand = new WorldRegenerateCommand(plugin);
        this.startTimer = new StartTimer(plugin, this, worldRegenerateCommand);

        startPeriodicCheck();
    }

    public List<Player> getParticipants() {
        return participants;
    }

    public void addParticipant(Player player) {
        if (!participants.contains(player)) {
            participants.add(player);
        }
    }

    public void removeParticipant(Player player) {
        participants.remove(player);
    }

    public StartTimer getStartTimer() {
        return startTimer;
    }

    public static boolean isGameRunning() {
        return isGameRunning;
    }

    public static void setGameRunning(boolean gameRunning) {
        isGameRunning = gameRunning;
    }

    public void startGame() {
        plugin.getLogger().info("Starting game...");
        setGameRunning(true);

        World gameWorld = Bukkit.getWorld("GameWorld");
        if (gameWorld == null) {
            plugin.getLogger().severe("The world 'GameWorld' is not loaded or does not exist!");
            return;
        }

        Location startLocation = gameWorld.getSpawnLocation();

        for (Player participant : participants) {
            participant.teleport(startLocation);
            participant.setGameMode(GameMode.SURVIVAL);
            participant.getInventory().clear();

            if (statsDataBase != null) {
                try {
                    statsDataBase.incrementGamesPlayed(participant);
                    plugin.getLogger().info("Incremented games played for player: " + participant.getName());
                } catch (Exception e) {
                    plugin.getLogger().severe("Error incrementing games played for player " + participant.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                plugin.getLogger().severe("StatsDataBase is null when trying to increment games played for player: " + participant.getName());
            }

            // Open the kit selection menu for each participant
            if (plugin instanceof Core) {
                Core core = (Core) plugin;
                KitStart kitStart = core.getKitStart();
                if (kitStart != null) {
                    kitStart.openKitSelectionMenu(participant);
                } else {
                    plugin.getLogger().severe("KitStart is null when trying to open kit selection menu for player: " + participant.getName());
                }
            } else {
                plugin.getLogger().severe("Plugin is not an instance of Core. Cannot open kit selection menu.");
            }

            // Start 15-minute timer for each participant
            startTimerForPlayer(participant);
        }

        DragonSpawner.spawnEnderDragon(plugin, defaultDragonHealth);
        participants.clear();

        if (perkStart != null) {
            perkStart.applyPerksToAllPlayers();
        } else {
            plugin.getLogger().severe("PerkStart is null when trying to apply perks to all players.");
        }

        if (plugin instanceof Core) {
            Core core = (Core) plugin;
            this.stopInstance = new Stop(core);
        } else {
            plugin.getLogger().severe("Plugin is not an instance of Core. Cannot initialize Stop instance.");
        }

        plugin.getLogger().info("Game started successfully.");
    }

    private void startTimerForPlayer(Player player) {
        final int[] timeLeft = {15 * 60};

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isGameRunning() || !player.isOnline()) {
                    this.cancel();
                    return;
                }

                int minutes = timeLeft[0] / 60;
                int seconds = timeLeft[0] % 60;
                String timeString = String.format("%02d:%02d", minutes, seconds);

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Time left: " + ChatColor.GOLD + timeString));

                // Countdown in title for last 5 seconds
                if (timeLeft[0] <= 5 && timeLeft[0] > 0) {
                    player.sendTitle(
                            ChatColor.RED + Integer.toString(timeLeft[0]),
                            ChatColor.GOLD + "Game ending soon!",
                            0, 20, 0
                    );
                }

                timeLeft[0]--;

                if (timeLeft[0] < 0) {
                    this.cancel();
                    player.sendTitle(
                            ChatColor.RED + "Time's up!",
                            ChatColor.GOLD + "Game Over",
                            0, 60, 20
                    );
                    if (stopInstance != null) {
                        Bukkit.getScheduler().runTask(plugin, () -> stopInstance.stopGame());
                    } else {
                        plugin.getLogger().warning("Stop instance is null. Cannot stop the game.");
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 20L); // Run every second
    }

    private void startPeriodicCheck() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isGameRunning) {
                    World gameWorld = Bukkit.getWorld("GameWorld");
                    if (gameWorld != null && gameWorld.getPlayers().isEmpty()) {
                        if (stopInstance != null) {
                            stopInstance.stopGame();
                        } else {
                            plugin.getLogger().warning("Stop instance is null. Cannot stop the game.");
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}