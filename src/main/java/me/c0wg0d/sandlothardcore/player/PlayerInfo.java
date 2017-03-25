package me.c0wg0d.sandlothardcore.player;

import dk.lockfuglsang.minecraft.file.FileUtil;
import dk.lockfuglsang.minecraft.util.LocationUtil;
import dk.lockfuglsang.minecraft.yml.YmlConfiguration;
import me.c0wg0d.sandlothardcore.SandlotHardcore;
import me.c0wg0d.sandlothardcore.util.UUIDUtil;
import me.c0wg0d.sandlothardcore.uuid.PlayerDB;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerInfo implements Serializable, PlayerInfoInterface {
    private static final String CN = PlayerInfo.class.getName();
    private static final Logger log = Logger.getLogger(CN);
    private static final long serialVersionUID = 1L;
    private static final int YML_VERSION = 1;
    private String playerName;
    private String displayName;
    private UUID uuid;

    private Location homeLocation;

    private volatile FileConfiguration playerData;
    private File playerConfigFile;

    private boolean dirty = false;

    public PlayerInfo(String currentPlayerName, UUID playerUUID) {
        this.uuid = playerUUID;
        this.playerName = currentPlayerName;
        // Prefer UUID over Name
        playerConfigFile = new File(SandlotHardcore.getInstance().directoryPlayers, UUIDUtil.asString(playerUUID) + ".yml");
        File nameFile = new File(SandlotHardcore.getInstance().directoryPlayers, playerName + ".yml");
        if (!playerConfigFile.exists() && nameFile.exists() && !currentPlayerName.equals(PlayerDB.UNKNOWN_PLAYER_NAME)) {
            nameFile.renameTo(playerConfigFile);
        }
        playerData = new YmlConfiguration();
        if (playerConfigFile.exists()) {
            FileUtil.readConfig(playerData, playerConfigFile);
        }
        loadPlayer();
    }

    @Override
    public Player getPlayer() {
        Player player = null;
        if (uuid != null) {
            player = SandlotHardcore.getInstance().getPlayerDB().getPlayer(uuid);
        }
        if (player == null && playerName != null) {
            player = SandlotHardcore.getInstance().getPlayerDB().getPlayer(playerName);
        }
        return player;
    }

    @Override
    public String getPlayerName() {
        return this.playerName;
    }

    public void setHomeLocation(final Location l) {
        this.homeLocation = l != null ? l.clone() : null;
    }

    @Override
    public boolean getHasHome() {
        return getHomeLocation() != null;
    }

    @Override
    public Location getHomeLocation() {
        return homeLocation != null ? homeLocation.clone() : null;
    }

    @Override
    public String getDisplayName() {
        return displayName != null ? displayName : playerName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    private void setupPlayer() {
        FileConfiguration playerConfig = playerData;
        ConfigurationSection pSection = playerConfig.createSection("player");
        pSection.set("homeX", 0);
        pSection.set("homeY", 0);
        pSection.set("homeZ", 0);
        pSection.set("homeYaw", 0);
        pSection.set("homePitch", 0);
    }

    private PlayerInfo loadPlayer() {
        if (!playerData.contains("player.homeY") || playerData.getInt("player.homeY", 0) == 0) {
            this.homeLocation = null;
            createPlayerConfig();
            return this;
        }
        try {
            this.displayName = playerData.getString("player.displayName", playerName);
            this.uuid = UUIDUtil.fromString(playerData.getString("player.uuid", null));
            this.homeLocation = new Location(SandlotHardcore.getHardcoreWorld(),
                    playerData.getInt("player.homeX") + 0.5, playerData.getInt("player.homeY") + 0.2, playerData.getInt("player.homeZ") + 0.5,
                    (float) playerData.getDouble("player.homeYaw", 0.0),
                    (float) playerData.getDouble("player.homePitch", 0.0));

            log.exiting(CN, "loadPlayer");
            return this;
        } catch (Exception e) {
            log.log(Level.INFO, "Returning null while loading, not good!");
            return null;
        }
    }

    private void createPlayerConfig() {
        log.log(Level.FINER, "Creating new player config!");
        setupPlayer();
    }

    public FileConfiguration getConfig() {
        return playerData;
    }

    public void save() {
        dirty = true;
        if (!playerConfigFile.exists()) {
            saveToFile();
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void saveToFile() {
        log.fine("Saving player-info for " + playerName + " to file");
        // TODO: 11/05/2015 - R4zorax: Instead of saving directly, schedule it for later...
        log.entering(CN, "save", playerName);
        if (playerData == null) {
            log.log(Level.INFO, "Can't save player data! (" + playerName + ", " + uuid + ", " + playerConfigFile + ")");
            return;
        }
        FileConfiguration playerConfig = playerData;
        playerConfig.set("version", YML_VERSION);
        playerConfig.set("player.displayName", displayName);
        playerConfig.set("player.uuid", UUIDUtil.asString(uuid));
        Location home = this.getHomeLocation();
        if (home != null) {
            playerConfig.set("player.homeX", home.getBlockX());
            playerConfig.set("player.homeY", home.getBlockY());
            playerConfig.set("player.homeZ", home.getBlockZ());
            playerConfig.set("player.homeYaw", home.getYaw());
            playerConfig.set("player.homePitch", home.getPitch());
        } else {
            playerConfig.set("player.homeX", 0);
            playerConfig.set("player.homeY", 0);
            playerConfig.set("player.homeZ", 0);
            playerConfig.set("player.homeYaw", 0);
            playerConfig.set("player.homePitch", 0);
        }
        try {
            playerConfig.save(playerConfigFile);
            log.log(Level.FINEST, "Player data saved!");
        } catch (IOException ex) {
            SandlotHardcore.getInstance().getLogger().log(Level.SEVERE, "Could not save config to " + playerConfigFile, ex);
        }
        log.exiting(CN, "save");
        dirty = false;
    }

    @Override
    public String toString() {
        // TODO: 01/06/2015 - R4zorax: use i18n.tr
        String str = "\u00a7bPlayer Info:\n";
        str += ChatColor.GRAY + "  - name: " + ChatColor.DARK_AQUA + getPlayerName() + "\n";
        str += ChatColor.GRAY + "  - nick: " + ChatColor.DARK_AQUA + getDisplayName() + "\n";
        str += ChatColor.GRAY + "  - home: " + ChatColor.DARK_AQUA + LocationUtil.asString(getHomeLocation()) + "\n";
        return str;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    public long getLastSaved() {
        return playerConfigFile.lastModified();
    }
}
