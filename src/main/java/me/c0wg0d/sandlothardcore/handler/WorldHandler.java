package me.c0wg0d.sandlothardcore.handler;

import me.c0wg0d.sandlothardcore.SandlotHardcore;
import me.c0wg0d.sandlothardcore.Settings;
import org.bukkit.GameRule;

public class WorldHandler {
    public static void setAlwaysNight() {
        if(Settings.ALWAYS_NIGHT) {
            SandlotHardcore.getHardcoreWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            SandlotHardcore.getHardcoreWorld().setTime(18000);
        }
    }

    public static void setAlwaysRaining() {
        if(Settings.ALWAYS_RAINING) {
            SandlotHardcore.getHardcoreWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            SandlotHardcore.getHardcoreWorld().setStorm(true);
            SandlotHardcore.getHardcoreWorld().setThundering(true);
            SandlotHardcore.getHardcoreWorld().setWeatherDuration(10000);
        }
    }

    public static void setDoFireTick() {
        if(Settings.DO_FIRE_TICK) {
            SandlotHardcore.getHardcoreWorld().setGameRule(GameRule.DO_FIRE_TICK, true);
        }
        else {
            SandlotHardcore.getHardcoreWorld().setGameRule(GameRule.DO_FIRE_TICK, false);
        }
    }
}
