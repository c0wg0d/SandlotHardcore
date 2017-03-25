package me.c0wg0d.sandlothardcore.event;

import me.c0wg0d.sandlothardcore.SandlotHardcore;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityEvents implements Listener {

    private final SandlotHardcore plugin;
    private final double creeperExplosionRadius;
    private final boolean creeperExplosionFire;

    public EntityEvents(SandlotHardcore plugin) {
        this.plugin = plugin;
        creeperExplosionRadius = plugin.getConfig().getDouble("options.creeper-explosion-radius", 3.0);
        creeperExplosionFire = plugin.getConfig().getBoolean("options.creeper-explosion-fire", true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity e = event.getEntity();

        Float radius = (float)creeperExplosionRadius;

        if (e.getType().equals(EntityType.CREEPER)) {
            event.getLocation().getWorld().createExplosion(event.getLocation(), radius, creeperExplosionFire);
        }
    }

}
