package me.c0wg0d.sandlothardcore.handler;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardHandler {

    public static void updateScoreboards() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective deathsObjective = scoreboard.registerNewObjective("deaths", "deathCount");
        deathsObjective.setDisplayName("Deaths");
        deathsObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);

        Objective pointsObjective = scoreboard.registerNewObjective("points", "dummy");
        pointsObjective.setDisplayName("Points");
        pointsObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for(Player online : Bukkit.getOnlinePlayers()) {
            int deaths = online.getStatistic(Statistic.DEATHS);
            Score deathsScore = deathsObjective.getScore(online.getName());
            deathsScore.setScore(deaths);
            online.setScoreboard(scoreboard);

            Score pointsScore = pointsObjective.getScore(online.getName());
            int score = (int) Math.floor(VaultHandler.getEcon().getBalance(online));

            // Set their score to % of total based on number of deaths, increments of 10%, max 10 deaths
            int deathsPenalty = (10 - deaths) * 10;
            if (deaths > 10) {
                deathsPenalty = 1;
            }
            score = score * deathsPenalty;

            pointsScore.setScore(score);
            online.setScoreboard(scoreboard);
        }
    }
}
