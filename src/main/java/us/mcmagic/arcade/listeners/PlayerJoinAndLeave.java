package us.mcmagic.arcade.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.mcmagic.arcade.Arcade;
import us.mcmagic.arcade.handlers.Lobby;
import us.mcmagic.arcade.leaderboard.LeaderboardSqlUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.util.ArrayList;

/**
 * Created by Marc on 1/20/15
 */
public class PlayerJoinAndLeave implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            Arcade.getInstance().login(event.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        event.setJoinMessage("");
        final User user = MCMagicCore.getUser(player.getUniqueId());
        Bukkit.getScheduler().runTaskAsynchronously(Arcade.getInstance(), new Runnable() {
            @Override
            public void run() {
                LeaderboardSqlUtil.login(player.getUniqueId(), user.getRank().getRankId() >= Rank.EARNINGMYEARS.getRankId());
            }
        });
        if (player.getLocation().getBlock().getType().equals(Material.PORTAL)) {
            player.teleport(Arcade.lobbyUtil.getLobby("lobby"));
        }
        for (String msg : Arcade.getInstance().getJoinMessages()) {
            player.sendMessage(msg);
        }
        player.removePotionEffect(PotionEffectType.SPEED);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 1, false, false));
        Arcade.inventoryUtil.setup(player);
        Arcade.visibilityUtil.login(player);
        Rank rank = user.getRank();
        if (rank.getRankId() == Rank.SPECIALGUEST.getRankId()) {
            Bukkit.broadcastMessage(rank.getTagColor() + player.getName() + ChatColor.AQUA +
                    " has joined the Arcade!");
        }
        if (rank.getRankId() > Rank.CASTMEMBER.getRankId()) {
            Arcade.visibilityUtil.setVanished(player);
        }
        if (!player.hasPlayedBefore()) {
            player.teleport(Arcade.lobbyUtil.getLobby("lobby"));
        }
        if (rank.getRankId() < Rank.CASTMEMBER.getRankId()) {
            player.setGameMode(GameMode.ADVENTURE);
        } else {
            player.setGameMode(GameMode.CREATIVE);
        }
        if (rank.getRankId() > Rank.GUEST.getRankId()) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
        Arcade.particleManager.join(player);
        moveToNearestLobby(player);
    }

    public static void moveToNearestLobby(Player player) {
        final Location loc = player.getLocation();
        Lobby l = null;
        double distance = -1;
        for (Lobby lobby : new ArrayList<>(Arcade.lobbyUtil.getLobbies())) {
            if (lobby == null) {
                continue;
            }
            if (distance == -1) {
                l = lobby;
                distance = lobby.distance(loc);
                continue;
            }
            double d = lobby.distance(loc);
            if (d < distance) {
                l = lobby;
                distance = d;
            }
        }
        if (l == null) {
            player.teleport(Arcade.lobbyUtil.getLobby("lobby"));
            return;
        }
        player.teleport(l);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage("");
        Arcade.visibilityUtil.logout(player);
        Arcade.particleManager.logout(player);
        Arcade.parkourManager.logout(player);
        Arcade.getInstance().logout(player.getUniqueId());
    }
}
