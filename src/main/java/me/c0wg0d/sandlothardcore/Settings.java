package me.c0wg0d.sandlothardcore;

import org.bukkit.configuration.file.FileConfiguration;

public class Settings {

    public static String WORLD_NAME;
    public static String WORLD_NAME_NETHER;
    public static String WORLD_NAME_THE_END;
    public static double CREEPER_EXPLOSION_RADIUS;
    public static boolean CREEPER_EXPLOSION_FIRE;
    public static boolean DISABLE_GOLDEN_APPLE;
    public static boolean DISABLE_REGEN;
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

        try {
            CREEPER_EXPLOSION_RADIUS = config.getInt("options.creeper-explosion-radius");
            if (CREEPER_EXPLOSION_RADIUS < 0) {
                CREEPER_EXPLOSION_RADIUS = 0;
            }
        } catch (Exception e) {
            CREEPER_EXPLOSION_RADIUS = 3;
        }

        CREEPER_EXPLOSION_FIRE = config.getBoolean("options.creeper-explosion-fire");
        DISABLE_GOLDEN_APPLE = config.getBoolean("options.disable-golden-apple-recipe");
        DISABLE_REGEN = config.getBoolean("options.disable-regen");

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
