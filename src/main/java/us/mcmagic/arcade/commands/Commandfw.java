package us.mcmagic.arcade.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import us.mcmagic.arcade.Arcade;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Marc on 2/14/15
 */
public class Commandfw implements CommandExecutor {
    private List<UUID> delay = new ArrayList<>();
    private boolean allowed = true;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can do this!");
            return true;
        }
        final Player player = (Player) sender;
        Rank rank = MCMagicCore.getUser(player.getUniqueId()).getRank();
        if (rank.getRankId() < Rank.DVCMEMBER.getRankId()) {
            player.sendMessage(ChatColor.RED + "You must be the " + Rank.DVCMEMBER.getNameWithBrackets() + ChatColor.RED +
                    " to use this!");
            return true;
        }
        if (rank.getRankId() >= Rank.CASTMEMBER.getRankId()) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("toggle")) {
                    allowed = !allowed;
                    player.sendMessage(ChatColor.RED + "Toggled Fireworks!");
                    return true;
                }
            }
        }
        if (delay.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You have to wait at least " + ChatColor.GREEN + "10s " + ChatColor.RED +
                    " before launching another Firework!");
            return true;
        }
        if (!allowed) {
            player.sendMessage(ChatColor.RED + "Fireworks are disabled right now!");
            return true;
        }
        player.sendMessage(ChatColor.GREEN + "You have launched a firework!");
        Firework fw = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta fm = fw.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder().withColor(randomColor()).withColor(randomColor()).with(randomType()).flicker(true).trail(true).build());
        fm.setPower(1);
        fw.setFireworkMeta(fm);
        if (rank.getRankId() < Rank.CASTMEMBER.getRankId()) {
            delay.add(player.getUniqueId());
            Bukkit.getScheduler().runTaskLater(Arcade.getInstance(), new Runnable() {
                @Override
                public void run() {
                    delay.remove(player.getUniqueId());
                }
            }, 200L);
        }
        return true;
    }

    public FireworkEffect.Type randomType() {
        Random r = new Random();
        int i = r.nextInt(5) + 1;
        switch (i) {
            case 1:
                return FireworkEffect.Type.BALL;
            case 2:
                return FireworkEffect.Type.BALL_LARGE;
            case 3:
                return FireworkEffect.Type.STAR;
            case 4:
                return FireworkEffect.Type.CREEPER;
            case 5:
                return FireworkEffect.Type.BURST;
            default:
                return FireworkEffect.Type.BURST;
        }
    }

    public Color randomColor() {
        Random r = new Random();
        int i = r.nextInt(17) + 1;
        switch (i) {
            case 1:
                return Color.BLACK;
            case 2:
                return Color.OLIVE;
            case 3:
                return Color.ORANGE;
            case 4:
                return Color.BLUE;
            case 5:
                return Color.AQUA;
            case 6:
                return Color.FUCHSIA;
            case 7:
                return Color.GRAY;
            case 8:
                return Color.GREEN;
            case 9:
                return Color.LIME;
            case 10:
                return Color.MAROON;
            case 11:
                return Color.NAVY;
            case 12:
                return Color.PURPLE;
            case 13:
                return Color.SILVER;
            case 14:
                return Color.TEAL;
            case 15:
                return Color.WHITE;
            case 16:
                return Color.YELLOW;
            case 17:
                return Color.RED;
            default:
                return Color.AQUA;
        }
    }
}
