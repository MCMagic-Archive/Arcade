package us.mcmagic.arcade.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import us.mcmagic.mcmagiccore.arcade.GameType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marc on 2/14/15
 */
public class PlayerPortal implements Listener {
    private HashMap<String, Location> portals = new HashMap<>();

    public PlayerPortal() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/Arcade/config.yml"));
        for (GameType type : GameType.values()) {
            String id = type.getName();
            portals.put(id, new Location(Bukkit.getWorlds().get(0), config.getDouble("portal." + id + ".x"),
                    config.getDouble("portal." + id + ".y"), config.getDouble("portal." + id + ".z")));
        }
        try {
            portals.put("parkour", new Location(Bukkit.getWorlds().get(0), config.getDouble("portal.parkour.x"),
                    config.getDouble("portal.parkour.y"), config.getDouble("portal.parkour.z")));
        } catch (NullPointerException ignored) {
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() != to.getBlockX() && from.getBlockZ() != to.getBlockZ()) {
            return;
        }
        if (event.getTo().getBlock().getType().equals(Material.PORTAL)) {
            for (Map.Entry<String, Location> entry : portals.entrySet()) {
                if (entry.getValue().distance(from) <= 10) {
                    player.performCommand("hub " + entry.getKey());
                    return;
                }
            }
        }
    }
}
