package us.mcmagic.arcade.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

/**
 * Created by Marc on 2/14/15
 */
public class Commandfly implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "/fly [Player]");
                return true;
            }
            Player player = PlayerUtil.findPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
            if (player.isFlying()) {
                player.setAllowFlight(false);
                player.setFlying(false);
                player.sendMessage(ChatColor.RED + "You can no longer fly!");
                sender.sendMessage(ChatColor.RED + player.getName() + " can no longer fly!");
            } else {
                player.setAllowFlight(true);
                player.setFlying(true);
                player.teleport(player.getLocation().add(0, 0.2, 0));
                player.sendMessage(ChatColor.GREEN + "You can now fly!");
                sender.sendMessage(ChatColor.GREEN + player.getName() + " can now fly!");
            }
        }
        Player player = (Player) sender;
        if (MCMagicCore.getUser(player.getUniqueId()).getRank().getRankId() >= Rank.CASTMEMBER.getRankId()) {
            if (args.length == 1) {
                Player tp = PlayerUtil.findPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return true;
                }
                if (player.isFlying()) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.sendMessage(ChatColor.RED + "You can no longer fly!");
                    sender.sendMessage(ChatColor.RED + player.getName() + " can no longer fly!");
                } else {
                    player.setAllowFlight(true);
                    player.setFlying(true);
                    player.teleport(player.getLocation().add(0, 0.2, 0));
                    player.sendMessage(ChatColor.GREEN + "You can now fly!");
                    sender.sendMessage(ChatColor.GREEN + player.getName() + " can now fly!");
                }
                return true;
            }
        }
        if (player.isFlying()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.sendMessage(ChatColor.RED + "You can no longer fly!");
        } else {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.teleport(player.getLocation().add(0, 0.2, 0));
            player.sendMessage(ChatColor.GREEN + "You can now fly!");
        }
        return true;
    }
}
