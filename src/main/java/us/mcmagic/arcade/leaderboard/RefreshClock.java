package us.mcmagic.arcade.leaderboard;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.YamlConfiguration;
import us.mcmagic.arcade.Arcade;
import us.mcmagic.mcmagiccore.uuidconverter.UUIDConverter;

import java.io.File;
import java.util.List;

public class RefreshClock {

    public RefreshClock() {
        final List<String> games = YamlConfiguration.loadConfiguration(
                new File("plugins/Arcade/leaderboard.yml")).getStringList("games");
        Bukkit.getScheduler().runTaskTimer(Arcade.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (String s : games) {
                    update(s);
                }
            }
        }, 20L, 6000L);
    }

    public void update(String game) {
        LeaderboardSqlUtil.refreshData(game);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/Arcade/leaderboard.yml"));
        for (int i = 1; i < 4; i++) {
            int x = config.getInt(game + ".head." + i + ".x");
            int y = config.getInt(game + ".head." + i + ".y");
            int z = config.getInt(game + ".head." + i + ".z");
            String player = config.getString(game + ".head." + i + ".player");
            Location loc = new Location(Bukkit.getWorlds().get(0), x, y, z);
            String name = UUIDConverter.convert(player);
            Block b = loc.getBlock();
            if (b.getType().equals(Material.SKULL)) {
                Skull s = (Skull) b.getState();
                s.setOwner(name);
                s.update();
            }
            int sx = config.getInt(game + ".sign." + i + ".x");
            int sy = config.getInt(game + ".sign." + i + ".y");
            int sz = config.getInt(game + ".sign." + i + ".z");
            Location sloc = new Location(Bukkit.getWorlds().get(0), sx, sy, sz);
            if (!Arcade.isSign(sloc.getBlock().getType())) {
                continue;
            }
            Sign sign = (Sign) sloc.getBlock().getState();
            int score = config.getInt(game + ".head." + i + ".playerscore");
            sign.setLine(1, name);
            sign.setLine(2, score + " " + config.getString(game + ".sign.type"));
            sign.update();
        }
    }
}