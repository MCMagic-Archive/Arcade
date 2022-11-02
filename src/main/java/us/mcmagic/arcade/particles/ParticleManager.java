package us.mcmagic.arcade.particles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import us.mcmagic.arcade.Arcade;
import us.mcmagic.arcade.handlers.PlayerData;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Marc on 2/12/15
 */
public class ParticleManager {
    private HashMap<UUID, ParticleEffect> particles = new HashMap<>();
    private HashMap<UUID, Integer> taskIds = new HashMap<>();

    public void join(Player player) {
        PlayerData data = Arcade.getInstance().getPlayerData(player.getUniqueId());
        String particle = "none";//data.getString("particle");
        if (particle == null || particle.equalsIgnoreCase("none")) {
            return;
        }
        ParticleEffect effect = ParticleEffect.fromString(particle);
        particles.put(player.getUniqueId(), effect);
        taskIds.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(Arcade.getInstance(),
                new PlayParticle(player, effect), 0L, 2L).getTaskId());
    }

    public void logout(Player player) {
        stop(player);
        if (particles.containsKey(player.getUniqueId())) {
            particles.remove(player.getUniqueId());
        }
    }

    public void stop(Player player) {
        Integer taskID = taskIds.remove(player.getUniqueId());
        if (taskID != null) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }

    public void clearParticle(Player player) {
        if (particles.containsKey(player.getUniqueId())) {
            particles.remove(player.getUniqueId());
        }
        stop(player);
        //Arcade.getInstance().getPlayerData(player.getUniqueId()).setString("particle", "none");
        player.sendMessage(ChatColor.GREEN + "You cleared your Particle!");
        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 1);
        Connection connection = MCMagicCore.permSqlUtil.getConnection();
        try {
            PreparedStatement sql = connection.prepareStatement("UPDATE arcade_data SET particle=? WHERE uuid=?");
            sql.setString(1, "none");
            sql.setString(2, player.getUniqueId().toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void setParticle(Player player, String particle, String displayName) {
        if (particles.containsKey(player.getUniqueId())) {
            particles.remove(player.getUniqueId());
        }
        stop(player);
        switch (particle) {
            case "notes":
                particles.put(player.getUniqueId(), ParticleEffect.NOTE);
                taskIds.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(Arcade.getInstance(),
                        new PlayParticle(player, ParticleEffect.NOTE), 0L, 2L).getTaskId());
                break;
            case "firework spark":
                particles.put(player.getUniqueId(), ParticleEffect.FIREWORKS_SPARK);
                taskIds.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(Arcade.getInstance(),
                        new PlayParticle(player, ParticleEffect.FIREWORKS_SPARK), 0L, 2L).getTaskId());
                break;
            case "mickey head":
                particles.put(player.getUniqueId(), ParticleEffect.ANGRY_VILLAGER);
                taskIds.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(Arcade.getInstance(),
                        new PlayParticle(player, ParticleEffect.ANGRY_VILLAGER), 0L, 2L).getTaskId());
                break;
            case "enchantment":
                particles.put(player.getUniqueId(), ParticleEffect.ENCHANTMENT_TABLE);
                taskIds.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(Arcade.getInstance(),
                        new PlayParticle(player, ParticleEffect.ENCHANTMENT_TABLE), 0L, 2L).getTaskId());
                break;
            case "flame":
                particles.put(player.getUniqueId(), ParticleEffect.FLAME);
                taskIds.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(Arcade.getInstance(),
                        new PlayParticle(player, ParticleEffect.FLAME), 0L, 2L).getTaskId());
                break;
            case "hearts":
                particles.put(player.getUniqueId(), ParticleEffect.HEART);
                taskIds.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(Arcade.getInstance(),
                        new PlayParticle(player, ParticleEffect.HEART), 0L, 2L).getTaskId());
                break;
            case "portal":
                particles.put(player.getUniqueId(), ParticleEffect.PORTAL);
                taskIds.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(Arcade.getInstance(),
                        new PlayParticle(player, ParticleEffect.PORTAL), 0L, 2L).getTaskId());
                break;
            case "lava":
                particles.put(player.getUniqueId(), ParticleEffect.LAVA);
                taskIds.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(Arcade.getInstance(),
                        new PlayParticle(player, ParticleEffect.LAVA), 0L, 2L).getTaskId());
                break;
            case "witch magic":
                particles.put(player.getUniqueId(), ParticleEffect.WITCH_MAGIC);
                taskIds.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(Arcade.getInstance(),
                        new PlayParticle(player, ParticleEffect.WITCH_MAGIC), 0L, 2L).getTaskId());
                break;
            default:
                return;
        }
        player.sendMessage(ChatColor.GREEN + "You have selected the " + displayName + ChatColor.GREEN + " Particle!");
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        //Arcade.getInstance().getPlayerData(player.getUniqueId()).setString("particle", particles.get(player.getUniqueId()).getName());
        Connection connection = MCMagicCore.permSqlUtil.getConnection();
        try {
            PreparedStatement sql = connection.prepareStatement("UPDATE arcade_data SET particle=? WHERE uuid=?");
            sql.setString(1, particles.get(player.getUniqueId()).getName());
            sql.setString(2, player.getUniqueId().toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}