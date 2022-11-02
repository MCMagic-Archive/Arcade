package us.mcmagic.arcade.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import us.mcmagic.arcade.Arcade;
import us.mcmagic.arcade.handlers.InventoryType;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;

/**
 * Created by Marc on 2/18/15
 */
public class InventoryClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();
        for (InventoryType type : InventoryType.values()) {
            if (inv.getName().equals(type.getTitle())) {
                event.setCancelled(true);
                Arcade.inventoryUtil.clickInventory(event, type, player, event.getCurrentItem());
                return;
            }
        }
        event.setCancelled(MCMagicCore.getUser(player.getUniqueId()).getRank().getRankId() < Rank.CASTMEMBER.getRankId());
    }
}
