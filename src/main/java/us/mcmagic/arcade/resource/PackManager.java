package us.mcmagic.arcade.resource;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.arcade.Arcade;
import us.mcmagic.arcade.handlers.PlayerData;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.player.User;
import us.mcmagic.mcmagiccore.resource.CurrentPackReceivedEvent;
import us.mcmagic.mcmagiccore.resource.ResourceManager;
import us.mcmagic.mcmagiccore.resource.ResourcePack;
import us.mcmagic.mcmagiccore.resource.ResourceStatusEvent;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Marc on 6/7/15
 */
public class PackManager implements Listener {
    private static HashMap<String, ItemStack> packItems = new HashMap<>();

    public PackManager() {
        initialize();
    }

    @SuppressWarnings("deprecation")
    public static void initialize() {
        packItems.clear();
        File file = new File("plugins/Arcade/packs.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> list = config.getStringList("pack-list");
        for (String s : list) {
            String name = config.getString("packs." + s + ".name");
            List<String> loreList = config.getStringList("packs." + s + ".lore");
            List<String> lore = new ArrayList<>();
            for (String st : loreList) {
                lore.add(ChatColor.translateAlternateColorCodes('&', st));
            }
            packItems.put(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', name)),
                    new ItemCreator(Material.getMaterial(config.getInt("packs." + s + ".id")),
                            ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', config.getString("packs." +
                                    s + ".name")), lore));
        }
    }

    public static HashMap<String, ItemStack> getPackItems() {
        return new HashMap<>(packItems);
    }

    @EventHandler
    public void onCurrentPackReceive(CurrentPackReceivedEvent event) {
        User user = event.getUser();
        Player player = Bukkit.getPlayer(user.getUniqueId());
        String current = event.getPacks();
        PlayerData data = Arcade.getPlayerData(player.getUniqueId());
        String preferred = data.getPack();
        if (current.equalsIgnoreCase("tron") && player.getLocation().distance(Arcade.lobbyUtil.getLobby("Tron")) < 10) {
            return;
        }
        if (preferred.equals("none")) {
            return;
        }
        if (preferred.equals("NoPrefer")) {
            if (!current.equals("none")) {
                MCMagicCore.resourceManager.sendPack(player, "Blank");
                user.setResourcePack("none");
                MCMagicCore.resourceManager.setCurrentPack(user, "none");
            }
            return;
        }
        if (!current.equals(preferred)) {
            MCMagicCore.resourceManager.sendPack(player, preferred);
        }
    }

    @EventHandler
    public void onResourceStatus(ResourceStatusEvent event) {
        Player player = event.getPlayer();
        switch (event.getStatus()) {
            case ACCEPTED:
                player.sendMessage(ChatColor.GREEN + "Resource Pack accepted! Downloading now...");
                break;
            case LOADED:
                player.sendMessage(ChatColor.GREEN + "Resource Pack loaded!");
                break;
            case FAILED:
                player.sendMessage(ChatColor.RED + "Download failed! Please report this to a Staff Member. (Error Code 101)");
                break;
            case DECLINED:
                for (int i = 0; i < 5; i++) {
                    player.sendMessage(" ");
                }
                player.sendMessage(ChatColor.RED + "You have declined the Resource Pack!");
                player.sendMessage(ChatColor.YELLOW + "For help with this, visit: " + ChatColor.AQUA +
                        "http://mcmagic.us/rphelp");
                break;
            default:
                player.sendMessage(ChatColor.RED + "Download failed! Please report this to a Staff Member. (Error Code 101)");
        }
    }

    public static void handle(InventoryClickEvent event, Player player) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (item.getItemMeta() == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        if (name.endsWith("(SELECTED)")) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You already have this selected!");
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
            return;
        }
        PlayerData data = Arcade.getPlayerData(player.getUniqueId());
        if (event.getSlot() == 0) {
            player.sendMessage(ChatColor.RED + "On Arcade, you will be sent a Blank Resource Pack!");
            player.closeInventory();
            if (!user.getResourcePack().equalsIgnoreCase("none")) {
                MCMagicCore.resourceManager.sendPack(player, "Blank");
            }
            data.setPack("none");
            setPack(player, "NoPrefer");
            return;
        }
        if (event.getSlot() == 8) {
            player.sendMessage(ChatColor.RED + "We will never send you a Resource Pack on the Arcade!");
            player.closeInventory();
            if (!user.getResourcePack().equalsIgnoreCase("none")) {
                MCMagicCore.resourceManager.sendPack(player, "Blank");
            }
            data.setPack("none");
            setPack(player, "none");
            return;
        }
        for (Map.Entry<String, ItemStack> entry : packItems.entrySet()) {
            ItemStack stack = entry.getValue();
            if (stack.getType().equals(item.getType())) {
                player.closeInventory();
                MCMagicCore.resourceManager.sendPack(player, entry.getKey());
                setPack(player, entry.getKey());
                return;
            }
        }
    }

    private static void setPack(final Player player, final String pack) {
        Arcade.getPlayerData(player.getUniqueId()).setPack(pack);
        Bukkit.getScheduler().runTaskAsynchronously(Arcade.getInstance(), new Runnable() {
            @Override
            public void run() {
                try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
                    PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET arcadepack=? WHERE uuid=?");
                    sql.setString(1, pack);
                    sql.setString(2, player.getUniqueId().toString());
                    sql.execute();
                    sql.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void openMenu(Player player) {
        PlayerData data = Arcade.getPlayerData(player.getUniqueId());
        List<ResourcePack> packs = getPacks(packItems.keySet());
        Inventory menu = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Resource Packs");
        int place = 13;
        String preferred = data.getPack();
        ItemStack noPrefer;
        ItemStack none;
        if (preferred.equalsIgnoreCase("noprefer")) {
            noPrefer = new ItemCreator(Material.STONE, 1, (byte) 6, ChatColor.BLUE + "Blank " + ChatColor.GREEN +
                    "(SELECTED)", Arrays.asList(ChatColor.DARK_AQUA + "We will send you a Blank Resource",
                    ChatColor.DARK_AQUA + "Pack when you join the Arcade", ChatColor.DARK_AQUA +
                            "Server, so you will not see ", ChatColor.DARK_AQUA + "any textures from MCMagic."));
        } else {
            noPrefer = new ItemCreator(Material.STONE, 1, (byte) 6, ChatColor.BLUE + "Blank", Arrays.asList(ChatColor.DARK_AQUA +
                            "We will send you a Blank Resource", ChatColor.DARK_AQUA + "Pack when you join the Arcade",
                    ChatColor.DARK_AQUA + "Server, so you will not see ", ChatColor.DARK_AQUA + "any textures from MCMagic."));
        }
        if (preferred.equalsIgnoreCase("none")) {
            none = new ItemCreator(Material.BARRIER, 1, ChatColor.RED + "None " + ChatColor.GREEN + "(SELECTED)",
                    Arrays.asList(ChatColor.DARK_AQUA + "We will never send you a", ChatColor.DARK_AQUA +
                                    "Resource Pack when you join", ChatColor.DARK_AQUA + "the Arcade unless you change",
                            ChatColor.DARK_AQUA + "this setting, we promise!"));
        } else {
            none = new ItemCreator(Material.BARRIER, 1, ChatColor.RED + "None",
                    Arrays.asList(ChatColor.DARK_AQUA + "We will never send you a", ChatColor.DARK_AQUA +
                                    "Resource Pack when you join", ChatColor.DARK_AQUA + "the Arcade if you choose",
                            ChatColor.DARK_AQUA + "this setting, we promise!"));
        }
        int amount = 1;
        //If even, increase place by 1
        if (packs.size() % 2 == 0) {
            place++;
            amount++;
        }
        for (Map.Entry<String, ItemStack> entry : packItems.entrySet()) {
            if (place > 16) {
                break;
            }
            ItemStack pack = new ItemStack(entry.getValue());
            if (entry.getKey().equalsIgnoreCase(preferred)) {
                ItemMeta meta = pack.getItemMeta();
                meta.setDisplayName(meta.getDisplayName() + ChatColor.GREEN + " (SELECTED)");
                pack.setItemMeta(meta);
            }
            menu.setItem(place, pack);
            //If even
            if (amount % 2 == 0) {
                place -= amount;
            } else {
                place += amount;
            }
            amount++;
        }
        menu.setItem(0, noPrefer);
        menu.setItem(8, none);
        player.openInventory(menu);
    }

    private static List<ResourcePack> getPacks(Set<String> plist) {
        List<ResourcePack> list = new ArrayList<>();
        ResourceManager rm = MCMagicCore.resourceManager;
        for (String s : plist) {
            ResourcePack pack = rm.getPack(s);
            list.add(pack);
        }
        return list;
    }
}