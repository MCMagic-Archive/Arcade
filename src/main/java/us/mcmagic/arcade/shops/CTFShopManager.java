package us.mcmagic.arcade.shops;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.arcade.Arcade;
import us.mcmagic.arcade.handlers.InventoryType;
import us.mcmagic.arcade.handlers.PlayerData;
import us.mcmagic.arcade.handlers.games.CTFData;
import us.mcmagic.arcade.utils.InventoryUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.arcade.ctf.CTFKit;
import us.mcmagic.mcmagiccore.chat.formattedmessage.FormattedMessage;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Marc on 9/18/15
 */
public class CTFShopManager {
    private HashMap<UUID, Long> purchaseDelay = new HashMap<>();
    private HashMap<UUID, UUID> managerMap = new HashMap<>();

    public void openInventory(InventoryType type, Player player, PlayerData data, UUID manager) {
        managerMap.remove(player.getUniqueId());
        managerMap.put(player.getUniqueId(), manager);
        openInventory(type, player, data);
    }

    public void openInventory(InventoryType type, Player player, PlayerData data) {
        CTFData ctfd = data.getCTFData();
        switch (type) {
            case SHOP_CTF:
                Inventory ctf = Bukkit.createInventory(player, 27, type.getTitle());
                CTFKit sel = CTFKit.fromString(ctfd.getKit());
                ItemStack ha = new ItemCreator(Material.FIREBALL, ChatColor.RED + "Hades Kit", lore(CTFKit.HADES, ctfd));
                ItemStack ba = new ItemCreator(Material.DIAMOND, ChatColor.AQUA + "Baymax Kit", lore(CTFKit.BAYMAX, ctfd));
                ItemStack he = new ItemCreator(Material.STONE_SWORD, ChatColor.GOLD + "Hercules Kit",
                        lore(CTFKit.HERCULES, ctfd));
                ItemStack me = new ItemCreator(Material.BOW, ChatColor.DARK_GREEN + "Merida Kit", lore(CTFKit.MERIDA, ctfd));
                ItemStack bo = new ItemCreator(Material.BONE, ChatColor.YELLOW + "Bolt Kit", lore(CTFKit.BOLT, ctfd));
                if (sel != null) {
                    if (sel.equals(CTFKit.HADES)) {
                        ha.addUnsafeEnchantment(Enchantment.LUCK, 1);
                    }
                    if (sel.equals(CTFKit.BAYMAX)) {
                        ba.addUnsafeEnchantment(Enchantment.LUCK, 1);
                    }
                    if (sel.equals(CTFKit.HERCULES)) {
                        he.addUnsafeEnchantment(Enchantment.LUCK, 1);
                    }
                    if (sel.equals(CTFKit.MERIDA)) {
                        me.addUnsafeEnchantment(Enchantment.LUCK, 1);
                    }
                    if (sel.equals(CTFKit.BOLT)) {
                        bo.addUnsafeEnchantment(Enchantment.LUCK, 1);
                    }
                }
                ctf.setItem(11, ha);
                ctf.setItem(12, ba);
                ctf.setItem(13, he);
                ctf.setItem(14, me);
                ctf.setItem(15, bo);
                player.openInventory(ctf);
                break;
            case SHOP_CTF_HADES:
                Inventory hades = Bukkit.createInventory(player, 54, type.getTitle());
                shopInv(hades, CTFKit.HADES, player, data.getCTFData());
                break;
            case SHOP_CTF_BAYMAX:
                Inventory baymax = Bukkit.createInventory(player, 54, type.getTitle());
                shopInv(baymax, CTFKit.BAYMAX, player, data.getCTFData());
                break;
            case SHOP_CTF_HERCULES:
                Inventory hercules = Bukkit.createInventory(player, 54, type.getTitle());
                shopInv(hercules, CTFKit.HERCULES, player, data.getCTFData());
                break;
            case SHOP_CTF_MERIDA:
                Inventory merida = Bukkit.createInventory(player, 54, type.getTitle());
                shopInv(merida, CTFKit.MERIDA, player, data.getCTFData());
                break;
            case SHOP_CTF_BOLT:
                Inventory bolt = Bukkit.createInventory(player, 54, type.getTitle());
                shopInv(bolt, CTFKit.BOLT, player, data.getCTFData());
                break;
        }
    }

