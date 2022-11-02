package us.mcmagic.arcade.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import us.mcmagic.arcade.Arcade;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Marc on 9/25/15
 */
public class Commandparkour implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set")) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/Arcade/parkours.yml"));
                String n = args[2];
                String n2 = args[2].toLowerCase();
                Location loc = player.getLocation();
                List<String> list = config.getStringList("parkours");
                if (!list.contains(n2)) {
                    list.add(n2);
                    config.set("parkours", list);
                }
                switch (args[1].toLowerCase()) {
                    case "spawn":
                        config.set("parkour." + n2 + ".name", n);
                        config.set("parkour." + n2 + ".spawn.x", loc.getX());
                        config.set("parkour." + n2 + ".spawn.y", loc.getY());
                        config.set("parkour." + n2 + ".spawn.z", loc.getZ());
                        config.set("parkour." + n2 + ".spawn.yaw", loc.getYaw());
                        config.set("parkour." + n2 + ".spawn.pitch", loc.getPitch());
                        try {
                            config.save(new File("plugins/Arcade/parkours.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        player.sendMessage(ChatColor.GREEN + "Successfully set " + ChatColor.AQUA + "Spawn " +
                                ChatColor.GREEN + "for " + ChatColor.BLUE + n);
                        Arcade.parkourManager.initialize();
                        break;
                    case "start":
                        config.set("parkour." + n2 + ".name", n);
                        config.set("parkour." + n2 + ".start.x", loc.getBlockX());
                        config.set("parkour." + n2 + ".start.y", loc.getBlockY());
                        config.set("parkour." + n2 + ".start.z", loc.getBlockZ());
                        try {
                            config.save(new File("plugins/Arcade/parkours.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        player.sendMessage(ChatColor.GREEN + "Successfully set " + ChatColor.AQUA + "Start Location " +
                                ChatColor.GREEN + "for " + ChatColor.BLUE + n);
                        Arcade.parkourManager.initialize();
                        break;
                    case "finish":
                        config.set("parkour." + n2 + ".name", n);
                        config.set("parkour." + n2 + ".finish.x", loc.getBlockX());
                        config.set("parkour." + n2 + ".finish.y", loc.getBlockY());
                        config.set("parkour." + n2 + ".finish.z", loc.getBlockZ());
                        try {
                            config.save(new File("plugins/Arcade/parkours.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        player.sendMessage(ChatColor.GREEN + "Successfully set " + ChatColor.AQUA + "Finish Location " +
                                ChatColor.GREEN + "for " + ChatColor.BLUE + n);
                        Arcade.parkourManager.initialize();
                        break;
                }
                return true;
            }
        }
        helpMenu(player);
        return true;
    }

    private void helpMenu(Player player) {
        User user = MCMagicCore.getUser(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Parkour Commands:");
        if (user.getRank().getRankId() >= Rank.CASTMEMBER.getRankId()) {
            player.sendMessage(ChatColor.GREEN + "/parkour set [spawn/start/finish] [Name] " + ChatColor.YELLOW + "- " +
                    ChatColor.AQUA + "Set values for a Parkour.");
        }
    }
}