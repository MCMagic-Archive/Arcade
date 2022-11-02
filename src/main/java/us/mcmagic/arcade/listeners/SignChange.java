package us.mcmagic.arcade.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import us.mcmagic.arcade.gamemanager.GameManager;

/**
 * Created by Marc on 2/14/15
 */
public class SignChange implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        Block b = event.getBlock();
        if (!(b.getType().equals(Material.SIGN) || b.getType().equals(Material.SIGN_POST) ||
                b.getType().equals(Material.WALL_SIGN))) {
            return;
        }
        Sign s = (Sign) b.getState();
        switch (event.getLine(0).toLowerCase()) {
            case "[hub]":
                event.setLine(0, ChatColor.AQUA + "[" + ChatColor.BLUE + "Hub" + ChatColor.AQUA + "]");
                event.setLine(1, ChatColor.DARK_GRAY + "Click me to");
                event.setLine(2, ChatColor.DARK_GRAY + "return to");
                event.setLine(3, ChatColor.DARK_GRAY + "the Hub");
                player.sendMessage(ChatColor.BLUE + "Hub sign created!");
                return;
            case "gamesign":
                GameManager.setupGamesign(event);
                return;
            case "[parkour]":
                event.setLine(0, ChatColor.WHITE + "[" + ChatColor.GREEN + "Parkour" + ChatColor.WHITE + "]");
                event.setLine(2, ChatColor.YELLOW + event.getLine(1));
                event.setLine(1, ChatColor.BLUE + "Click to do the");
                event.setLine(3, ChatColor.BLUE + "Parkour!");
                return;
            case "stats":
                switch (event.getLine(1)) {
                    case "pixie":
                        event.setLine(0, ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Stats" + ChatColor.DARK_GRAY + "]");
                        event.setLine(1, ChatColor.YELLOW + "Pixie Dust");
                        event.setLine(2, ChatColor.GREEN + "Click to see");
                        event.setLine(3, ChatColor.GREEN + "your score!");
                        break;
                    case "ctf":
                        event.setLine(0, ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Stats" + ChatColor.DARK_GRAY + "]");
                        event.setLine(1, ChatColor.DARK_GREEN + "CTF");
                        event.setLine(2, ChatColor.GREEN + "Click to see");
                        event.setLine(3, ChatColor.GREEN + "your score!");
                        break;
                    case "tron":
                        event.setLine(0, ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Stats" + ChatColor.DARK_GRAY + "]");
                        event.setLine(1, ChatColor.RED + "Tron");
                        event.setLine(2, ChatColor.GREEN + "Click to see");
                        event.setLine(3, ChatColor.GREEN + "your score!");
                        break;
                }
        }
        for (int i = 0; i < 4; i++) {
            event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
        }
    }
}
