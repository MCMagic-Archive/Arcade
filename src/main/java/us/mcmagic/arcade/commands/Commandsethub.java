package us.mcmagic.arcade.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.arcade.Arcade;

import java.io.IOException;

/**
 * Created by Marc on 2/15/15
 */
public class Commandsethub implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            try {
                Arcade.lobbyUtil.setMainLobby(player);
                player.sendMessage(ChatColor.BLUE + "Main Lobby" + ChatColor.AQUA + " set!");
            } catch (IOException e) {
                player.sendMessage(ChatColor.RED + "There was an error setting that lobby!");
            }
            return true;
        }
        try {
            Arcade.lobbyUtil.addLobby(player, args[0], args[1]);
            player.sendMessage(ChatColor.AQUA + "Lobby " + ChatColor.BLUE + args[1] + ChatColor.AQUA + " set!");
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "There was an error setting that lobby!");
            e.printStackTrace();
        }
        return true;
    }
}
