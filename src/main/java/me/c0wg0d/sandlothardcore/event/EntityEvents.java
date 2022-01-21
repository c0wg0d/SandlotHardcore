package me.c0wg0d.sandlothardcore.event;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import me.c0wg0d.sandlothardcore.SandlotHardcore;
import me.c0wg0d.sandlothardcore.Settings;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

public class EntityEvents implements Listener {

    private final SandlotHardcore plugin;

    public EntityEvents(SandlotHardcore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityExplode(EntityExplodeEvent event) {
        Float radius = (float) Settings.CREEPER_EXPLOSION_RADIUS;

        if (event.getEntity().getType() == EntityType.CREEPER) {
            event.getLocation().getWorld().createExplosion(event.getEntity(), radius, Settings.CREEPER_EXPLOSION_FIRE);
        }
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if (event.getEntity().getType() == EntityType.CREEPER && Settings.CREEPERS_ALWAYS_CHARGED) {
            Creeper creeper = (Creeper) event.getEntity();
            creeper.setPowered(true);
            return;
        }

        if (event.getEntity().getType() == EntityType.SPIDER && Settings.SPIDERS_ALWAYS_INVISIBLE) {
            Spider spider = (Spider) event.getEntity();
            spider.setInvisible(true);
            return;
        }

        if (event.getEntity().getType() == EntityType.SKELETON && Settings.SKELETONS_ALWAYS_HAVE_ENCHANTED_BOW) {
            Skeleton skeleton = (Skeleton) event.getEntity();
            ItemStack bow = new ItemStack(Material.BOW, 1);
            bow.addEnchantment(Enchantment.ARROW_DAMAGE, 5);
            skeleton.getEquipment().setItemInMainHand(bow);
            return;
        }

        if (event.getEntity().getType() == EntityType.ZOMBIE && Settings.ZOMBIES_ALWAYS_HAVE_IRON_ARMOR) {
            Zombie zombie = (Zombie) event.getEntity();
            zombie.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
            zombie.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            zombie.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            zombie.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
            return;
        }

        if (event.getEntity().getType() == EntityType.ZOMBIE && Settings.ZOMBIE_TOWER_HEIGHT > 0) {
            Zombie zombie = (Zombie) event.getEntity();
            if (zombie.getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
                return;
            }
            addBabyZombiesAsPassengers(zombie, Settings.ZOMBIE_TOWER_HEIGHT);
        }
    }

    @EventHandler
    public void onEntityAddToWorldEvent(EntityAddToWorldEvent event) {
        if (Settings.DISABLE_MOBS.contains(event.getEntity().getType().toString().toLowerCase())) {
            event.getEntity().remove();
            return;
        }
    }
    
    private void addBabyZombiesAsPassengers(Zombie zombie, int amount) {
        Zombie baby = (Zombie) zombie.getWorld().spawnEntity(zombie.getLocation(), EntityType.ZOMBIE);
        baby.setBaby();
        zombie.addPassenger(baby);
        if (amount > 0)
            addBabyZombiesAsPassengers(baby, amount - 1);
        return;
    }
}
