package me.c0wg0d.sandlothardcore.event;

import com.hm.achievement.utils.PlayerAdvancedAchievementEvent;
import me.c0wg0d.sandlothardcore.SandlotHardcore;
import me.c0wg0d.sandlothardcore.handler.ScoreboardHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class AdvancedAchievementsEvents implements Listener {

    private final SandlotHardcore plugin;

    public AdvancedAchievementsEvents(SandlotHardcore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerAdvancedAchievementReception(PlayerAdvancedAchievementEvent event) {
        ScoreboardHandler.updateScores();
    }
}
