package me.c0wg0d.sandlothardcore;

import dk.lockfuglsang.minecraft.util.TimeUtil;
import me.c0wg0d.sandlothardcore.event.EntityEvents;
import me.c0wg0d.sandlothardcore.event.InventoryEvents;
import me.c0wg0d.sandlothardcore.event.PlayerEvents;
import me.c0wg0d.sandlothardcore.handler.RecipeHandler;
import me.c0wg0d.sandlothardcore.handler.VaultHandler;
import me.c0wg0d.sandlothardcore.player.PlayerInfo;
import me.c0wg0d.sandlothardcore.player.PlayerLogic;
import me.c0wg0d.sandlothardcore.util.PlayerHeadProvider;
import me.c0wg0d.sandlothardcore.uuid.BukkitPlayerDB;
import me.c0wg0d.sandlothardcore.uuid.FilePlayerDB;
import me.c0wg0d.sandlothardcore.uuid.MemoryPlayerDB;
import me.c0wg0d.sandlothardcore.uuid.PlayerDB;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import dk.lockfuglsang.minecraft.file.FileUtil;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

import static me.c0wg0d.sandlothardcore.handler.ScoreboardHandler.updateScoreboards;

public final class SandlotHardcore extends JavaPlugin {

    private static SandlotHardcore instance;
    public File directoryPlayers;
    private PlayerDB playerDB;
    private PlayerLogic playerLogic;
    private PlayerHeadProvider provider;

    public SandlotHardcore() {
    }

    public static SandlotHardcore getInstance() { return SandlotHardcore.instance; }

    @Override
    public void onEnable() {
        instance = this;
        registerEvents();
        reloadConfigs();
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    public PlayerInfo getPlayerInfo(Player player) {
        return playerLogic.getPlayerInfo(player);
    }

    public boolean homeSet(final Player player) {
        if (!player.getWorld().getName().equalsIgnoreCase(getHardcoreWorld().getName())) {
            player.sendMessage(ChatColor.YELLOW + "You can only set your home in the overworld");
            return true;
        }

        PlayerInfo playerInfo = playerLogic.getPlayerInfo(player);
        playerInfo.setHomeLocation(player.getLocation());
        playerInfo.save();
        player.sendMessage(ChatColor.BLUE + "Your home has been set to your current location");
        return true;
    }

    public void registerEvents() {
        HandlerList.unregisterAll(this);
        final PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new PlayerEvents(this), this);
        manager.registerEvents(new EntityEvents(this), this);
        manager.registerEvents(new InventoryEvents(this), this);
        RecipeHandler.register(this);
    }

    private void reloadConfigs() {
        createFolders();
        VaultHandler.setupEconomy();
        VaultHandler.setupPermissions();
        if (Settings.loadPluginConfig(getConfig())) {
            saveConfig();
        }
        reloadConfig();
        FileUtil.reload();

        String playerDbStorage = getConfig().getString("options.advanced.playerdb.storage", "yml");
        if (playerDbStorage.equalsIgnoreCase("yml")) {
            playerDB = new FilePlayerDB(this);
        } else if (playerDbStorage.equalsIgnoreCase("memory")) {
            playerDB = new MemoryPlayerDB(getConfig());
        } else {
            playerDB = new BukkitPlayerDB();
        }
        playerLogic = new PlayerLogic(this);

        registerEvents();
    }

    private void createFolders() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        directoryPlayers = new File(getDataFolder() + File.separator + "players");
        if (!directoryPlayers.exists()) {
            directoryPlayers.mkdirs();
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("shc")) {
            if(args.length == 0) {
                sender.sendMessage(ChatColor.DARK_PURPLE + "Sandlot Hardcore");
                return true;
            }
            else {
                if(args[0].equalsIgnoreCase("reload")) {
                    reloadConfigs();
                    sender.sendMessage(ChatColor.DARK_PURPLE + "Reloaded config!");
                    return true;
                }
            }
        }

        if (cmd.getName().equalsIgnoreCase("deaths") && (sender instanceof Player)) {
            if(args.length == 0) {
                sender.sendMessage(ChatColor.GRAY + "You have died " + ((Player) sender).getStatistic(Statistic.DEATHS) + " time(s).");
            }
            else if (args.length == 1) {
                Player playerLookup = Bukkit.getPlayer(args[0]);
                if(playerLookup == null) {
                    sender.sendMessage(ChatColor.GRAY + "No player found with that name");
                }
                else {
                    sender.sendMessage(ChatColor.RED + playerLookup.getName() + ChatColor.GRAY + " has died " + playerLookup.getStatistic(Statistic.DEATHS) + " time(s).");
                }
            }
            else if (args.length == 2 && args[1].equalsIgnoreCase("reset")) {
                if (!sender.hasPermission("server.moderator")) {
                    return true;
                }
                Player playerLookup = Bukkit.getPlayer(args[0]);
                if(playerLookup == null) {
                    sender.sendMessage(ChatColor.GRAY + "No player found with that name");
                }
                else {
                    playerLookup.setStatistic(Statistic.DEATHS, 0);
                    sender.sendMessage(ChatColor.GRAY + "Reset deaths to 0 for " + ChatColor.RED + playerLookup.getName());
                }
            }
        }

        return true;
    }

    public BukkitTask async(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(this, runnable);
    }

    public BukkitTask async(Runnable runnable, long delayMs) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(this, runnable,
                TimeUtil.millisAsTicks(delayMs));
    }

    public BukkitTask async(Runnable runnable, long delay, long every) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(this, runnable,
                TimeUtil.millisAsTicks(delay),
                TimeUtil.millisAsTicks(every));
    }

    public BukkitTask sync(Runnable runnable) {
        return Bukkit.getScheduler().runTask(this, runnable);
    }

    public BukkitTask sync(Runnable runnable, long delayMs) {
        return Bukkit.getScheduler().runTaskLater(this, runnable,
                TimeUtil.millisAsTicks(delayMs));
    }

    public BukkitTask sync(Runnable runnable, long delay, long every) {
        return Bukkit.getScheduler().runTaskTimer(this, runnable,
                TimeUtil.millisAsTicks(delay),
                TimeUtil.millisAsTicks(every));
    }

    public PlayerDB getPlayerDB() {
        return playerDB;
    }

    public static World getHardcoreWorld() {
        return Bukkit.getWorld(Settings.optionWorldName);
    }

    public static World getHardcoreWorldNether() {
        return Bukkit.getWorld(Settings.optionWorldNameNether);
    }

    public static World getHardcoreWorldTheEnd() {
        return Bukkit.getWorld(Settings.optionWorldNameTheEnd);
    }

}