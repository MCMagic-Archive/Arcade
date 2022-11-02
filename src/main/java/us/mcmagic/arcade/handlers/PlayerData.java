package us.mcmagic.arcade.handlers;

import us.mcmagic.arcade.handlers.games.CTFData;
import us.mcmagic.arcade.handlers.games.PixieData;
import us.mcmagic.arcade.parkour.ParkourScore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {
    private UUID uuid;
    private CTFData ctfData;
    private PixieData pixieData;
    private String pack;
    private List<ParkourScore> scores;

    public PlayerData(UUID uuid, CTFData ctfData, PixieData pixieData, String pack, List<ParkourScore> scores) {
        this.uuid = uuid;
        this.ctfData = ctfData;
        this.pixieData = pixieData;
        this.pack = pack;
        this.scores = scores;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public CTFData getCTFData() {
        return ctfData;
    }

    public PixieData getPixieData() {
        return pixieData;
    }

    public String getPack() {
        return pack;
    }

    public List<ParkourScore> getParkourScores() {
        return new ArrayList<>(scores);
    }

    public void setScores(List<ParkourScore> scores) {
        this.scores = scores;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }
}