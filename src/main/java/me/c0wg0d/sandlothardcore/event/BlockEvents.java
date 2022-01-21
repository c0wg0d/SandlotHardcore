package me.c0wg0d.sandlothardcore.event;

import me.c0wg0d.sandlothardcore.SandlotHardcore;
import me.c0wg0d.sandlothardcore.Settings;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class BlockEvents implements Listener {

    private final SandlotHardcore plugin;

    public BlockEvents(SandlotHardcore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if(event.getBlockPlaced().getType() == Material.TORCH && event.getBlockPlaced().getLocation().getBlockY() <= Settings.DISABLE_TORCHES_BELOW_Y) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onLeavesDecayEvent(LeavesDecayEvent event) {
        if(Settings.LEAVES_DO_NOT_DROP_SAPLINGS) {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if(Settings.LEAVES_DO_NOT_DROP_SAPLINGS) {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
        }
    }
}
