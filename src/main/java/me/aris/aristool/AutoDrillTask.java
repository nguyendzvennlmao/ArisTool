package me.aris.aristool;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class AutoDrillTask {
    private final ArisTool plugin;
    private final Location center;
    private final int length, width;
    private final Player player;

    public AutoDrillTask(ArisTool plugin, Location center, int length, int width, Player player) {
        this.plugin = plugin;
        this.center = center;
        this.length = length;
        this.width = width;
        this.player = player;
    }

    public void start() {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.auto_drill_start").replace("%size%", length + "x" + width)));
        plugin.getServer().getRegionScheduler().runDelayed(plugin, center, (task) -> {
            for (int y = center.getBlockY(); y > center.getWorld().getMinHeight(); y--) {
                breakCustomLayer(y);
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.auto_drill_complete")));
        }, 10L);
    }

    private void breakCustomLayer(int y) {
        int rL = length / 2;
        int rW = width / 2;
        for (int x = -rL; x <= rL; x++) {
            for (int z = -rW; z <= rW; z++) {
                Block b = center.getWorld().getBlockAt(center.getBlockX() + x, y, center.getBlockZ() + z);
                if (b.getType() != Material.AIR && b.getType() != Material.BEDROCK) {
                    b.breakNaturally();
                    if (Math.abs(x) == rL || Math.abs(z) == rW) {
                        b.getWorld().spawnParticle(Particle.DRAGON_BREATH, b.getLocation().add(0.5, 0, 0.5), 1);
                    }
                }
            }
        }
    }
              }
