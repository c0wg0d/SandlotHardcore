package me.c0wg0d.sandlothardcore.event;

import me.c0wg0d.sandlothardcore.SandlotHardcore;
import me.c0wg0d.sandlothardcore.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityEvents implements Listener {

    private final SandlotHardcore plugin;

    public EntityEvents(SandlotHardcore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity e = event.getEntity();

        Float radius = (float) Settings.CREEPER_EXPLOSION_RADIUS;

        if (e.getType().equals(EntityType.CREEPER)) {
            event.getLocation().getWorld().createExplosion(event.getLocation(), radius, Settings.CREEPER_EXPLOSION_FIRE);
            Bukkit.broadcastMessage("Create explosion with radius " + radius + "!");
        }
    }

}
