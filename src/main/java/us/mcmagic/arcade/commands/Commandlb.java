package us.mcmagic.arcade.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import us.mcmagic.arcade.Arcade;

import java.io.File;
import java.io.IOException;

/**
 * Created by Marc on 2/17/15
 */
public class Commandlb implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can do this!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("update")) {
                player.sendMessage(ChatColor.GREEN + "Starting update");
                YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/Arcade/leaderboard.yml"));
                for (String s : config.getStringList("games")) {
                    player.sendMessage(ChatColor.BLUE + "Updating " + s);
                    Arcade.getInstance().refreshClock.update(s);
                }
                player.sendMessage(ChatColor.GREEN + "Update finished!");
                return true;
            }
            return true;
        }
        if (args.length == 4) {
            if (!args[0].equalsIgnoreCase("set")) {
                return true;
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/Arcade/leaderboard.yml"));
            Location loc = player.getLocation();
            config.set(args[1].toLowerCase() + "." + args[2].toLowerCase() + "." + args[3] + "." + ".x", loc.getBlockX());
            config.set(args[1].toLowerCase() + "." + args[2].toLowerCase() + "." + args[3] + "." + ".y", loc.getBlockY());
            config.set(args[1].toLowerCase() + "." + args[2].toLowerCase() + "." + args[3] + "." + ".z", loc.getBlockZ());
            try {
                config.save(new File("plugins/Arcade/leaderboard.yml"));
            } catch (IOException e) {
                e.printStackTrace();
                player.sendMessage(ChatColor.RED + "Error!");
            }
            player.sendMessage(ChatColor.BLUE + args[2].substring(0, 1).toUpperCase() + args[2].substring(1).toUpperCase()
                    + " location set for " + args[1] + " place " + args[3]);
        }
        return true;
    }
}
