package us.mcmagic.arcade.handlers.games;

import us.mcmagic.mcmagiccore.arcade.ctf.CTFKit;

import java.util.UUID;

/**
 * Created by Marc on 8/22/15
 */
public class CTFData {
    private UUID uuid;
    private String kit;
    private int hades1;
    private int hades2;
    private int hades3;
    private int baymax1;
    private int baymax2;
    private int baymax3;
    private int hercules1;
    private int hercules2;
    private int hercules3;
    private int merida1;
    private int merida2;
    private int merida3;
    private int bolt1;
    private int bolt2;
    private int bolt3;

    public CTFData(UUID uuid, String kit, int hades1, int hades2, int hades3, int baymax1, int baymax2, int baymax3,
                   int hercules1, int hercules2, int hercules3, int merida1, int merida2, int merida3, int bolt1,
                   int bolt2, int bolt3) {
        this.uuid = uuid;
        this.kit = kit;
        this.hades1 = hades1;
        this.hades2 = hades2;
        this.hades3 = hades3;
        this.baymax1 = baymax1;
        this.baymax2 = baymax2;
        this.baymax3 = baymax3;
        this.hercules1 = hercules1;
        this.hercules2 = hercules2;
        this.hercules3 = hercules3;
        this.merida1 = merida1;
        this.merida2 = merida2;
        this.merida3 = merida3;
        this.bolt1 = bolt1;
        this.bolt2 = bolt2;
        this.bolt3 = bolt3;
    }

    public String getKit() {
        return kit;
    }

    public int get(CTFKit kit, int type) {
        switch (kit) {
            case HADES:
                switch (type) {
                    case 1:
                        return hades1;
                    case 2:
                        return hades2;
                    case 3:
                        return hades3;
                }
                break;
            case BAYMAX:
                switch (type) {
                    case 1:
                        return baymax1;
                    case 2:
                        return baymax2;
                    case 3:
                        return baymax3;
                }
                break;
            case HERCULES:
                switch (type) {
                    case 1:
                        return hercules1;
                    case 2:
                        return hercules2;
                    case 3:
                        return hercules3;
                }
                break;
            case MERIDA:
                switch (type) {
                    case 1:
                        return merida1;
                    case 2:
                        return merida2;
                    case 3:
                        return merida3;
                }
                break;
            case BOLT:
                switch (type) {
                    case 1:
                        return bolt1;
                    case 2:
                        return bolt2;
                    case 3:
                        return bolt3;
                }
                break;
        }
        return 0;
    }

    public void setKit(String kit) {
        this.kit = kit;
    }

    public void set(CTFKit kit, int type, int value) {
        switch (kit) {
            case HADES:
                switch (type) {
                    case 1:
                        this.hades1 = value;
                        break;
                    case 2:
                        this.hades2 = value;
                        break;
                    case 3:
                        this.hades3 = value;
                        break;
                }
                break;
            case BAYMAX:
                switch (type) {
                    case 1:
                        this.baymax1 = value;
                        break;
                    case 2:
                        this.baymax2 = value;
                        break;
                    case 3:
                        this.baymax3 = value;
                        break;
                }
                break;
            case HERCULES:
                switch (type) {
                    case 1:
                        this.hercules1 = value;
                        break;
                    case 2:
                        this.hercules2 = value;
                        break;
                    case 3:
                        this.hercules3 = value;
                        break;
                }
                break;
            case MERIDA:
                switch (type) {
                    case 1:
                        this.merida1 = value;
                        break;
                    case 2:
                        this.merida2 = value;
                        break;
                    case 3:
                        this.merida3 = value;
                        break;
                }
                break;
            case BOLT:
                switch (type) {
                    case 1:
                        this.bolt1 = value;
                        break;
                    case 2:
                        this.bolt2 = value;
                        break;
                    case 3:
                        this.bolt3 = value;
                        break;
                }
                break;
        }
    }

    public UUID getUniqueId() {
        return uuid;
    }
}