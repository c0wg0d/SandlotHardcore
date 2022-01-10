package me.c0wg0d.sandlothardcore.event;

import me.c0wg0d.sandlothardcore.SandlotHardcore;
import me.c0wg0d.sandlothardcore.handler.ScoreboardHandler;
import me.c0wg0d.sandlothardcore.player.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class InventoryEvents implements Listener {

    private final SandlotHardcore plugin;

    public InventoryEvents(SandlotHardcore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onPlayerUse(PlayerInteractEvent event) {
        ScoreboardHandler.updateScores();

        Player player = event.getPlayer();

        if(player.getInventory().getItemInMainHand().getType() == Material.COMPASS){
            if(player.isSneaking()) {
                // set home to current location
                plugin.homeSet(player);
            }
            else {
                // set compass direction to home location
                PlayerInfo pi = plugin.getPlayerInfo(player);
                if(pi.getHasHome()) {
                    player.setCompassTarget(pi.getHomeLocation());
                    player.sendMessage(ChatColor.BLUE + "Your compass is now pointing to your home at "
                        + pi.getHomeLocation().getBlockX() + ", "
                        + pi.getHomeLocation().getBlockY() + ", "
                        + pi.getHomeLocation().getBlockZ());
                }
                else {
                    player.sendMessage(ChatColor.YELLOW + "You have not set a home location");
                }
            }
        }
    }
}
