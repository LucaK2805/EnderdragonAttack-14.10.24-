package gamemode.enderdragonattack.Perks;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PerkManager implements Listener {

    private final File perkFile;
    private FileConfiguration perkConfig;
    private final PerkStop perkStop;
    private final Set<String> validPerks = new HashSet<>();

    public PerkManager(File dataFolder, PerkStop perkStop) {
        this.perkFile = new File(dataFolder, "Perk.yml");
        this.perkStop = perkStop;
        loadPerkConfig();
        initializeValidPerks();
    }

    private void loadPerkConfig() {
        if (!perkFile.exists()) {
            perkFile.getParentFile().mkdirs();
            try {
                perkFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        perkConfig = YamlConfiguration.loadConfiguration(perkFile);
    }

    private void initializeValidPerks() {
        validPerks.add("Nightvision");
        validPerks.add("Jumpboost");
        validPerks.add("Speed");
        validPerks.add("Haste");
        validPerks.add("Slowfalling");
    }

    private void savePerkConfig() {
        try {
            perkConfig.save(perkFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        createPlayerEntry(player);
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        perkStop.removePerksFromPlayer(player);
    }

    public void createPlayerEntry(Player player) {
        UUID playerUUID = player.getUniqueId();
        String playerName = player.getName();

        if (!perkConfig.contains(playerUUID.toString())) {
            perkConfig.set(playerUUID.toString() + ".Playername", playerName);
            perkConfig.set(playerUUID.toString() + ".Nightvision", "");
            perkConfig.set(playerUUID.toString() + ".Jumpboost", "");
            perkConfig.set(playerUUID.toString() + ".Speed", "");
            perkConfig.set(playerUUID.toString() + ".Haste", "");
            perkConfig.set(playerUUID.toString() + ".Slowfalling", "");

            savePerkConfig();
        }
    }

    public void unlockPerkForPlayer(Player player, String perkName) {
        if (isValidPerk(perkName)) {
            unlockPerk(player, perkName);
        } else {
            player.sendMessage("The perk " + perkName + " is not valid.");
        }
    }

    public boolean isPerkUnlockedForPlayer(Player player, String perkName) {
        return isValidPerk(perkName) && isPerkUnlocked(player, perkName);
    }

    private void unlockPerk(Player player, String perkName) {
        UUID playerUUID = player.getUniqueId();

        if (perkConfig.contains(playerUUID.toString())) {
            perkConfig.set(playerUUID.toString() + "." + perkName, "x");
            savePerkConfig();
        } else {
            player.sendMessage("Your entry in the perk configuration could not be found.");
        }
    }

    public void removePerkFromPlayer(Player player, String perkName) {
        if (isValidPerk(perkName)) {
            UUID playerUUID = player.getUniqueId();
            if (perkConfig.contains(playerUUID.toString())) {
                perkConfig.set(playerUUID.toString() + "." + perkName, "");
                savePerkConfig();
            }
        }
    }

    private boolean isPerkUnlocked(Player player, String perkName) {
        UUID playerUUID = player.getUniqueId();
        return "x".equals(perkConfig.getString(playerUUID.toString() + "." + perkName));
    }

    private boolean isValidPerk(String perkName) {
        return validPerks.contains(perkName);
    }
}