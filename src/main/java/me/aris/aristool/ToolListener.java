package me.aris.aristool;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ToolListener implements Listener {
    private final ArisTool plugin;
    private final Map<UUID, Location> pendingDrills = new HashMap<>();

    public ToolListener(ArisTool plugin) { this.plugin = plugin; }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        if (item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Máy Đào Tự Động")) {
            Player p = e.getPlayer();
            Location loc = e.getBlock().getLocation();
            BlockDisplay display = loc.getWorld().spawn(loc, BlockDisplay.class);
            display.setBlock(Material.GLOWSTONE.createBlockData());
            display.setTransformation(new Transformation(new Vector3f(0,0,0), new org.joml.Quaternionf(), new Vector3f(1.05f, 1.05f, 1.05f), new org.joml.Quaternionf()));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.auto_drill_spawn")));
            e.getBlock().setType(Material.BIRCH_SIGN);
            Sign sign = (Sign) e.getBlock().getState();
            sign.setLine(0, "10x17");
            sign.setLine(1, "^^^^^^^^^");
            sign.setLine(2, "Nhập Dài x Rộng");
            sign.update();
            pendingDrills.put(p.getUniqueId(), loc);
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        Player p = e.getPlayer();
        if (pendingDrills.containsKey(p.getUniqueId())) {
            Location loc = pendingDrills.remove(p.getUniqueId());
            String input = e.getLine(0).toLowerCase().replace(" ", "");
            if (input.contains("x")) {
                try {
                    String[] parts = input.split("x");
                    int length = Math.min(Integer.parseInt(parts[0]), 100);
                    int width = Math.min(Integer.parseInt(parts[1]), 100);
                    e.getBlock().setType(Material.AIR);
                    new AutoDrillTask(plugin, loc, length, width, p).start();
                } catch (Exception ex) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.invalid_format")));
                    e.getBlock().setType(Material.AIR);
                }
            } else { e.getBlock().setType(Material.AIR); }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        if (item == null || !item.hasItemMeta()) return;
        if (item.getType() == Material.FIREWORK_ROCKET) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || !e.getPlayer().isGliding()) {
                e.setCancelled(true);
                return;
            }
            item.setAmount(64);
        }
        if (e.getClickedBlock() != null && item.getItemMeta().getDisplayName().contains("Đa Năng")) {
            Material type = e.getClickedBlock().getType();
            if (type.name().contains("LOG")) item.setType(Material.NETHERITE_AXE);
            else if (type.name().contains("GRASS") || type.name().contains("DIRT") || type.name().contains("SAND")) item.setType(Material.NETHERITE_SHOVEL);
            else item.setType(Material.NETHERITE_PICKAXE);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta()) return;
        String name = item.getItemMeta().getDisplayName();
        if (name.contains("Tree Chopper")) breakTree(e.getBlock());
        else if (name.contains("3x3") || name.contains("Đa Năng")) breakArea(e.getBlock(), 1, 1, e.getPlayer());
        else if (name.contains("9x9")) breakArea(e.getBlock(), 4, 4, e.getPlayer());
    }

    private void breakTree(Block b) {
        if (!b.getType().name().contains("LOG") && !b.getType().name().contains("LEAVES")) return;
        b.breakNaturally();
        for (int x = -1; x <= 1; x++) for (int y = -1; y <= 1; y++) for (int z = -1; z <= 1; z++) breakTree(b.getRelative(x, y, z));
    }

    private void breakArea(Block b, int lw, int ww, Player p) {
        for (int x = -lw; x <= lw; x++) for (int y = -lw; y <= lw; y++) for (int z = -ww; z <= ww; z++) {
            Block t = b.getRelative(x, y, z);
            if (t.getType() != Material.AIR && t.getType() != Material.BEDROCK) t.breakNaturally(p.getInventory().getItemInMainHand());
        }
    }
              }
