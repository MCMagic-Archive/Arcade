package us.mcmagic.arcade;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.mcmagic.arcade.commands.*;
import us.mcmagic.arcade.gamemanager.GameManager;
import us.mcmagic.arcade.handlers.PlayerData;
import us.mcmagic.arcade.leaderboard.RefreshClock;
import us.mcmagic.arcade.listeners.*;
import us.mcmagic.arcade.parkour.ParkourManager;
import us.mcmagic.arcade.particles.ParticleManager;
import us.mcmagic.arcade.resource.PackManager;
import us.mcmagic.arcade.shops.CTFShopManager;
import us.mcmagic.arcade.shops.PixieShopManager;
import us.mcmagic.arcade.threads.CTFShopManagerTask;
import us.mcmagic.arcade.utils.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 2/12/15
 */
public class Arcade extends JavaPlugin {
    private static Arcade instance;
    public static GameManager gameManager;
    public static VisibilityUtil visibilityUtil;
    public static SqlUtil sqlUtil;
    public static ParticleManager particleManager;
    public static LobbyUtil lobbyUtil;
    public static RefreshClock refreshClock;
    public static InventoryUtil inventoryUtil;
    public static NPCManager npcManager;
    public static ParkourManager parkourManager;
    public static CTFShopManager ctfShopManager = new CTFShopManager();
    public static PixieShopManager pixieShopManager = new PixieShopManager();
    public static PlayerMove playerMove;
    private static HashMap<UUID, PlayerData> playerData = new HashMap<>();

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();
        getLogger().info("Enabling Arcade...");
        instance = this;
        sqlUtil = new SqlUtil();
        gameManager = new GameManager();
        lobbyUtil = new LobbyUtil();
        inventoryUtil = new InventoryUtil();
        parkourManager = new ParkourManager();
        visibilityUtil = new VisibilityUtil();
        particleManager = new ParticleManager();
        refreshClock = new RefreshClock();
        npcManager = new NPCManager();
        playerMove = new PlayerMove();
        registerCommands();
        registerListeners();
        Bukkit.getScheduler().runTaskTimer(this, new CTFShopManagerTask(), 0L, 10L);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getLogger().info("Arcade Enabled (" + (System.currentTimeMillis() - time) + "ms)");
    }

    @Override
    public void onDisable() {
        getLogger().info("Arcade Disabled");
    }

    public static Arcade getInstance() {
        return instance;
    }

    public void login(UUID uuid) {
        playerData.put(uuid, sqlUtil.login(uuid));
    }

    public void logout(UUID uuid) {
        playerData.remove(uuid);
        playerMove.removeFromSent(uuid);
    }

    public static PlayerData getPlayerData(UUID uuid) {
        return playerData.get(uuid);
    }

    public List<String> getJoinMessages() {
        return Arrays.asList(ChatColor.GREEN + "Welcome to the " + ChatColor.AQUA + "" + ChatColor.BOLD + "MCMagic Arcade!");
    }

    public static boolean isSign(Material m) {
        return m.equals(Material.SIGN) || m.equals(Material.SIGN_POST) || m.equals(Material.WALL_SIGN);
    }

    private void registerCommands() {
        getCommand("bc").setExecutor(new Commandbc());
        getCommand("day").setExecutor(new Commandday());
        getCommand("fly").setExecutor(new Commandfly());
        getCommand("fw").setExecutor(new Commandfw());
        getCommand("gamesign").setExecutor(new Commandgamesign());
        getCommand("helpop").setExecutor(new Commandhelpop());
        getCommand("helpop").setAliases(Arrays.asList("ac"));
        getCommand("hub").setExecutor(new Commandhub());
        getCommand("hub").setAliases(Arrays.asList("lobby"));
        getCommand("lb").setExecutor(new Commandlb());
        getCommand("night").setExecutor(new Commandnight());
        getCommand("noon").setExecutor(new Commandnoon());
        getCommand("parkour").setExecutor(new Commandparkour());
        getCommand("pt").setExecutor(new Commandpt());
        getCommand("sethub").setExecutor(new Commandsethub());
        getCommand("sethub").setAliases(Arrays.asList("setlobby"));
        getCommand("vanish").setExecutor(new Commandvanish());
        getCommand("vanish").setAliases(Arrays.asList("v"));
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new InventoryClick(), this);
        pm.registerEvents(new ItemListener(), this);
        pm.registerEvents(new PlayerDamage(), this);
        pm.registerEvents(new PlayerInteract(), this);
        pm.registerEvents(new PlayerJoinAndLeave(), this);
        pm.registerEvents(new PlayerPortal(), this);
        pm.registerEvents(new PlayerTeleport(), this);
        pm.registerEvents(new PlayerToggleFlight(), this);
        pm.registerEvents(new PackManager(), this);
        pm.registerEvents(playerMove, this);
        pm.registerEvents(new SignChange(), this);
        pm.registerEvents(new Weather(), this);
        pm.registerEvents(npcManager, this);
    }
}
