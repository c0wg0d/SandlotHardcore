package me.c0wg0d.sandlothardcore.event;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import dk.lockfuglsang.minecraft.util.TimeUtil;
import me.c0wg0d.sandlothardcore.SandlotHardcore;
import me.c0wg0d.sandlothardcore.Settings;
import me.c0wg0d.sandlothardcore.handler.ScoreboardHandler;
import me.c0wg0d.sandlothardcore.util.BlockUtil;
import me.c0wg0d.sandlothardcore.util.LocationUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class PlayerEvents implements Listener {

    private final SandlotHardcore plugin;

    public PlayerEvents(SandlotHardcore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.getInventory().contains(Material.COMPASS)) {
            player.getInventory().addItem(new ItemStack(Material.COMPASS));
        }

        player.setInvulnerable(false);

        ScoreboardHandler.addPlayer(player);
        ScoreboardHandler.updateScores();

        if (Settings.MOON_GRAVITY) {
            PotionEffect slowFallingEffect = new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 2, true, false, false);
            PotionEffect jumpBoostEffect = new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 4, true, false, false);
            PotionEffect slownessEffect = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1, true, false, false);
            player.addPotionEffect(slowFallingEffect);
            player.addPotionEffect(jumpBoostEffect);
            player.addPotionEffect(slownessEffect);
        }

        if (player.hasPlayedBefore()) {
            return;
        }

        if (Settings.RANDOM_RESPAWN_ON_FIRST_JOIN) {
            Location respawnLocation = LocationUtil.findNearestSafeLocation(randomRespawnLocation(), null);
            int tries = 100;
            while (respawnLocation == null) {
                respawnLocation = LocationUtil.findNearestSafeLocation(randomRespawnLocation(), null);
                if (tries == 0) {
                    break;
                }
                tries--;
            }
            if (respawnLocation == null) {
                player.sendMessage(ChatColor.BLUE + "No suitable spawn location found, sending you to world spawn");
                return;
            }
            player.teleport(respawnLocation);
        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.setInvulnerable(false);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        BlockUtil.putHeadOnStake(player);
        ScoreboardHandler.updateScores();

        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            onlinePlayer.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
        }

        String deathMessage = ChatColor.RED + player.getDisplayName() + ChatColor.GRAY + " just died at "
                + player.getLocation().getBlockX() + ", "
                + player.getLocation().getBlockY() + ", "
                + player.getLocation().getBlockZ();

        if (player.getLocation().getWorld().getName().equals(Settings.WORLD_NAME_NETHER)) {
            deathMessage += " in the nether";
        }

        if (player.getLocation().getWorld().getName().equals(Settings.WORLD_NAME_THE_END)) {
            deathMessage += " in the end";
        }

        deathMessage += " :(";
        Bukkit.broadcast(Component.text(deathMessage));
    }

    // Disable health regen from eating and beacons
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (!Settings.DISABLE_REGEN) {
            return;
        }
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED
                || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN
                || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC_REGEN) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        ScoreboardHandler.updateScores();

        Player player = event.getPlayer();
        player.getInventory().addItem(new ItemStack(Material.COMPASS));
        player.setInvulnerable(true);
        plugin.async(new Runnable() {
            @Override
            public void run() {
                player.setInvulnerable(false);
                player.sendMessage(ChatColor.BLUE + "Your invulnerability has worn out. Be careful out there!");
            }
        }, TimeUtil.secondsAsMillis(Settings.INVULNERABLE_TIME_AFTER_DEATH_IN_SECONDS));

        if (!Settings.RANDOM_RESPAWN_AFTER_DEATH) {
            return;
        }
        Location respawnLocation = LocationUtil.findNearestSafeLocation(randomRespawnLocation(), null);
        int tries = 10;
        while (respawnLocation == null) {
            respawnLocation = LocationUtil.findNearestSafeLocation(randomRespawnLocation(), null);
            if (tries == 0) {
                break;
            }
            tries--;
        }
        if (respawnLocation == null) {
            player.sendMessage(ChatColor.BLUE + "No suitable spawn location found, sending you to world spawn");
            return;
        }
        event.setRespawnLocation(respawnLocation);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ScoreboardHandler.removePlayer(player);
    }

    @EventHandler
    public void onCraftEvent(CraftItemEvent event) {
        if (Settings.DISABLE_GOLDEN_APPLE && event.getRecipe().getResult().getType() == Material.GOLDEN_APPLE) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlayerSetSpawnEvent(PlayerSetSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBedEnterEvent(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (player.isSleeping()) {
                player.wakeup(false);
            }
        }, 1L);
    }

    @EventHandler
    public void onPlayerJumpEvent(PlayerJumpEvent event) {
        if (Settings.DISABLE_PLAYER_JUMPING) {
            event.setCancelled(true);
            return;
        }
    }

    public Location randomRespawnLocation() {
        Random random = new Random();
        int x = random.nextInt(Settings.RANDOM_RESPAWN_MAX_X - Settings.RANDOM_RESPAWN_MIN_X) + Settings.RANDOM_RESPAWN_MIN_X;
        int z = random.nextInt(Settings.RANDOM_RESPAWN_MAX_Z - Settings.RANDOM_RESPAWN_MIN_Z) + Settings.RANDOM_RESPAWN_MIN_Z;
        int y = plugin.getHardcoreWorld().getHighestBlockYAt(x, z);
        return new Location(plugin.getHardcoreWorld(), x, y, z);
    }
}
