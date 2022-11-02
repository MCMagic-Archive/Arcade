package us.mcmagic.arcade.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.player.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 9/16/15
 */
public class PlayerMove implements Listener {
    private List<UUID> sent = new ArrayList<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location to = event.getTo();
        Player player = event.getPlayer();
        if (to.getBlockY() <= 0) {
            PlayerJoinAndLeave.moveToNearestLobby(player);
            return;
        }
        Location from = event.getFrom();
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        double x = to.getX();
        double z = to.getZ();
        if (z <= 1 && z >= -4 && x <= 279 && x >= 240) {
            User user = MCMagicCore.getUser(player.getUniqueId());
            if (!sent.contains(player.getUniqueId())) {
                if (user.getResourcePack().equals("unknown")) {
                    return;
                }
                if (!user.getResourcePack().equalsIgnoreCase("tron")) {
                    sent.add(player.getUniqueId());
                    MCMagicCore.resourceManager.sendPack(player, "Tron");
                }
            } else {
                if (!user.getResourcePack().equals("Tron")) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You must accept the Resource Pack to enter this area!");
                }
            }
        }
    }

    public void removeFromSent(UUID uuid) {
        sent.remove(uuid);
    }
}