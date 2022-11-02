package us.mcmagic.arcade.handlers;

import org.bukkit.ChatColor;

/**
 * Created by Marc on 2/18/15
 */
public enum InventoryType {
    MAIN(ChatColor.YELLOW + "Navigation"),
    RESOURCE(ChatColor.BLUE + "Resource Packs"),
    SHOP_PIXIE(ChatColor.YELLOW + "Pixie Dust Shootout Shop"),
    SHOP_CTF(ChatColor.GREEN + "Capture The Flag Shop"),
    SHOP_CTF_HADES(ChatColor.RED + "Hades Kit"),
    SHOP_CTF_BAYMAX(ChatColor.AQUA + "Baymax Kit"),
    SHOP_CTF_HERCULES(ChatColor.GOLD + "Hercules Kit"),
    SHOP_CTF_MERIDA(ChatColor.DARK_GREEN + "Merida Kit"),
    SHOP_CTF_BOLT(ChatColor.YELLOW + "Bolt Kit"),
    PARTICLE(ChatColor.BLUE + "Particle Menu");

    private String title;

    InventoryType(String s) {
        title = s;
    }

    public String getTitle() {
        return title;
    }

    public static InventoryType fromString(String s) {
        for (InventoryType type : InventoryType.values()) {
            if (type.name().equalsIgnoreCase(s)) {
                return type;
            }
        }
        return null;
    }
}