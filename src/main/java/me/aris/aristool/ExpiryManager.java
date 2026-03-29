package me.aris.aristool;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ExpiryManager {
    private final ArisTool plugin;
    public ExpiryManager(ArisTool plugin) { this.plugin = plugin; }
    public void update(Player p) {
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (!meta.getPersistentDataContainer().has(ArisTool.EXPIRY_KEY, PersistentDataType.LONG)) return;
        long expiry = meta.getPersistentDataContainer().get(ArisTool.EXPIRY_KEY, PersistentDataType.LONG);
        if (System.currentTimeMillis() > expiry) {
            p.getInventory().setItemInMainHand(null);
        }
    }
}
