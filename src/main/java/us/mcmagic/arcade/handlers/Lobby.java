package us.mcmagic.arcade.handlers;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by Marc on 2/12/15
 */
public class Lobby extends Location {
    private String name;
    private String displayName;

    public Lobby(String name, String displayName, World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
        this.name = name;
        this.displayName = displayName;
    }

    /*
    public Lobby(String name, String displayName, Location location) {
        this.name = name;
        this.displayName = displayName;
        this.location = location;
    }
    */

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
