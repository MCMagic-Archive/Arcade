package us.mcmagic.arcade.leaderboard;

import org.bukkit.configuration.file.YamlConfiguration;
import us.mcmagic.mcmagiccore.MCMagicCore;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LeaderboardSqlUtil {

    public static void refreshData(String gamename) {
        if (!containsGameColumn(gamename)) {
            addGameColumn(gamename);
        }
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            File file = new File("plugins/Arcade/leaderboard.yml");
            YamlConfiguration locf = YamlConfiguration.loadConfiguration(file);
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM  leaderboard WHERE staff=0 ORDER BY leaderboard." +
                    gamename + " DESC");
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                return;
            }
            locf.set(gamename + ".head.1.player", result.getString("uuid"));
            locf.set(gamename + ".head.1.playerscore", result.getInt(gamename));
            if (!result.next()) {
                return;
            }
            locf.set(gamename + ".head.2.player", result.getString("uuid"));
            locf.set(gamename + ".head.2.playerscore", result.getInt(gamename));
            if (!result.next()) {
                return;
            }
            locf.set(gamename + ".head.3.player", result.getString("uuid"));
            locf.set(gamename + ".head.3.playerscore", result.getInt(gamename));
            try {
                locf.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            result.close();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void login(UUID uuid, boolean isStaff) {
        int i = isStaff ? 1 : 0;
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM leaderboard WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                result.close();
                sql.close();
                PreparedStatement add = connection.prepareCall("INSERT INTO leaderboard (uuid, staff) VALUES('" +
                        uuid.toString() + "', " + i + ");");
                add.execute();
                add.close();
                return;
            }
            if (result.getInt("staff") != i) {
                result.close();
                sql.close();
                PreparedStatement update = connection.prepareStatement("UPDATE leaderboard SET staff=? WHERE uuid=?");
                update.setInt(1, i);
                update.setString(2, uuid.toString());
                update.execute();
                return;
            }
            result.close();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addGameColumn(String columnName) {
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            String statement = "ALTER TABLE leaderboard ADD COLUMN " + columnName + " INT(10) NOT NULL DEFAULT  '0'";
            PreparedStatement sql = connection.prepareStatement(statement);
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean containsGameColumn(String gname) {
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SHOW COLUMNS FROM leaderboard LIKE '" + gname + "';");
            ResultSet resultset = sql.executeQuery();
            boolean containsColumn = resultset.next();
            sql.close();
            resultset.close();
            return containsColumn;
        } catch (SQLException e) {
            return false;
        }
    }
}