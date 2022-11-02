package us.mcmagic.arcade.shops;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.arcade.Arcade;
import us.mcmagic.arcade.handlers.InventoryType;
import us.mcmagic.arcade.handlers.PlayerData;
import us.mcmagic.arcade.handlers.games.PixieData;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 9/18/15
 */
public class PixieShopManager {
    private HashMap<UUID, Long> purchaseDelay = new HashMap<>();
    private HashMap<UUID, UUID> managerMap = new HashMap<>();

    public void openInventory(InventoryType type, Player player, PlayerData data, UUID manager) {
        managerMap.remove(player.getUniqueId());
        managerMap.put(player.getUniqueId(), manager);
        openInventory(type, player, data);
    }

    public void openInventory(InventoryType type, Player player, PlayerData data) {
        PixieData pixied = data.getPixieData();
        if (!type.equals(InventoryType.SHOP_PIXIE)) {
            return;
        }
        int bal = MCMagicCore.economy.getBalance(player.getUniqueId());
        int delay = pixied.getDelay();
        int jump = pixied.getJump();
        Inventory pixie = Bukkit.createInventory(player, 45, type.getTitle());
        ItemStack d16 = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 5, ChatColor.GREEN + "1.6s Delay",
                getLore(16, player, data, bal));
        ItemStack d15 = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 5, (delay < 16 ? ChatColor.GREEN :
                ChatColor.RED) + "1.5s Delay", getLore(15, player, data, bal));
        ItemStack d14 = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 5, (delay < 15 ? ChatColor.GREEN :
                ChatColor.RED) + "1.4s Delay", getLore(14, player, data, bal));
        ItemStack d13 = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 5, (delay < 14 ? ChatColor.GREEN :
                ChatColor.RED) + "1.3s Delay", getLore(13, player, data, bal));
        ItemStack d12 = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 5, (delay < 13 ? ChatColor.GREEN :
                ChatColor.RED) + "1.2s Delay", getLore(12, player, data, bal));
        ItemStack d11 = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 5, (delay < 12 ? ChatColor.GREEN :
                ChatColor.RED) + "1.1s Delay", getLore(11, player, data, bal));
        ItemStack d10 = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 5, (delay < 11 ? ChatColor.GREEN :
                ChatColor.RED) + "1.0s Delay", getLore(10, player, data, bal));
        ItemStack j1 = new ItemCreator(Material.POTION, 1, (byte) 8267, ChatColor.GREEN + "Jump 1", getLore(1, player,
                data, bal));
        ItemStack j3 = new ItemCreator(Material.POTION, 1, (byte) 8267, (jump > 1 ? ChatColor.GREEN : ChatColor.RED) +
                "Jump 3", getLore(3, player, data, bal));
        ItemStack j5 = new ItemCreator(Material.POTION, 1, (byte) 8267, (jump > 3 ? ChatColor.GREEN : ChatColor.RED) +
                "Jump 5", getLore(5, player, data, bal));
        ItemStack j7 = new ItemCreator(Material.POTION, 1, (byte) 8267, (jump > 5 ? ChatColor.GREEN : ChatColor.RED) +
                "Jump 7", getLore(7, player, data, bal));
        ItemStack j9 = new ItemCreator(Material.POTION, 1, (byte) 8267, (jump > 7 ? ChatColor.GREEN : ChatColor.RED) +
                "Jump 9", getLore(9, player, data, bal));
        pixie.setItem(10, d16);
        pixie.setItem(11, d15);
        pixie.setItem(12, d14);
        pixie.setItem(13, d13);
        pixie.setItem(14, d12);
        pixie.setItem(15, d11);
        pixie.setItem(16, d10);
        pixie.setItem(29, j1);
        pixie.setItem(30, j3);
        pixie.setItem(31, j5);
        pixie.setItem(32, j7);
        pixie.setItem(33, j9);
        player.openInventory(pixie);
    }

    private List<String> getLore(int i, Player player, PlayerData data, int bal) {
        List<String> lore = new ArrayList<>();
        PixieData pdata = data.getPixieData();
        if (i >= 10) {
            //Delay
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.CEILING);
            int price = getPrice(i);
            lore.add(ChatColor.GRAY + "Decrease your shooting delay to " + getNum(i) + "s!");
            lore.add("");
            if (pdata.getDelay() > i) {
                lore.add(ChatColor.YELLOW + "Price: " + (bal >= price ? ChatColor.GREEN : ChatColor.RED) + "$" + price);
            } else {
                lore.add(ChatColor.DARK_GRAY + "Price: " + ChatColor.STRIKETHROUGH + "$" + price);
            }
            lore.add("");
        } else {
            //Jump
            int price = getPrice(i);
            lore.add(ChatColor.GRAY + "Get " + i + " Double Jump" + (i > 1 ? "s" : "") + " every game!");
            lore.add("");
            if (pdata.getJump() < i) {
                lore.add(ChatColor.YELLOW + "Price: " + (bal >= price ? ChatColor.GREEN : ChatColor.RED) + "$" + price);
            } else {
                lore.add(ChatColor.DARK_GRAY + "Price: " + ChatColor.STRIKETHROUGH + "$" + price);
            }
            lore.add("");
        }
        return lore;
    }

    private double getNum(int i) {
        switch (i) {
            case 16:
                return 1.6;
            case 15:
                return 1.5;
            case 14:
                return 1.4;
            case 13:
                return 1.3;
            case 12:
                return 1.2;
            case 11:
                return 1.1;
            case 10:
                return 1.0;
        }
        return 0;
    }

    private int getPrice(int i) {
        int price = 0;
        if (i < 16 && i > 9) {
            price = (16 - i) * 100;
        }
        if (i > 1 && i < 10) {
            price = (((i - 1) / 2) * 150);
        }
        return price;
    }

    public void clickInventory(InventoryClickEvent event, InventoryType type, Player player, ItemStack current) {
        ItemMeta meta = current.getItemMeta();
        if (meta == null || meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName().toLowerCase());
        final PlayerData data = Arcade.getInstance().getPlayerData(player.getUniqueId());
        if (!type.equals(InventoryType.SHOP_PIXIE)) {
            return;
        }
        if (name.contains("delay")) {
            int delay = (int) (Double.parseDouble(name.replace("s delay", "")) * 10);
            purchase(player, data, delay, "delay", ((float) delay / 10) + "s Delay");
        } else if (name.contains("jump")) {
            int jump = Integer.parseInt(name.replace("jump ", ""));
            purchase(player, data, jump, "doublejump", "Jump " + jump);
        }
    }

    private void managerPurchaseParticle(Player player) {
        UUID entity = managerMap.get(player.getUniqueId());
        if (entity == null) {
            return;
        }
        Entity e = getEntity(player.getWorld(), entity);
        if (e == null) {
            return;
        }
        player.getWorld().playSound(player.getLocation(), Sound.GHAST_FIREBALL, 4f, 1f);
        ParticleUtil.spawnParticle(ParticleEffect.LAVA, e.getLocation(), 0.5f, 0.5f, 0.5f, 0, 10);
    }

    private Entity getEntity(World world, UUID entity) {
        for (Entity e : world.getEntities()) {
            if (e.getUniqueId().equals(entity)) {
                return e;
            }
        }
        return null;
    }

    private void purchase(Player player, PlayerData data, int value, String type, String name) {
        int current = 0;
        switch (type) {
            case "delay":
                current = data.getPixieData().getDelay();
                if (value >= current) {
                    return;
                }
                if (value < (current - 1)) {
                    player.sendMessage(ChatColor.RED + "You must purchase the next Delay, first!");
                    player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 10f, 0f);
                    return;
                }
                break;
            case "doublejump":
                current = data.getPixieData().getJump();
                if (value <= current) {
                    return;
                }
                if (value > (current + 2)) {
                    player.sendMessage(ChatColor.RED + "You must purchase the next Double Jump, first!");
                    player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 10f, 0f);
                    return;
                }
                break;
        }
        int price = getPrice(value);
        if (purchaseDelay.containsKey(player.getUniqueId())) {
            if ((System.currentTimeMillis() - purchaseDelay.get(player.getUniqueId())) < 5000) {
                player.sendMessage(ChatColor.RED + "You have to wait at least 5 seconds in-between purchases!");
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 25, 1);
                return;
            }
            purchaseDelay.remove(player.getUniqueId());
        }
        int bal = MCMagicCore.economy.getBalance(player.getUniqueId());
        if (bal < price) {
            player.sendMessage(ChatColor.RED + "You can't afford that!");
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 25, 1);
            return;
        }
        player.sendMessage(ChatColor.GREEN + "You purchased the " + ChatColor.YELLOW + "Pixie Dust Shootout " + name + "!");
        MCMagicCore.economy.addBalance(player.getUniqueId(), -price);
        purchaseDelay.put(player.getUniqueId(), System.currentTimeMillis());
        managerPurchaseParticle(player);
        switch (type.toLowerCase()) {
            case "delay":
                data.getPixieData().setDelay(value);
                break;
            case "doublejump":
                data.getPixieData().setJump(value);
                break;
        }
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql =
                    connection.prepareStatement("UPDATE pixie_data SET " + type.toLowerCase() + "=? WHERE uuid=?");
            sql.setInt(1, value);
            sql.setString(2, player.getUniqueId().toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5f, 2f);
    }
}