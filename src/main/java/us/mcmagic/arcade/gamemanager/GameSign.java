package us.mcmagic.arcade.gamemanager;

import org.bukkit.Location;

/**
 * Created by Marc on 2/13/15
 */
public class GameSign {
    private int signId;
    private String game;
    private String serverName;
    private String map;
    private Location location;
    private int maxAmount;
    private GameSignState state;
    private int pcount;
    private boolean needChange = false;

    public GameSign(int signId, String game, String serverName, String map, Location location, int maxAmount, GameSignState state, int pcount) {
        this.signId = signId;
        this.game = game;
        this.serverName = serverName;
        this.map = map;
        this.location = location;
        this.maxAmount = maxAmount;
        this.state = state;
        this.pcount = pcount;
    }

    public int getSignId() {
        return signId;
    }

    public String getGame() {
        return game;
    }

    public String getServerName() {
        return serverName;
    }

    public String getMap() {
        return map;
    }

    public Location getLocation() {
        return location;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public GameSignState getState() {
        return state;
    }

    public int getPlayerCount() {
        return pcount;
    }

    public boolean needsUpdate() {
        return needChange;
    }

    public void setState(GameSignState state) {
        this.state = state;
    }

    public void setPlayerCount(int count) {
        this.pcount = count;
    }

    public void updateNeeded() {
        needChange = true;
    }

    public void noUpdateNeeded() {
        needChange = false;
    }
}
