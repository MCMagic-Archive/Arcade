package us.mcmagic.arcade.parkour;

import java.util.UUID;

/**
 * Created by Marc on 9/25/15
 */
public class Race {
    private UUID uuid;
    private final long startTime;

    public Race(UUID uuid) {
        this.uuid = uuid;
        startTime = System.currentTimeMillis();
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public long getStartTime() {
        return startTime;
    }
}