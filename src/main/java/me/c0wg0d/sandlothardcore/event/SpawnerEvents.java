package me.c0wg0d.sandlothardcore.event;

import me.c0wg0d.sandlothardcore.SandlotHardcore;
import me.c0wg0d.sandlothardcore.Settings;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

public class SpawnerEvents implements Listener {

    private final SandlotHardcore plugin;

    public SpawnerEvents(SandlotHardcore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawnerSpawnEvent(SpawnerSpawnEvent event) {
        if(Settings.DISABLE_BLAZE_SPAWNERS && event.getEntityType() == EntityType.BLAZE) {
            event.setCancelled(true);
            return;
        }
    }
}
