package gamemode.enderdragonattack.Perks;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class PerkStop {

    private final PerkManager perkManager;

    public PerkStop(PerkManager perkManager) {
        this.perkManager = perkManager;
    }

    public void removePerksFromAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            removePerksFromPlayer(player);
        }
    }

    public void removePerksFromPlayer(Player player) {
        UUID playerUUID = player.getUniqueId();

        if (perkManager.isPerkUnlockedForPlayer(player, "Nightvision")) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }

        if (perkManager.isPerkUnlockedForPlayer(player, "Jumpboost")) {
            player.removePotionEffect(PotionEffectType.JUMP);
        }

        if (perkManager.isPerkUnlockedForPlayer(player, "Speed")) {
            player.removePotionEffect(PotionEffectType.SPEED);
        }

        if (perkManager.isPerkUnlockedForPlayer(player, "Haste")) {
            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
        }

        if (perkManager.isPerkUnlockedForPlayer(player, "Slowfalling")) {
            player.removePotionEffect(PotionEffectType.SLOW_FALLING);
        }
    }

}