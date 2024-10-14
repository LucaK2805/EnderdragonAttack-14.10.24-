package gamemode.enderdragonattack.Enderdragon;

import gamemode.enderdragonattack.Start_Stop.Stop;
import gamemode.enderdragonattack.Core;
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class KillEvent implements Listener {

    private final JavaPlugin plugin;
    private Stop stopInstance;

    public KillEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        // We'll initialize stopInstance when it's needed
    }

    @EventHandler
    public void onEnderDragonKill(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EnderDragon) {
            if (stopInstance == null) {
                initializeStopInstance();
            }

            if (stopInstance != null) {
                stopInstance.stopGame();
            } else {
                Bukkit.getLogger().severe("Failed to initialize Stop instance. Cannot stop the game.");
            }
        }
    }

    private void initializeStopInstance() {
        if (plugin instanceof Core) {
            Core core = (Core) plugin;
            this.stopInstance = new Stop(core);
        } else {
            Bukkit.getLogger().severe("Plugin is not an instance of Core. Cannot initialize Stop instance.");
        }
    }
}