package us.mcmagic.arcade.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import us.mcmagic.arcade.Arcade;

/**
 * Created by Marc on 9/30/15
 */
public class PlayerToggleFlight implements Listener {

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        Arcade.parkourManager.cancelParkour(player, "you cannot fly during a Parkour Race");
    }
}