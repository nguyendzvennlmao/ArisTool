package me.aris.aristool;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ToolCommand implements CommandExecutor {
    private final ArisTool plugin;

    public ToolCommand(ArisTool plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            player.getInventory().addItem(createTool(args[1], parseTime(args[2])));
            return true;
        }
        return false;
    }

    private long parseTime(String input) {
        try {
            long num = Long.parseLong(input.substring(0, input.length() - 1));
            if (input.endsWith("s")) return num;
            if (input.endsWith("m")) return num * 60;
            if (input.endsWith("d")) return num * 86400;
        } catch (Exception e) { return 60; }
        return 60;
    }

    private ItemStack createTool(String type, long seconds) {
        String path = "items." + type;
        Material mat = Material.DIAMOND_PICKAXE;
        if (type.contains("tree")) mat = Material.DIAMOND_AXE;
        if (type.equals("firework")) mat = Material.FIREWORK_ROCKET;
        if (type.equals("auto_drill")) mat = Material.BEACON;

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(translate(plugin.getConfig().getString(path + ".name")));
        long expiry = System.currentTimeMillis() + (seconds * 1000L);
        meta.getPersistentDataContainer().set(ArisTool.EXPIRY_KEY, PersistentDataType.LONG, expiry);
        List<String> lore = new ArrayList<>();
        lore.add(translate(plugin.getConfig().getString(path + ".role")));
        for (String s : plugin.getConfig().getStringList(path + ".lore")) lore.add(translate(s));
        lore.add(translate("&7Dụng cụ hết hạn sau " + formatTime(seconds)));
        meta.setLore(lore);
        for (String en : plugin.getConfig().getStringList(path + ".enchants")) {
            String[] s = en.split(":");
            meta.addEnchant(Enchantment.getByName(s[0]), Integer.parseInt(s[1]), true);
        }
        item.setItemMeta(meta);
        return item;
    }

    private String translate(String s) { return ChatColor.translateAlternateColorCodes('&', s); }
    private String formatTime(long s) {
        if (s >= 86400) return (s / 86400) + "d";
        if (s >= 60) return (s / 60) + "m";
        return s + "s";
    }
              }
