package us.mcmagic.arcade.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.arcade.Arcade;
import us.mcmagic.arcade.handlers.InventoryType;
import us.mcmagic.arcade.handlers.PlayerData;
import us.mcmagic.arcade.resource.PackManager;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.permissions.Rank;

import java.util.*;

/**
 * Created by Marc on 2/18/15
 */
public class InventoryUtil {
    //Inventory
    public final ItemStack dust = new ItemCreator(Material.GLOWSTONE_DUST, ChatColor.GOLD + "Navigation",
            Arrays.asList(ChatColor.GRAY + "Right-Click to open up the", ChatColor.GRAY +
                    "Navigation Menu and select", ChatColor.GRAY + "one of our Arcade Games to play!"));
    public final ItemStack rp = new ItemCreator(Material.NETHER_STAR, ChatColor.GREEN + "Resource Packs",
            Arrays.asList(ChatColor.GRAY + "Have our Auto-Installing Resource", ChatColor.GRAY +
                            "Pack setup but want to use", ChatColor.GRAY + "a different one for the Arcade?",
                    ChatColor.GRAY + "Select one here!"));
    public final ItemStack visible = new ItemCreator(Material.GOLD_INGOT, ChatColor.AQUA + "Player Visibility " +
            ChatColor.GOLD + "➠" + ChatColor.GREEN + " Enabled", new ArrayList<String>());
    public final ItemStack hidden = new ItemCreator(Material.IRON_INGOT, ChatColor.AQUA + "Player Visibility " +
            ChatColor.GOLD + "➠" + ChatColor.RED + " Disabled", new ArrayList<String>());
    public static final ItemStack back = new ItemCreator(Material.ARROW, ChatColor.GREEN + "Back");
    //Nav Menu
    public final ItemStack hub = new ItemCreator(Material.BOOKSHELF, ChatColor.GREEN + "Hub", new ArrayList<String>());
    public final ItemStack pixie = new ItemCreator(Material.BLAZE_ROD, 1, (byte) 0, ChatColor.GOLD +
            "Pixie Dust Shootout", Arrays.asList(ChatColor.GREEN + "12 players fire at the opposing team",
            ChatColor.GREEN + "with " + ChatColor.YELLOW + "Pixie Dust " + ChatColor.GREEN + "from Tinker Bell! The",
            ChatColor.GREEN + "team with the most hits first wins!"));
    public final ItemStack parkour = new ItemCreator(Material.LADDER, ChatColor.GREEN + "Parkour", Arrays.asList(""));
    public final ItemStack ctf = new ItemCreator(Material.DIAMOND_SWORD, ChatColor.GREEN + "Capture The Flag",
            Arrays.asList(ChatColor.DARK_GREEN + "4 teams of 10 players battle against", ChatColor.DARK_GREEN +
                            "each other to protect their flags!", ChatColor.DARK_GREEN + "Can you Capture all of the",
                    ChatColor.DARK_GREEN + "Flags before another team does?"));
    //Stores
    public final ItemStack noHats = new ItemCreator(Material.REDSTONE_BLOCK,
            ChatColor.RED + "There are currently no Hats for sale!", Collections.singletonList(""));
    public final ItemStack noTamb = new ItemCreator(Material.REDSTONE_BLOCK,
            ChatColor.RED + "ThingAMaBobs are being reworked right now!", Collections.singletonList(""));
    //Particles
    public final ItemStack note = new ItemCreator(Material.NOTE_BLOCK, ChatColor.GREEN + "Notes",
            new ArrayList<String>());
    public final ItemStack spark = new ItemCreator(Material.FIREWORK, ChatColor.GREEN + "Firework Spark",
            new ArrayList<String>());
    public final ItemStack mickey = new ItemCreator(Material.APPLE, ChatColor.GREEN + "Mickey Head",
            new ArrayList<String>());
    public final ItemStack enchant = new ItemCreator(Material.ENCHANTMENT_TABLE, ChatColor.GREEN + "Enchantment",
            new ArrayList<String>());
    public final ItemStack flame = new ItemCreator(Material.FLINT_AND_STEEL, ChatColor.GREEN + "Flame",
            new ArrayList<String>());
    public final ItemStack heart = new ItemCreator(Material.DIAMOND, ChatColor.GREEN + "Hearts",
            new ArrayList<String>());
    public final ItemStack portal = new ItemCreator(Material.BLAZE_POWDER, ChatColor.GREEN + "Portal",
            new ArrayList<String>());
    public final ItemStack lava = new ItemCreator(Material.LAVA_BUCKET, ChatColor.GREEN + "Lava",
            new ArrayList<String>());
    public final ItemStack witch = new ItemCreator(Material.POTION, 1, (byte) 8196, ChatColor.GREEN + "Witch Magic",
            new ArrayList<String>());
    public final ItemStack none = new ItemCreator(Material.GLASS, ChatColor.RED + "Clear Particle",
            new ArrayList<String>());
    private HashMap<UUID, Long> purchaseDelay = new HashMap<>();

