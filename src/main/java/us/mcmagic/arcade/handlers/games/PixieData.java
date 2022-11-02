package us.mcmagic.arcade.handlers.games;

import java.util.UUID;

/**
 * Created by Marc on 2/11/16
 */
public class PixieData {
    private UUID uuid;
    private int pdelay;
    private int jump;

    public PixieData(UUID uuid, int pdelay, int jump) {
        this.uuid = uuid;
        this.pdelay = pdelay;
        this.jump = jump;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public int getDelay() {
        return pdelay;
    }

    public int getJump() {
        return jump;
    }

    public void setDelay(int pdelay) {
        this.pdelay = pdelay;
    }

    public void setJump(int jump) {
        this.jump = jump;
    }
}