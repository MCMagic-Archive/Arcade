package us.mcmagic.arcade.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.arcade.Arcade;

/**
 * Created by Marc on 2/12/15
 */
public class Commandvanish implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can do this!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            Arcade.visibilityUtil.toggleVanish(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("list")) {
            Arcade.visibilityUtil.listVanishedPlayers(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("check")) {
            if (Arcade.visibilityUtil.isVanished(player.getUniqueId())) {
                player.sendMessage(ChatColor.DARK_AQUA + "You are vanished.");
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "You are visible.");
            }
            return true;
        }
        return true;
    }
}
