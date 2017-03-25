package me.c0wg0d.sandlothardcore.event;

import me.c0wg0d.sandlothardcore.SandlotHardcore;
import me.c0wg0d.sandlothardcore.player.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import static me.c0wg0d.sandlothardcore.handler.ScoreboardHandler.updateScoreboards;

public class InventoryEvents implements Listener {

    private final SandlotHardcore plugin;

    public InventoryEvents(SandlotHardcore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onPlayerUse(PlayerInteractEvent event) {
        updateScoreboards();

        Player p = event.getPlayer();

        if(p.getInventory().getItemInMainHand().getType() == Material.COMPASS){
            if(p.isSneaking()) {
                // set home to current location
                plugin.homeSet(p);
            }
            else {
                // set compass direction to home location
                PlayerInfo pi = plugin.getPlayerInfo(p);
                if(pi.getHasHome()) {
                    p.setCompassTarget(pi.getHomeLocation());
                    p.sendMessage(ChatColor.BLUE + "Your compass is now pointing to your home at "
                        + pi.getHomeLocation().getBlockX() + ", "
                        + pi.getHomeLocation().getBlockY() + ", "
                        + pi.getHomeLocation().getBlockZ());
                }
                else {
                    p.sendMessage(ChatColor.YELLOW + "You have not set a home location");
                }
            }
        }
    }
}
