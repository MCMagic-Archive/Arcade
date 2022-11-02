package us.mcmagic.arcade.threads;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;

/**
 * Created by Marc on 10/10/15
 */
public class CTFShopManagerTask implements Runnable {
    private Location loc1 = new Location(Bukkit.getWorlds().get(0), -240.5, 17, -1.5);
    private Location loc2 = new Location(Bukkit.getWorlds().get(0), -240.5, 17, 6.5);

    @Override
    public void run() {
        ParticleUtil.spawnParticle(ParticleEffect.ENCHANTMENT_TABLE, loc1, 0.5f, 0.5f, 0.5f, 3, 25);
        ParticleUtil.spawnParticle(ParticleEffect.ENCHANTMENT_TABLE, loc2, 0.5f, 0.5f, 0.5f, 3, 25);
    }
}