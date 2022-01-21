package me.c0wg0d.sandlothardcore;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Settings {

    public static String WORLD_NAME;
    public static String WORLD_NAME_NETHER;
    public static String WORLD_NAME_THE_END;
    public static boolean ALWAYS_NIGHT;
    public static boolean ALWAYS_RAINING;
    public static boolean DO_FIRE_TICK;
    public static boolean MOON_GRAVITY;
    public static boolean LEAVES_DO_NOT_DROP_SAPLINGS;
    public static double CREEPER_EXPLOSION_RADIUS;
    public static boolean CREEPER_EXPLOSION_FIRE;
    public static boolean CREEPERS_ALWAYS_CHARGED;
    public static boolean SPIDERS_ALWAYS_INVISIBLE;
    public static boolean SKELETONS_ALWAYS_HAVE_ENCHANTED_BOW;
    public static boolean ZOMBIES_ALWAYS_HAVE_IRON_ARMOR;
    public static int ZOMBIE_TOWER_HEIGHT;
    public static boolean DISABLE_GOLDEN_APPLE;
    public static boolean DISABLE_REGEN;
    public static int DISABLE_TORCHES_BELOW_Y;
    public static List<String> DISABLE_MOBS;
    public static boolean DISABLE_PLAYER_JUMPING;
    public static boolean DISABLE_VILLAGER_SPAWNS;
    public static boolean ALLOW_CURED_VILLAGERS;
    public static boolean ALLOW_BABY_VILLAGERS;
    public static boolean DISABLE_BLAZE_SPAWNERS;
    public static int INVULNERABLE_TIME_AFTER_DEATH_IN_SECONDS;
    public static boolean RANDOM_RESPAWN_ON_FIRST_JOIN;
    public static boolean RANDOM_RESPAWN_AFTER_DEATH;
    public static int RANDOM_RESPAWN_MIN_X;
    public static int RANDOM_RESPAWN_MAX_X;
    public static int RANDOM_RESPAWN_MIN_Z;
    public static int RANDOM_RESPAWN_MAX_Z;

    public void loadPluginConfig(SandlotHardcore plugin) {

        FileConfiguration config = plugin.getConfig();
        WORLD_NAME = config.getString("options.world-name");
        WORLD_NAME_NETHER = WORLD_NAME + "_nether";
        WORLD_NAME_THE_END = WORLD_NAME + "_the_end";
        ALWAYS_NIGHT = config.getBoolean("options.always-night");
        ALWAYS_RAINING = config.getBoolean("options.always-raining");
        DO_FIRE_TICK = config.getBoolean("options.do-fire-tick");
        MOON_GRAVITY = config.getBoolean("options.moon-gravity");
        LEAVES_DO_NOT_DROP_SAPLINGS = config.getBoolean("options.leaves-do-not-drop-saplings");

        try {
            CREEPER_EXPLOSION_RADIUS = config.getInt("options.creeper-explosion-radius");
            if (CREEPER_EXPLOSION_RADIUS < 0) {
                CREEPER_EXPLOSION_RADIUS = 0;
            }
        } catch (Exception e) {
            CREEPER_EXPLOSION_RADIUS = 3;
        }

        CREEPER_EXPLOSION_FIRE = config.getBoolean("options.creeper-explosion-fire");
        CREEPERS_ALWAYS_CHARGED = config.getBoolean("options.creepers-always-charged");
        SPIDERS_ALWAYS_INVISIBLE = config.getBoolean("options.spiders-always-invisible");
        SKELETONS_ALWAYS_HAVE_ENCHANTED_BOW = config.getBoolean("options.skeletons-always-have-enchanted-bow");
        ZOMBIES_ALWAYS_HAVE_IRON_ARMOR = config.getBoolean("options.zombies-always-have-iron-armor");

        try {
            ZOMBIE_TOWER_HEIGHT = config.getInt("options.zombie-tower-height");
            if (ZOMBIE_TOWER_HEIGHT > 5) {
                ZOMBIE_TOWER_HEIGHT = 5;
            }
        } catch (Exception e) {
            ZOMBIE_TOWER_HEIGHT = 0;
        }
        
        DISABLE_GOLDEN_APPLE = config.getBoolean("options.disable-golden-apple-recipe");
        DISABLE_REGEN = config.getBoolean("options.disable-regen");

        try {
            DISABLE_TORCHES_BELOW_Y = config.getInt("options.disable-torches-below-y");
        } catch (Exception e) {
            DISABLE_TORCHES_BELOW_Y = 0;
        }

        DISABLE_MOBS = config.getStringList("options.disable-mobs");
        DISABLE_PLAYER_JUMPING = config.getBoolean("options.disable-player-jumping");
        DISABLE_VILLAGER_SPAWNS = config.getBoolean("options.disable-villager-spawns");
        ALLOW_CURED_VILLAGERS = config.getBoolean("options.allow-cured-villagers");
        ALLOW_BABY_VILLAGERS = config.getBoolean("options.allow-baby-villagers");
        DISABLE_BLAZE_SPAWNERS = config.getBoolean("options.disable-blaze-spawners");

        try {
            INVULNERABLE_TIME_AFTER_DEATH_IN_SECONDS = config.getInt("options.invulnerable-time-after-death-in-seconds");
        } catch (Exception e) {
            INVULNERABLE_TIME_AFTER_DEATH_IN_SECONDS = 60;
        }

        RANDOM_RESPAWN_ON_FIRST_JOIN = config.getBoolean("options.random-respawn-on-first-join");
        RANDOM_RESPAWN_AFTER_DEATH = config.getBoolean("options.random-respawn");

        try {
            RANDOM_RESPAWN_MIN_X = config.getInt("options.random-respawn-limits.min-x", -1000);
            RANDOM_RESPAWN_MAX_X = config.getInt("options.random-respawn-limits.max-x", 1000);
            RANDOM_RESPAWN_MIN_Z = config.getInt("options.random-respawn-limits.min-z", -1000);
            RANDOM_RESPAWN_MAX_Z = config.getInt("options.random-respawn-limits.max-z", 1000);
        } catch (Exception e) {
            CREEPER_EXPLOSION_RADIUS = 3;
        }
    }

}
