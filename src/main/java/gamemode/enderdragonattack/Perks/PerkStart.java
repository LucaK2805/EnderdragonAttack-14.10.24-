package gamemode.enderdragonattack.Perks;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class PerkStart implements Listener {

    private final PerkManager perkManager;

    public PerkStart(PerkManager perkManager) {
        this.perkManager = perkManager;
    }

    public void applyPerksToAllPlayers() {
        World gameWorld = Bukkit.getWorld("GameWorld");
        if (gameWorld != null) {
            for (Player player : gameWorld.getPlayers()) {
                applyPerksToPlayer(player);
            }
        }
    }

    public void applyPerksToPlayer(Player player) {
        UUID playerUUID = player.getUniqueId();

        if (perkManager.isPerkUnlockedForPlayer(player, "Nightvision")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true, false));
        }

        if (perkManager.isPerkUnlockedForPlayer(player, "Jumpboost")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0, true, false));
        }

        if (perkManager.isPerkUnlockedForPlayer(player, "Speed")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true, false));
        }

        if (perkManager.isPerkUnlockedForPlayer(player, "Haste")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1, true, false));
        }

        if (perkManager.isPerkUnlockedForPlayer(player, "Slowfalling")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 0, true, false));
        }
    }

}