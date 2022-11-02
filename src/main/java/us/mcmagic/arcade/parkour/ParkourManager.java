package us.mcmagic.arcade.parkour;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import us.mcmagic.arcade.Arcade;
import us.mcmagic.arcade.handlers.PlayerData;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.uuidconverter.UUIDConverter;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Marc on 9/25/15
 */
public class ParkourManager {
    private List<Parkour> parkours = new ArrayList<>();
    private HashMap<UUID, Long> cooldown = new HashMap<>();
    private HashMap<Location, String[]> signUpdates = new HashMap<>();

    public ParkourManager() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Arcade.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Parkour p : getParkours()) {
                    try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
                        PreparedStatement sql = connection.prepareStatement("SELECT * FROM parkour WHERE name=? ORDER BY time ASC LIMIT 0,3");
                        sql.setString(1, p.getName());
                        ResultSet result = sql.executeQuery();
                        int i = 1;
                        while (result.next()) {
                            String name = UUIDConverter.convert(result.getString("uuid"));
                            Location loc = getLoc(p.getName(), i);
                            signUpdates.put(loc, new String[]{ChatColor.BLUE + "#" + ChatColor.BOLD + i, name,
                                    ChatColor.BLUE + "" + ChatColor.BOLD + "Time:", ChatColor.DARK_GREEN +
                                    formatTime(result.getLong("time"))});
                            i++;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0L, 2000L);
        Bukkit.getScheduler().runTaskTimer(Arcade.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<Location, String[]> entry : signUpdates.entrySet()) {
                    Block b = entry.getKey().getBlock();
                    if (!Arcade.isSign(b.getType())) {
                        continue;
                    }
                    Sign s = (Sign) b.getState();
                    for (int i = 0; i < entry.getValue().length; i++) {
                        s.setLine(i, entry.getValue()[i]);
                    }
                    s.update();
                }
            }
        }, 20L, 20L);
        initialize();
    }

    private Location getLoc(String name, int i) {
        Location loc;
        if (name.equalsIgnoreCase("up")) {
            loc = new Location(Bukkit.getWorlds().get(0), 4 + i, 7, 244);
        } else {
            loc = new Location(Bukkit.getWorlds().get(0), 8 - i, 7, 270);
        }
        return loc;
    }

    public void initialize() {
        parkours.clear();
        File file = new File("plugins/Arcade/parkours.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> plist = config.getStringList("parkours");
        for (String s : plist) {
            Location spawn = new Location(Bukkit.getWorlds().get(0), config.getDouble("parkour." + s + ".spawn.x"),
                    config.getDouble("parkour." + s + ".spawn.y"), config.getDouble("parkour." + s + ".spawn.z"),
                    config.getInt("parkour." + s + ".spawn.yaw"), config.getInt("parkour." + s + ".spawn.pitch"));
            Location start = new Location(Bukkit.getWorlds().get(0), config.getInt("parkour." + s + ".start.x"),
                    config.getInt("parkour." + s + ".start.y"), config.getInt("parkour." + s + ".start.z"));
            Location finish = new Location(Bukkit.getWorlds().get(0), config.getInt("parkour." + s + ".finish.x"),
                    config.getInt("parkour." + s + ".finish.y"), config.getInt("parkour." + s + ".finish.z"));
            Parkour p = new Parkour(config.getString("parkour." + s + ".name"), spawn, start, finish);
            parkours.add(p);
        }
    }

    public void handleMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
    }

    public void handleInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block b = event.getClickedBlock();
        if (!b.getType().equals(Material.GOLD_PLATE)) {
            if (!b.getType().equals(Material.IRON_PLATE)) {
                return;
            }
            event.setCancelled(true);
            if (cooldown.containsKey(player.getUniqueId())) {
                if (cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    return;
                }
                cooldown.remove(player.getUniqueId());
            }
            Parkour p = getParkourFromEnd(b.getLocation());
            if (p == null) {
                return;
            }
            for (Race r : p.getCurrentRaces()) {
                if (r.getUniqueId().equals(player.getUniqueId())) {
                    long start = p.stopRace(player.getUniqueId());
                    long score = System.currentTimeMillis() - start;
                    String time = formatTime(score);
                    player.sendMessage(ChatColor.GREEN + "You finished the " + ChatColor.BLUE + p.getName() +
                            " Parkour" + ChatColor.GREEN + " in " + ChatColor.AQUA + time + "!");
                    sound(player);
                    cooldown.put(player.getUniqueId(), System.currentTimeMillis() + 1000);
                    finishParkour(player, p, new ParkourScore(player.getUniqueId(), p.getName(), score));
                    return;
                }
            }
            player.sendMessage(ChatColor.GREEN + "You " + ChatColor.YELLOW + "" + ChatColor.ITALIC + "must " +
                    ChatColor.GREEN + "step on the " + ChatColor.GOLD + "Gold Pressure Plate " + ChatColor.GREEN +
                    "at the beginning of the Parkour!");
            sound(player);
            cooldown.put(player.getUniqueId(), System.currentTimeMillis() + 1000);
            return;
        }
        event.setCancelled(true);
        if (cooldown.containsKey(player.getUniqueId())) {
            if (cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                return;
            }
            cooldown.remove(player.getUniqueId());
        }
        Parkour p = getParkourFromStart(b.getLocation());
        for (Parkour pk : getParkours()) {
            for (Race r : pk.getCurrentRaces()) {
                if (r.getUniqueId().equals(player.getUniqueId())) {
                    if (pk.getStart().equals(p.getStart())) {
                        pk.stopRace(player.getUniqueId());
                        p.startNewRace(player.getUniqueId());
                        player.sendMessage(ChatColor.GREEN + "Your Race Time has been reset for the " +
                                ChatColor.BLUE + p.getName() + " Parkour!");
                        sound(player);
                        cooldown.put(player.getUniqueId(), System.currentTimeMillis() + 1000);
                        return;
                    }
                    pk.stopRace(player.getUniqueId());
                    p.startNewRace(player.getUniqueId());
                    player.sendMessage(ChatColor.GREEN + "You stopped the " + ChatColor.BLUE + pk.getName() +
                            ChatColor.GREEN + " Parkour and started the " + ChatColor.BLUE + p.getName() + " Parkour!");
                    sound(player);
                    cooldown.put(player.getUniqueId(), System.currentTimeMillis() + 1000);
                    return;
                }
            }
        }
        cooldown.put(player.getUniqueId(), System.currentTimeMillis() + 1000);
        if (player.isFlying()) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You can't be flying when you start a Parkour Race!");
            return;
        }
        p.startNewRace(player.getUniqueId());
        sound(player);
        player.sendMessage(ChatColor.GREEN + "You started the " + ChatColor.BLUE + p.getName() + " Parkour!");
    }

    public void cancelParkour(Player player, String reason) {
        for (Parkour p : getParkours()) {
            for (Race r : p.getCurrentRaces()) {
                if (r.getUniqueId().equals(player.getUniqueId())) {
                    p.stopRace(player.getUniqueId());
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Parkour stopped, " + reason + "!");
                    break;
                }
            }
        }
    }

    private String formatTime(long l) {
        double sec = ((double) (l % 60000)) / 1000;
        int min = (int) (l / 60000);
        String s = (min >= 10 ? min : "0" + min) + ":" + (sec >= 10 ? sec : "0" + sec);
        return s;
    }

    public void teleportToParkour(Player player, String name) {
        Parkour p = getParkour(name);
        if (p == null) {
            player.sendMessage(ChatColor.RED + "Parkour not found!");
            return;
        }
        player.teleport(p.getSpawn());
        player.sendMessage(ChatColor.GREEN + "You have been teleported to the " + ChatColor.BLUE + name +
                ChatColor.GREEN + " Parkour!");
        player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 100f, 1f);
    }

    public Parkour getParkourFromStart(Location loc) {
        for (Parkour p : getParkours()) {
            if (p.getStart().equals(loc)) {
                return p;
            }
        }
        return null;
    }

    private Parkour getParkourFromEnd(Location loc) {
        for (Parkour p : getParkours()) {
            if (p.getFinish().equals(loc)) {
                return p;
            }
        }
        return null;
    }

    public Parkour getParkour(String name) {
        for (Parkour p : getParkours()) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public List<Parkour> getParkours() {
        return new ArrayList<>(parkours);
    }

    public void logout(Player player) {
        for (Parkour p : getParkours()) {
            for (Race r : p.getCurrentRaces()) {
                if (r.getUniqueId().equals(player.getUniqueId())) {
                    p.stopRace(player.getUniqueId());
                }
            }
        }
    }

    private void sound(Player player) {
        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 10f, 1f);
    }

    private void finishParkour(final Player player, final Parkour p, final ParkourScore score) {
        if (player.isFlying()) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You can't finish a race while flying!");
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(Arcade.getInstance(), new Runnable() {
            @Override
            public void run() {
                PlayerData data = Arcade.getPlayerData(player.getUniqueId());
                List<ParkourScore> scores = data.getParkourScores();
                boolean isRecord = false;
                boolean contains = false;
                boolean update = false;
                for (ParkourScore s : new ArrayList<>(scores)) {
                    if (s.getName().equalsIgnoreCase(score.getName())) {
                        contains = true;
                        if (score.getTime() < s.getTime()) {
                            scores.remove(s);
                            scores.add(score);
                            isRecord = true;
                            update = true;
                            break;
                        }
                    }
                }
                if (!contains) {
                    isRecord = true;
                    scores.add(score);
                }
                data.setScores(scores);
                if (!isRecord) {
                    return;
                }
                try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
                    String query;
                    if (update) {
                        query = "UPDATE parkour SET time=? WHERE uuid=? AND name=?";
                    } else {
                        query = "INSERT INTO parkour (time, uuid, name) VALUES (?,?,?)";
                    }
                    PreparedStatement sql = connection.prepareStatement(query);
                    sql.setLong(1, score.getTime());
                    sql.setString(2, player.getUniqueId().toString());
                    sql.setString(3, score.getName());
                    sql.execute();
                    sql.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}