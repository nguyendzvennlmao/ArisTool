package me.aris.aristool;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class ArisTool extends JavaPlugin {
    private static ArisTool instance;
    public static NamespacedKey EXPIRY_KEY;

    @Override
    public void onEnable() {
        instance = this;
        EXPIRY_KEY = new NamespacedKey(this, "expiry_time");
        saveDefaultConfig();
        getCommand("at").setExecutor(new ToolCommand(this));
        getServer().getPluginManager().registerEvents(new ToolListener(this), this);
        getServer().getGlobalRegionScheduler().runAtFixedRate(this, (task) -> {
            Bukkit.getOnlinePlayers().forEach(player -> new ExpiryManager(this).update(player));
        }, 20L, 20L);
    }

    public static ArisTool getInstance() { return instance; }
}
