package us.mcmagic.arcade.utils;

import us.mcmagic.arcade.handlers.PlayerData;
import us.mcmagic.arcade.handlers.games.CTFData;
import us.mcmagic.arcade.handlers.games.PixieData;
import us.mcmagic.arcade.parkour.ParkourScore;
import us.mcmagic.mcmagiccore.MCMagicCore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 2/9/15
 */
public class SqlUtil {

    public PlayerData login(UUID uuid) {
        String pack;
        CTFData ctfdata = getCTFData(uuid);
        PixieData pdata = getPixieData(uuid);
        List<ParkourScore> scores = new ArrayList<>();
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT arcadepack FROM player_data WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                return null;
            }
            pack = result.getString("arcadepack");
            result.close();
            sql.close();
            PreparedStatement parkour = connection.prepareStatement("SELECT name,time FROM parkour WHERE uuid=?");
            parkour.setString(1, uuid.toString());
            ResultSet res = parkour.executeQuery();
            while (res.next()) {
                scores.add(new ParkourScore(uuid, res.getString("name"), res.getLong("time")));
            }
            res.close();
            parkour.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return new PlayerData(uuid, ctfdata, pdata, pack, scores);
    }

    private CTFData getCTFData(UUID uuid) {
        CTFData data;
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * from ctf_data WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                return createCTF(connection, uuid);
            }
            data = new CTFData(uuid, result.getString("kit"), result.getInt("hades1"), result.getInt("hades2"),
                    result.getInt("hades3"), result.getInt("baymax1"), result.getInt("baymax2"), result.getInt("baymax3"),
                    result.getInt("hercules1"), result.getInt("hercules2"), result.getInt("hercules3"),
                    result.getInt("merida1"), result.getInt("merida2"), result.getInt("merida3"), result.getInt("bolt1"),
                    result.getInt("bolt2"), result.getInt("bolt3"));
            result.close();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return data;
    }

    private PixieData getPixieData(UUID uuid) {
        PixieData data;
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM pixie_data WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                return createPixieData(connection, uuid);
            }
            data = new PixieData(uuid, result.getInt("delay"), result.getInt("doublejump"));
            result.close();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return data;
    }

    public CTFData createCTF(Connection connection, UUID uuid) {
        try {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO ctf_data (uuid) VALUES('" +
                    uuid.toString() + "');");
            sql.execute();
            sql.close();
            return new CTFData(uuid, "none", 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private PixieData createPixieData(Connection connection, UUID uuid) {
        try {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO pixie_data (uuid) VALUES('" +
                    uuid.toString() + "')");
            sql.execute();
            sql.close();
            return new PixieData(uuid, 16, 1);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
