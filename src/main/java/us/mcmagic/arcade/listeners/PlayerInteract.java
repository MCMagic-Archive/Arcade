package us.mcmagic.arcade.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.arcade.Arcade;
import us.mcmagic.arcade.handlers.InventoryType;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Marc on 2/16/15
 */
public class PlayerInteract implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        Action action = event.getAction();
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (user == null) {
            event.setCancelled(true);
            return;
        }
        if (action.equals(Action.PHYSICAL)) {
            Material type = event.getClickedBlock().getType();
            if (type.equals(Material.GOLD_PLATE) || type.equals(Material.IRON_PLATE)) {
                Arcade.parkourManager.handleInteract(event);
                return;
            }
            if (user.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                event.setCancelled(true);
            }
            return;
        }
        ItemStack item = player.getItemInHand();
        if (item != null && item.getItemMeta() != null) {
            String name = item.getItemMeta().getDisplayName();
            if (name != null) {
                if (ChatColor.stripColor(name.toLowerCase()).startsWith("player visibility")) {
                    event.setCancelled(true);
                    Arcade.visibilityUtil.togglePlayers(player);
                    return;
                }
                switch (ChatColor.stripColor(name.toLowerCase())) {
                    case "navigation":
                        event.setCancelled(true);
                        Arcade.inventoryUtil.openInventory(InventoryType.MAIN, player);
                        break;
                    case "resource packs":
                        event.setCancelled(true);
                        Arcade.inventoryUtil.openInventory(InventoryType.RESOURCE, player);
                        break;
                }
            }
        }
        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }
        Block b = event.getClickedBlock();
        if (!Arcade.isSign(b.getType())) {
            if (user.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                event.setCancelled(true);
            }
            return;
        }
        Sign s = (Sign) b.getState();
        String line1 = s.getLine(0);
        final String line2 = s.getLine(1);
        if (line1.equals(ChatColor.WHITE + "[" + ChatColor.GREEN + "Parkour" + ChatColor.WHITE + "]")) {
            Arcade.parkourManager.teleportToParkour(player, strip(s.getLine(2)));
            return;
        }
        if (line1.equals(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Stats" + ChatColor.DARK_GRAY + "]")) {
            Bukkit.getScheduler().runTaskAsynchronously(Arcade.getInstance(), new Runnable() {
                @Override
                public void run() {
                    String game = ChatColor.stripColor(line2.toLowerCase());
                    switch (game) {
                        case "pixie dust":
                            try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
                                PreparedStatement sql = connection.prepareStatement("SELECT pixie FROM leaderboard WHERE uuid=?");
                                sql.setString(1, player.getUniqueId().toString());
                                ResultSet result = sql.executeQuery();
                                if (!result.next()) {
                                    return;
                                }
                                player.sendMessage(ChatColor.GREEN + "You have " + ChatColor.YELLOW + result.getInt("pixie")
                                        + " Hits " + ChatColor.GREEN + "in " + ChatColor.GOLD + "Pixie Dust Shootout!");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                    }
                }
            });
            return;
        }
        if (line1.equals(ChatColor.AQUA + "[" + ChatColor.BLUE + "Hub" + ChatColor.AQUA + "]")) {
            player.teleport(Arcade.lobbyUtil.getLobby("lobby"));
            return;
        }
        if (line1.equals(ChatColor.GREEN + "[JOIN]") || line1.equals(ChatColor.RED + "[RESTARTING]") ||
                line1.equals(ChatColor.RED + "[INGAME]") || line1.equals(ChatColor.DARK_GREEN + "[SPECTATE]") ||
                line1.equals(ChatColor.RED + "[NOTJOINABLE]") || line1.equals(ChatColor.RED + "[FULL]")) {
            Arcade.gameManager.handleClick(player, s);
        }
    }

    private String strip(String line) {
        return ChatColor.stripColor(line);
    }
}
