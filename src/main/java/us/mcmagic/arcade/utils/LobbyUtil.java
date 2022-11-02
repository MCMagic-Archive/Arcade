package us.mcmagic.arcade.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import us.mcmagic.arcade.handlers.Lobby;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Marc on 2/13/15
 */
public class LobbyUtil {
    private HashMap<String, Lobby> lobbies = new HashMap<>();

    public LobbyUtil() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/Arcade/lobbies.yml"));
        List<String> list = config.getStringList("lobby-list");
        for (String s : list) {
            lobbies.put(s, new Lobby(config.getString(s).toLowerCase(), config.getString(s + ".name"), Bukkit.getWorlds().get(0),
                    config.getDouble(s + ".x"), config.getDouble(s + ".y"), config.getDouble(s + ".z"),
                    config.getInt(s + ".yaw"), config.getInt(s + ".pitch")));
        }
    }

    public Lobby getLobby(String name) {
        return lobbies.get(name.toLowerCase());
    }

    public void teleportToLobby(Player player, String name) throws NullPointerException {
        player.teleport(lobbies.get(name));
    }

    public void addLobby(Player player, String n, String displayName) throws IOException {
        String name = n.toLowerCase();
        Location l = player.getLocation();
        if (name.equals("lobby")) {
            if (lobbies.containsKey(name)) {
                lobbies.remove(name);
            }
            Lobby lobby = new Lobby(name, "Lobby", l.getWorld(), l.getX(), l.getY(), l.getZ(), l.getYaw(),
                    l.getPitch());
            lobbies.put(name, lobby);
            return;
        }
        if (lobbies.containsKey(name)) {
            lobbies.remove(name);
        }
        Lobby lobby = new Lobby(name, displayName, l.getWorld(), l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
        lobbies.put(name, lobby);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/Arcade/lobbies.yml"));
        List<String> list = config.getStringList("lobby-list");
        list.add(name);
        config.set("lobby-list", list);
        config.set(name + ".name", displayName);
        config.set(name + ".x", lobby.getX());
        config.set(name + ".y", lobby.getY());
        config.set(name + ".z", lobby.getZ());
        config.set(name + ".yaw", lobby.getYaw());
        config.set(name + ".pitch", lobby.getPitch());
        config.save(new File("plugins/Arcade/lobbies.yml"));
        player.sendMessage(ChatColor.AQUA + "The lobby " + ChatColor.BLUE + displayName + ChatColor.AQUA +
                " has been set. Type " + ChatColor.BLUE + "/hub " + name + ChatColor.AQUA + " to get to it.");
    }

    public void setMainLobby(Player player) throws IOException {
        addLobby(player, "lobby", "Lobby");
    }

    public List<Lobby> getLobbies() {
        return new ArrayList<>(lobbies.values());
    }
}