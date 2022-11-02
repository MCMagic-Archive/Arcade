package us.mcmagic.arcade.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.arcade.Arcade;
import us.mcmagic.arcade.handlers.Lobby;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.PlayerUtil;
import us.mcmagic.mcmagiccore.player.User;

/**
 * Created by Marc on 2/14/15
 */
public class Commandhub implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "/" + label + " [Lobby Name] [Player Name]");
                return true;
            }
            Lobby lobby = Arcade.lobbyUtil.getLobby(args[0]);
            if (lobby == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is not a registered lobby!");
                return true;
            }
            Player tp = PlayerUtil.findPlayer(args[1]);
            if (tp == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
            User user = MCMagicCore.getUser(tp.getUniqueId());
            if (user.getResourcePack().equalsIgnoreCase("tron")) {
                if (!args[0].equalsIgnoreCase("tron")) {
                    String pack = Arcade.getPlayerData(tp.getUniqueId()).getPack();
                    if (!pack.equalsIgnoreCase("none")) {
                        MCMagicCore.resourceManager.sendPack(tp, pack.equals("NoPrefer") ? "Blank" : pack);
                        Arcade.playerMove.removeFromSent(tp.getUniqueId());
                    } else {
                        MCMagicCore.resourceManager.sendPack(tp, user.getPreferredPack().equals("NoPrefer") ?
                                "Blank" : user.getPreferredPack());
                        Arcade.playerMove.removeFromSent(tp.getUniqueId());
                    }
                }
            }
            tp.teleport(lobby);
            tp.sendMessage(ChatColor.BLUE + "You have arrived at the " + ChatColor.AQUA + lobby.getDisplayName() +
                    " Lobby");
            return true;
        }
        Player player = (Player) sender;
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (args.length == 0) {
            if (user.getResourcePack().equalsIgnoreCase("tron")) {
                String pack = Arcade.getPlayerData(player.getUniqueId()).getPack();
                if (!pack.equalsIgnoreCase("none")) {
                    MCMagicCore.resourceManager.sendPack(player, pack.equals("NoPrefer") ? "Blank" : pack);
                    Arcade.playerMove.removeFromSent(player.getUniqueId());
                } else {
                    MCMagicCore.resourceManager.sendPack(player, user.getPreferredPack().equals("NoPrefer") ?
                            "Blank" : user.getPreferredPack());
                    Arcade.playerMove.removeFromSent(player.getUniqueId());
                }
            }
            player.teleport(Arcade.lobbyUtil.getLobby("lobby"));
            player.sendMessage(ChatColor.AQUA + "You have teleported to the " + ChatColor.BLUE + "Lobby");
            return true;
        }
        if (args.length != 1 && args.length != 2) {
            player.sendMessage(ChatColor.RED + "/" + label + " [Lobby Name]");
            return true;
        }
        if (args[0].toLowerCase().startsWith("staff")) {
            if (user.getRank().getRankId() < Rank.EARNINGMYEARS.getRankId()) {
                player.sendMessage(ChatColor.RED + "You cannot access that Lobby!");
                return true;
            }
        }
        Lobby lobby = Arcade.lobbyUtil.getLobby(args[0]);
        if (lobby == null) {
            player.sendMessage(ChatColor.RED + "'" + args[0] + "' is not a registered lobby!");
            return true;
        }
        if (args.length == 2) {
            if (user.getRank().getRankId() >= Rank.CASTMEMBER.getRankId()) {
                Player tp = PlayerUtil.findPlayer(args[1]);
                if (tp == null) {
                    player.sendMessage(ChatColor.RED + "Player not found!");
                    return true;
                }
                if (MCMagicCore.getUser(tp.getUniqueId()).getResourcePack().equalsIgnoreCase("tron")) {
                    if (!args[0].equalsIgnoreCase("tron")) {
                        String pack = Arcade.getPlayerData(tp.getUniqueId()).getPack();
                        if (!pack.equalsIgnoreCase("none")) {
                            MCMagicCore.resourceManager.sendPack(tp, pack.equals("NoPrefer") ? "Blank" : pack);
                            Arcade.playerMove.removeFromSent(tp.getUniqueId());
                        } else {
                            MCMagicCore.resourceManager.sendPack(tp, user.getPreferredPack().equals("NoPrefer") ?
                                    "Blank" : user.getPreferredPack());
                            Arcade.playerMove.removeFromSent(tp.getUniqueId());
                        }
                    }
                }
                tp.teleport(lobby);
                if (lobby.getDisplayName().equalsIgnoreCase("lobby")) {
                    tp.sendMessage(ChatColor.AQUA + "You have teleported to the" + ChatColor.BLUE + " Lobby");
                    player.sendMessage(ChatColor.AQUA + "You have teleported " + ChatColor.BLUE + tp.getName() +
                            ChatColor.AQUA + " to the " + ChatColor.BLUE + " Lobby");
                } else {
                    tp.sendMessage(ChatColor.AQUA + "You have teleported to the " + ChatColor.BLUE +
                            lobby.getDisplayName() + " Lobby");
                    player.sendMessage(ChatColor.AQUA + "You have teleported " + ChatColor.BLUE + tp.getName() +
                            ChatColor.AQUA + " to the " + ChatColor.BLUE + lobby.getDisplayName() + " Lobby");
                }
            } else {
                player.sendMessage(ChatColor.RED + "/" + label + " [Lobby Name]");
                return true;
            }
        }
        if (user.getResourcePack().equalsIgnoreCase("tron")) {
            if (!args[0].equalsIgnoreCase("tron")) {
                String pack = Arcade.getPlayerData(player.getUniqueId()).getPack();
                if (!pack.equalsIgnoreCase("none")) {
                    MCMagicCore.resourceManager.sendPack(player, pack.equals("NoPrefer") ? "Blank" : pack);
                    Arcade.playerMove.removeFromSent(player.getUniqueId());
                } else {
                    MCMagicCore.resourceManager.sendPack(player, user.getPreferredPack().equals("NoPrefer") ?
                            "Blank" : user.getPreferredPack());
                    Arcade.playerMove.removeFromSent(player.getUniqueId());
                }
            }
        }
        player.teleport(lobby);
        player.sendMessage(ChatColor.AQUA + "You have teleported to the " + ChatColor.BLUE +
                lobby.getDisplayName() + " Lobby");
        return true;
    }
}
