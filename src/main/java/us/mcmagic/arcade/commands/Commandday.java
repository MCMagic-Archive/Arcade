package us.mcmagic.arcade.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Marc on 2/14/15
 */
public class Commandday implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Bukkit.getWorlds().get(0).setTime(1000);
        sender.sendMessage(ChatColor.GRAY + "Time is now " + ChatColor.GREEN + "Day");
        return true;
    }
}
