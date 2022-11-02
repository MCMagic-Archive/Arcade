package us.mcmagic.arcade.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.arcade.Arcade;
import us.mcmagic.arcade.handlers.InventoryType;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;

/**
 * Created by Marc on 2/19/15
 */
public class Commandpt implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this!");
        }
        Player player = (Player) sender;
        Rank rank = MCMagicCore.getUser(player.getUniqueId()).getRank();
        if (rank.getRankId() < Rank.DVCMEMBER.getRankId()) {
            player.sendMessage(ChatColor.RED + "You must be the " + Rank.DVCMEMBER.getNameWithBrackets() +
                    ChatColor.RED + " rank to use this!");
            return true;
        }
        Arcade.inventoryUtil.openInventory(InventoryType.PARTICLE, player);
        return true;
    }
}
