package us.mcmagic.arcade.gamemanager;

import org.bukkit.ChatColor;

/**
 * Created by Marc on 2/16/15
 */
public enum GameSignState {
    JOIN(ChatColor.GREEN + "[JOIN]", 1), FULL(ChatColor.RED + "[FULL]", 2),
    SPECTATE(ChatColor.DARK_GREEN + "[SPECTATE]", 3), INGAME(ChatColor.RED + "[INGAME]", 4),
    RESTARTING(ChatColor.RED + "[RESTARTING]", 5), NOTJOINABLE(ChatColor.RED + "[NOTJOINABLE]", 6);
    private String title;
    private int id;

    GameSignState(String title, int id) {
        this.title = title;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public static GameSignState fromString(String s) {
        switch (ChatColor.stripColor(s.toLowerCase())) {
            case "[join]":
                return JOIN;
            case "[full]":
                return FULL;
            case "[spectate]":
                return SPECTATE;
            case "[ingame]":
                return INGAME;
            case "[restarting]":
                return RESTARTING;
            case "[notjoinable]":
                return NOTJOINABLE;
            default:
                return NOTJOINABLE;
        }
    }
}
