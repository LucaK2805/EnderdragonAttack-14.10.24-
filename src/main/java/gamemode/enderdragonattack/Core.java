package gamemode.enderdragonattack;

import gamemode.enderdragonattack.Achievement.AchievementCommand;
import gamemode.enderdragonattack.Achievement.AchievementDatabase;
import gamemode.enderdragonattack.Achievement.AchievementManager;
import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.Config.PlayerDataBase;
import gamemode.enderdragonattack.Config.PlaytimeUpdater;
import gamemode.enderdragonattack.Enderdragon.KillEvent;
import gamemode.enderdragonattack.GameWorld.BlockBreakProtection;
import gamemode.enderdragonattack.GameWorld.BlockPlaceProtection;
import gamemode.enderdragonattack.GameWorld.DeathListener;
import gamemode.enderdragonattack.GameWorld.WorldRegenerateCommand;
import gamemode.enderdragonattack.Kit.*;
import gamemode.enderdragonattack.LevelSystem.*;
import gamemode.enderdragonattack.Listener.AntiHungerListener;
import gamemode.enderdragonattack.Listener.MobSpawnBlocker;
import gamemode.enderdragonattack.Listener.PlayerJoin;
import gamemode.enderdragonattack.Perks.*;
import gamemode.enderdragonattack.SetAddRemoveCommands.*;
import gamemode.enderdragonattack.Start_Stop.Start;
import gamemode.enderdragonattack.Start_Stop.Stop;
import gamemode.enderdragonattack.Stats.StatsCommand;
import gamemode.enderdragonattack.Stats.StatsDataBase;
import gamemode.enderdragonattack.Stats.StatsTop;
import gamemode.enderdragonattack.Utilitis.*;
import gamemode.enderdragonattack.CoinSystem.DamageTracker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public final class Core extends JavaPlugin {

    private static Core instance;
    private double defaultDragonHealth;
    private PlaytimeUpdater playtimeUpdater;
    private PlayerDataBase playerDataBase;
    private LevelSystem levelSystem;
    private BukkitTask worldPlayerCheckerTask;
    private Stop stopHandler;
    private WorldLoader worldLoader;
    private WorldRegenerateCommand worldRegenerateCommand;
    private PerkManager perkManager;
    private PerkStart perkStart;
    private PerkStop perkStop;
    private PerkUnlockCommand perkUnlockCommand;
    private PerkShop perkShop;
    private KitManager kitManager;
    private KitsShop kitsShop;
    private KitItems kitItems;
    private KitStart kitStart;
    private LobbyAreaMarker lobbyAreaMarker;
    private final Gradient pluginInstance = new Gradient();
    private final String gradientPrefix = pluginInstance.generateGradient("Dragon");
    private final String prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";
    private DamageTracker damageTracker;
    private Start startHandler;
    private StatsDataBase statsDataBase;
    private LevelRank levelRank;
    private AchievementDatabase achievementDatabase;
    private AchievementManager achievementManager;



    @Override
    public void onEnable() {
        instance = this;
        initializeConfiguration();
        initializePerkManagement();
        initializeKitManagement();
        initializeLobbyAndWorldManagement();
        registerCommands();
        registerEventListeners();
        startWeatherControl();
        this.levelRank = new LevelRank();
        this.getCommand("rank").setExecutor(new PersonalRank(levelSystem));

        this.achievementDatabase = new AchievementDatabase(this);
        this.achievementManager = new AchievementManager(this, statsDataBase, achievementDatabase);

        this.getCommand("achievements").setExecutor(new AchievementCommand(achievementManager));


        sendMessageToOps(prefix + ChatColor.GRAY + "Enderdragonattack Plugin has been started!");
    }

    @Override
    public void onDisable() {
        if (stopHandler != null) {
            stopHandler.stopGame();
        }
        sendMessageToOps(prefix + ChatColor.GRAY + "Enderdragonattack Plugin has been stopped!");
    }

    private void initializeConfiguration() {
        this.playerDataBase = new PlayerDataBase(this);
        this.statsDataBase = new StatsDataBase(this);
        this.levelSystem = new LevelSystem(this, playerDataBase, statsDataBase);
        saveDefaultConfig();
        reloadConfig();
        defaultDragonHealth = 200;
        this.damageTracker = new DamageTracker(this, playerDataBase, statsDataBase);
        this.stopHandler = new Stop(this);
    }

    private void initializePerkManagement() {
        perkManager = new PerkManager(getDataFolder(), new PerkStop(perkManager));
        perkStart = new PerkStart(perkManager);
        perkUnlockCommand = new PerkUnlockCommand(perkManager, new PerkStop(perkManager));
        perkShop = new PerkShop(playerDataBase, perkManager);
    }

    private void initializeKitManagement() {
        kitManager = new KitManager(getDataFolder());
        kitItems = new KitItems();
        kitStart = new KitStart(kitManager, kitItems);
        kitsShop = new KitsShop(playerDataBase, kitManager);
    }

    private void initializeLobbyAndWorldManagement() {
        startHandler = new Start(this, defaultDragonHealth, perkStart, statsDataBase);
        lobbyAreaMarker = new LobbyAreaMarker(this, startHandler);
        worldLoader = new WorldLoader(this);
        worldRegenerateCommand = new WorldRegenerateCommand(this);
        worldLoader.loadWorld("Lobby");
        worldLoader.loadWorld("GameWorld");
        worldRegenerateCommand.resetWorld("GameWorld", "ExampleWorld", "Lobby");
        playtimeUpdater = new PlaytimeUpdater(this, playerDataBase);
        ReloadArea();
    }

    private void registerCommands() {
        getCommand("setcoins").setExecutor(new SetCoinsCommand(this, playerDataBase));
        getCommand("addcoins").setExecutor(new AddCoinsCommand(this, playerDataBase));
        getCommand("removecoins").setExecutor(new RemoveCoinsCommand(this, playerDataBase));
        getCommand("worldteleport").setExecutor(new WorldTeleporter());
        getCommand("list").setExecutor(lobbyAreaMarker);
        getCommand("level").setExecutor(new LevelCommand(levelSystem));
        getCommand("addExperience").setExecutor(new AddExperienceCommand(levelSystem, playerDataBase));
        getCommand("setExperience").setExecutor(new SetExperienceCommand(levelSystem, playerDataBase));
        getCommand("removeExperience").setExecutor(new RemoveExperienceCommand(levelSystem, playerDataBase));
        getCommand("resetworld").setExecutor(worldRegenerateCommand);
        getCommand("setperk").setExecutor(perkUnlockCommand);
        getCommand("perk").setExecutor(new PerkCommand(perkShop));
        getCommand("kit").setExecutor(new KitCommand(kitsShop));
        getCommand("kitstart").setExecutor(new KitStartCommand(kitStart));
        getCommand("stats").setExecutor(new StatsCommand(this, statsDataBase));
        getCommand("top").setExecutor(new StatsTop(this, statsDataBase, levelSystem));
        getCommand("ranklist").setExecutor(new RankCommand());
    }

    private void registerEventListeners() {
        getServer().getPluginManager().registerEvents(new KillEvent(this), this);
        getServer().getPluginManager().registerEvents(new MobSpawnBlocker(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoin(this, levelSystem, lobbyAreaMarker), this);
        getServer().getPluginManager().registerEvents(new LobbyPermission(), this);
        getServer().getPluginManager().registerEvents(new AntiHungerListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakProtection(), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceProtection(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(perkManager, this);
        getServer().getPluginManager().registerEvents(perkShop, this);
        getServer().getPluginManager().registerEvents(kitManager, this);
        getServer().getPluginManager().registerEvents(kitsShop, this);
        getServer().getPluginManager().registerEvents(damageTracker, this);
        getServer().getPluginManager().registerEvents(statsDataBase, this);
        getServer().getPluginManager().registerEvents(new KitSelectionListener(kitStart), this);
    }

    private void startWeatherControl() {
        WeatherManager weatherManager = new WeatherManager(this);
        weatherManager.startWeatherControl();
    }

    public void sendMessageToOps(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                player.sendMessage(message);
            }
        }
    }

    public void reload() {
        if (statsDataBase != null) {
            statsDataBase.onReload();
        }
    }
    public AchievementDatabase getAchievementDatabase() {
        return achievementDatabase;
    }
    public AchievementManager getAchievementManager() {
        return achievementManager;
    }

    public void ReloadArea() {
        new BukkitRunnable() {
            @Override
            public void run() {
                lobbyAreaMarker.initialize();
            }
        }.runTaskLater(this, 100L);
    }

    public DamageTracker getDamageTracker() {
        return damageTracker;
    }

    public StatsDataBase getStatsDataBase() {
        return statsDataBase;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public KitStart getKitStart() {
        return kitStart;
    }

    public static Core getInstance() {
        return instance;
    }
}