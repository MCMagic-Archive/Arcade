package us.mcmagic.arcade.commands;

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.arcade.Arcade;

/**
 * Created by Marc on 2/16/15
 */
public class Commandgamesign implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this!");
            return true;
        }
        Player player = (Player) sender;
        PacketPlayOutEntityEquipment e;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                Arcade.gameManager.reloadGamesigns(player);
            }
        }
        return true;
    }
}
