package us.mcmagic.arcade.gamemanager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import us.mcmagic.arcade.Arcade;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.bungee.BungeeUtil;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Marc on 2/12/15
 */
public class GameManager {
    private static HashMap<String, GameSign> signs = new HashMap<>();
    private List<String> tempList = new ArrayList<>();
    private HashMap<String, Integer> ids = new HashMap<>();
    private boolean reloading = false;

    public GameManager() {
        initialize();
    }

    public void initialize() {
        final File file = new File("plugins/Arcade/gamesigns.yml");
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> games = config.getStringList("game-names");
        for (String game : games) {
            for (String map : config.getStringList(game + ".maps")) {
                GameSign sign = new GameSign(config.getInt(game + "." + map + ".id"), game,
                        config.getString(game + "." + map + ".servername"), map,
                        new Location(Bukkit.getWorlds().get(0), config.getDouble(game + "." + map + ".x"),
                                config.getDouble(game + "." + map + ".y"), config.getDouble(game + "." + map + ".z")),
                        config.getInt(game + "." + map + ".max"), GameSignState.NOTJOINABLE, 0);
                signs.put(game + ":" + map, sign);
                tempList.add(game + ":" + map);
            }
        }
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM game_info");
            ResultSet result = sql.executeQuery();
            while (result.next()) {
                String game = result.getString("game");
                String map = result.getString("map");
                String title = game + ":" + map;
                if (tempList.contains(title)) {
                    tempList.remove(title);
                }
            }
            result.close();
            sql.close();
            String insertStatement = "INSERT INTO game_info (sign_id, game, servername, map) VALUES";
            for (int i = 0; i < tempList.size(); i++) {
                String s = tempList.get(i);
                GameSign sign = signs.get(s);
                String[] list = s.split(":");
                insertStatement += "(" + sign.getSignId() + ",'" + sign.getGame() + "','" + sign.getServerName() + "','" + sign.getMap() + "')";
                if (i < tempList.size() - 1) {
                    insertStatement += ",";
                } else {
                    insertStatement += ";";
                }
            }
            Bukkit.getLogger().info("GameManager insert query: " + insertStatement);
            if (insertStatement.endsWith(";")) {
                PreparedStatement insert = connection.prepareStatement(insertStatement);
                insert.execute();
            }
            tempList.clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ids.put("sql", Bukkit.getScheduler().runTaskTimerAsynchronously(Arcade.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        if (Bukkit.getOnlinePlayers().size() == 0) {
                            return;
                        }
                        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
                            PreparedStatement sql = connection.prepareStatement("SELECT * FROM game_info");
                            ResultSet result = sql.executeQuery();
                            while (result.next()) {
                                String game = result.getString("game");
                                String map = result.getString("map");
                                String title = game + ":" + map;
                                if (!signs.containsKey(title)) {
                                    continue;
                                }
                                GameSign sign = signs.get(title);
                                int[] stateArray = new int[]{result.getInt("online"), result.getInt("rebooting"),
                                        result.getInt("ingame"), result.getInt("spectating")};
                                int pcount = result.getInt("playercount");
                                int[] currentArray = stateArray(sign.getState().getTitle());
                                int currentCount = sign.getPlayerCount();
                                if (!sign.getState().equals(GameSignState.fromString(stateString(stateArray)))) {
                                    sign.setState(GameSignState.fromString(stateString(stateArray)));
                                    sign.updateNeeded();
                                }
                                if (currentCount != pcount) {
                                    sign.setPlayerCount(pcount);
                                    sign.updateNeeded();
                                }
                            }
                            result.close();
                            sql.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }, 0L, 20L).getTaskId()
        );
        ids.put("sign", Bukkit.getScheduler().runTaskTimer(Arcade.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        if (Bukkit.getOnlinePlayers().size() == 0) {
                            return;
                        }
                        for (GameSign sign : signs.values()) {
                            if (!sign.needsUpdate()) {
                                continue;
                            }
                            Block b = sign.getLocation().getBlock();
                            if (!Arcade.isSign(b.getType())) {
                                continue;
                            }
                            Sign s = (Sign) b.getState();
                            String count = "" + ChatColor.LIGHT_PURPLE + sign.getPlayerCount() + "/" + sign.getMaxAmount();
                            String mapLine = "" + ChatColor.DARK_GRAY + (sign.getMap().substring(0, 1).toUpperCase() +
                                    sign.getMap().substring(1));
                            String idLine = "" + ChatColor.DARK_GRAY + sign.getSignId();
                            boolean changed = false;
                            if (!s.getLine(0).equals(sign.getState().getTitle())) {
                                s.setLine(0, sign.getState().getTitle());
                                changed = true;
                            }
                            if (sign.getState().equals(GameSignState.JOIN)) {
                                if (sign.getPlayerCount() >= sign.getMaxAmount()) {
                                    s.setLine(0, GameSignState.FULL.getTitle());
                                    changed = true;
                                }
                            }
                            if (!count.equals(s.getLine(3))) {
                                s.setLine(3, count);
                                changed = true;
                            }
                            if (!mapLine.equals(s.getLine(2))) {
                                s.setLine(2, mapLine);
                                changed = true;
                            }
                            if (!idLine.equals(s.getLine(1))) {
                                s.setLine(1, idLine);
                                changed = true;
                            }
                            if (changed) {
                                s.update();
                            }
                            sign.noUpdateNeeded();
                        }
                    }
                }, 10L, 20L).getTaskId()

        );
    }

    public void reloadGamesigns(final Player player) {
        if (reloading) {
            player.sendMessage(ChatColor.GREEN + "Currently reloading, try again in a moment!");
            return;
        }
        reloading = true;
        player.sendMessage(ChatColor.GREEN + "Stopping tasks...");
        Integer sql = ids.remove("sql");
        Integer sign = ids.remove("sign");
        if (sql != null) {
            Bukkit.getScheduler().cancelTask(sql);
            player.sendMessage(ChatColor.GREEN + "SQL task stopped.");
        }
        if (sign != null) {
            Bukkit.getScheduler().cancelTask(sign);
            player.sendMessage(ChatColor.GREEN + "Sign task stopped.");
        }
        for (GameSign gs : signs.values()) {
            if (!Arcade.isSign(gs.getLocation().getBlock().getType())) {
                continue;
            }
            Sign s = (Sign) gs.getLocation().getBlock().getState();
            s.setLine(0, "");
            s.setLine(1, ChatColor.DARK_GRAY + "Reloading");
            s.setLine(2, ChatColor.DARK_GRAY + "Servers...");
            s.setLine(3, "");
            s.update();
        }
        signs.clear();
        player.sendMessage(ChatColor.GREEN + "Sign Array cleared. Initializing task...");
        Bukkit.getScheduler().runTaskLater(Arcade.getInstance(), new Runnable() {
            @Override
            public void run() {
                initialize();
                player.sendMessage(ChatColor.GREEN + "Successfully reloaded GameSigns!");
                reloading = false;
            }
        }, 60L);
    }

    public void handleClick(Player player, Sign s) {
        User user = MCMagicCore.getUser(player.getUniqueId());
        switch (ChatColor.stripColor(s.getLine(0)).toLowerCase()) {
            case "[join]":
                player.sendMessage(ChatColor.GREEN + "Now joining the game...");
                for (GameSign sign : signs.values()) {
                    if (sign.getLocation().equals(s.getLocation())) {
                        BungeeUtil.sendToServer(player, sign.getServerName());
                        return;
                    }
                }
                break;
            case "[restarting]":
                player.sendMessage(ChatColor.RED + "That server is restarting!");
                break;
            case "[ingame]":
                if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                    player.sendMessage(ChatColor.RED + "That game is In-Game right now!");
                    return;
                }
                player.sendMessage(ChatColor.GREEN + "Now joining the game...");
                for (GameSign sign : signs.values()) {
                    if (sign.getLocation().equals(s.getLocation())) {
                        BungeeUtil.sendToServer(player, sign.getServerName());
                        return;
                    }
                }
                break;
            case "[spectate]":
                player.sendMessage(ChatColor.GREEN + "Now joining the game...");
                for (GameSign sign : signs.values()) {
                    if (sign.getLocation().equals(s.getLocation())) {
                        BungeeUtil.sendToServer(player, sign.getServerName());
                        return;
                    }
                }
                break;
            case "[full]":
                player.sendMessage(ChatColor.RED + "That game is full!");
                break;
            case "[notjoinable]":
                player.sendMessage(ChatColor.RED + "That server is offline right now!");
                break;
            default:
                player.sendMessage(ChatColor.RED + "Error!");
        }

    }

    public static void setupGamesign(SignChangeEvent event) {
        Player player = event.getPlayer();
        Block b = event.getBlock();
        if (Arcade.isSign(b.getType())) {
            if (event.getLine(0).equalsIgnoreCase("gamesign")) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/Arcade/gamesigns.yml"));
                String line2 = event.getLine(1);
                String line3 = event.getLine(2);
                String line4 = event.getLine(3);
                List<String> games = config.getStringList("game-names");
                if (!games.contains(line2)) {
                    player.sendMessage(ChatColor.RED + "Game not recognized!");
                    return;
                }
                List<String> maps = config.getStringList(line2 + ".maps");
                if (!maps.contains(line3)) {
                    player.sendMessage(ChatColor.RED + "Map not recognized!");
                    return;
                }
                GameSign sign = new GameSign(config.getInt(line2 + "." + line3 + ".id"), line2, line4, line3,
                        b.getLocation(), config.getInt(line2 + "." + line3 + ".max"), GameSignState.fromString("notjoinable"), 0);
                signs.put(line2 + ":" + line3, sign);
                player.sendMessage(ChatColor.BLUE + "Game sign created!");
                Location loc = b.getLocation();
                config.set(line2 + "." + line3 + ".x", loc.getBlockX());
                config.set(line2 + "." + line3 + ".y", loc.getBlockY());
                config.set(line2 + "." + line3 + ".z", loc.getBlockZ());
                try {
                    config.save(new File("plugins/Arcade/gamesigns.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                    player.sendMessage(ChatColor.RED + "Error saving file!");
                }
            }
        }
    }

    private String stateString(int[] array) {
        if (array[0] == 1 && array[2] == 1) {
            return GameSignState.INGAME.getTitle();
        }
        if (array[0] == 1 && array[3] == 1) {
            return GameSignState.SPECTATE.getTitle();
        }
        if (array[1] == 1) {
            return GameSignState.RESTARTING.getTitle();
        }
        if (array[0] == 1) {
            return GameSignState.JOIN.getTitle();
        }
        return GameSignState.NOTJOINABLE.getTitle();
    }

    private int[] stateArray(String string) {
        switch (ChatColor.stripColor(string.toLowerCase())) {
            case "[join]":
                return new int[]{1, 0, 0, 0};
            case "[ingame]":
                return new int[]{1, 0, 1, 0};
            case "[spectate]":
                return new int[]{1, 0, 0, 1};
            case "[restarting]":
                return new int[]{0, 1, 0, 0};
            case "[notjoinable]":
                return new int[]{0, 0, 0, 0};
            default:
                return new int[]{0, 0, 0, 0};
        }
    }
}
