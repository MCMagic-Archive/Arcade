package us.mcmagic.arcade.parkour;

import java.util.UUID;

/**
 * Created by Marc on 9/30/15
 */
public class ParkourScore {
    private UUID uuid;
    private String name;
    private long time;

    public ParkourScore(UUID uuid, String name, long time) {
        this.uuid = uuid;
        this.name = name;
        this.time = time;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public long getTime() {
        return time;
    }
}