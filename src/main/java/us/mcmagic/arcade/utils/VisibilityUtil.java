package us.mcmagic.arcade.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import us.mcmagic.arcade.Arcade;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 2/12/15
 */
public class VisibilityUtil {
    private List<UUID> vanished = new ArrayList<>();
    private List<UUID> hiddenPlayers = new ArrayList<>();
    private List<UUID> delay = new ArrayList<>();

    public void login(Player player) {
        Rank rank = MCMagicCore.getUser(player.getUniqueId()).getRank();
        if (rank.getRankId() < Rank.SPECIALGUEST.getRankId()) {
            for (UUID uuid : getVanished()) {
                Player tp = Bukkit.getPlayer(uuid);
                player.hidePlayer(tp);
            }
            for (UUID uuid : new ArrayList<>(hiddenPlayers)) {
                Player tp = Bukkit.getPlayer(uuid);
                tp.hidePlayer(player);
            }
        }
    }

    public void listVanishedPlayers(Player player) {
        StringBuilder list = new StringBuilder();
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (vanished.contains(tp.getUniqueId())) {
                if (list.length() > 0) {
                    list.append(ChatColor.DARK_AQUA);
                    list.append(", ");
                }
                list.append(ChatColor.AQUA);
                list.append(tp.getName());
            }
        }
        list.insert(0, "Vanished: ");
        list.insert(0, ChatColor.DARK_AQUA);
        player.sendMessage(list.toString());
    }

    public boolean isVanished(UUID uuid) {
        return vanished.contains(uuid);
    }

    public void setVanished(Player player) {
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (tp.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            User user = MCMagicCore.getUser(tp.getUniqueId());
            if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                tp.hidePlayer(player);
            }
        }
        vanished.add(player.getUniqueId());
    }

    public void toggleVanish(Player player) {
        if (!vanished.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.DARK_AQUA + "You have vanished. Poof!");
            for (Player tp : Bukkit.getOnlinePlayers()) {
                if (tp.getUniqueId().equals(player.getUniqueId())) {
                    continue;
                }
                User user = MCMagicCore.getUser(tp.getUniqueId());
                if (user.getRank().getRankId() >= Rank.SPECIALGUEST.getRankId()) {
                    tp.sendMessage(ChatColor.YELLOW + player.getName() + " has vanished. Poof!");
                } else {
                    tp.hidePlayer(player);
                }
            }
            vanished.add(player.getUniqueId());
            return;
        }
        player.sendMessage(ChatColor.DARK_AQUA + "You have become visible.");
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (tp.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            User user = MCMagicCore.getUser(tp.getUniqueId());
            if (user.getRank().getRankId() >= Rank.SPECIALGUEST.getRankId()) {
                tp.sendMessage(ChatColor.YELLOW + player.getName() + " has become visible.");
            } else {
                tp.showPlayer(player);
            }
        }
        vanished.remove(player.getUniqueId());
    }

    public List<UUID> getVanished() {
        return new ArrayList<>(vanished);
    }

    public void togglePlayers(final Player player) {
        if (delay.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You have to wait " + ChatColor.GREEN + "3s" + ChatColor.RED
                    + " before using this!");
            return;
        }
        delay.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(Arcade.getInstance(), new Runnable() {
            @Override
            public void run() {
                delay.remove(player.getUniqueId());
            }
        }, 60L);
        if (hiddenPlayers.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.GREEN + "You toggled players on!");
            player.playSound(player.getLocation(), Sound.CLICK, 100, 0.6f);
            for (Player tp : Bukkit.getOnlinePlayers()) {
                if (tp.getUniqueId().equals(player.getUniqueId())) {
                    continue;
                }
                User user = MCMagicCore.getUser(tp.getUniqueId());
                if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                    player.showPlayer(tp);
                }
            }
            hiddenPlayers.remove(player.getUniqueId());
            player.getInventory().setItem(8, Arcade.inventoryUtil.visible);
            return;
        }
        player.sendMessage(ChatColor.RED + "You toggled players off!");
        player.playSound(player.getLocation(), Sound.CLICK, 100, 0.4f);
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (tp.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            User user = MCMagicCore.getUser(tp.getUniqueId());
            if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                player.hidePlayer(tp);
            }
        }
        hiddenPlayers.add(player.getUniqueId());
        player.getInventory().setItem(8, Arcade.inventoryUtil.hidden);
    }

    public boolean hasPlayersHidden(Player player) {
        return hiddenPlayers.contains(player.getUniqueId());
    }

    public void logout(Player player) {
        if (vanished.contains(player.getUniqueId())) {
            vanished.remove(player.getUniqueId());
        }
        if (hiddenPlayers.contains(player.getUniqueId())) {
            hiddenPlayers.remove(player.getUniqueId());
        }
        if (delay.contains(player.getUniqueId())) {
            delay.remove(player.getUniqueId());
        }
    }
}