    public void setup(Player player) {
        Rank rank = MCMagicCore.getUser(player.getUniqueId()).getRank();
        PlayerInventory inv = player.getInventory();
        inv.clear();
        if (rank.getRankId() >= Rank.CASTMEMBER.getRankId()) {
            inv.setItem(0, new ItemStack(Material.COMPASS));
            inv.setItem(1, dust);
            inv.setItem(7, rp);
            inv.setItem(8, visible);
            return;
        }
        inv.setItem(0, dust);
        inv.setItem(7, rp);
        inv.setItem(8, visible);
    }

    public void openInventory(InventoryType type, Player player) {
        PlayerData data = Arcade.getInstance().getPlayerData(player.getUniqueId());
        String n = type.name().toLowerCase();
        if (n.contains("ctf")) {
            Arcade.ctfShopManager.openInventory(type, player, data);
            return;
        }
        if (n.contains("pixie")) {
            Arcade.pixieShopManager.openInventory(type, player, data);
            return;
        }
        switch (type) {
            case MAIN:
                Inventory main = Bukkit.createInventory(player, 27, type.getTitle());
                main.setItem(4, hub);
                main.setItem(11, ctf);
                main.setItem(13, parkour);
                main.setItem(15, pixie);
                player.openInventory(main);
                break;
            case RESOURCE:
                PackManager.openMenu(player);
                break;
            case PARTICLE:
                Inventory pt = Bukkit.createInventory(player, 27, type.getTitle());
                pt.setItem(9, note);
                pt.setItem(10, spark);
                pt.setItem(11, flame);
                pt.setItem(12, enchant);
                pt.setItem(13, mickey);
                pt.setItem(14, heart);
                pt.setItem(15, portal);
                pt.setItem(16, lava);
                pt.setItem(17, witch);
                pt.setItem(22, none);
                player.openInventory(pt);
                break;
        }
    }

    public void clickInventory(InventoryClickEvent event, InventoryType type, final Player player, ItemStack current) {
        ItemMeta meta = current.getItemMeta();
        if (meta == null || meta.getDisplayName() == null) {
            return;
        }
        if (type.name().toLowerCase().contains("pixie")) {
            Arcade.pixieShopManager.clickInventory(event, type, player, current);
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName().toLowerCase());
        boolean right = event.isRightClick();
        final PlayerData data = Arcade.getInstance().getPlayerData(player.getUniqueId());
        int place = event.getSlot();
        if (current.getType().equals(Material.ARROW)) {
            String[] list = type.name().split("_");
            String n = "";
            boolean first = true;
            for (int i = 0; i < 2; i++) {
                n += list[i];
                if (first) {
                    n += "_";
                    first = !first;
                }
            }
            openInventory(InventoryType.fromString(n), player);
            return;
        }
        if (type.name().toLowerCase().contains("ctf")) {
            Arcade.ctfShopManager.clickInventory(event, type, player, current);
            return;
        }
        switch (type) {
            case MAIN:
                switch (name) {
                    case "hub":
                        player.performCommand("hub");
                        player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 100f, 1f);
                        return;
                    case "parkour":
                        player.performCommand("hub parkour");
                        player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 100f, 1f);
                        return;
                    case "tron":
                        player.performCommand("hub tron");
                        player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 100f, 1f);
                        return;
                    case "pixie dust shootout":
                        player.performCommand("hub pixie");
                        player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 100f, 1f);
                        return;
                    case "capture the flag":
                        player.performCommand("hub ctf");
                        player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 100f, 1f);
                        return;
                }
                break;
            case RESOURCE:
                PackManager.handle(event, player);
                break;
            case PARTICLE:
                break;
        }
    }
}