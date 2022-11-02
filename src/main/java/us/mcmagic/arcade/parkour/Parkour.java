package us.mcmagic.arcade.parkour;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 9/25/15
 */
public class Parkour {
    private final String name;
    private Location spawn;
    private final Location start;
    private final Location finish;
    private List<Race> currentRaces = new ArrayList<>();

    public Parkour(String name, Location spawn, Location start, Location finish) {
        this.name = name;
        this.spawn = spawn;
        this.start = start;
        this.finish = finish;
    }

    public String getName() {
        return name;
    }

    public Location getSpawn() {
        return spawn;
    }

    public Location getStart() {
        return start;
    }

    public Location getFinish() {
        return finish;
    }

    public Race startNewRace(UUID uuid) {
        Race race = new Race(uuid);
        currentRaces.add(race);
        return race;
    }

    public List<Race> getCurrentRaces() {
        return new ArrayList<>(currentRaces);
    }

    public long stopRace(UUID uuid) {
        long start = 0;
        for (Race r : getCurrentRaces()) {
            if (r.getUniqueId().equals(uuid)) {
                start = r.getStartTime();
                currentRaces.remove(r);
            }
        }
        return start;
    }
}