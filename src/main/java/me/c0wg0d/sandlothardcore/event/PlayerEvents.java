package me.c0wg0d.sandlothardcore.event;

import dk.lockfuglsang.minecraft.util.TimeUtil;
import me.c0wg0d.sandlothardcore.SandlotHardcore;
import me.c0wg0d.sandlothardcore.handler.ScoreboardHandler;
import me.c0wg0d.sandlothardcore.util.BlockUtil;
import me.c0wg0d.sandlothardcore.util.LocationUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

import static me.c0wg0d.sandlothardcore.handler.ScoreboardHandler.updateScoreboards;

public class PlayerEvents implements Listener {

    private final SandlotHardcore plugin;
    private final int invulnerableTime;
    private final boolean disableRegen;
    private final boolean randomRespawn;
    private final int minX;
    private final int maxX;
    private final int minZ;
    private final int maxZ;

    public PlayerEvents(SandlotHardcore plugin) {
        this.plugin = plugin;
        invulnerableTime = plugin.getConfig().getInt("options.invulnerable-time-after-death-in-seconds", 60);
        disableRegen = plugin.getConfig().getBoolean("options.disable-regen", true);
        randomRespawn = plugin.getConfig().getBoolean("options.random-respawn", true);
        minX = plugin.getConfig().getInt("options.random-respawn-limits.min-x", 100);
        maxX = plugin.getConfig().getInt("options.random-respawn-limits.max-x", 100);
        minZ = plugin.getConfig().getInt("options.random-respawn-limits.min-z", 100);
        maxZ = plugin.getConfig().getInt("options.random-respawn-limits.max-z", 100);

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if(!p.getInventory().contains(Material.COMPASS)) {
            p.getInventory().addItem(new ItemStack(Material.COMPASS));
        }

        p.setInvulnerable(false);

        updateScoreboards();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        p.setInvulnerable(false);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        Player p = e.getEntity();

        BlockUtil.putHeadOnStake(p);
        updateScoreboards();

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_THUNDER, 1, 1);
        }

        String deathMessage = ChatColor.RED + p.getDisplayName() + ChatColor.GRAY + " just died at "
                + p.getLocation().getBlockX() + ", "
                + p.getLocation().getBlockY() + ", "
                + p.getLocation().getBlockZ();

        if(p.getLocation().getWorld().getName().equals(plugin.getHardcoreWorldNether())) {
            deathMessage += " in the nether";
        }

        if(p.getLocation().getWorld().getName().equals(plugin.getHardcoreWorldTheEnd())) {
            deathMessage += " in the end";
        }

        deathMessage +=  " :(";
        Bukkit.broadcastMessage(deathMessage);
    }

    // Disable health regen from eating and beacons
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if(!disableRegen) {
            return;
        }
        if(event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED
                || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN
                || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC_REGEN) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawnEvent(PlayerRespawnEvent e) {
        updateScoreboards();

        Player p = e.getPlayer();
        p.getInventory().addItem(new ItemStack(Material.COMPASS));
        p.setInvulnerable(true);
        plugin.async(new Runnable() {
            @Override
            public void run() {
                p.setInvulnerable(false);
                p.sendMessage(ChatColor.BLUE + "Your invulnerability has worn out. Be careful out there!");
            }
        }, TimeUtil.secondsAsMillis(invulnerableTime));

        if(!randomRespawn) {
            return;
        }
        Location respawnLocation = LocationUtil.findNearestSafeLocation(randomRespawnLocation(), null);
        int tries = 10;
        while(respawnLocation == null) {
            respawnLocation = LocationUtil.findNearestSafeLocation(randomRespawnLocation(), null);
            if(tries == 0) {
                break;
            }
            tries--;
        }
        if(respawnLocation == null) {
            p.sendMessage(ChatColor.BLUE + "No suitable spawn location found, sending you to world spawn");
            return;
        }
        e.setRespawnLocation(respawnLocation);
    }

    public Location randomRespawnLocation() {
        Random random = new Random();
        int x = random.nextInt(maxX - minX + minX);
        int z = random.nextInt(maxZ - minZ + minZ);
        int y = plugin.getHardcoreWorld().getHighestBlockYAt(x, z);
        return new Location(plugin.getHardcoreWorld(), x, y, z);
    }
}
