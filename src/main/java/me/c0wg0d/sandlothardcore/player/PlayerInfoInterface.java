package me.c0wg0d.sandlothardcore.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface PlayerInfoInterface {

    Player getPlayer();

    String getPlayerName();

    UUID getUniqueId();

    boolean getHasHome();

    Location getHomeLocation();

    String getDisplayName();
}
