package me.c0wg0d.sandlothardcore;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class Settings {
    private static final Logger log = Logger.getLogger(Settings.class.getName());
    public static String optionWorldName;
    public static String optionWorldNameNether;
    public static String optionWorldNameTheEnd;
    public static int optionScoreboardUpdateIntervalInSeconds;
    public static double optionCreeperExplosionRadius;
    public static boolean optionCreeperExplosionFire;
    public static boolean optionDisableGoldenAppleRecipe;
    public static boolean optionDisableRegen;

    public static boolean loadPluginConfig(FileConfiguration config) {
        boolean changed = false;

        optionWorldName = config.getString("options.world-name");
        optionWorldNameNether = optionWorldName + "_nether";
        optionWorldNameTheEnd = optionWorldName + "_the_end";

        try {
            optionScoreboardUpdateIntervalInSeconds = config.getInt("options.scoreboard-update-interval-in-seconds");
            if (optionScoreboardUpdateIntervalInSeconds < 0) {
                optionScoreboardUpdateIntervalInSeconds = 60;
            }
        } catch (Exception e) {
            optionScoreboardUpdateIntervalInSeconds = 60;
        }

        try {
            optionCreeperExplosionRadius = config.getInt("options.creeper-explosion-radius");
            if (optionCreeperExplosionRadius < 0) {
                optionCreeperExplosionRadius = 0;
            }
        } catch (Exception e) {
            optionCreeperExplosionRadius = 3;
        }

        optionCreeperExplosionFire = config.getBoolean("options.creeper-explosion-fire");
        optionDisableGoldenAppleRecipe = config.getBoolean("options.disable-golden-apple-recipe");
        optionDisableRegen = config.getBoolean("options.disable-regen");

        return changed;
    }

}
