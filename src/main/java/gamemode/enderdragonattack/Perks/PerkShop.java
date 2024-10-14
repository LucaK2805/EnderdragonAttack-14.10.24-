package gamemode.enderdragonattack.Perks;

import gamemode.enderdragonattack.Config.PlayerDataBase;
import gamemode.enderdragonattack.Color.Gradient;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import java.util.*;

public class PerkShop implements Listener {
    private final PlayerDataBase playerDataBase;
    private final PerkManager perkManager;
    private final Map<String, Integer> perkPrices;
    private final Map<String, Material> perkIcons;
    private final Map<String, String> perkDescriptions;
    private final String Prefix;

    public PerkShop(PlayerDataBase playerDataBase, PerkManager perkManager) {
        this.playerDataBase = playerDataBase;
        this.perkManager = perkManager;
        this.perkPrices = new HashMap<>();
        this.perkIcons = new HashMap<>();
        this.perkDescriptions = new HashMap<>();
        initializePerkData();
        this.Prefix = "[" + new Gradient().generateGradient("Dragon") + ChatColor.RESET + "] ";
    }

    private void initializePerkData() {
        String[] perks = {"Nightvision", "Jumpboost", "Speed", "Haste", "Slowfalling"};
        int[] prices = {800, 2000, 1500, 2500, 3000};
        Material[] icons = {Material.ENDER_PEARL, Material.POTION, Material.DIAMOND_BOOTS, Material.DIAMOND_PICKAXE, Material.FEATHER};
        String[] descriptions = {
                "Gives you night vision\nwhile the game is running",
                "Gives you increased jump height\nwhile the game is running",
                "Gives you increased movement speed\nwhile the game is running",
                "Gives you increased mining speed\nwhile the game is running",
                "Gives you reduced fall speed\nwhile the game is running"
        };

        for (int i = 0; i < perks.length; i++) {
            perkPrices.put(perks[i], prices[i]);
            perkIcons.put(perks[i], icons[i]);
            perkDescriptions.put(perks[i], descriptions[i]);
        }
    }

    public void openPerkShop(Player player) {
        Inventory perkShop = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Perk Shop");
        int[] perkSlots = {1, 7, 13, 19, 25};
        int index = 0;
        for (Map.Entry<String, Integer> entry : perkPrices.entrySet()) {
            if (index < perkSlots.length) {
                perkShop.setItem(perkSlots[index], createPerkItem(entry.getKey(), perkManager.isPerkUnlockedForPlayer(player, entry.getKey()), entry.getValue()));
                index++;
            }
        }
        player.openInventory(perkShop);
    }

    private ItemStack createPerkItem(String perkName, boolean isUnlocked, int price) {
        ItemStack item = new ItemStack(perkIcons.get(perkName));
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof PotionMeta) {
            ((PotionMeta) meta).clearCustomEffects();
        }
        meta.setDisplayName((isUnlocked ? ChatColor.GREEN : ChatColor.RED) + perkName + ChatColor.GRAY + " (" + (isUnlocked ? "Unlocked" : "Not unlocked") + ")");
        meta.setLore(Arrays.asList(
                "",
                ChatColor.WHITE + perkDescriptions.get(perkName),
                "",
                (isUnlocked ? ChatColor.GRAY : ChatColor.RED) + "Status: " + (isUnlocked ? "Unlocked" : "Not unlocked"),
                "",
                ChatColor.YELLOW + "Price: " + price + " coins"
        ));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GOLD + "Perk Shop")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                String perkName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()).split(" \\(")[0];
                if (!perkManager.isPerkUnlockedForPlayer(player, perkName)) {
                    openConfirmationGUI(player, perkName, perkPrices.get(perkName));
                } else {
                    player.sendMessage(Prefix + ChatColor.RED + "You already have this perk unlocked.");
                }
            }
        } else if (event.getView().getTitle().startsWith(ChatColor.GOLD + "Confirm Purchase: ")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                String perkName = ChatColor.stripColor(event.getView().getTitle().split(": ")[1]);
                int price = perkPrices.get(perkName);
                if (clickedItem.getType() == Material.EMERALD_BLOCK) {
                    if (playerDataBase.getPlayerCoins(player) >= price) {
                        playerDataBase.setPlayerCoins(player, playerDataBase.getPlayerCoins(player) - price);
                        perkManager.unlockPerkForPlayer(player, perkName);
                        player.sendMessage(Prefix + ChatColor.GREEN + "You have unlocked the " + perkName + " perk!");
                        player.closeInventory();
                    } else {
                        player.sendMessage(Prefix + ChatColor.RED + "You do not have enough coins to purchase this perk.");
                        player.closeInventory();
                    }
                } else if (clickedItem.getType() == Material.REDSTONE_BLOCK) {
                    openPerkShop(player);
                }
            }
        }
    }

    private void openConfirmationGUI(Player player, String perkName, int price) {
        Inventory confirmGUI = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Confirm Purchase: " + perkName);
        confirmGUI.setItem(11, createConfirmationItem(Material.REDSTONE_BLOCK, ChatColor.RED + "Cancel Purchase",
                "Click to cancel the purchase", "and return to the shop."));
        confirmGUI.setItem(15, createConfirmationItem(Material.EMERALD_BLOCK, ChatColor.GREEN + "Confirm Purchase",
                "Click to confirm the purchase of", perkName + " for " + price + " coins."));
        player.openInventory(confirmGUI);
    }

    private ItemStack createConfirmationItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}