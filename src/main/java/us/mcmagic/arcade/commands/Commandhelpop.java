package us.mcmagic.arcade.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;

/**
 * Created by Marc on 2/14/15
 */
public class Commandhelpop implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/" + label + " [Message]");
            return true;
        }
        String msg;
        if (!(sender instanceof Player)) {
            msg = ChatColor.DARK_RED + "[CM CHAT] " + ChatColor.GRAY + "Console: " + ChatColor.WHITE;
        } else {
            msg = ChatColor.DARK_RED + "[CM CHAT] " + ChatColor.GRAY + sender.getName() + ": " + ChatColor.WHITE;
        }
        for (String arg : args) {
            msg += ChatColor.translateAlternateColorCodes('&', arg + " ");
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (MCMagicCore.getUser(player.getUniqueId()).getRank().getRankId() >= Rank.CASTMEMBER.getRankId()) {
                player.sendMessage(msg);
            }
        }
        return true;
    }
}
