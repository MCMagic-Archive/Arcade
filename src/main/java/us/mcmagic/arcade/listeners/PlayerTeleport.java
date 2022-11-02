package us.mcmagic.arcade.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import us.mcmagic.arcade.Arcade;

/**
 * Created by Marc on 9/30/15
 */
public class PlayerTeleport implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Arcade.parkourManager.cancelParkour(player, "you cannot teleport during a Parkour Race");
    }
}