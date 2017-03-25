package me.c0wg0d.sandlothardcore.player;

import com.google.common.cache.*;
import dk.lockfuglsang.minecraft.util.TimeUtil;
import me.c0wg0d.sandlothardcore.SandlotHardcore;
import me.c0wg0d.sandlothardcore.uuid.PlayerDB;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Holds the active players
 */
public class PlayerLogic {
    private static final Logger log = Logger.getLogger(PlayerLogic.class.getName());
    private static final PlayerInfo UNKNOWN_PLAYER = new PlayerInfo(PlayerDB.UNKNOWN_PLAYER_NAME, PlayerDB.UNKNOWN_PLAYER_UUID);
    private final LoadingCache<UUID, PlayerInfo> playerCache;
    private final SandlotHardcore plugin;
    private final BukkitTask saveTask;
    private final PlayerDB playerDB;

    public PlayerLogic(SandlotHardcore plugin) {
        this.plugin = plugin;
        playerDB = plugin.getPlayerDB();
        playerCache = CacheBuilder
                .from(plugin.getConfig().getString("options.advanced.playerCache", "maximumSize=200,expireAfterWrite=15m,expireAfterAccess=10m"))
                .removalListener(new RemovalListener<UUID, PlayerInfo>() {
                    @Override
                    public void onRemoval(RemovalNotification<UUID, PlayerInfo> removal) {
                        log.fine("Removing player-info for " + removal.getKey() + " from cache");
                        PlayerInfo playerInfo = removal.getValue();
                        if (playerInfo.isDirty()) {
                            playerInfo.saveToFile();
                        }
                    }
                })
                .build(new CacheLoader<UUID, PlayerInfo>() {
                           @Override
                           public PlayerInfo load(UUID s) throws Exception {
                               log.fine("Loading player-info from " + s + " into cache!");
                               return loadPlayerData(s);
                           }
                       }
                );
        long every = TimeUtil.secondsAsMillis(plugin.getConfig().getInt("options.advanced.player.saveEvery", 2*60));
        saveTask = plugin.async(new Runnable() {
            @Override
            public void run() {
                saveDirtyToFiles();
            }
        }, every, every);
    }

    private void saveDirtyToFiles() {
        // asMap.values() should NOT touch the cache.
        for (PlayerInfo pi : playerCache.asMap().values()) {
            if (pi.isDirty()) {
                pi.saveToFile();
            }
        }
    }

    private PlayerInfo loadPlayerData(UUID uuid) {
        if (UNKNOWN_PLAYER.getUniqueId().equals(uuid)) {
            return UNKNOWN_PLAYER;
        }
        return loadPlayerData(uuid, playerDB.getName(uuid));
    }

    private PlayerInfo loadPlayerData(UUID playerUUID, String playerName) {
        if (playerUUID == null) {
            playerUUID = PlayerDB.UNKNOWN_PLAYER_UUID;
        }
        if (playerName == null) {
            playerName = "__UNKNOWN__";
        }
        log.log(Level.FINER, "Loading player data for " + playerUUID + "/" + playerName);

        final PlayerInfo playerInfo = new PlayerInfo(playerName, playerUUID);

        final Player onlinePlayer = SandlotHardcore.getInstance().getPlayerDB().getPlayer(playerUUID);
        return playerInfo;
    }

    public PlayerInfo getPlayerInfo(Player player) {
        return getPlayerInfo(player.getName());
    }

    public PlayerInfo getPlayerInfo(String playerName) {
        UUID uuid = playerDB.getUUIDFromName(playerName);
        return getPlayerInfo(uuid);
    }

    public PlayerInfo getPlayerInfo(UUID uuid) {
        try {
            return playerCache.get(uuid);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e); // Escalate - we need it in the server log
        }
    }

    public void loadPlayerDataAsync(final Player player) {
        plugin.async(new Runnable() {
            @Override
            public void run() {
                playerCache.refresh(player.getUniqueId());
            }
        });
    }

    public void removeActivePlayer(PlayerInfo pi) {
        playerCache.invalidate(pi.getPlayerName());
    }

    public void shutdown() {
        saveTask.cancel();
        flushCache();
    }

    public long flushCache() {
        long size = playerCache.size();
        playerCache.invalidateAll();
        return size;
    }

    public int getSize() {
        String[] list = plugin.directoryPlayers.list();
        return list != null ? list.length : 0;
    }
}
