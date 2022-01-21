package me.c0wg0d.sandlothardcore.event;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import me.c0wg0d.sandlothardcore.SandlotHardcore;
import me.c0wg0d.sandlothardcore.Settings;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;

public class VillagerEvents implements Listener {

    private final SandlotHardcore plugin;

    public VillagerEvents(SandlotHardcore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.VILLAGER
                && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CURED) {
            Entity entity = event.getEntity();
            entity.getPersistentDataContainer().set(SandlotHardcore.getKeepVillagerKey(), PersistentDataType.INTEGER, 1);
        }
    }

    @EventHandler
    public void onEntityAddToWorldEvent(EntityAddToWorldEvent event) {
        if (event.getEntityType() == EntityType.VILLAGER) {

            if (!Settings.DISABLE_VILLAGER_SPAWNS) {
                return;
            }

            Ageable entity = (Ageable) event.getEntity();

            // First check if this is a baby, and set the tag to keep it
            if (event.getEntity() instanceof Ageable) {
                if (!entity.isAdult() && Settings.ALLOW_BABY_VILLAGERS) {
                    entity.getPersistentDataContainer().set(SandlotHardcore.getKeepVillagerKey(), PersistentDataType.INTEGER, 1);
                }
            }

            // Keep villager if the tag is set
            if (entity.getPersistentDataContainer().has(SandlotHardcore.getKeepVillagerKey(), PersistentDataType.INTEGER)
                    && entity.getPersistentDataContainer().get(SandlotHardcore.getKeepVillagerKey(), PersistentDataType.INTEGER) == 1
                    && (Settings.ALLOW_BABY_VILLAGERS || Settings.ALLOW_CURED_VILLAGERS)) {
                return;
            }
            entity.remove();
        }
    }
}
