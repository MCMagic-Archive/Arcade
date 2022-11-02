package us.mcmagic.arcade.utils;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.PlayerCreateNPCEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import us.mcmagic.arcade.Arcade;
import us.mcmagic.arcade.handlers.InventoryType;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.player.User;

/**
 * Created by Marc on 8/22/15
 */
public class NPCManager implements Listener {

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity e = event.getRightClicked();
        if (!e.hasMetadata("NPC")) {
            return;
        }
        NPC npc = CitizensAPI.getNPCRegistry().getNPC(e);
        User user = MCMagicCore.getUser(player.getUniqueId());
        switch (npc.getId()) {
            case 1:
            case 2:
                Arcade.ctfShopManager.openInventory(InventoryType.SHOP_CTF, player,
                        Arcade.getPlayerData(player.getUniqueId()), e.getUniqueId());
                break;
            case 3:
                Arcade.pixieShopManager.openInventory(InventoryType.SHOP_PIXIE, player,
                        Arcade.getPlayerData(player.getUniqueId()), e.getUniqueId());
                break;
        }
    }

    @EventHandler
    public void onPlayerCreateNPC(PlayerCreateNPCEvent event) {
        if (event.getCreator().getName().equals("Legobuilder0813")) {
            return;
        }
        event.setCancelled(true);
        event.getCreator().sendMessage(ChatColor.RED + "You can't create NPCs on the Arcade!");
    }
}