    private List<String> lore(CTFKit kit, CTFData ctfd) {
        List<String> list = Arrays.asList("", ChatColor.GREEN + "LEFT-CLICK " + ChatColor.YELLOW + "to customize this Kit",
                ctfd.getKit().equalsIgnoreCase(kit.name()) ? ChatColor.GREEN + "SELECTED" : ChatColor.GREEN +
                        "RIGHT-CLICK " + ChatColor.YELLOW + "to select this Kit");
        return list;
    }

    private void shopInv(final Inventory inv, final CTFKit kit, final Player player, final CTFData data) {
        int bal = MCMagicCore.economy.getBalance(player.getUniqueId());
        int abl = data.get(kit, 1) + 1;
        for (int i = 1; i < 8; i++) {
            List<String> lore = abilLoreFromKit(kit, i);
            int price = getPriceForTier(i);
            String pr = "$" + price;
            ItemStack stack;
            if (i < abl) {
                lore.add(" ");
                lore.add(ChatColor.DARK_GRAY + "Price: " + ChatColor.STRIKETHROUGH + pr);
                lore.add(" ");
                stack = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 5, ChatColor.GREEN + "Ability Upgrade " +
                        romNum(i), lore);
            } else {
                lore.add(" ");
                lore.add(ChatColor.YELLOW + "Price: " + (bal >= price ? ChatColor.GREEN : ChatColor.RED) + pr);
                lore.add(" ");
                stack = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 14, (i == abl ? ChatColor.GOLD :
                        ChatColor.RED) + "Ability Upgrade " + romNum(i), lore);
            }
            inv.setItem(i + 9, stack);
        }
        List<ItemStack> items = kit.getItems(data.get(kit, 2));
        int itm = data.get(kit, 2) + 1;
        for (int i = 1; i < 8; i++) {
            List<String> lore = loreFromItems(kit.getItems(i));
            int price = getPriceForTier(i);
            String pr = "$" + price;
            ItemStack stack;
            if (i < itm) {
                lore.add(" ");
                lore.add(ChatColor.DARK_GRAY + "Price: " + ChatColor.STRIKETHROUGH + pr);
                lore.add(" ");
                stack = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 5, ChatColor.GREEN + "Kit Upgrade " +
                        romNum(i), lore);
            } else {
                lore.add(" ");
                lore.add(ChatColor.YELLOW + "Price: " + (bal >= price ? ChatColor.GREEN : ChatColor.RED) + pr);
                lore.add(" ");
                stack = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 14, (i == itm ? ChatColor.GOLD :
                        ChatColor.RED) + "Kit Upgrade " + romNum(i), lore);
            }
            inv.setItem(i + 18, stack);
        }
        int min = data.get(kit, 3) + 1;
        for (int i = 1; i < 8; i++) {
            List<String> lore = miningLoreFromKit(kit, i);
            int price = getPriceForTier(i);
            String pr = "$" + price;
            ItemStack stack;
            if (i < min) {
                lore.add(" ");
                lore.add(ChatColor.DARK_GRAY + "Price: " + ChatColor.STRIKETHROUGH + pr);
                lore.add(" ");
                stack = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 5, ChatColor.GREEN + "Mining Upgrade " +
                        romNum(i), lore);
            } else {
                lore.add(" ");
                lore.add(ChatColor.YELLOW + "Price: " + (bal >= price ? ChatColor.GREEN : ChatColor.RED) + pr);
                lore.add(" ");
                stack = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 14, (i == min ? ChatColor.GOLD :
                        ChatColor.RED) + "Mining Upgrade " + romNum(i), lore);
            }
            inv.setItem(i + 27, stack);
        }
        inv.setItem(49, InventoryUtil.back);
        player.openInventory(inv);
    }

    private List<String> miningLoreFromKit(CTFKit kit, int i) {
        List<String> lore = new ArrayList<>();
        switch (kit) {
            case HADES:
                switch (i) {
                    case 1:
                        lore.add(ChatColor.GRAY + "7.14% chance for mined ores");
                        lore.add(ChatColor.GRAY + "to instantly smelt");
                        break;
                    case 2:
                        lore.add(ChatColor.GRAY + "14.29% chance for mined ores");
                        lore.add(ChatColor.GRAY + "to instantly smelt");
                        break;
                    case 3:
                        lore.add(ChatColor.GRAY + "21.43% chance for mined ores");
                        lore.add(ChatColor.GRAY + "to instantly smelt");
                        break;
                    case 4:
                        lore.add(ChatColor.GRAY + "28.57% chance for mined ores");
                        lore.add(ChatColor.GRAY + "to instantly smelt");
                        break;
                    case 5:
                        lore.add(ChatColor.GRAY + "35.71% chance for mined ores");
                        lore.add(ChatColor.GRAY + "to instantly smelt");
                        break;
                    case 6:
                        lore.add(ChatColor.GRAY + "42.86% chance for mined ores");
                        lore.add(ChatColor.GRAY + "to instantly smelt");
                        break;
                    case 7:
                        lore.add(ChatColor.GRAY + "50% chance for mined ores");
                        lore.add(ChatColor.GRAY + "to instantly smelt");
                        break;
                }
                break;
            case BAYMAX:
                switch (i) {
                    case 1:
                        lore.add(ChatColor.GRAY + "1.5% chance to get an iron block");
                        lore.add(ChatColor.GRAY + "when you mine iron ore");
                        break;
                    case 2:
                        lore.add(ChatColor.GRAY + "2.5% chance to get an iron block");
                        lore.add(ChatColor.GRAY + "when you mine iron ore");
                        break;
                    case 3:
                        lore.add(ChatColor.GRAY + "4% chance to get an iron block");
                        lore.add(ChatColor.GRAY + "when you mine iron ore");
                        break;
                    case 4:
                        lore.add(ChatColor.GRAY + "5% chance to get an iron block");
                        lore.add(ChatColor.GRAY + "when you mine iron ore");
                        break;
                    case 5:
                        lore.add(ChatColor.GRAY + "6.5% chance to get an iron block");
                        lore.add(ChatColor.GRAY + "when you mine iron ore");
                        break;
                    case 6:
                        lore.add(ChatColor.GRAY + "18% chance to get an iron block");
                        lore.add(ChatColor.GRAY + "when you mine iron ore");
                        break;
                    case 7:
                        lore.add(ChatColor.GRAY + "10% chance to get an iron block");
                        lore.add(ChatColor.GRAY + "when you mine iron ore");
                        break;
                }
                break;
            case HERCULES:
                switch (i) {
                    case 1:
                        lore.add(ChatColor.GRAY + "7.14% chance to break block");
                        lore.add(ChatColor.GRAY + "next to mined block");
                        break;
                    case 2:
                        lore.add(ChatColor.GRAY + "14.29% chance to break block");
                        lore.add(ChatColor.GRAY + "next to mined block");
                        break;
                    case 3:
                        lore.add(ChatColor.GRAY + "21.43% chance to break block");
                        lore.add(ChatColor.GRAY + "next to mined block");
                        break;
                    case 4:
                        lore.add(ChatColor.GRAY + "28.57% chance to break block");
                        lore.add(ChatColor.GRAY + "next to mined block");
                        break;
                    case 5:
                        lore.add(ChatColor.GRAY + "35.71% chance to break block");
                        lore.add(ChatColor.GRAY + "next to mined block");
                        break;
                    case 6:
                        lore.add(ChatColor.GRAY + "42.86% chance to break block");
                        lore.add(ChatColor.GRAY + "next to mined block");
                        break;
                    case 7:
                        lore.add(ChatColor.GRAY + "50% chance to break block");
                        lore.add(ChatColor.GRAY + "next to mined block");
                        break;
                }
                break;
            case MERIDA:
                switch (i) {
                    case 1:
                        lore.add(ChatColor.GRAY + "5% chance to get an extra arrow");
                        lore.add(ChatColor.GRAY + "when mining ores");
                        break;
                    case 2:
                        lore.add(ChatColor.GRAY + "15% chance to get an extra arrow");
                        lore.add(ChatColor.GRAY + "when mining ores");
                        break;
                    case 3:
                        lore.add(ChatColor.GRAY + "20% chance to get an extra arrow");
                        lore.add(ChatColor.GRAY + "when mining ores");
                        break;
                    case 4:
                        lore.add(ChatColor.GRAY + "30% chance to get an extra arrow");
                        lore.add(ChatColor.GRAY + "when mining ores");
                        break;
                    case 5:
                        lore.add(ChatColor.GRAY + "35% chance to get an extra arrow");
                        lore.add(ChatColor.GRAY + "when mining ores");
                        break;
                    case 6:
                        lore.add(ChatColor.GRAY + "40% chance to get an extra arrow");
                        lore.add(ChatColor.GRAY + "when mining ores");
                        break;
                    case 7:
                        lore.add(ChatColor.GRAY + "50% chance to get an extra arrow");
                        lore.add(ChatColor.GRAY + "when mining ores");
                        break;
                }
                break;
            case BOLT:
                switch (i) {
                    case 1:
                        lore.add(ChatColor.GRAY + "10% chance for extra drops");
                        lore.add(ChatColor.GRAY + "when mining ores");
                        break;
                    case 2:
                        lore.add(ChatColor.GRAY + "20% chance for extra drops");
                        lore.add(ChatColor.GRAY + "when mining ores");
                        break;
                    case 3:
                        lore.add(ChatColor.GRAY + "30% chance for extra drops");
                        lore.add(ChatColor.GRAY + "when mining ores");
                        break;
                    case 4:
                        lore.add(ChatColor.GRAY + "40% chance for extra drops");
                        lore.add(ChatColor.GRAY + "when mining ores");
                        break;
                    case 5:
                        lore.add(ChatColor.GRAY + "50% chance for extra drops");
                        lore.add(ChatColor.GRAY + "when mining ores");
                        break;
                    case 6:
                        lore.add(ChatColor.GRAY + "60% chance for extra drops");
                        lore.add(ChatColor.GRAY + "when mining ores");
                        break;
                    case 7:
                        lore.add(ChatColor.GRAY + "75% chance for extra drops");
                        lore.add(ChatColor.GRAY + "when mining ores");
                        break;
                }
                break;
        }
        return lore;
    }

    private List<String> abilLoreFromKit(CTFKit kit, int level) {
        List<String> lore = new ArrayList<>();
        switch (kit) {
            case HADES:
                switch (level) {
                    case 1:
                        lore.add(ChatColor.GRAY + "Flame Burst deals 5 damage");
                        break;
                    case 2:
                        lore.add(ChatColor.GRAY + "Flame Burst deals 2.86% more damage");
                        break;
                    case 3:
                        lore.add(ChatColor.GRAY + "Flame Burst deals 4.29% more damage");
                        break;
                    case 4:
                        lore.add(ChatColor.GRAY + "Flame Burst deals 5.71% more damage");
                        break;
                    case 5:
                        lore.add(ChatColor.GRAY + "Flame Burst deals 7.14% more damage");
                        break;
                    case 6:
                        lore.add(ChatColor.GRAY + "Flame Burst deals 8.57% more damage");
                        break;
                    case 7:
                        lore.add(ChatColor.GRAY + "Flame Burst deals 10% more damage");
                        break;
                }
                break;
            case BAYMAX:
                switch (level) {
                    case 1:
                        lore.add(ChatColor.GRAY + "Heal Teammates in a 4-block");
                        lore.add(ChatColor.GRAY + "radius for 5 health");
                        break;
                    case 2:
                        lore.add(ChatColor.GRAY + "Heal Teammates in a 5-block");
                        lore.add(ChatColor.GRAY + "radius for 5 health");
                        break;
                    case 3:
                        lore.add(ChatColor.GRAY + "Heal Teammates in a 5-block");
                        lore.add(ChatColor.GRAY + "radius for 6 health");
                        break;
                    case 4:
                        lore.add(ChatColor.GRAY + "Heal Teammates in a 6-block");
                        lore.add(ChatColor.GRAY + "radius for 6 health");
                        break;
                    case 5:
                        lore.add(ChatColor.GRAY + "Heal Teammates in a 6-block");
                        lore.add(ChatColor.GRAY + "radius for 7 health");
                        break;
                    case 6:
                        lore.add(ChatColor.GRAY + "Heal Teammates in a 7-block");
                        lore.add(ChatColor.GRAY + "radius for 7 health");
                        break;
                    case 7:
                        lore.add(ChatColor.GRAY + "Heal Teammates in a 7-block");
                        lore.add(ChatColor.GRAY + "radius for 8 health");
                        break;
                }
                break;
            case HERCULES:
                switch (level) {
                    case 1:
                        lore.add(ChatColor.GRAY + "Damage multiplied 1.25x");
                        lore.add(ChatColor.GRAY + "for 3 seconds");
                        break;
                    case 2:
                        lore.add(ChatColor.GRAY + "Damage multiplied 1.5x");
                        lore.add(ChatColor.GRAY + "for 3 seconds");
                        break;
                    case 3:
                        lore.add(ChatColor.GRAY + "Damage multiplied 1.75x");
                        lore.add(ChatColor.GRAY + "for 3.5 seconds");
                        break;
                    case 4:
                        lore.add(ChatColor.GRAY + "Damage multiplied 2.0x");
                        lore.add(ChatColor.GRAY + "for 4 seconds");
                        break;
                    case 5:
                        lore.add(ChatColor.GRAY + "Damage multiplied 2.25x");
                        lore.add(ChatColor.GRAY + "for 4 seconds");
                        break;
                    case 6:
                        lore.add(ChatColor.GRAY + "Damage multiplied 2.5x");
                        lore.add(ChatColor.GRAY + "for 4.5 seconds");
                        break;
                    case 7:
                        lore.add(ChatColor.GRAY + "Damage multiplied 3x");
                        lore.add(ChatColor.GRAY + "for 5 seconds");
                        break;
                }
                break;
            case MERIDA:
                switch (level) {
                    case 1:
                        lore.add(ChatColor.GRAY + "Explosive arrow with a radius of ");
                        lore.add(ChatColor.GRAY + "2 and damage value of 3");
                        break;
                    case 2:
                        lore.add(ChatColor.GRAY + "Explosive arrow with a radius of ");
                        lore.add(ChatColor.GRAY + "2 and damage value of 4");
                        break;
                    case 3:
                        lore.add(ChatColor.GRAY + "Explosive arrow with a radius of ");
                        lore.add(ChatColor.GRAY + "3 and damage value of 4");
                        break;
                    case 4:
                        lore.add(ChatColor.GRAY + "Explosive arrow with a radius of ");
                        lore.add(ChatColor.GRAY + "3 and damage value of 6");
                        break;
                    case 5:
                        lore.add(ChatColor.GRAY + "Explosive arrow with a radius of ");
                        lore.add(ChatColor.GRAY + "4 and damage value of 6");
                        break;
                    case 6:
                        lore.add(ChatColor.GRAY + "Explosive arrow with a radius of ");
                        lore.add(ChatColor.GRAY + "4 and damage value of 8");
                        break;
                    case 7:
                        lore.add(ChatColor.GRAY + "Explosive arrow with a radius of ");
                        lore.add(ChatColor.GRAY + "5 and damage value of 10");
                        break;
                }
                break;
            case BOLT:
                switch (level) {
                    case 1:
                        lore.add(ChatColor.GRAY + "Deal 2 damage to players");
                        lore.add(ChatColor.GRAY + "in path of bark");
                        break;
                    case 2:
                        lore.add(ChatColor.GRAY + "Deal 3.85 damage to players");
                        lore.add(ChatColor.GRAY + "in path of bark");
                        break;
                    case 3:
                        lore.add(ChatColor.GRAY + "Deal 5.7 damage to players");
                        lore.add(ChatColor.GRAY + "in path of bark");
                        break;
                    case 4:
                        lore.add(ChatColor.GRAY + "Deal 7.55 damage to players");
                        lore.add(ChatColor.GRAY + "in path of bark");
                        break;
                    case 5:
                        lore.add(ChatColor.GRAY + "Deal 9.4 damage to players");
                        lore.add(ChatColor.GRAY + "in path of bark");
                        break;
                    case 6:
                        lore.add(ChatColor.GRAY + "Deal 12.18 damage to players");
                        lore.add(ChatColor.GRAY + "in path of bark");
                        break;
                    case 7:
                        lore.add(ChatColor.GRAY + "Deal 15 damage to players");
                        lore.add(ChatColor.GRAY + "in path of bark");
                        break;
                }
                break;
        }
        return lore;
    }

    private List<String> loreFromItems(List<ItemStack> items) {
        List<String> lore = new ArrayList<>();
        for (ItemStack item : items) {
            if (item.getType().equals(Material.POTION)) {
                lore.add(ChatColor.GRAY + (item.getAmount() > 1 ? item.getAmount() + " " : "") +
                        "Health Potion (3" + ChatColor.RED + "❤" + ChatColor.GRAY + ")");
                continue;
            }
            String s = ChatColor.GRAY + (item.getAmount() > 1 ? item.getAmount() + " " : "") +
                    format(item.getType().name());
            if (!item.getEnchantments().isEmpty()) {
                s += " " + ench(item);
            }
            lore.add(s);
        }
        return lore;
    }

    private String ench(ItemStack item) {
        String s = "";
        int i = 0;
        int size = item.getEnchantments().size();
        for (Map.Entry<Enchantment, Integer> e : item.getEnchantments().entrySet()) {
            s += getName(e.getKey()) + " " + romNum(e.getValue());
            if (i < (size - 1)) {
                s += " & ";
            }
            i++;
        }
        return s;
    }

    private String getName(Enchantment key) {
        String s = "";
        if (key.equals(Enchantment.ARROW_DAMAGE)) {
            s += "Power";
        } else if (key.equals(Enchantment.ARROW_FIRE)) {
            s += "Flame";
        } else if (key.equals(Enchantment.ARROW_KNOCKBACK)) {
            s += "Punch";
        } else if (key.equals(Enchantment.DAMAGE_ALL)) {
            s += "Sharpness";
        } else if (key.equals(Enchantment.PROTECTION_ENVIRONMENTAL)) {
            s += "Protection";
        } else if (key.equals(Enchantment.PROTECTION_FALL)) {
            s += "Feather Falling";
        } else if (key.equals(Enchantment.PROTECTION_PROJECTILE)) {
            s += "Projectile Protection";
        } else if (key.equals(Enchantment.DIG_SPEED)) {
            s += "Efficiency";
        }
        return s;
    }

    private String format(String name) {
        String s = "";
        boolean first = true;
        for (char c : name.toCharArray()) {
            if (first) {
                s += Character.toUpperCase(c);
                first = false;
            } else {
                if (c == '_') {
                    s += " ";
                    first = true;
                } else {
                    s += Character.toLowerCase(c);
                }
            }
        }
        return s;
    }

    private String romNum(int i) {
        switch (i) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            default:
                return "";
        }
    }

    private int romToNum(String numeral) {
        switch (numeral.toUpperCase()) {
            case "I":
                return 1;
            case "II":
                return 2;
            case "III":
                return 3;
            case "IV":
                return 4;
            case "V":
                return 5;
            case "VI":
                return 6;
            case "VII":
                return 7;
            default:
                return 0;
        }
    }

    private int getPriceForTier(int tier) {
        switch (tier) {
            case 1:
                return 0;
            case 2:
                return 200;
            case 3:
                return 800;
            case 4:
                return 1400;
            case 5:
                return 2000;
            case 6:
                return 2600;
            case 7:
                return 3500;
        }
        return 100000;
    }

    public void clickInventory(InventoryClickEvent event, InventoryType type, Player player, ItemStack current) {
        ItemMeta meta = current.getItemMeta();
        if (meta == null || meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName().toLowerCase());
        boolean right = event.isRightClick();
        final PlayerData data = Arcade.getInstance().getPlayerData(player.getUniqueId());
        int place = event.getSlot();
        if (!type.equals(InventoryType.SHOP_CTF)) {
            if (meta.getDisplayName().startsWith(ChatColor.GREEN.toString())) {
                return;
            }
        }
        switch (type) {
            case SHOP_CTF:
                switch (name) {
                    case "hades kit":
                        if (right) {
                            selectKit(CTFKit.HADES, player, data.getCTFData());
                            return;
                        }
                        Arcade.inventoryUtil.openInventory(InventoryType.SHOP_CTF_HADES, player);
                        break;
                    case "baymax kit":
                        if (right) {
                            selectKit(CTFKit.BAYMAX, player, data.getCTFData());
                            return;
                        }
                        Arcade.inventoryUtil.openInventory(InventoryType.SHOP_CTF_BAYMAX, player);
                        break;
                    case "hercules kit":
                        if (right) {
                            selectKit(CTFKit.HERCULES, player, data.getCTFData());
                            return;
                        }
                        Arcade.inventoryUtil.openInventory(InventoryType.SHOP_CTF_HERCULES, player);
                        break;
                    case "merida kit":
                        if (right) {
                            selectKit(CTFKit.MERIDA, player, data.getCTFData());
                            return;
                        }
                        Arcade.inventoryUtil.openInventory(InventoryType.SHOP_CTF_MERIDA, player);
                        break;
                    case "bolt kit":
                        if (right) {
                            selectKit(CTFKit.BOLT, player, data.getCTFData());
                            return;
                        }
                        Arcade.inventoryUtil.openInventory(InventoryType.SHOP_CTF_BOLT, player);
                        break;
                }
                break;
            case SHOP_CTF_HADES:
                if (place > 9 && place < 17) {
                    abilityPurchase(player, data, name, CTFKit.HADES);
                } else if (place > 18 && place < 26) {
                    inventoryPurchase(player, data, name, CTFKit.HADES);
                } else if (place > 27 && place < 35) {
                    miningPurchase(player, data, name, CTFKit.HADES);
                }
                break;
            case SHOP_CTF_BAYMAX:
                if (place > 9 && place < 17) {
                    abilityPurchase(player, data, name, CTFKit.BAYMAX);
                } else if (place > 18 && place < 26) {
                    inventoryPurchase(player, data, name, CTFKit.BAYMAX);
                } else if (place > 27 && place < 35) {
                    miningPurchase(player, data, name, CTFKit.BAYMAX);
                }
                break;
            case SHOP_CTF_HERCULES:
                if (place > 9 && place < 17) {
                    abilityPurchase(player, data, name, CTFKit.HERCULES);
                } else if (place > 18 && place < 26) {
                    inventoryPurchase(player, data, name, CTFKit.HERCULES);
                } else if (place > 27 && place < 35) {
                    miningPurchase(player, data, name, CTFKit.HERCULES);
                }
                break;
            case SHOP_CTF_MERIDA:
                if (place > 9 && place < 17) {
                    abilityPurchase(player, data, name, CTFKit.MERIDA);
                } else if (place > 18 && place < 26) {
                    inventoryPurchase(player, data, name, CTFKit.MERIDA);
                } else if (place > 27 && place < 35) {
                    miningPurchase(player, data, name, CTFKit.MERIDA);
                }
                break;
            case SHOP_CTF_BOLT:
                if (place > 9 && place < 17) {
                    abilityPurchase(player, data, name, CTFKit.BOLT);
                } else if (place > 18 && place < 26) {
                    inventoryPurchase(player, data, name, CTFKit.BOLT);
                } else if (place > 27 && place < 35) {
                    miningPurchase(player, data, name, CTFKit.BOLT);
                }
                break;
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

    private void miningPurchase(Player player, PlayerData data, String name, CTFKit kit) {
        String numeral = name.split(" ")[name.split(" ").length - 1];
        int num = romToNum(numeral);
        if (num < data.getCTFData().get(kit, 3)) {
            return;
        }
        if (num > (data.getCTFData().get(kit, 3) + 1)) {
            player.sendMessage(ChatColor.RED + "You must purchase the previous Level, first!");
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 10f, 0f);
            return;
        }
        int price = getPriceForTier(num);
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
        MCMagicCore.economy.addBalance(player.getUniqueId(), -price);
        purchaseDelay.put(player.getUniqueId(), System.currentTimeMillis());
        managerPurchaseParticle(player);
        data.getCTFData().set(kit, 3, num);
        data.getCTFData().setKit(kit.name().toLowerCase());
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql =
                    connection.prepareStatement("UPDATE ctf_data SET " + kit.name().toLowerCase() +
                            "3=?,kit=? WHERE uuid=?");
            sql.setInt(1, num);
            sql.setString(2, kit.name().toLowerCase());
            sql.setString(3, player.getUniqueId().toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5f, 2f);
    }

    private void abilityPurchase(Player player, PlayerData data, String name, CTFKit kit) {
        String numeral = name.split(" ")[name.split(" ").length - 1];
        int num = romToNum(numeral);
        if (num < data.getCTFData().get(kit, 1)) {
            return;
        }
        if (num > (data.getCTFData().get(kit, 1) + 1)) {
            player.sendMessage(ChatColor.RED + "You must purchase the previous Level, first!");
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 10f, 0f);
            return;
        }
        int price = getPriceForTier(num);
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
        MCMagicCore.economy.addBalance(player.getUniqueId(), -price);
        purchaseDelay.put(player.getUniqueId(), System.currentTimeMillis());
        managerPurchaseParticle(player);
        data.getCTFData().set(kit, 1, num);
        data.getCTFData().setKit(kit.name().toLowerCase());
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql =
                    connection.prepareStatement("UPDATE ctf_data SET " + kit.name().toLowerCase() +
                            "1=?,kit=? WHERE uuid=?");
            sql.setInt(1, num);
            sql.setString(2, kit.name().toLowerCase());
            sql.setString(3, player.getUniqueId().toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5f, 2f);
    }

    private void inventoryPurchase(Player player, PlayerData data, String name, CTFKit kit) {
        String numeral = name.split(" ")[name.split(" ").length - 1];
        int num = romToNum(numeral);
        if (num < data.getCTFData().get(kit, 2)) {
            return;
        }
        if (num > (data.getCTFData().get(kit, 2) + 1)) {
            player.sendMessage(ChatColor.RED + "You must purchase the previous Level, first!");
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 10f, 0f);
            return;
        }
        int price = getPriceForTier(num);
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
        MCMagicCore.economy.addBalance(player.getUniqueId(), -price);
        purchaseDelay.put(player.getUniqueId(), System.currentTimeMillis());
        managerPurchaseParticle(player);
        data.getCTFData().set(kit, 2, num);
        data.getCTFData().setKit(kit.name().toLowerCase());
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql =
                    connection.prepareStatement("UPDATE ctf_data SET " + kit.name().toLowerCase() +
                            "2=?,kit=? WHERE uuid=?");
            sql.setInt(1, num);
            sql.setString(2, kit.name().toLowerCase());
            sql.setString(3, player.getUniqueId().toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5f, 2f);
    }

    private void selectKit(final CTFKit kit, final Player player, CTFData data) {
        if (data.getKit().equalsIgnoreCase(kit.name())) {
            player.sendMessage(ChatColor.RED + "You already selected this Kit!");
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 5f, 0f);
            return;
        }
        FormattedMessage msg = new FormattedMessage("You selected the ").color(ChatColor.GREEN).then(kit.getName())
                .itemTooltip(new ItemCreator(Material.STONE, kit.getName(),
                        loreFromItems(kit.getItems(data.get(kit, 2))))).then(" Kit!").color(ChatColor.GREEN)
                .then(" (Hover for stats)").color(ChatColor.GRAY);
        msg.send(player);
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5f, 2f);
        player.closeInventory();
        data.setKit(kit.name().toLowerCase());
        Bukkit.getScheduler().runTaskAsynchronously(Arcade.getInstance(), new Runnable() {
            @Override
            public void run() {
                try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
                    PreparedStatement sql = connection.prepareStatement("UPDATE ctf_data SET kit=? WHERE uuid=?");
                    sql.setString(1, kit.name().toLowerCase());
                    sql.setString(2, player.getUniqueId().toString());
                    sql.execute();
                    sql.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}