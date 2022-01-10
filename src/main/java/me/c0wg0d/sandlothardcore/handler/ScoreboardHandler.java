package me.c0wg0d.sandlothardcore.handler;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardHandler {

    private static final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    private static Scoreboard scoreboard;
    private static Objective deathsObjective;
    private static Objective pointsObjective;

    public static void createScoreboard() {
        assert scoreboardManager != null;
        scoreboard = scoreboardManager.getNewScoreboard();
        deathsObjective = scoreboard.registerNewObjective("deaths", "deathCount", "Deaths");
        deathsObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);

        pointsObjective = scoreboard.registerNewObjective("points", "dummy", "Points");
        pointsObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public static void addPlayer(Player player) {
        Score deathsScore = deathsObjective.getScore(player.getName());
        deathsScore.setScore(0);
        Score pointsScore = pointsObjective.getScore(player.getName());
        pointsScore.setScore(0);
        player.setScoreboard(scoreboard);
    }

    public static void removePlayer(Player player) {
        for (String entry : scoreboard.getEntries()) {
            if (entry.contains(player.getName()) || (player != null && !player.isOnline()))
                scoreboard.resetScores(entry);
        }
    }

    public static void updateScores() {
        for(Player online : Bukkit.getOnlinePlayers()) {
            // Update deaths
            int deaths = online.getStatistic(Statistic.DEATHS);
            Score deathsScore = deathsObjective.getScore(online.getName());
            deathsScore.setScore(deaths);

            // Update points
            Score pointsScore = pointsObjective.getScore(online.getName());
            int score = (int) Math.floor(VaultHandler.getEcon().getBalance(online));

            // Set their score to % of total based on number of deaths, increments of 10%, max 10 deaths
            int deathsPenalty = (10 - deaths) * 10;
            if (deaths >= 10) {
                deathsPenalty = 1;
            }
            score = score * deathsPenalty;

            pointsScore.setScore(score);
        }
    }


}
